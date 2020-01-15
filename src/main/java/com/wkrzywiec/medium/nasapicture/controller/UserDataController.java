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
    private final int INPUT_CONFLICT = 4;
    private final int OUTPUT_CONFLICT = 5;
    private final int INTERNAL_ERROR_IN_ORIGIN = 6;

    private final int OPERATION_SUCCESS = 201;
    private final int OPERATION_CONFLICT = 409;
    private final int INTERNAL_ERROR = 500;

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
        try {
            int operationResultCode = makePostRequest(userData, "entradas");
            return processResultCode(operationResultCode, INPUT_SUCCESS, INPUT_CONFLICT, INPUT_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return INPUT_ERROR;
    }

    private int registerOutput(UserData userData) {
        try {
            int operationResultCode = makePostRequest(userData, "salidas");
            return processResultCode(operationResultCode, OUTPUT_SUCCESS, OUTPUT_CONFLICT, OUTPUT_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return OUTPUT_ERROR;
    }

    private int processResultCode(int operationResultCode, int success, int conflict, int defaultReturnValue) {
        switch (operationResultCode) {
            case OPERATION_SUCCESS : return success;
            case OPERATION_CONFLICT : return conflict;
            case INTERNAL_ERROR : return INTERNAL_ERROR_IN_ORIGIN;
            default: return defaultReturnValue;
        }
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
            case INPUT_CONFLICT : return "inputConflict";
            case OUTPUT_CONFLICT : return "outputConflict";
            case INTERNAL_ERROR_IN_ORIGIN : return "internalErrorOrigin";
            default: return "error";
        }
    }
}
