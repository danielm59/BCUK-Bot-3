package com.expiredminotaur.bcukbot.web.view.settings;

import com.expiredminotaur.bcukbot.sql.sfx.SFX;
import com.expiredminotaur.bcukbot.sql.sfx.SFXRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import elemental.json.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Route(value = "settings/sfx", layout = MainLayout.class)
public class SFXView extends HorizontalLayout
{
    private final Logger log = LoggerFactory.getLogger(SFXView.class);

    public SFXView(@Autowired SFXRepository sfxCommands)
    {
        setSizeFull();
        VerticalLayout fileManagerLayout = new VerticalLayout();
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        Text message = new Text("");
        Grid<String> fileList = new Grid<>();
        File f = new File("sfx");
        if (!f.exists())
        {
            f.mkdir();
        }

        upload.setAcceptedFileTypes(".mp3");
        upload.addStartedListener(event -> message.setText(""));
        upload.addFileRejectedListener(event -> message.setText(event.getErrorMessage()));
        upload.addSucceededListener(event ->
        {
            try
            {
                File targetFile = new File("sfx/" + event.getFileName());
                OutputStream outStream = new FileOutputStream(targetFile);
                InputStream initialStream = buffer.getInputStream();
                byte[] byteBuffer = new byte[initialStream.available()];
                initialStream.read(byteBuffer);
                outStream.write(byteBuffer);
                upload.getElement().setPropertyJson("files", Json.createArray());
                fileList.setItems(f.list());
            } catch (IOException e)
            {
                log.error("Error reading SFX upload", e);
                message.setText("Error uploading file");
            }
        });

        Grid.Column<String> column = fileList.addColumn(s -> s);
        column.setHeader("Files");
        fileList.setItems(f.list());
        fileManagerLayout.add(upload, message, fileList);

        VerticalLayout commandManagerLayout = new VerticalLayout();
        Grid<SFX> sfxCommandGrid = new Grid<>(SFX.class);
        sfxCommandGrid.setColumns("triggerCommand", "file", "weight");
        sfxCommandGrid.setItems(sfxCommands.findAll());
        //TODO: add a way to add triggers
        commandManagerLayout.add(sfxCommandGrid);

        add(fileManagerLayout, commandManagerLayout);
        setFlexGrow(0, fileManagerLayout);
        setFlexGrow(1, commandManagerLayout);
    }
}
