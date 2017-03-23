package com.wrike.github.meter.service;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

public class VaadinServices implements InitializingBean {
    private GitHubDataService gitHubDataService;
    private GitHubQueryService gitHubQueryService;

    private static VaadinServices instance = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        instance = this;
    }

    @Required
    public void setGitHubDataService(GitHubDataService gitHubDataService) {
        this.gitHubDataService = gitHubDataService;
    }

    @Required
    public void setGitHubQueryService(GitHubQueryService gitHubQueryService) {
        this.gitHubQueryService = gitHubQueryService;
    }

    private static VaadinServices getInstance() {
        return Preconditions.checkNotNull(instance);
    }

    public static GitHubDataService getGitHubDataService() {
        return getInstance().gitHubDataService;
    }

    public static GitHubQueryService getGitHubQueryService() {
        return getInstance().gitHubQueryService;
    }
}
