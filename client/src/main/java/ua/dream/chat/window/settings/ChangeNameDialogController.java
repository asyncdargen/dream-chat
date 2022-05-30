package ua.dream.chat.window.settings;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.packet.user.PacketUserUpdateName;
import ua.dream.chat.util.References;
import ua.dream.chat.window.DialogWindow;

public class ChangeNameDialogController extends DialogWindow<ChangeNameDialogController> {

    @FXML
    private TextField nameTextField;

    public ChangeNameDialogController() {
        super("settings/ChangeNameWindow.fxml");
    }

    @Override
    public void handleOpen() {
        nameTextField.setText(App.getUserRepository().getSelfUser().getName());
    }

    public void cancel() {
//        App.SETTINGS_WINDOW.getController().handleOpen();
        close();
    }

    public void save() {
        val name = nameTextField.getText();
        if (name.length() >= References.NAME_MIN_LENGTH && name.length() <= References.NAME_MAX_LENGTH) {
            App.getUserRepository().getSelfUser().setName(name);
            App.getClient().write(new PacketUserUpdateName(nameTextField.getText()));
        }

        cancel();
    }

}
