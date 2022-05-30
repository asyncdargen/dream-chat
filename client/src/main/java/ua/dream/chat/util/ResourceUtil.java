package ua.dream.chat.util;

import lombok.experimental.UtilityClass;

import java.net.URL;

@UtilityClass
public class ResourceUtil {

    public ClassLoader CONTEXT_LOADER = Thread.currentThread().getContextClassLoader();

    public URL getResourceURL(String path) {
        return CONTEXT_LOADER.getResource(path);
    }

    public URL getFxmlResourceURL(String path) {
        return getResourceURL("fxml/" + path);
    }

    public URL getCSSResourceURL(String path) {
        return getResourceURL("css/" + path);
    }

    public URL getImageResourceURL(String path) {
        return getResourceURL("image/" + path);
    }

}
