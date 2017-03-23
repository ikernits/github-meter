package com.wrike.github.meter.domain;

public class GitHubAvatar {
    private byte[] data;
    private String type;

    public GitHubAvatar(byte[] data, String type) {
        this.data = data;
        this.type = type;
    }

    public byte[] getData() {
        return data;
    }

    public String getType() {
        return type;
    }
}
