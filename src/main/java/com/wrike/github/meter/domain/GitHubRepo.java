package com.wrike.github.meter.domain;

public class GitHubRepo {
    private GitHubUser owner;

    private String name;
    private long size;
    private boolean fork;
    private long stargazers_count;
    private long forks_count;
    private String language;

    public GitHubUser getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public long getSize() {
        return size;
    }

    public boolean isFork() {
        return fork;
    }

    public long getStargazers_count() {
        return stargazers_count;
    }

    public long getForks_count() {
        return forks_count;
    }

    public String getLanguage() {
        return language;
    }
}
