package com.bio.dhh.bio.dto;

import lombok.Data;

@Data
public class SettingsUpdateDTO {
    private String userId;
    private Boolean showStats;
    private Boolean emailNotifications;
    private Boolean analyticsEnabled;
    private Boolean publicProfile;
}