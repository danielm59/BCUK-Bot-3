package com.expiredminotaur.bcukbot.web.view.commands;

import com.expiredminotaur.bcukbot.Role;
import com.expiredminotaur.bcukbot.sql.sfx.SFX;
import com.expiredminotaur.bcukbot.sql.sfx.SFXRepository;
import com.expiredminotaur.bcukbot.web.component.Form;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.expiredminotaur.bcukbot.web.security.AccessLevel;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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

@Route(value = "sfx", layout = MainLayout.class)
@AccessLevel(Role.MOD)
public class SFXView extends HorizontalLayout
{
    private final Logger log = LoggerFactory.getLogger(SFXView.class);
    private final File folder = new File("sfx");
    private final Grid<SFX> sfxCommandGrid = new Grid<>(SFX.class);
    private final SFXRepository sfxCommands;

    public SFXView(@Autowired SFXRepository sfxCommands)
    {
        this.sfxCommands = sfxCommands;
        setSizeFull();
        VerticalLayout fileManagerLayout = new VerticalLayout();
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        Text message = new Text("");
        Grid<String> fileList = new Grid<>();
        if (!folder.exists())
        {
            if (!folder.mkdir())
            {
                add(new H1("SFX Folder is missing, contact Admin"));
                return;
            }
        }

        upload.setAcceptedFileTypes(".mp3", ".flac", ".wav", ".mp4", ".m4a", ".ogg", ".aac", ".opus");
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
                if (initialStream.read(byteBuffer) > 0)
                {
                    outStream.write(byteBuffer);
                    upload.getElement().setPropertyJson("files", Json.createArray());
                    fileList.setItems(folder.list());
                } else
                {
                    message.setText("Error uploading file");
                }
            } catch (IOException e)
            {
                log.error("Error reading SFX upload", e);
                message.setText("Error uploading file");
            }
        });

        Grid.Column<String> column = fileList.addColumn(s -> s);
        column.setHeader("Files");
        fileList.setItems(folder.list());
        fileManagerLayout.add(upload, message, fileList);

        VerticalLayout commandManagerLayout = new VerticalLayout();

        SfxForm sfxForm = new SfxForm();
        Button addTriggerButton = new Button("Add Trigger", e -> sfxForm.open(new SFX()));

        sfxCommandGrid.setColumns("triggerCommand", "file", "weight", "hidden");
        sfxCommandGrid.addColumn(new ComponentRenderer<>(sfx -> new Button("Edit", e -> sfxForm.open(sfx))))
                .setHeader("Edit")
                .setFlexGrow(0);
        sfxCommandGrid.setItems(sfxCommands.findAll());
        sfxCommandGrid.getColumns().forEach(c -> c.setAutoWidth(true));
        sfxCommandGrid.recalculateColumnWidths();
        commandManagerLayout.add(addTriggerButton, sfxCommandGrid);

        add(fileManagerLayout, commandManagerLayout);
        setFlexGrow(0, fileManagerLayout);
        setFlexGrow(1, commandManagerLayout);
    }

    private class SfxForm extends Form<SFX>
    {
        public SfxForm()
        {
            super(SFX.class);
            addField("Trigger Command", new TextField(), "triggerCommand");
            addField("SFX File", new ComboBox<String>(), "file").setItems(folder.list());
            addField("Weight", new TextField(), "weight", new StringToIntegerConverter("Invalid number"));
            addField("Hidden", new Checkbox(), "hidden");
        }

        @Override
        protected void saveData(SFX data)
        {
            sfxCommands.save(data);
            sfxCommandGrid.setItems(sfxCommands.findAll());
            sfxCommandGrid.recalculateColumnWidths();
        }
    }
}
