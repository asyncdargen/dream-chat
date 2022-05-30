package ua.dream.chat.window.main;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.val;
import lombok.var;
import ua.dream.chat.App;
import ua.dream.chat.util.References;
import ua.dream.chat.window.DialogWindow;

public class SearchProfileDialogController extends DialogWindow<SearchProfileDialogController> {

    @FXML
    private TextField nameTextField;

    public SearchProfileDialogController() {
        super("main/AddProfileWindow.fxml");
    }

    @Override
    public void handleOpen() {
        nameTextField.setPromptText("Name or id");
    }

    public void cancel() {
//        App.SETTINGS_WINDOW.getController().handleOpen();
        close();
    }

    public void save() {
        val name = nameTextField.getText();
        if (name.length() >= References.NAME_MIN_LENGTH && name.length() <= References.NAME_MAX_LENGTH) {
            var user = App.getUserRepository().getUser(name);
            if (user == null) try {
                user = App.getUserRepository().getUser(Long.parseLong(name));
            } catch (Throwable t) {}
            if (user != null) {
                App.getUserRepository().getSelfUser().getChatsIds().add(user.getId());
                Platform.runLater(() -> {
                        App.MAIN_WINDOW.getController().refreshContacts();
                });
            }
        }

        cancel();
    }

}
