package controller;

import model.HttpRequest;
import model.HttpResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class Controller {
    protected static final String DEFAULT_PATH = "/index.html";
    protected static final String LOGIN_PATH = "/user/login.html";
    protected static final String LOGIN_FAIL_PATH = "/user/login_failed.html";


    private static Map<String, Controller> controllers = new HashMap<>();

    static {
        controllers.put("/user/create", new CreateController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/list", new ListController());
    }

    public static Controller getController(String path) {
        Controller result = controllers.get(path);
        if (Objects.isNull(result)) {
            result = new DefaultController();
        }
        return result;
    }

    abstract public void service(HttpRequest request, HttpResponse response) throws IOException;
}
