package com.example.sk_blog.service;

import com.example.sk_blog.api.request.RegisterRequest;
import com.example.sk_blog.api.response.CaptchaResponse;
import com.example.sk_blog.api.response.InitResponse;
import com.example.sk_blog.api.response.RegisterResponse;
import com.example.sk_blog.model.CaptchaCode;
import com.example.sk_blog.model.User;
import com.example.sk_blog.repositories.*;
import com.github.cage.Cage;
import com.github.cage.GCage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.LinkedHashMap;

@Service
public class ApiAuthService {

    private final CaptchaCodeRepository captchaCodeRepository;
    private final UserRepository userRepository;

    @Value("${api.captcha.expiration:3600}")
    private Integer captchaExpiration;

    @Autowired
    public ApiAuthService(GlobalSettingsRepository globalSettingsRepository, PostRepository postRepository, InitResponse initResponse, TagRepository tagRepository, CaptchaCodeRepository captchaCodeRepository, UserRepository userRepository) {
        this.captchaCodeRepository = captchaCodeRepository;
        this.userRepository = userRepository;
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

    public RegisterResponse register(RegisterRequest request, BindingResult bindingResult) {
        LinkedHashMap<String, String> errors = new LinkedHashMap<>();

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
            User user = new User(0, LocalDateTime.now(), request.getName(), request.getEmail(), request.getPassword());
            userRepository.save(user);
            return new RegisterResponse(true);
        } else {
            return new RegisterResponse(false, errors);
        }
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
}
