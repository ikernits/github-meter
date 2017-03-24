package com.wrike.github.meter.ui;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.wrike.github.meter.domain.GitHubRepo;
import com.wrike.github.meter.service.GitHubDataService;
import com.wrike.github.meter.service.GitHubQueryService;
import com.wrike.github.meter.service.VaadinServices;
import org.apache.log4j.Logger;
import org.ikernits.vaadin.VaadinBuilders;
import org.ikernits.vaadin.VaadinComponentAttributes;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.ikernits.vaadin.VaadinComponentAttributes.ComponentAttributes.*;

/**
 * Created by ikernitsky on 3/24/17.
 */
public class LeaderBoardUI {
    static Logger log = Logger.getLogger(RegistrationUI.class);

    private GitHubDataService gitHubDataService = VaadinServices.getGitHubDataService();
    private GitHubQueryService gitHubQueryService = VaadinServices.getGitHubQueryService();


    private static class GitHubUserWithMetric {
        private String username;
        private long count;

        public GitHubUserWithMetric(String username, long count) {
            this.username = username;
            this.count = count;
        }

        public String getUsername() {
            return username;
        }

        public long getCount() {
            return count;
        }
    }

    public Component create() {
        Grid grid = VaadinBuilders.grid()
                .setAttributes(vaWidth100, vaHeight100)
                .build();

        grid.addColumn("Avatar");
        grid.addColumn("Name");
        grid.addColumn("Stars");

        Set<String> gitHubUsers = gitHubDataService.listGitHubUsers();
        Map<String, AtomicInteger> metricCounter = new HashMap<>();

        List<GitHubUserWithMetric> usersWithMetric = gitHubUsers.stream()
                .map(user -> {
                    List<GitHubRepo> repos = gitHubDataService.listGitHubUserRepos(user);
                    long count = repos.stream()
                            .filter(r -> user.equals(r.getOwner().getLogin()))
                            .mapToLong(GitHubRepo::getStargazers_count)
                            .sum();
                    return new GitHubUserWithMetric(user, count);
                })
                .sorted(Comparator.comparing(
                        GitHubUserWithMetric::getCount)
                        .thenComparing(GitHubUserWithMetric::getUsername)
                )
                .limit(10)
                .collect(Collectors.toList());

        usersWithMetric.forEach(um -> {
            String avatar = gitHubDataService.findGitHubUserAvatarName(um.getUsername());
            String
            grid.addRow("<img src=\"" +  + "\"></img>", "")

        });

        VerticalLayout content = VaadinBuilders.verticalLayout()
                .addComponent(VaadinBuilders.label()
                    .setValue("Leaderboard")
                    .build())
                .addComponent(grid)
                .build();

        Panel panel = VaadinBuilders.panel()
                .setWidth(1200, Sizeable.Unit.PIXELS)
                .setHeight(800, Sizeable.Unit.PIXELS)
                .setContent(content)
                .build();
    }
}
