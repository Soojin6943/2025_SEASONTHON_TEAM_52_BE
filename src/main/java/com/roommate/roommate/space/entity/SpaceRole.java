package com.roommate.roommate.space.entity;

public enum SpaceRole {
    OWNER("방장"),
    MEMBER("멤버");

    private final String description;

    SpaceRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
