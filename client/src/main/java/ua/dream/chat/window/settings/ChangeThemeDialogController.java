package ua.dream.chat.window.settings;

import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import ua.dream.chat.util.LocalSettings;
import ua.dream.chat.window.DialogWindow;

import java.util.Arrays;

public class ChangeThemeDialogController extends DialogWindow<ChangeThemeDialogController> {

    @FXML
    private HBox buttons;
    private ToggleGroup group = new ToggleGroup();
    private LocalSettings.Theme selectedTheme;

    public ChangeThemeDialogController() {
        super("settings/ChangeThemeWindow.fxml");
    }

    @Override
    public void handleOpen() {
        buttons.getChildren().clear();
        Arrays.stream(LocalSettings.Theme.values()).map(theme -> {
            RadioButton selector = new RadioButton(theme.getName());
            selector.setToggleGroup(group);
            selector.setSelected(LocalSettings.currentTheme() == theme);
            selector.setOnAction(event -> selectedTheme = theme);
            return selector;
        }).forEach(buttons.getChildren()::add);
    }

    public void cancel() {
        close();
    }

    public void save() {
       if (selectedTheme != null) {
           LocalSettings.setCurrentTheme(selectedTheme);
           selectedTheme = null;
       }

        cancel();
    }

}
