package com.wkrzywiec.medium.nasapicture.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserData {

    @JsonProperty("codigo_usuario")
    private String code;
}