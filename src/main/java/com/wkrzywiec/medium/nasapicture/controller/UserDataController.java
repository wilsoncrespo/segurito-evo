package com.wkrzywiec.medium.nasapicture.controller;

import com.google.gson.Gson;
import com.wkrzywiec.medium.nasapicture.model.UserData;
import com.wkrzywiec.medium.nasapicture.service.UserDataService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class UserDataController {

    @Autowired
    private UserDataService service;

    @Value("${swagger.assistance.control.url}")
    private String swaggerAssistanceControlUrl;

    private final int INPUT_ERROR = 0;
    private final int OUTPUT_ERROR = 1;
    private final int INPUT_SUCCESS = 2;
    private final int OUTPUT_SUCCESS = 3;

    private final int OPERATION_SUCCESS = 201;

    @GetMapping("/")
    public String showUserData(ModelMap model) {
        model.addAttribute("userData", service.getUserData());
        return "principal";
    }

    @PostMapping(path = "/registrar")
    public String register(HttpServletRequest request, UriComponentsBuilder uriComponentsBuilder, UserData userData) {
        int requestResult = 0;
        if (request.getParameter("registrarEntrada") != null) {
            requestResult = registerInput(userData);
        } else if (request.getParameter("registrarSalida") != null) {
            requestResult = registerOutput(userData);
        }
        return processResultCode(requestResult);
    }

    private int registerInput(UserData userData) {
        int result = INPUT_ERROR;
        try {
            result = makePostRequest(userData, "entradas") == OPERATION_SUCCESS ? INPUT_SUCCESS : INPUT_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private int registerOutput(UserData userData) {
        int result = OUTPUT_ERROR;
        try {
            result = makePostRequest(userData, "salidas") == OPERATION_SUCCESS ? OUTPUT_SUCCESS : OUTPUT_ERROR;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private int makePostRequest(UserData userData, String serviceMethod) throws IOException {
        String serviceUrl = swaggerAssistanceControlUrl + serviceMethod;
        Gson gson = new Gson();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(serviceUrl);
        StringEntity postingString = new StringEntity(gson.toJson(userData));
        post.setEntity(postingString);
        post.setHeader("Content-type", "application/json");
        HttpResponse response = httpClient.execute(post);
        return response.getStatusLine().getStatusCode();
    }

    private String processResultCode(int resultCode) {
        switch (resultCode) {
            case INPUT_ERROR : return "inputError";
            case OUTPUT_ERROR : return "outputError";
            case INPUT_SUCCESS : return "inputSuccess";
            case OUTPUT_SUCCESS : return "outputSuccess";
            default: return "error";
        }
    }
}
