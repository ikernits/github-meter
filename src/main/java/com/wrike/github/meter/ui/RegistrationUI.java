package com.wrike.github.meter.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
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
public class RegistrationUI extends UI {


    static Logger log = Logger.getLogger(RegistrationUI.class);


    private String gitHubUser;
    private long gitHubReposCount;
    private String avatarUrl;

    private GitHubDataService gitHubDataService = VaadinServices.getGitHubDataService();
    private GitHubQueryService gitHubQueryService = VaadinServices.getGitHubQueryService();

    protected Component createSearchPanel() {
        VerticalLayout detailsLayout;
        HorizontalLayout userLayout = VaadinBuilders.horizontalLayout()
            .setAttributes(vaStyleMarginNormal)
            .setWidth(600, Unit.PIXELS)
            .setHeight(180, Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(VaadinBuilders.label()
                .setWidth(200, Unit.PIXELS)
                .setHeight(160, Unit.PIXELS)
                .setValue("<img style=\"width:160px; height:160px\" src=\"" + avatarUrl + "\"></img>")
                .setContentMode(ContentMode.HTML)
                .build())
            .addComponent(detailsLayout = VaadinBuilders.verticalLayout()
                .addComponent(VaadinBuilders.label()
                    .setValue("<span style=\"font-size: 50px\">" + gitHubUser + "</span>")
                    .setContentMode(ContentMode.HTML)
                    .build())
                .addComponent(VaadinBuilders.label()
                    .setValue("<span style=\"font-size: 80px\">" + gitHubReposCount + "</span>" +
                        "<span style=\"font-size: 16px\"> repos </span>")
                    .setContentMode(ContentMode.HTML)
                    .build())
                .build())
            .setExpandRatio(detailsLayout, 1.f)
            .build();

        VerticalLayout form = VaadinBuilders.verticalLayout()
            .setAttributes(vaStyleMarginNormal)
            .setWidth(600, Unit.PIXELS)
            .setHeightUndefined()
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(VaadinBuilders.label()
                .setAttributes(vaStyleMarginNormal)
                .setWidthUndefined()
                .setHeight(50, Unit.PIXELS)
                .setContentMode(ContentMode.HTML)
                .setValue("<h1><span style = \"align:middle\">We have found you!</span></h1>")
                .build())
            .addComponent(userLayout)
            .addComponent(VaadinBuilders.button()
                .setAttributes(vaStyleMarginNormal)
                .setCaption("Challenge!")
                .addClickListener(e -> UI.getCurrent().setContent(createRegistrationPanel()))
                .build())
            .build();

        VerticalLayout content = VaadinBuilders.verticalLayout()
            .setAttributes(vaWidth100, vaHeight100)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(form)
            .build();

        Panel panel = VaadinBuilders.panel()
            .setWidth(700, Unit.PIXELS)
            .setHeight(500, Unit.PIXELS)
            .setContent(content)
            .build();


        return VaadinBuilders.verticalLayout()
            .setAttributes(vaWidth100, vaHeight100, vaSpacing)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(panel)
            .build();
    }

    private boolean loadGitHubUserData(String username) {
        GitHubUser user = gitHubQueryService.findGitHubUser(username);
        if (user == null) {
            return false;
        }

        List<GitHubRepo> repos = gitHubQueryService.listGitHubRepos(user);
        if (repos.size() == 0) {
            return false;
        }

        GitHubAvatar avatar = gitHubQueryService.loadGitHubAvatar(user);
        gitHubDataService.addGitHubUser(user, repos, avatar);

        gitHubUser = user.getLogin();
        avatarUrl = user.getAvatar_url();
        gitHubReposCount = repos.stream()
            .filter(r -> r.getOwner().getId() == user.getId())
            .count();

        return true;
    }

    protected Component createRegistrationPanel() {
        TextField username;

        VerticalLayout form = VaadinBuilders.verticalLayout()
            .setAttributes(vaStylePaddingNormal, vaSpacing)
            .setWidth(500, Unit.PIXELS)
            .setHeight(300, Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(VaadinBuilders.label()
                .setWidthUndefined()
                .setContentMode(ContentMode.HTML)
                .setValue("<h1><span style = \"align:middle\">Welcome to GitHub Meter</span></h1>")
                .build())
            .addComponent(username = VaadinBuilders.textField()
                .setCaption("Enter your GitHub Username")
                .setAttributes(vaWidth100)
                .build())
            .addComponent(VaadinBuilders.button()
                .setCaption("Get ready for challenge")
                .addClickListener(e -> {
                    loadGitHubUserData(username.getValue());
                    UI.getCurrent().setContent(createSearchPanel());
                })
                .build())
            .build();

        VerticalLayout content = VaadinBuilders.verticalLayout()
            .setAttributes(vaWidth100, vaHeight100)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(form)
            .build();

        Panel panel = VaadinBuilders.panel()
            .setWidth(700, Unit.PIXELS)
            .setHeight(500, Unit.PIXELS)
            .setContent(content)
            .build();

        return VaadinBuilders.verticalLayout()
            .setAttributes(vaWidth100, vaHeight100, vaSpacing)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(panel)
            .build();
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setContent(createRegistrationPanel());
    }
}

