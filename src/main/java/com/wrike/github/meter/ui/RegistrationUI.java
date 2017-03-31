package com.wrike.github.meter.ui;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.wrike.github.meter.domain.GitHubAvatar;
import com.wrike.github.meter.domain.GitHubRepo;
import com.wrike.github.meter.domain.GitHubUser;
import com.wrike.github.meter.service.GitHubDataService;
import com.wrike.github.meter.service.GitHubQueryService;
import com.wrike.github.meter.service.VaadinServices;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.ikernits.vaadin.VaadinBuilders;

import java.util.List;

import static org.ikernits.vaadin.VaadinComponentAttributes.ComponentAttributes.vaStylePaddingNormal;
import static org.ikernits.vaadin.VaadinComponentAttributes.ComponentAttributes.vaWidth100;
import static org.ikernits.vaadin.VaadinComponentAttributes.LayoutAttributes.vaSpacing;

public class RegistrationUI {
    static Logger log = Logger.getLogger(RegistrationUI.class);

    private GitHubUser gitHubUser;
    private List<GitHubRepo> gitHubRepos;
    private long gitHubReposCount;

    private GitHubDataService gitHubDataService = VaadinServices.getGitHubDataService();
    private GitHubQueryService gitHubQueryService = VaadinServices.getGitHubQueryService();

    protected HorizontalLayout createUserLayout() {
        Label reposLabel;
        HorizontalLayout userLayout = VaadinBuilders.horizontalLayout()
                .setWidth(360, Sizeable.Unit.PIXELS)
                .setHeight(180, Sizeable.Unit.PIXELS)
                .setDefaultComponentAlignment(Alignment.MIDDLE_LEFT)
                .addComponent(VaadinBuilders.label()
                        .setWidth(160, Sizeable.Unit.PIXELS)
                        .setHeight(160, Sizeable.Unit.PIXELS)
                        .setValue("<img style=\"width:160px; height:160px\" src=\"" + gitHubUser.getAvatar_url() + "\"></img>")
                        .setContentMode(ContentMode.HTML)
                        .build())
                .setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT)
                .addComponent(VaadinBuilders.verticalLayout()
                        .setWidth(160, Sizeable.Unit.PIXELS)
                        .setHeight(160, Sizeable.Unit.PIXELS)
                        .setDefaultComponentAlignment(Alignment.TOP_LEFT)
                        .addComponent(VaadinBuilders.label()
                                .setHeight(60.f, Sizeable.Unit.PIXELS)
                                .setValue("<span style=\"font-size: 40px; color: white\">" + gitHubUser.getLogin() + "</span>")
                                .setContentMode(ContentMode.HTML)
                                .build())
                        .addComponent(reposLabel = VaadinBuilders.label()
                                .setHeight(80.f, Sizeable.Unit.PIXELS)
                                .setValue("<div style=\"width: 200px\">" +
                                    "   <span style=\"font-size: 80px; font-weight: 800; color: white\">" + gitHubReposCount + "</span>" +
                                        "<span style=\"font-size: 16px; color: white\">repos</span></div>")
                                .setContentMode(ContentMode.HTML)
                                .build())
                        .setExpandRatio(reposLabel, 1.f)
                        .build())
                .build();
        return userLayout;
    }

    protected Component createSearchPanel() {
        VerticalLayout form = VaadinBuilders.verticalLayout()
            .setWidth(580, Sizeable.Unit.PIXELS)
            .setHeight(480, Sizeable.Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(VaadinBuilders.label()
                .setWidthUndefined()
                .setHeight(50, Sizeable.Unit.PIXELS)
                .setContentMode(ContentMode.HTML)
                .setValue("<div style = \"" +
                    "text-align: center;" +
                    "font-size: 44px;" +
                    "font-weight: 400;" +
                    "color: white" +
                    "\">We have found you!</div>")
                .build())
            .addComponent(createUserLayout())
            .addComponent(VaadinBuilders.horizontalLayout()
                .setWidth(360.f, Sizeable.Unit.PIXELS)
                .setDefaultComponentAlignment(Alignment.MIDDLE_LEFT)
                .addComponent(
                    VaadinBuilders.button()
                        .setWidth(160.f, Sizeable.Unit.PIXELS)
                        .setHeight(45.f, Sizeable.Unit.PIXELS)
                        .addStyleName("bg-color-button-orange")
                        .setCaption("<span style=\"color: white; font-size: 20px\">No, it is not me</span>")
                        .setCaptionAsHtml(true)
                        .addClickListener(e -> {
                            gitHubUser = null;
                            container.removeAllComponents();
                            container.addComponent(createRegistrationPanel());
                        })
                        .build())
                .setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT)
                .addComponent(
                    VaadinBuilders.button()
                        .setWidth(160.f, Sizeable.Unit.PIXELS)
                        .setHeight(45.f, Sizeable.Unit.PIXELS)
                        .addStyleName("bg-color-button-green")
                        .setCaption("<span style=\"color: white; font-size: 20px\">I'm going in!</span>")
                        .setCaptionAsHtml(true)
                        .addClickListener(e -> {
                            storeGitHubUser();
                            container.removeAllComponents();
                            container.addComponent(createCompletionPanel());
                        })
                        .build())
                .build())
            .build();

        VerticalLayout content = VaadinBuilders.verticalLayout()
            .setWidth(640, Sizeable.Unit.PIXELS)
            .setHeight(480, Sizeable.Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(form)
            .addStyleName("bg-color-window")
            .build();

        return content;
    }

    protected Component createCompletionPanel() {
        long starCount = gitHubRepos.stream()
                .filter(r -> r.getOwner().getId() == gitHubUser.getId())
                .mapToLong(GitHubRepo::getStargazers_count)
                .sum();
        long followerCount = gitHubUser.getFollowers();
        int total = gitHubDataService.listGitHubUsers().size();
        long starPlace = gitHubDataService.findGitHubUserRating(gitHubUser.getLogin(), gitHubDataService::getStarSumMapper);
        long followerPlace = gitHubDataService.findGitHubUserRating(gitHubUser.getLogin(), gitHubDataService::getFollowersMapper);

        VerticalLayout form = VaadinBuilders.verticalLayout()
            .setWidth(580, Sizeable.Unit.PIXELS)
            .setHeight(400, Sizeable.Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(createUserLayout())
            .addComponent(VaadinBuilders.horizontalLayout()
                .setWidth(580.f, Sizeable.Unit.PIXELS)
                .setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT)
                .addComponent(VaadinBuilders.label()
                    .setHeight(180.f, Sizeable.Unit.PIXELS)
                    .setWidthUndefined()
                    .setContentMode(ContentMode.HTML)
                    .setValue("<div style=\"min-width:160px; padding-right: 20px;\">" +
                        "<div>" +
                        "  <div style=\"display: inline-block; vertical-align: middle\"><img align=\"middle\" style=\"width: 60px;height: 60px\" src=\"/img/element-08.png\"/></div>" +
                        "  <div style=\"font-size: 60px;color: white; font-weight: 600; " +
                        "vertical-align: middle; display: inline;" +
                        "padding-left: 10px; padding-right: 10px\">-</div>" +
                        "  <div style=\"vertical-align: middle; font-size: 60px;color: white; font-weight: 600; float: right\">" + starCount + "</div>" +
                        "</div>" +
                        "<div>" +
                        "  <div style=\"display: inline-block; vertical-align: middle\"><img align=\"middle\" style=\"width: 60px;height: 60px\" src=\"/img/element-07.png\"/></div>" +
                        "  <div style=\"font-size: 60px;color: white; font-weight: 600; " +
                        "vertical-align: middle; display: inline;" +
                        "padding-left: 10px; padding-right: 10px\">-</div>" +
                        "  <div style=\"vertical-align: middle; font-size: 60px;color: white; font-weight: 600; float: right\">" + followerCount + "</div>" +
                        "</div>" +
                        "</div>"
                    )
                    .build())
                .setDefaultComponentAlignment(Alignment.MIDDLE_LEFT)
                .addComponent(VaadinBuilders.label()
                    .setHeight(180.f, Sizeable.Unit.PIXELS)
                    .setWidthUndefined()
                    .setContentMode(ContentMode.HTML)
                    .setValue("<div style=\"min-width:160px; padding-left: 20px;\">" +
                        "  <div style=\"vertical-align: middle; font-size: 40px;color: white; font-weight: 200; padding-top: 20px\">#" + starPlace + " of " + total + "</div>" +
                        "  <div style=\"vertical-align: middle; font-size: 40px;color: white; font-weight: 200; padding-top: 30px\">#" + followerPlace + " of " + total + "</div>" +
                        "</div>"
                    )
                    .build())
                .build())
            .build();

        VerticalLayout content = VaadinBuilders.verticalLayout()
            .setWidth(640, Sizeable.Unit.PIXELS)
            .setHeight(480, Sizeable.Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(form)
            .addStyleName("bg-color-window")
            .build();

        return content;
    }


    private boolean loadGitHubUserData(String username) {
        gitHubUser = gitHubQueryService.findGitHubUser(username);
        if (gitHubUser == null) {
            return false;
        }

        gitHubRepos = gitHubQueryService.listGitHubRepos(gitHubUser);
        if (gitHubRepos.size() == 0) {
            return false;
        }

        gitHubReposCount = gitHubRepos.stream()
            .filter(r -> r.getOwner().getId() == gitHubUser.getId())
            .count();

        return true;
    }

    private void storeGitHubUser() {
        if (gitHubUser != null) {
            GitHubAvatar avatar = gitHubQueryService.loadGitHubAvatar(gitHubUser);
            gitHubDataService.addGitHubUser(gitHubUser, gitHubRepos, avatar);
        }
    }

    private Component createNotFoundPanel(String username) {
        VerticalLayout form = VaadinBuilders.verticalLayout()
            .setWidth(500, Sizeable.Unit.PIXELS)
            .setHeight(480, Sizeable.Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(VaadinBuilders.label()
                .setWidthUndefined()
                .setContentMode(ContentMode.HTML)
                .setValue(
                    "<div style = \"text-align:center; font-size: 32px; font-weight: 800; color: white\">Sorry</div>" +
                    "<div style = \"text-align:center; font-size: 40px; font-weight: 800; color: white\">" + username + "</div>" +
                    "<div style = \"text-align:center; font-size: 32px; font-weight: 200; color: white\">we have not found you on GitHub</div>"
                )
                .build())
            .addComponent(VaadinBuilders.button()
                .setHeight(80.f, Sizeable.Unit.PIXELS)
                .setWidth(280.f, Sizeable.Unit.PIXELS)
                .setStyleName("bg-color-button-green")
                .setCaption("<span style = \"" +
                    "font-size: 20px;" +
                    "color: white;" +
                    "\">Go back to registration</span>")
                .setCaptionAsHtml(true)
                .addClickListener(e -> {
                    container.removeAllComponents();
                    container.addComponent(createRegistrationPanel());
                })
                .build())
            .build();

        VerticalLayout content = VaadinBuilders.verticalLayout()
            .setWidth(640, Sizeable.Unit.PIXELS)
            .setHeight(480, Sizeable.Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addStyleName("bg-color-window")
            .addComponent(form)
            .build();

        return content;
    }

    private void processRegistrationEnter(String username) {
        if (!StringUtils.isBlank(username)) {
            container.removeAllComponents();
            if (loadGitHubUserData(username)) {
                container.addComponent(createSearchPanel());
            } else {
                container.addComponent(createNotFoundPanel(username));
            }
        }
    }

    private Component createRegistrationPanel() {
        TextField username;

        VerticalLayout form = VaadinBuilders.verticalLayout()
            .setAttributes(vaStylePaddingNormal, vaSpacing)
            .setWidth(580, Sizeable.Unit.PIXELS)
            .setHeight(480, Sizeable.Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(VaadinBuilders.verticalLayout()
                .setHeight(180.f, Sizeable.Unit.PIXELS)
                .setWidth(500.f, Sizeable.Unit.PIXELS)
                .setDefaultComponentAlignment(Alignment.TOP_CENTER)
                .addComponent(VaadinBuilders.label()
                    .setWidthUndefined()
                    .setContentMode(ContentMode.HTML)
                    .setValue("<div style = \"" +
                        "text-align: center;" +
                        "font-size: 44px;" +
                        "font-weight: 400;" +
                        "color: white" +
                        "\">Welcome to GitHub Meter</div>")
                    .build())
                .setDefaultComponentAlignment(Alignment.BOTTOM_CENTER)
                .addComponent(username = VaadinBuilders.textField()
                    .setHeight(48.f, Sizeable.Unit.PIXELS)
                    .setCaption("<span style = \"" +
                        "color: white" +
                        "\">Enter your GitHub Username</span>")
                    .setCaptionAsHtml(true)
                    .setAttributes(vaWidth100)
                    .build())
                .build())
            .addComponent(VaadinBuilders.verticalLayout()
                .setHeight(240.f, Sizeable.Unit.PIXELS)
                .setWidth(500.f, Sizeable.Unit.PIXELS)
                .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
                .setWidthUndefined()
                .addComponent(VaadinBuilders.button()
                    .setHeight(80.f, Sizeable.Unit.PIXELS)
                    .setWidth(280.f, Sizeable.Unit.PIXELS)
                    .setStyleName("bg-color-button-green")
                    .setCaption("<span style = \"" +
                        "font-size: 20px;" +
                        "color: white;" +
                        "\">Get ready for challenge</span>")
                    .setCaptionAsHtml(true)
                    .addClickListener(e -> processRegistrationEnter(username.getValue()))
                    .build())
                .build())
            .build();

        username.addShortcutListener(new ShortcutListener("Enter", ShortcutAction.KeyCode.ENTER, null) {
            @Override
            public void handleAction(Object sender, Object target) {
                processRegistrationEnter(username.getValue());
            }
        });

        VerticalLayout content = VaadinBuilders.verticalLayout()
            .setWidth(640, Sizeable.Unit.PIXELS)
            .setHeight(480, Sizeable.Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(form)
            .addStyleName("bg-color-window")
            .build();

        return content;
    }

    VerticalLayout container = VaadinBuilders.verticalLayout()
        .setWidthUndefined()
        .setHeightUndefined()
        .build();

    protected Component create() {
        container.removeAllComponents();
        container.addComponent(createRegistrationPanel());
        return container;
    }
}
