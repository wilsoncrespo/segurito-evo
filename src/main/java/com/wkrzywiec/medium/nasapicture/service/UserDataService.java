package com.wkrzywiec.medium.nasapicture.service;

import com.wkrzywiec.medium.nasapicture.model.UserData;
import org.springframework.stereotype.Service;

@Service
public class UserDataService {

    public UserData getUserData() {
        return new UserData();
    }
}
