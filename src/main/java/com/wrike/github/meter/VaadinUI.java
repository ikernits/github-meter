package com.wrike.github.meter;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.apache.log4j.Logger;
import org.ikernits.vaadin.VaadinBuilders;
import org.ikernits.vaadin.VaadinComponentStyles.ColorStyle;

import static org.ikernits.vaadin.VaadinComponentAttributes.ComponentAttributes.vaHeight100;
import static org.ikernits.vaadin.VaadinComponentAttributes.ComponentAttributes.vaStylePaddingNormal;
import static org.ikernits.vaadin.VaadinComponentAttributes.ComponentAttributes.vaWidth100;
import static org.ikernits.vaadin.VaadinComponentAttributes.LayoutAttributes.vaSpacing;

/**
 * Created by ikernits on 10/10/15.
 */
@Title("Vaadin UI")
@Theme("valo-ext-flat")
public class VaadinUI extends UI {


    static Logger log = Logger.getLogger(VaadinUI.class);

    protected Component createSearchPanel() {
        VerticalLayout form = VaadinBuilders.verticalLayout()
            .setAttributes(vaStylePaddingNormal, vaSpacing)
            .setWidth(500, Unit.PIXELS)
            .setHeight(300, Unit.PIXELS)
            .setDefaultComponentAlignment(Alignment.MIDDLE_CENTER)
            .addComponent(VaadinBuilders.label()
                .setWidthUndefined()
                .setContentMode(ContentMode.HTML)
                .setValue("<h1><span style = \"align:middle\">Looking for you on GitHub</span></h1>")
                .build())
            .addComponent(VaadinBuilders.label()
                .setValue("Text and avatar")
                .build())
            .addComponent(VaadinBuilders.label()
                .setValue("Repository counter")
                .build())
            .addComponent(VaadinBuilders.button()
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

    protected Component createRegistrationPanel() {
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
            .addComponent(VaadinBuilders.textField()
                .setCaption("Enter your GitHub Username")
                .setAttributes(vaWidth100)
                .build())
            .addComponent(VaadinBuilders.button()
                .setCaption("Find me on GitHub")
                .setDescription("Find me on GitHub")
                .addClickListener(e -> UI.getCurrent().setContent(createSearchPanel()))
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

