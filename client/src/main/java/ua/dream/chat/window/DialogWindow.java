package ua.dream.chat.window;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.SneakyThrows;
import ua.dream.chat.App;
import ua.dream.chat.util.ResourceUtil;

import java.net.URL;

@Getter
public class DialogWindow<C> extends Dialog<Void> {

    protected final Scene scene;
    protected final C controller;

    @SneakyThrows
    public DialogWindow(String scenePath) {
        URL sceneURL = ResourceUtil.getFxmlResourceURL(scenePath);
        FXMLLoader loader = new FXMLLoader(sceneURL);
        loader.setController(controller = (C) this);
        Pane root = loader.load();
        scene = getDialogPane().getScene();

        scene.setFill(Color.TRANSPARENT);

        Stage stage = (Stage) scene.getWindow();
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.NONE);
        stage.initOwner(App.getModalStage());

        getDialogPane().setContent(root);
        getDialogPane().getStylesheets().add(ResourceUtil.getCSSResourceURL("style.css").toExternalForm());
        getDialogPane().setStyle("-fx-background-color: transparent");

        //Need to add close button to allow close the window
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.setVisible(false);
    }

    public void showOnTopStage() {
        handleOpen();

        showAndWait();
    }

    public void handleOpen() {}

}
