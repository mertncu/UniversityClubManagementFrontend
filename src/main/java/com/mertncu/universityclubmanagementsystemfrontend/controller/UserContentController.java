package com.mertncu.universityclubmanagementsystemfrontend.controller;

import com.mertncu.universityclubmanagementsystemfrontend.model.AuthToken;
import com.mertncu.universityclubmanagementsystemfrontend.service.ApiService;

public interface UserContentController {
    void setApiService(ApiService apiService);
    void setAuthToken(AuthToken authToken);
    void setUserId(String userId);
    void initializeData();
    void setViewName(String viewName);
}