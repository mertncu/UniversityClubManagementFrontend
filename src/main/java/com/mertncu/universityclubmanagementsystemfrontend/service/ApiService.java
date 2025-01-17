package com.mertncu.universityclubmanagementsystemfrontend.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mertncu.universityclubmanagementsystemfrontend.model.AuthReqResDTO;
import com.mertncu.universityclubmanagementsystemfrontend.model.AuthToken;
import com.mertncu.universityclubmanagementsystemfrontend.model.Club;
import com.mertncu.universityclubmanagementsystemfrontend.model.User;
import com.mertncu.universityclubmanagementsystemfrontend.utils.HttpClientUtil;
import com.mertncu.universityclubmanagementsystemfrontend.utils.LocalDateTypeAdapter;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ApiService {

    private static final String BASE_URL = "http://localhost:8080"; // Replace with your backend URL
    private Gson gson = new Gson();
    private AuthToken authToken;

    public void setAuthToken(AuthToken token) {
        this.authToken = token;
    }

    public ApiService() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .create();
    }

    // Authentication
    public AuthReqResDTO login(AuthReqResDTO loginRequest) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientUtil.createHttpClient()) {
            HttpPost httpPost = new HttpPost(BASE_URL + "/auth/login");
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            StringEntity entity = new StringEntity(gson.toJson(loginRequest));
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return gson.fromJson(responseBody, AuthReqResDTO.class);
            }
        }
    }

    // User Management
    public AuthReqResDTO createUser(User user) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientUtil.createHttpClient()) {
            HttpPost httpPost = new HttpPost(BASE_URL + "/admin/create-user");

            // Debugging print statements (Keep these for now)
            System.out.println("createUser() - Sending Authorization: Bearer " + authToken.getAccessToken());

            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getAccessToken());
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            // Debugging print statement (Keep this for now)
            System.out.println("createUser() - Authorization header set to: " + httpPost.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue());

            StringEntity entity = new StringEntity(gson.toJson(user));
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 201 || statusCode == 200) { // Assuming 201 Created or 200 OK for success
                    return gson.fromJson(responseBody, AuthReqResDTO.class);
                } else {
                    // Create an error AuthReqResDTO
                    AuthReqResDTO errorResponse = new AuthReqResDTO();
                    errorResponse.setStatusCode(statusCode);
                    errorResponse.setError("Server Error");

                    // Try to get a more detailed message from the response body
                    if (responseBody != null && !responseBody.isEmpty()) {
                        errorResponse.setMessage("Server returned an error: " + responseBody);
                    } else {
                        errorResponse.setMessage("Server returned an error: " + statusCode);
                    }

                    return errorResponse;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle exceptions and create an appropriate error response
            AuthReqResDTO errorResponse = new AuthReqResDTO();
            errorResponse.setStatusCode(500); // Internal Server Error
            errorResponse.setError("Client Error");
            errorResponse.setMessage("An error occurred on the client side: " + e.getMessage());
            return errorResponse;
        }
    }

    public void assignUserToClub(String userId, int clubId) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientUtil.createHttpClient()) {
            HttpPost httpPost = new HttpPost(BASE_URL + "/public/club/member/assign?userId=" + userId + "&clubId=" + clubId);
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getAccessToken());

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Failed to assign user to club: " + response.getStatusLine().getReasonPhrase());
                }
            }
        }
    }

    public AuthReqResDTO getAllUsers() throws IOException {
        try (CloseableHttpClient httpClient = HttpClientUtil.createHttpClient()) {
            HttpGet httpGet = new HttpGet(BASE_URL + "/admin/get-all-users");

            System.out.println("Sending Authorization: Bearer " + authToken.getAccessToken()); // Keep for debugging
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getAccessToken());

            System.out.println("Authorization header set to: " +
                    httpGet.getFirstHeader(HttpHeaders.AUTHORIZATION).getValue()); // Keep for debugging

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                int statusCode = response.getStatusLine().getStatusCode(); // Get the status code

                if (statusCode == 200) {
                    // Success: Parse the response as AuthReqResDTO
                    return gson.fromJson(responseBody, AuthReqResDTO.class);
                } else {
                    // Error: Create an error AuthReqResDTO
                    AuthReqResDTO errorResponse = new AuthReqResDTO();
                    errorResponse.setStatusCode(statusCode);
                    errorResponse.setError("Server Error");
                    errorResponse.setMessage("Server returned an error: " + statusCode);

                    // Optionally, try to extract more details from the response body
                    if (responseBody != null && !responseBody.isEmpty()) {
                        errorResponse.setMessage(responseBody); // Use the response body as the message
                    }
                    return errorResponse;
                }
            }
        }
    }
    public AuthReqResDTO updateUser(String userId, User updatedUser) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientUtil.createHttpClient()) {
            HttpPut httpPut = new HttpPut(BASE_URL + "/admin/update/" + userId);
            httpPut.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getAccessToken());
            httpPut.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            StringEntity entity = new StringEntity(gson.toJson(updatedUser));
            httpPut.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return gson.fromJson(responseBody, AuthReqResDTO.class);
            }
        }
    }

    public AuthReqResDTO deleteUser(String userId) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientUtil.createHttpClient()) {
            HttpDelete httpDelete = new HttpDelete(BASE_URL + "/admin/delete/" + userId);
            httpDelete.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getAccessToken());

            try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return gson.fromJson(responseBody, AuthReqResDTO.class);
            }
        }
    }

    // Club Management
    public List<Club> getAllClubs() throws IOException {
        try (CloseableHttpClient httpClient = HttpClientUtil.createHttpClient()) {
            HttpGet httpGet = new HttpGet(BASE_URL + "/public/clubs/getAllClubs");
            // Assuming no authentication is needed for this endpoint

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                String responseBody = EntityUtils.toString(response.getEntity());

                Type clubListType = new TypeToken<List<Club>>() {}.getType();
                return gson.fromJson(responseBody, clubListType);
            }
        }
    }

    public Club createClub(Club club) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientUtil.createHttpClient()) {
            HttpPost httpPost = new HttpPost(BASE_URL + "/public/clubs/createClub");
            httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getAccessToken());
            httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            StringEntity entity = new StringEntity(gson.toJson(club));
            httpPost.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return gson.fromJson(responseBody, Club.class);
            }
        }
    }

    public Club updateClub(Long clubId, Club updatedClub) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientUtil.createHttpClient()) {
            HttpPut httpPut = new HttpPut(BASE_URL + "/public/clubs/updateClub/" + clubId);
            httpPut.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getAccessToken());
            httpPut.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            StringEntity entity = new StringEntity(gson.toJson(updatedClub));
            httpPut.setEntity(entity);

            try (CloseableHttpResponse response = httpClient.execute(httpPut)) {
                String responseBody = EntityUtils.toString(response.getEntity());
                return gson.fromJson(responseBody, Club.class);
            }
        }
    }

    public void deleteClub(Long clubId) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientUtil.createHttpClient()) {
            HttpDelete httpDelete = new HttpDelete(BASE_URL + "/public/clubs/deleteClub/" + clubId);
            httpDelete.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getAccessToken());

            try (CloseableHttpResponse response = httpClient.execute(httpDelete)) {
                if (response.getStatusLine().getStatusCode() != 204) {
                    throw new RuntimeException("Failed to delete club: " + response.getStatusLine().getReasonPhrase());
                }
            }
        }
    }

    public Integer getClubIdByName(String clubName) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientUtil.createHttpClient()) {
            // URL-encode the clubName
            String encodedClubName = URLEncoder.encode(clubName, StandardCharsets.UTF_8);
            String url = BASE_URL + "/public/clubs/getClub/name?name=" + encodedClubName;

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getAccessToken());

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    Club club = gson.fromJson(responseBody, Club.class);
                    return club.getClubId().intValue();
                } else {
                    throw new RuntimeException("Failed to get club by name: " + response.getStatusLine().getReasonPhrase());
                }
            }
        }
    }

    public CompletableFuture<AuthReqResDTO> getMyProfile() {
        return CompletableFuture.supplyAsync(() -> {
            try (CloseableHttpClient httpClient = HttpClientUtil.createHttpClient()) {
                HttpGet httpGet = new HttpGet(BASE_URL + "/adminuser/get-profile");
                httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getAccessToken());

                try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    return gson.fromJson(responseBody, AuthReqResDTO.class);
                }
            } catch (IOException e) {
                // Log the exception or handle it as per your application's needs
                e.printStackTrace(); // This line should stay for debugging.
                throw new RuntimeException("Failed to fetch user profile", e);
            }
        });
    }

    public CompletableFuture<AuthReqResDTO> getTasksByUser(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            try (CloseableHttpClient httpClient = HttpClientUtil.createHttpClient()) {
                HttpGet httpGet = new HttpGet(BASE_URL + "/public/tasks/getTasksByUser/" + userId);
                httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authToken.getAccessToken());

                try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                    String responseBody = EntityUtils.toString(response.getEntity());
                    return gson.fromJson(responseBody, AuthReqResDTO.class);
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to get tasks by user", e);
            }
        });
    }

}