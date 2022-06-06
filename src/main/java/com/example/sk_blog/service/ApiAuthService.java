package com.example.sk_blog.service;

import com.example.sk_blog.api.request.EmailRequest;
import com.example.sk_blog.api.request.LoginRequest;
import com.example.sk_blog.api.request.PasswordChangeRequest;
import com.example.sk_blog.api.request.RegisterRequest;
import com.example.sk_blog.api.response.CaptchaResponse;
import com.example.sk_blog.api.response.LoginResponse;
import com.example.sk_blog.api.response.TrueOrErrorsResponse;
import com.example.sk_blog.api.response.UserLoginResponse;
import com.example.sk_blog.model.CaptchaCode;
import com.example.sk_blog.model.User;
import com.example.sk_blog.model.enums.ModerationStatus;
import com.example.sk_blog.repositories.CaptchaCodeRepository;
import com.example.sk_blog.repositories.GlobalSettingsRepository;
import com.example.sk_blog.repositories.PostRepository;
import com.example.sk_blog.repositories.UserRepository;
import com.github.cage.Cage;
import com.github.cage.GCage;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ApiAuthService {

    private final CaptchaCodeRepository captchaCodeRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PostRepository postRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender javaMailSender;
    private final JWTUtil jwtUtil;
    private final GlobalSettingsRepository globalSettingsRepository;

    @Value("${api.captcha.expiration}")
    private Integer captchaExpiration;

    @Autowired
    public ApiAuthService(CaptchaCodeRepository captchaCodeRepository, UserRepository userRepository, AuthenticationManager authenticationManager, PostRepository postRepository, PasswordEncoder passwordEncoder, JavaMailSender javaMailSender, JWTUtil jwtUtil, GlobalSettingsRepository globalSettingsRepository) {
        this.captchaCodeRepository = captchaCodeRepository;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.postRepository = postRepository;
        this.passwordEncoder = passwordEncoder;
        this.javaMailSender = javaMailSender;
        this.jwtUtil = jwtUtil;
        this.globalSettingsRepository = globalSettingsRepository;
    }

    public CaptchaResponse getCaptcha() {
        deleteExpiredCaptcha();

        Cage cage = new GCage();

        String code = cage.getTokenGenerator().next();

        byte[] byteImage = drawImage(cage, code);
        String encodedCaptcha = Base64.getEncoder().encodeToString(byteImage);

        String secretCode = getSecretCode();

        CaptchaCode captchaEntity = new CaptchaCode(code, secretCode, LocalDateTime.now());

        captchaCodeRepository.save(captchaEntity);

        return new CaptchaResponse(secretCode, "data:image/png;base64, " + encodedCaptcha);
    }

    public ResponseEntity<?> register(RegisterRequest request, BindingResult bindingResult) {
        if (globalSettingsRepository.findByCode("MULTIUSER_MODE").getValue().equals("NO")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Map<String, String> errors = new LinkedHashMap<>();

        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                String fieldName = ((FieldError) error).getField();

                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            }
        }

        if (!errors.containsKey("email")) {
            if (userRepository.existsByEmail(request.getEmail()))  {
                errors.put("email", "Этот e-mail уже зарегистрирован");
            }
        }

        CaptchaCode captchaCode = captchaCodeRepository.findBySecretCode(request.getCaptchaSecret());

        if (captchaCode == null || !captchaCode.getCode().equals(request.getCaptcha())) {
            errors.put("captcha", "Код с картинки введён неверно");
        }

        if (errors.isEmpty()) {
            User user = new User(0, LocalDateTime.now(), request.getName(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
            return ResponseEntity.ok(new TrueOrErrorsResponse(true));
        } else {
            return ResponseEntity.ok(new TrueOrErrorsResponse(errors));
        }
    }


    public LoginResponse login(LoginRequest loginRequest) {
        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(auth);
            org.springframework.security.core.userdetails.User securityUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();

            return getLoginResponse(securityUser.getUsername());
        } catch (AuthenticationException e) {
            return new LoginResponse();      // or BindingResult?
        }
    }

    public LoginResponse authCheck(Principal principal) {
        if (principal == null) {
            return new LoginResponse();
        }

        return getLoginResponse(principal.getName());
    }

    public TrueOrErrorsResponse restorePassword(EmailRequest emailRequest) {
        Optional<User> optional = userRepository.findByEmail(emailRequest.getEmail());

        if (optional.isPresent()) {
            User user = optional.get();

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("schlechtgut@mail.ru");
            message.setTo(emailRequest.getEmail());
            message.setSubject("Password changing");

            String code = jwtUtil.generateToken(emailRequest.getEmail());

            message.setText("http://localhost:8080/login/change-password/" + code);
            user.setCode(code);
            userRepository.save(user);

            javaMailSender.send(message);

            return new TrueOrErrorsResponse(true);
        }

        return new TrueOrErrorsResponse();
    }


    public TrueOrErrorsResponse changePassword(PasswordChangeRequest passwordChangeRequest) {
        Map<String, String> errors = new LinkedHashMap<>();
        String code = passwordChangeRequest.getCode();
        String captcha = passwordChangeRequest.getCaptcha();
        String captchaSecret = passwordChangeRequest.getCaptchaSecret();
        String password = passwordChangeRequest.getPassword();

        CaptchaCode captchaCode = captchaCodeRepository.findBySecretCode(captchaSecret);

        User user = userRepository.findByCode(code);

        if (user == null) {
            errors.put("code", "code is incorrect");
        } else if (jwtUtil.isTokenExpired(passwordChangeRequest.getCode())) {
            errors.put("code", "Ссылка для восстановления пароля устарела. <a href=\"/login/restore-password\">Запросить ссылку снова</a>");
        } else if (captcha == null) {
            errors.put("captcha_secret", "такой капчи нет");
        } else if (!captchaCode.getCode().equals(captcha)) {
            errors.put("captcha", "Код с картинки введён неверно");
        }

        if (errors.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            return new TrueOrErrorsResponse(true);
        }

        return new TrueOrErrorsResponse(errors);
    }





    /////////////////////////private///////////////////////////////////////////////////



    private String getSecretCode() {
        KeyGenerator keyGenerator;
        String encodedKey = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] rawData = secretKey.getEncoded();
            encodedKey = Base64.getEncoder().encodeToString(rawData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return encodedKey;
    }

    private byte[] drawImage(Cage cage, String code) {
        BufferedImage originalImage = cage.drawImage(code);

        BufferedImage resizedImage = new BufferedImage(100, 35, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, 100, 35, null);
        graphics2D.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(resizedImage, "jpg", baos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray();
    }

    private void deleteExpiredCaptcha() {
        captchaCodeRepository.deleteAllByTimeBefore(LocalDateTime.now().minusSeconds(captchaExpiration));
    }

    private LoginResponse getLoginResponse(String email) {
        com.example.sk_blog.model.User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));

        UserLoginResponse userResponse = new UserLoginResponse();
        userResponse.setEmail(user.getEmail());
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setPhoto(user.getPhoto());

        if (user.isModerator()) {
            userResponse.setModeration(true);
            userResponse.setSettings(true);
            userResponse.setModerationCount(postRepository.countByModerationStatus(ModerationStatus.NEW));
        }

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setResult(true);
        loginResponse.setUserLoginResponse(userResponse);

        return loginResponse;
    }

    private String generateCode() {
        return RandomStringUtils.randomAlphanumeric(45);
    }

}
