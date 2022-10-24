package controller;

import java.util.HashMap;
import java.util.Map;

public class Controller {
    private static Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/user/create", new CreateController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/list", new ListController());
        controllers.put("/", new HomeController());
    }

    public static Controller getController(String path) {
        return controllers.get(path);
    }
}
