package com.wrike.github.meter.ui;

import com.vaadin.ui.Component;
import com.wrike.github.meter.service.VaadinServices;
import org.ikernits.vaadin.VaadinBuilders;

/**
 * Created by ikernitsky on 5/29/17.
 */
public class ManageUI {

    public Component create() {
        return VaadinBuilders.verticalLayout()
                .setSpacing(true)
                .setMargin(true)
                .addComponent(VaadinBuilders.button()
                    .setCaption("Clear active github scores")
                    .addClickListener(e -> VaadinServices.getGitHubDataService().resetDb())
                    .build()
                )
                .build();
    }
}
