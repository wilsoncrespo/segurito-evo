package com.wkrzywiec.medium.nasapicture.service;

import com.wkrzywiec.medium.nasapicture.model.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserDataService {

    @Autowired
    private RestTemplate restTemplate;

    public UserData getUserData() {
        return restTemplate.getForObject(
                "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY&hd",
                UserData.class);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
