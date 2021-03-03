package com.expiredminotaur.bcukbot.web.view.settings;

import com.expiredminotaur.bcukbot.sql.sfx.SFX;
import com.expiredminotaur.bcukbot.sql.sfx.SFXRepository;
import com.expiredminotaur.bcukbot.web.layout.MainLayout;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.IntegerRangeValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
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
    private final File folder = new File("sfx");
    private final Grid<SFX> sfxCommandGrid = new Grid<>(SFX.class);

    public SFXView(@Autowired SFXRepository sfxCommands)
    {
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

        Button addTriggerButton = new Button("Add Trigger", e -> editTrigger(sfxCommands, new SFX()));

        sfxCommandGrid.setColumns("triggerCommand", "file", "weight", "hidden");
        sfxCommandGrid.addColumn(new ComponentRenderer<>(sfx -> new Button("Edit", e -> editTrigger(sfxCommands, sfx))))
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

    private void editTrigger(SFXRepository sfxCommands, SFX sfx)
    {
        Dialog addTriggerDialog = new Dialog();

        Binder<SFX> binder = new Binder<>(SFX.class);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("600px", 1, FormLayout.ResponsiveStep.LabelsPosition.ASIDE));

        TextField triggerCommand = new TextField();
        ComboBox<String> sfxFile = new ComboBox<>();
        sfxFile.setItems(folder.list());
        TextField weight = new TextField();
        Checkbox hidden = new Checkbox();

        weight.setValue("1");

        formLayout.addFormItem(triggerCommand, "Trigger Command");
        formLayout.addFormItem(sfxFile, "SFX file");
        formLayout.addFormItem(weight, "Weight");
        formLayout.addFormItem(hidden, "Hidden");

        binder.forField(triggerCommand)
                .withValidator(new StringLengthValidator("Must be entered", 1, Integer.MAX_VALUE))
                .bind("triggerCommand");
        binder.forField(sfxFile)
                .withValidator(new StringLengthValidator("Must be entered", 1, Integer.MAX_VALUE))
                .bind("file");
        binder.forField(weight)
                .withConverter(new StringToIntegerConverter("Invalid number"))
                .withValidator(new IntegerRangeValidator("Must be greater than zero", 1, Integer.MAX_VALUE))
                .bind("weight");
        binder.forField(hidden).bind("hidden");

        HorizontalLayout buttons = new HorizontalLayout();
        Button save = new Button("Save", e ->
        {
            try
            {
                if (binder.isValid())
                {
                    binder.writeBean(sfx);
                    sfxCommands.save(sfx);
                    sfxCommandGrid.setItems(sfxCommands.findAll());
                    sfxCommandGrid.recalculateColumnWidths();
                    addTriggerDialog.close();
                }
            } catch (ValidationException ex)
            {
                ex.printStackTrace();
            }
        });
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel", e -> addTriggerDialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        buttons.add(save, cancel);
        buttons.setJustifyContentMode(JustifyContentMode.END);

        addTriggerDialog.add(formLayout, buttons);

        binder.readBean(sfx);
        addTriggerDialog.open();
    }
}
