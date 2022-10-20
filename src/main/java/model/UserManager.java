package model;

import db.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestParser;
import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class UserManager {
    private static Logger log = LoggerFactory.getLogger(UserManager.class);

    public static boolean signIn(BufferedReader br) throws IOException {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(new HttpRequestParser(br).bodyOf(br));
        log.debug(parameters.get("userId") + " " + parameters.get("password"));
        try {
            return Objects.equals(DataBase.findUserById(parameters.get("userId")).getPassword()
                    , parameters.get("password"));
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static void create(BufferedReader br) throws IOException {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(new HttpRequestParser(br).bodyOf(br));
        DataBase.addUser(new User(parameters.get("userId"), parameters.get("password"), parameters.get("name"), parameters.get("email")));
        log.debug("ID: " + parameters.get("userId") + " made");
    }
}
