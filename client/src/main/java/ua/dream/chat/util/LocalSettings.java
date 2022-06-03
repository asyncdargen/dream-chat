package ua.dream.chat.util;

import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.experimental.UtilityClass;
import ua.dream.chat.App;
import ua.dream.chat.model.Theme;
import ua.dream.chat.window.Window;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@UtilityClass
public class LocalSettings {

    public String FILE = "settings.dat";
    public Theme DEFAULT_THEME = new Theme("default", new String[]{"-bg-primary-color: #782b78;",
            "-bg-primary-hover-color: #10449D;",
            "-bg-primary-selected-color: #3F1F85;",
            "-bg-secondary-color: #200e20;",
            "-bg-secondary2-color: #2b172b;",
            "-bg-secondary2-hover-color: #372037;",
            "-font-primary-color: #EEEEEE;",
            "-font-secondary-color: #72879B;",
            "-font-secondary-hover-color: #B3B5B7;"
    });
    public Map<String, Theme> THEMES = new ConcurrentHashMap<>();
    public Map<String, String> SETTINGS;

    static {
        SETTINGS = LocalUtil.getContent(FILE, reader ->
                reader.lines()
                        .map(line -> line.split("="))
                        .filter(args -> args.length == 2)
                        .collect(Collectors.toMap(args -> args[0], args -> args[1]))
        );
        SETTINGS = SETTINGS == null ? new ConcurrentHashMap<>() : SETTINGS;
    }

    public void saveSettings() {
        LocalUtil.setContent(FILE, writer -> {
            SETTINGS.entrySet()
                    .stream()
                    .map(setting -> setting.getKey() + "=" + setting.getValue())
                    .forEach(line -> {
                        try {
                            writer.write(line + "\n");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        });
    }

    public String currentThemeName() {
        return SETTINGS.getOrDefault("theme", "default");
    }

    public Theme getThemeByName(String name) {
        return THEMES.get(name);
    }

    public Theme currentTheme() {
        return THEMES.getOrDefault(currentThemeName(), DEFAULT_THEME);
    }

    public void applyStyles(Parent parent) {
        Arrays.stream(currentTheme().getStyle()).forEach(parent::setStyle);
        parent.applyCss();
    }

    public void updateStyles() {
        Arrays.stream(App.class.getDeclaredFields())
                .filter(field -> field.getType().isAssignableFrom(Window.class))
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        return ((Window<?>) field.get(null));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(Window::getScene)
                .map(Scene::getRoot)
                .forEach(LocalSettings::applyStyles);
    }

    public void changeTheme(String themeName) {
        SETTINGS.put("theme", getThemeByName(themeName) == null ? "default" : themeName);

        updateStyles();

        saveSettings();
    }

}
