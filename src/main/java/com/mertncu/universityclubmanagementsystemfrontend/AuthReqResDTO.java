package com.mertncu.universityclubmanagementsystemfrontend;

import lombok.Data;

import java.util.List;

@Data
public class AuthReqResDTO {
    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String accessToken;
    private String expirationTime;
    private String id;
    private String name;
    private String email;
    private String city;
    private String role;
    private String password;
    private User user;
    private List<User> users;
}