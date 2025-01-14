package com.mertncu.universityclubmanagementsystemfrontend;


import lombok.Data;

@Data
public class User {
    private String id;
    private String name;
    private String email;
    private String role;
    private String password;
}
