package ua.dream.chat.window;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import ua.dream.chat.App;
import ua.dream.chat.util.ResourceUtil;

import java.net.URL;
import java.util.function.Consumer;

@Getter
public class Window<C extends WindowController> {

    public static <C extends WindowController> Window<C> create(String path) {
        return new Window<C>(path);
    }

    public static <C extends WindowController> Window<C> create(String path, Consumer<Window<C>> transform) {
        val window = new Window<C>(path);
        transform.accept(window);
        return window;
    }

    protected final Scene scene;
    protected final C controller;

    @SneakyThrows
    public Window(String scenePath) {
        URL sceneURL = ResourceUtil.getFxmlResourceURL(scenePath);
        FXMLLoader loader = new FXMLLoader(sceneURL);
        Pane rootPane = loader.load();
        controller = loader.getController();
        scene = new Scene(rootPane);
    }

    public void showAsMainStage() {
        controller.handleOpen();
        App.switchMainStageTo(this);
    }

    public void showAsModalStage() {
        controller.handleOpen();
        App.switchModalStageTo(this);
    }

}
