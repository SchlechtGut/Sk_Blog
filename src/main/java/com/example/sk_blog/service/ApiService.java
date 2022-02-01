package com.example.sk_blog.service;

import com.example.sk_blog.model.GlobalSetting;
import com.example.sk_blog.api.response.InitResponse;
import com.example.sk_blog.api.response.GlobalSettingsProperty;
import com.example.sk_blog.repositories.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ApiService {
    GlobalSettingsRepository globalSettingsRepository;
    InitResponse initResponse;

    @Autowired
    public ApiService(GlobalSettingsRepository globalSettingsRepository, InitResponse initResponse) {
        this.globalSettingsRepository = globalSettingsRepository;
        this.initResponse = initResponse;
    }

    public InitResponse getApiInitProviderProperties() {
        return initResponse;
    }

    public GlobalSettingsProperty getGlobalSettings() {
        String multi = null;
        String moder = null;
        String statistic = null;

        for(GlobalSetting setting : globalSettingsRepository.findAll()) {
            switch (setting.getCode()) {
                case "MULTIUSER_MODE": multi = setting.getValue();
                    break;
                case "POST_PREMODERATION": moder = setting.getValue();
                    break;
                case "STATISTICS_IS_PUBLIC": statistic = setting.getValue();
            }
        }

        List<String> list = new ArrayList<>(Arrays.asList(multi, moder, statistic));

        for (int i = 0;i < list.size();i++) {
            if (list.get(i).equals("YES")) {
                list.set(i, "true");
            } else {
                list.set(i, "false");
            }
        }

        return new GlobalSettingsProperty(Boolean.parseBoolean(list.get(0)), Boolean.parseBoolean(list.get(1)), Boolean.parseBoolean(list.get(2)));
    }
}
