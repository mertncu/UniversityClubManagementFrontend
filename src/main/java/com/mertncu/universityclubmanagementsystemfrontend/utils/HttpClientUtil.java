package com.mertncu.universityclubmanagementsystemfrontend.utils;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpClientUtil {
    public static CloseableHttpClient createHttpClient() {
        return HttpClients.createDefault();
    }
}