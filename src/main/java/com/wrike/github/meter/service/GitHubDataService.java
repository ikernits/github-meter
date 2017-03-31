package com.wrike.github.meter.service;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.wrike.github.meter.domain.GitHubAvatar;
import com.wrike.github.meter.domain.GitHubRepo;
import com.wrike.github.meter.domain.GitHubUser;
import com.wrike.github.meter.ui.LeaderBoardUI;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

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
                    GitHubUserData currentData = gitHubUserDataMap.get(userData.user.getLogin());
                    if (currentData == null || currentData.timestamp < userData.timestamp) {
                        gitHubUserDataMap.put(userData.user.getLogin(), userData);
                    }
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

    public GitHubUser findGitHubUser(String username) {
        GitHubUserData userData = gitHubUserDataMap.get(username);
        if (userData == null) {
            return null;
        } else {
            return userData.user;
        }
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

    public static class GitHubRating {
        private final long starPosition;
        private final long followerPosition;
        private final long total;

        public GitHubRating(long starPosition, long followerPosition, long total) {
            this.starPosition = starPosition;
            this.followerPosition = followerPosition;
            this.total = total;
        }

        public long getStarPosition() {
            return starPosition;
        }

        public long getFollowerPosition() {
            return followerPosition;
        }

        public long getTotal() {
            return total;
        }
    }


    public static class GitHubUserWithMetric {
        private GitHubUser user;
        private long count;

        public GitHubUserWithMetric(GitHubUser user, long count) {
            this.user = user;
            this.count = count;
        }

        public GitHubUser getUser() {
            return user;
        }

        public long getCount() {
            return count;
        }
    }

    public List<GitHubUserWithMetric> buildTopUsers(Function<GitHubUser, Long> metricMapper, int limit) {
        Set<String> gitHubUsers = listGitHubUsers();

        return gitHubUsers.stream()
            .map(this::findGitHubUser)
            .filter(Objects::nonNull)
            .map(user -> new GitHubUserWithMetric(user, metricMapper.apply(user)))
            .sorted(Comparator.comparing(
                GitHubUserWithMetric::getCount)
                .thenComparing(gm -> gm.getUser().getUpdated_at(), Comparator.reverseOrder())
                .reversed()
            )
            .limit(limit)
            .collect(Collectors.toList());
    }

    public long getStarSumMapper(GitHubUser user) {
        if (user == null) {
            return 0;
        } else {
            List<GitHubRepo> repos = listGitHubUserRepos(user.getLogin());
            return repos.stream()
                .filter(r -> r.getOwner().getId() == user.getId())
                .mapToLong(GitHubRepo::getStargazers_count)
                .sum();
        }
    }

    public long getFollowersMapper(GitHubUser user) {
        if (user == null) {
            return 0;
        } else {
            return user.getFollowers();
        }
    }

    public int findGitHubUserRating(String username, Function<GitHubUser, Long> metricMapper) {
        List<GitHubUserWithMetric> userWithMetricList = buildTopUsers(metricMapper, Integer.MAX_VALUE);
        int i = 1;
        for (GitHubUserWithMetric um : userWithMetricList) {
            if (username.equalsIgnoreCase(um.getUser().getLogin())) {
                break;
            }
            i++;
        }
        return i;
    }
}
