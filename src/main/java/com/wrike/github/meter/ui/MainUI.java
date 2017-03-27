package com.wrike.github.meter.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.wrike.github.meter.domain.GitHubAvatar;
import com.wrike.github.meter.domain.GitHubRepo;
import com.wrike.github.meter.domain.GitHubUser;
import com.wrike.github.meter.service.GitHubDataService;
import com.wrike.github.meter.service.GitHubQueryService;
import com.wrike.github.meter.service.VaadinServices;
import org.apache.log4j.Logger;
import org.ikernits.vaadin.VaadinBuilders;

import java.util.List;
import java.util.stream.Collectors;

import static org.ikernits.vaadin.VaadinComponentAttributes.ComponentAttributes.vaHeight100;
import static org.ikernits.vaadin.VaadinComponentAttributes.ComponentAttributes.vaStyleMarginNormal;
import static org.ikernits.vaadin.VaadinComponentAttributes.ComponentAttributes.vaStylePaddingNormal;
import static org.ikernits.vaadin.VaadinComponentAttributes.ComponentAttributes.vaWidth100;
import static org.ikernits.vaadin.VaadinComponentAttributes.LayoutAttributes.vaSpacing;

/**
 * Created by ikernits on 10/10/15.
 */
@Title("GitHub Meter Challenge")
@Theme("valo-ext-flat")
public class MainUI extends UI {

    static Logger log = Logger.getLogger(RegistrationUI.class);

    private GitHubDataService gitHubDataService = VaadinServices.getGitHubDataService();

    private long getStarSum(String username) {
        GitHubUser user = gitHubDataService.findGitHubUser(username);
        if (user == null) {
            return 0;
        }
        List<GitHubRepo> repos = gitHubDataService.listGitHubUserRepos(username);
        return repos.stream()
            .filter(r -> r.getOwner().getId() == user.getId())
            .mapToLong(GitHubRepo::getStargazers_count)
            .sum();
    }

    public long getFollowers(String username) {
        GitHubUser user = gitHubDataService.findGitHubUser(username);
        if (user == null) {
            return 0;
        }
        return user.getFollowers();
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        Component content;
        if (vaadinRequest instanceof VaadinServletRequest) {
            VaadinServletRequest request = (VaadinServletRequest) vaadinRequest;
            String path = request.getPathInfo();
            if ("/leaderboard/stars".equals(path)) {
                content = new LeaderBoardUI().create("Top GitHub Star Masters", this::getStarSum);
            } else if ("/leaderboard/watchers".equals(path)) {
                content = new LeaderBoardUI().create("Most Followed GitHub Commiters", this::getFollowers);
            } else if ("/leaderboard".equals(path)) {
                content = VaadinBuilders.horizontalLayout()
                        .setSpacing(true)
                        .addComponent(new LeaderBoardUI().create("Most Followed GitHub Commiters", this::getFollowers))
                        .addComponent(new LeaderBoardUI().create("Top GitHub Star Masters", this::getStarSum))
                        .build();
            } else {
                content = new RegistrationUI().create();
            }
            setContent(VaadinBuilders.verticalLayout()
                    .setAttributes(vaWidth100, vaHeight100, vaSpacing)
                .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
                .setStyleName("background-pattern")
                .addComponent(content)
                .build());
        }

        UI.getCurrent().setErrorHandler(e -> log.error("Vaadin UI Error", e.getThrowable()));
    }
}

