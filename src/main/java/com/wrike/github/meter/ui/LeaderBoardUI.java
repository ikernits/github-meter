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
import com.wrike.github.meter.domain.GitHubUser;
import com.wrike.github.meter.service.GitHubDataService;
import com.wrike.github.meter.service.GitHubDataService.GitHubUserWithMetric;
import com.wrike.github.meter.service.GitHubQueryService;
import com.wrike.github.meter.service.VaadinServices;
import org.apache.log4j.Logger;
import org.ikernits.vaadin.VaadinBuilders;
import org.ikernits.vaadin.VaadinComponentAttributes;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LeaderBoardUI {
    static Logger log = Logger.getLogger(MainUI.class);

    private GitHubDataService gitHubDataService = VaadinServices.getGitHubDataService();
    private GitHubQueryService gitHubQueryService = VaadinServices.getGitHubQueryService();

    private Component createUserLayout(GitHubUserWithMetric um) {
        String avatar = gitHubDataService.findGitHubUserAvatarName(um.getUser().getLogin());
        return VaadinBuilders.horizontalLayout()
            .setDefaultComponentAlignment(Alignment.MIDDLE_LEFT)
            .setHeight(70, Sizeable.Unit.PIXELS)
            .addComponent(
                VaadinBuilders.label()
                    .setWidth(70, Sizeable.Unit.PIXELS)
                    .setHeightUndefined()
                    .setContentMode(ContentMode.HTML)
                    .setValue("<div><img style = \"width: 50px; height: 50px; vertical-align: bottom;\" src=\"/avatars/" + avatar + "\"></img></div>")
                    .build()
            )
            .addComponent(
                VaadinBuilders.label()
                    .setWidth(260, Sizeable.Unit.PIXELS)
                    .setContentMode(ContentMode.HTML)
                    .setValue("<div style=\"font-size: 28px; color: white; font-weight: 400\">" + um.getUser().getLogin() + "</div>")
                    .build()
            )
            .addComponent(
                VaadinBuilders.label()
                    .setWidth(80, Sizeable.Unit.PIXELS)
                    .setContentMode(ContentMode.HTML)
                    .setValue("<div style=\"font-size: 40px; color: white; font-weight: 800; text-align: right\">" + um.getCount() + "</div>")
                    .build()
            )
            .build();
    }

    public Component create(String boardName, Function<GitHubUser, Long> metricMapper) {


        VerticalLayout users = VaadinBuilders.verticalLayout()
            .setWidth(410, Sizeable.Unit.PIXELS)
            .setHeight(350, Sizeable.Unit.PIXELS)
            .build();

        List<GitHubUserWithMetric> gitHubUsersWithMetric = gitHubDataService.buildTopUsers(metricMapper, 5);
        gitHubUsersWithMetric.forEach(um -> users.addComponent(createUserLayout(um)));
        for (int i = gitHubUsersWithMetric.size(); i < 5; ++i) {
            users.addComponent(VaadinBuilders.horizontalLayout().build());
        }


        VerticalLayout layout = VaadinBuilders.verticalLayout()
            .setWidth(480, Sizeable.Unit.PIXELS)
            .setHeight(450, Sizeable.Unit.PIXELS)
            .addStyleName("bg-color-window")
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(VaadinBuilders.label()
                .setWidth(420, Sizeable.Unit.PIXELS)
                .setContentMode(ContentMode.HTML)
                .setValue("<div style=\"margin-top: 20px; text-align: center; font-size: 28px; color: white; border-bottom-style: groove\">" + boardName + "</div>")
                .build())
            .addComponent(users)
            .setExpandRatio(users, 1.f)
            .build();


        return layout;
    }
}
