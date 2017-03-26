package com.wrike.github.meter.ui;

import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.wrike.github.meter.domain.GitHubRepo;
import com.wrike.github.meter.service.GitHubDataService;
import com.wrike.github.meter.service.GitHubQueryService;
import com.wrike.github.meter.service.VaadinServices;
import org.apache.log4j.Logger;
import org.ikernits.vaadin.VaadinBuilders;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LeaderBoardUI {
    static Logger log = Logger.getLogger(MainUI.class);

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
    /*

    background-color: ALICEBLUE;
    border-style: dashed;
    border-width: medium;
    border-color: lightseagreen;

        background-image: url(/img/pattern_01.png);
    background-color: aliceblue;
     */

    private Component createUserLayout(GitHubUserWithMetric um) {
        String avatar = gitHubDataService.findGitHubUserAvatarName(um.getUsername());
        return VaadinBuilders.horizontalLayout()
            .setDefaultComponentAlignment(Alignment.MIDDLE_LEFT)
            .setHeight(80, Sizeable.Unit.PIXELS)
            .addComponent(
                VaadinBuilders.label()
                    .setWidth(80, Sizeable.Unit.PIXELS)
                    .setHeightUndefined()
                    .setContentMode(ContentMode.HTML)
                    .setValue("<div><img style = \"width: 64px; height: 64px; vertical-align: bottom;\" src=\"/avatars/" + avatar + "\"></img></div>")
                    .build()
            )
            .addComponent(
                VaadinBuilders.label()
                    .setWidth(320, Sizeable.Unit.PIXELS)
                    .setContentMode(ContentMode.HTML)
                    .setValue("<div style=\"font-size: 36px\">" + um.username + "</div>")
                    .build()
            )
            .addComponent(
                VaadinBuilders.label()
                    .setWidth(100, Sizeable.Unit.PIXELS)
                    .setContentMode(ContentMode.HTML)
                    .setValue("<div style=\"font-size: 36px; text-align: right\">" + um.count + "</div>")
                    .build()
            )
            .build();
    }

    public Component create(String boardName, Function<String, Long> metricMappper) {
        Set<String> gitHubUsers = gitHubDataService.listGitHubUsers();

        List<GitHubUserWithMetric> usersWithMetric = gitHubUsers.stream()
                .map(user -> new GitHubUserWithMetric(user, metricMappper.apply(user)))
                //.filter(m -> m.count != 0)
                .sorted(Comparator.comparing(
                        GitHubUserWithMetric::getCount)
                        .thenComparing(GitHubUserWithMetric::getUsername)
                        .reversed()
                )
                .limit(5)
                .collect(Collectors.toList());

        VerticalLayout layout = VaadinBuilders.verticalLayout()
            .setWidth(600, Sizeable.Unit.PIXELS)
            .setStyleName("leaderboard")
            .setHeightUndefined()
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(VaadinBuilders.label()
                .setWidth(550, Sizeable.Unit.PIXELS)
                .setContentMode(ContentMode.HTML)
                .setValue("<div style=\"text-align: center; font-size: 32px; border-bottom-style: groove\">" + boardName + "</div>")
                .build())
            .build();

        usersWithMetric.forEach(um -> layout.addComponent(createUserLayout(um)));

        return layout;
    }
}
