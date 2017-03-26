package com.wrike.github.meter.domain;

public class GitHubUser {
    private String login;
    private long id;
    private String avatar_url;
    private String repos_url;
    private String email;
    private long public_repos;
    private long followers;

    public String getLogin() {
        return login;
    }

    public long getId() {
        return id;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getRepos_url() {
        return repos_url;
    }

    public String getEmail() {
        return email;
    }

    public long getPublic_repos() {
        return public_repos;
    }

    public long getFollowers() {
        return followers;
    }
}
