package com.wrike.github.meter.ui;

import com.vaadin.server.FileDownloader;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.wrike.github.meter.service.VaadinServices;
import org.ikernits.vaadin.VaadinBuilders;

import java.io.ByteArrayInputStream;

/**
 * Created by ikernitsky on 5/29/17.
 */
public class ManageUI {

    public Component create() {
        FileDownloader fileDownloader = new FileDownloader(new StreamResource((StreamResource.StreamSource)
            () -> new ByteArrayInputStream(VaadinServices.getGitHubDataService().generateGitHubUserCsv().getBytes()),
            "git-users.csv"
        ));

        Button downloadButton = VaadinBuilders.button()
            .setHeight(80.f, Sizeable.Unit.PIXELS)
            .setWidth(350.f, Sizeable.Unit.PIXELS)
            .setStyleName("bg-color-button-green")
            .setCaption("<span style = \"" +
                "font-size: 20px;" +
                "color: white;" +
                "\">Download registered users as CSV</span>")
            .setCaptionAsHtml(true)
            .build();

        fileDownloader.extend(downloadButton);

        return VaadinBuilders.verticalLayout()
                .setSpacing(true)
                .setMargin(true)
                .setHeight(300, Sizeable.Unit.PIXELS)
                .setWidth(400, Sizeable.Unit.PIXELS)
                .addComponent(VaadinBuilders.button()
                    .setHeight(80.f, Sizeable.Unit.PIXELS)
                    .setWidth(350.f, Sizeable.Unit.PIXELS)
                    .setStyleName("bg-color-button-orange")
                    .setCaption("<span style = \"" +
                        "font-size: 20px;" +
                        "color: white;" +
                        "\">Clear active github scores</span>")
                    .setCaptionAsHtml(true)
                    .addClickListener(e -> VaadinServices.getGitHubDataService().resetDb())
                    .build()
                )
                .addComponent(downloadButton)
                .build();
    }
}
