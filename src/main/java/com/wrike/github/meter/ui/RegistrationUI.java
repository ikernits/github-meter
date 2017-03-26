package com.wrike.github.meter.ui;

import com.vaadin.server.ErrorMessage;
import com.vaadin.server.Sizeable;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
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
import java.util.concurrent.ExecutorService;

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
        VerticalLayout detailsLayout;
        HorizontalLayout userLayout = VaadinBuilders.horizontalLayout()
            .setAttributes(vaStyleMarginNormal)
            .setWidth(600, Sizeable.Unit.PIXELS)
            .setHeight(180, Sizeable.Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(VaadinBuilders.label()
                .setWidth(200, Sizeable.Unit.PIXELS)
                .setHeight(160, Sizeable.Unit.PIXELS)
                .setValue("<img style=\"width:160px; height:160px\" src=\"" + gitHubUser.getAvatar_url() + "\"></img>")
                .setContentMode(ContentMode.HTML)
                .build())
            .addComponent(detailsLayout = VaadinBuilders.verticalLayout()
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
            .setExpandRatio(detailsLayout, 1.f)
            .build();

        VerticalLayout form = VaadinBuilders.verticalLayout()
            .setAttributes(vaStyleMarginNormal)
            .setWidth(600, Sizeable.Unit.PIXELS)
            .setHeightUndefined()
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(VaadinBuilders.label()
                .setAttributes(vaStyleMarginNormal)
                .setWidthUndefined()
                .setHeight(50, Sizeable.Unit.PIXELS)
                .setContentMode(ContentMode.HTML)
                .setValue("<h1><span style = \"align:middle\">We have found you!</span></h1>")
                .build())
            .addComponent(userLayout)
            .addComponent(VaadinBuilders.horizontalLayout()
                .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
                .addComponent(
                    VaadinBuilders.button()
                        .setAttributes(vaStyleMarginNormal)
                        .setCaption("No, it is not me")
                        .addClickListener(e -> {
                            gitHubUser = null;
                            container.removeAllComponents();
                            container.addComponent(createRegistrationPanel());
                        })
                        .build())
                .addComponent(
                    VaadinBuilders.button()
                        .setAttributes(vaStyleMarginNormal)
                        .setCaption("I'm going in!")
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
            .setAttributes(vaWidth100, vaHeight100)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(form)
            .build();

        return VaadinBuilders.panel()
            .setWidth(700, Sizeable.Unit.PIXELS)
            .setHeight(500, Sizeable.Unit.PIXELS)
            .setContent(content)
            .build();
    }

    private Component createRegistrationPanel() {
        TextField username;

        VerticalLayout form = VaadinBuilders.verticalLayout()
            .setAttributes(vaStylePaddingNormal, vaSpacing)
            .setWidth(500, Sizeable.Unit.PIXELS)
            .setHeight(300, Sizeable.Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(VaadinBuilders.label()
                .setWidthUndefined()
                .setContentMode(ContentMode.HTML)
                .setValue("<div style = \"text-align:center; font-size: 32px\">Welcome to GitHub Meter</div>")
                .build())
            .addComponent(username = VaadinBuilders.textField()
                .setCaption("Enter your GitHub Username")
                .setAttributes(vaWidth100)
                .build())
            .addComponent(VaadinBuilders.button()
                .setCaption("Get ready for challenge")
                .addClickListener(e -> {
                    container.removeAllComponents();
                    if (loadGitHubUserData(username.getValue())) {
                        container.addComponent(createSearchPanel());
                    } else {
                        container.addComponent(createNotFoundPanel(username.getValue()));
                    }
                })
                .build())
            .build();

        VerticalLayout content = VaadinBuilders.verticalLayout()
            .setAttributes(vaWidth100, vaHeight100)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(form)
            .build();

        return VaadinBuilders.panel()
            .setWidth(700, Sizeable.Unit.PIXELS)
            .setHeight(500, Sizeable.Unit.PIXELS)
            .setContent(content)
            .build();
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
