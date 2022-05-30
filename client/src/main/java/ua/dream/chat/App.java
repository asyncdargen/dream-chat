package ua.dream.chat;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;
import ua.dream.chat.client.DreamChatClient;
import ua.dream.chat.repository.MessageRepository;
import ua.dream.chat.repository.UserRepository;
import ua.dream.chat.util.ResourceUtil;
import ua.dream.chat.window.DialogWindow;
import ua.dream.chat.window.Window;
import ua.dream.chat.window.auth.AuthWindowController;
import ua.dream.chat.window.auth.StartWindowController;
import ua.dream.chat.window.main.MainWindowController;
import ua.dream.chat.window.main.SearchProfileDialogController;
import ua.dream.chat.window.settings.ChangeAvatarDialogController;
import ua.dream.chat.window.settings.ChangeNameDialogController;
import ua.dream.chat.window.settings.ChangePasswordDialogController;
import ua.dream.chat.window.settings.SettingsMenuController;

import static ua.dream.chat.util.logger.Logger.LOGGER;
import static ua.dream.chat.window.Window.create;

public class App extends Application {

    public static final Window<StartWindowController> START_WINDOW = create("auth/StartWindow.fxml");
    public static final Window<AuthWindowController> AUTH_WINDOW = create("auth/AuthWindow.fxml");
    public static final Window<MainWindowController> MAIN_WINDOW = create("main/MainWindow.fxml");
    public static final DialogWindow<SearchProfileDialogController> SEARCH_PROFILE = new SearchProfileDialogController();

    public static final Window<SettingsMenuController> SETTINGS_WINDOW = create("settings/SettingsWindow.fxml", window -> window.getScene().setFill(Color.TRANSPARENT));
    public static final DialogWindow<ChangeNameDialogController> CHANGE_NAME_DIALOG = new ChangeNameDialogController();
    public static final DialogWindow<ChangePasswordDialogController> CHANGE_PASSWORD_DIALOG = new ChangePasswordDialogController();
    public static final DialogWindow<ChangeAvatarDialogController> CHANGE_AVATAR_DIALOG = new ChangeAvatarDialogController();

    @Getter
    private static DreamChatClient client;
    @Getter
    private static UserRepository userRepository;
    @Getter
    private static MessageRepository messageRepository;
    @Getter
    private static Window<?> currentWindow;
    @Getter @Setter
    private static Stage mainStage;
    @Getter @Setter
    private static Stage modalStage;

    public void start(Stage stage) throws Exception {
        LOGGER.info("Starting DreamChat application..");

        LOGGER.info("Creating connection...");
        client = new DreamChatClient();

        LOGGER.info("Initializing repositories...");
        userRepository = new UserRepository();
        messageRepository = new MessageRepository();

        mainStage = stage;

        LOGGER.info("Loading modal stage...");
        modalStage = new Stage();
        modalStage.initStyle(StageStyle.TRANSPARENT);
        modalStage.initOwner(App.mainStage);
        modalStage.initModality(Modality.NONE);

        LOGGER.info("Loading main stage...");
        mainStage.setTitle("DreamChat");
        START_WINDOW.showAsMainStage();
        mainStage.getIcons().add(new Image(ResourceUtil.getImageResourceURL("icon.png").openStream()));
    }

    public static void switchMainStageTo(Window<?> window) {
        if (modalStage.isShowing())
            modalStage.hide();

        switchStage(window, mainStage);
    }

    public static void switchModalStageTo(Window<?> window) {
        switchStage(window, modalStage);
    }

    public static void switchStage(Window<?> window, Stage stage) {
        currentWindow = window;
        stage.hide();
        stage.setScene(window.getScene());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
