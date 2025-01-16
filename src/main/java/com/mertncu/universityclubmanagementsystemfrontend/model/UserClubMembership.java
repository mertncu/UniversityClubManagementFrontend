package com.mertncu.universityclubmanagementsystemfrontend.model;

public class UserClubMembership {
    private String userId;
    private Integer clubId;

    // Constructors
    public UserClubMembership() {
    }

    public UserClubMembership(String userId, Integer clubId) {
        this.userId = userId;
        this.clubId = clubId;
    }

    // Getters and setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getClubId() {
        return clubId;
    }

    public void setClubId(Integer clubId) {
        this.clubId = clubId;
    }

    @Override
    public String toString() {
        return "UserClubMembership{" +
                "userId='" + userId + '\'' +
                ", clubId=" + clubId +
                '}';
    }
}