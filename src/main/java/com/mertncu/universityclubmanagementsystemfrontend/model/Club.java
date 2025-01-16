package com.mertncu.universityclubmanagementsystemfrontend.model;

public class Club {
    private Long clubId;
    private String name;
    private String description;
    private String clubPicture;

    // Getters and setters

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClubPicture() {
        return clubPicture;
    }

    public void setClubPicture(String clubPicture) {
        this.clubPicture = clubPicture;
    }

    @Override
    public String toString() {
        return name; // Or any other representation you prefer
    }
}