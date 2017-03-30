package com.wrike.github.meter.ui;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
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

import static org.ikernits.vaadin.VaadinComponentAttributes.ComponentAttributes.vaHeight100;
import static org.ikernits.vaadin.VaadinComponentAttributes.ComponentAttributes.vaStyleMarginNormal;
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

    protected Component createSearchPanel() {
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
                    .setValue("<span style=\"font-size: 80px; font-weight: 800; color: white\">" + gitHubReposCount + "</span>" +
                        "<span style=\"font-size: 16px; color: white\"> repos </span>")
                    .setContentMode(ContentMode.HTML)
                    .build())
                .setExpandRatio(reposLabel, 1.f)
                .build())
            .build();

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
            .addComponent(userLayout)
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


        VerticalLayout detailsLayout;
        HorizontalLayout userLayout = VaadinBuilders.horizontalLayout()
                .setAttributes(vaStyleMarginNormal)
                .setWidth(500, Sizeable.Unit.PIXELS)
                .setHeight(180, Sizeable.Unit.PIXELS)
                .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
                .addComponent(VaadinBuilders.label()
                        .setWidth(200, Sizeable.Unit.PIXELS)
                        .setHeight(160, Sizeable.Unit.PIXELS)
                        .setValue("<img style=\"width:160px; height:160px\" src=\"" + gitHubUser.getAvatar_url() + "\"></img>")
                        .setContentMode(ContentMode.HTML)
                        .build())
                .addComponent(VaadinBuilders.verticalLayout()
                        .addComponent(VaadinBuilders.label()
                                .setValue("<span style=\"font-size: 50px\">" + gitHubUser.getLogin() + "</span>")
                                .setContentMode(ContentMode.HTML)
                                .build())
                        .addComponent(VaadinBuilders.label()
                                .setValue("<span style=\"font-size: 80px\">" + gitHubReposCount + "</span>" +
                                        "<span style=\"font-size: 16px\"> repos </span>")
                                .setContentMode(ContentMode.HTML)
                                .build())
                        .build())
             //   .setExpandRatio(detailsLayout, 1.f)
                .build();

        VerticalLayout form = VaadinBuilders.verticalLayout()
                .setAttributes(vaStyleMarginNormal)
                .setWidth(600, Sizeable.Unit.PIXELS)
                .setHeightUndefined()
                .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
                .addComponent(userLayout)
                .addComponent(VaadinBuilders.horizontalLayout()
                        .setWidth(100.f, Sizeable.Unit.PERCENTAGE)
                        .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
                        .addComponent(VaadinBuilders.label()
                                .setWidthUndefined()
                                .setValue(
                                        "<div><span style=\"font-size: 48px; margin-right: 16px\">" + FontAwesome.STAR.getHtml() + "</span>" +
                                                "<span style=\"font-size: 60px\">" + starCount + "</span>" +
                                                "<span style=\"font-size: 16px\"> stars</span></div><div>#4 of 10</div>")
                                .setContentMode(ContentMode.HTML)
                                .build())
                        .addComponent(VaadinBuilders.label()
                                .setWidthUndefined()
                                .setValue(
                                        "<span style=\"font-size: 48px; margin-right: 16px\">" + FontAwesome.USERS.getHtml() + "</span>" +
                                                "<span style=\"font-size: 60px\">" + followerCount + "</span>" +
                                                "<span style=\"font-size: 16px\"> followers</span>")
                                .setContentMode(ContentMode.HTML)
                                .build())
                        .build())
                .addComponent(VaadinBuilders.horizontalLayout()
                        .addComponent(
                                VaadinBuilders.button()
                                        .setAttributes(vaStyleMarginNormal)
                                        .setCaption("Go to registration")
                                        .addClickListener(e -> {
                                            storeGitHubUser();
                                            container.removeAllComponents();
                                            container.addComponent(createRegistrationPanel());
                                        })
                                        .build())
                        .build())
                .build();

        VerticalLayout content = VaadinBuilders.verticalLayout()
                .setAttributes(vaWidth100, vaHeight100)
                .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
                .addComponent(form)
                .build();

        Panel panel = VaadinBuilders.panel()
                .setWidth(700, Sizeable.Unit.PIXELS)
                .setHeight(500, Sizeable.Unit.PIXELS)
                .setContent(content)
                .build();


        return VaadinBuilders.verticalLayout()
                .setAttributes(vaWidth100, vaHeight100, vaSpacing)
                .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
                .addComponent(panel)
                .build();
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
            .setAttributes(vaStylePaddingNormal, vaSpacing)
            .setWidth(500, Sizeable.Unit.PIXELS)
            .setHeight(300, Sizeable.Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(VaadinBuilders.label()
                .setWidthUndefined()
                .setContentMode(ContentMode.HTML)
                .setValue("<div style = \"text-align:center; font-size: 32px\">Sorry, we have not found</div>" +
                    "<div style = \"text-align:center; font-size: 40px\">" + username + "</div>")
                .build())
            .addComponent(VaadinBuilders.button()
                .setCaption("Go back to registration")
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
                    .addClickListener(e -> {
                        container.removeAllComponents();
                        if (loadGitHubUserData(username.getValue())) {
                            container.addComponent(createSearchPanel());
                        } else {
                            container.addComponent(createNotFoundPanel(username.getValue()));
                        }
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
