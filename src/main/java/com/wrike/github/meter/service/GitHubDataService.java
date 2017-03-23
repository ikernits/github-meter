package com.wrike.github.meter.service;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.wrike.github.meter.domain.GitHubAvatar;
import com.wrike.github.meter.domain.GitHubRepo;
import com.wrike.github.meter.domain.GitHubUser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class GitHubDataService implements InitializingBean {
    private static final Logger log = Logger.getLogger(GitHubDataService.class);
    private static final Gson gson = new Gson();

    protected String dataDir;
    protected String avatarDir;

    @Required
    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    @Required
    public void setAvatarDir(String avatarDir) {
        this.avatarDir = avatarDir;
    }

    private static class GitHubUserData {
        String uid;
        GitHubUser user;
        List<GitHubRepo> repos;
        String avatarName;
        long timestamp;

        public GitHubUserData(String uid, GitHubUser user, List<GitHubRepo> repos, String avatarName) {
            this.timestamp = System.currentTimeMillis();
            this.uid = uid;
            this.user = user;
            this.repos = repos;
            this.avatarName = avatarName;
        }
    }

    private Map<String, GitHubUserData> gitHubUserDataMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws IOException {
        FileUtils.forceMkdir(new File(dataDir));

        File[] userDataFiles = new File(dataDir).listFiles(pathname -> pathname.getName().endsWith(".json"));
        if (userDataFiles != null) {
            for (File dataFile : userDataFiles) {
                try {
                    GitHubUserData userData = gson.fromJson(FileUtils.readFileToString(dataFile), GitHubUserData.class);
                    gitHubUserDataMap.put(userData.user.getLogin(), userData);
                } catch (IOException | JsonParseException e) {
                    log.warn("failed to load userdata file: " + dataFile, e);
                }
            }
        }

        log.info("loaded " + gitHubUserDataMap.size() + " user data files");
    }

    public void addGitHubUser(GitHubUser user, List<GitHubRepo> repos, GitHubAvatar avatar) {
        String uid = RandomStringUtils.randomAlphanumeric(16);
        String avatarName = uid + "." + avatar.getType();
        File userDataFile = new File(dataDir, "user-" + uid + ".json");
        GitHubUserData userData = new GitHubUserData(uid, user, repos, avatarName);

        gitHubUserDataMap.put(userData.user.getLogin(), userData);

        try {
            FileUtils.writeStringToFile(userDataFile, gson.toJson(userData));
            FileUtils.writeByteArrayToFile(new File(avatarDir, avatarName), avatar.getData());
        } catch (IOException e) {
            FileUtils.deleteQuietly(userDataFile);
            log.error("failed to create github user data", e);
        }
    }

    public Set<String> listGitHubUsers() {
        return gitHubUserDataMap.keySet();
    }

    public List<GitHubRepo> listGitHubUserRepos(String username) {
        GitHubUserData userData = gitHubUserDataMap.get(username);
        if (userData == null) {
            return ImmutableList.of();
        } else {
            return userData.repos;
        }
    }

    public String findGitHubUserAvatarName(String username) {
        GitHubUserData userData = gitHubUserDataMap.get(username);
        if (userData == null) {
            return null;
        } else {
            return userData.avatarName;
        }
    }
}
