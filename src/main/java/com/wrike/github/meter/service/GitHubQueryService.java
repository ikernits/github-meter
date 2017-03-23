package com.wrike.github.meter.service;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import com.wrike.github.meter.domain.GitHubAvatar;
import com.wrike.github.meter.domain.GitHubRepo;
import com.wrike.github.meter.domain.GitHubUser;
import com.wrike.github.meter.service.HttpClientService.HttpRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public class GitHubQueryService {

    private static final Logger log = Logger.getLogger(GitHubQueryService.class);

    private static final String authToken = "7f271900ed3fb07041b98db5f8005e232bee1330";

    private HttpClientService httpClientService;

    @Required
    public void setHttpClientService(HttpClientService httpClientService) {
        this.httpClientService = httpClientService;
    }

    public GitHubUser findGitHubUser(String username) {
        HttpRequest request = HttpRequest.buildGet("https://api.github.com/users/" + username)
            .addHeader("Authorization", "token " + authToken)
            .build();
        try {
            return httpClientService.executeWithJsonResponse(request, GitHubUser.class);
        } catch (HttpClientService.HttpClientException e) {
            log.warn("failed to find github user: '" + username + "'", e);
            return null;
        }
    }

    public List<GitHubRepo> listGitHubRepos(GitHubUser gitHubUser) {
        ImmutableList.Builder<GitHubRepo> repos = ImmutableList.builder();

        try {
            for (int i = 1; i < 100; ++i) {
                HttpRequest request = HttpRequest.buildGet(gitHubUser.getRepos_url())
                    .addHeader("Authorization", "token " + authToken)
                    .addParam("page", "" + i)
                    .build();
                List<GitHubRepo> reposPage = httpClientService.executeWithJsonResponse(request, new TypeToken<List<GitHubRepo>>() {
                });
                repos.addAll(reposPage);
                if (reposPage.isEmpty()) {
                    break;
                }
            }
        } catch (HttpClientService.HttpClientException e) {
            log.warn("error when scanning github repos for user: '" + gitHubUser.getLogin() + "'", e);
        }

        return repos.build();
    }

    public GitHubAvatar loadGitHubAvatar(GitHubUser gitHubUser) {
        HttpRequest request = HttpRequest.buildGet(gitHubUser.getAvatar_url())
            .addHeader("Authorization", "token " + authToken)
            .build();

        try {
            HttpClientService.HttpResponse response = httpClientService.execute(request);
            String typeHeader = response.getFirstHeader("Content-Type");
            String typeName = StringUtils.removeStartIgnoreCase(typeHeader, "image/");
            String type;
            if ("jpeg".equals(typeName)) {
                type = "jpg";
            } else if ("gif".equals(typeName)) {
                type = "gif";
            } else if ("png".equals(typeName)) {
                type = "png";
            } else {
                type = "png";
            }
            return new GitHubAvatar(response.getBody(), type);
        } catch (HttpClientService.HttpClientException e) {
            log.warn("error when loading avatar for user: '" + gitHubUser.getLogin() + "'", e);
            return new GitHubAvatar(new byte[0], "none");
        }
    }


    public static void main(String[] args) throws Exception {
        GitHubQueryService gitHubService = new GitHubQueryService();

        List<String> usernames = ImmutableList.of(
            "ikernits",
            "averrin",
            "Anrock",
            "ilfa",
            "Aetet"
        );
    }
}
