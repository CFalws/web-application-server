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

public class UserRepository {
    private static Logger log = LoggerFactory.getLogger(UserRepository.class);

    public static boolean signIn(BufferedReader br) throws IOException {
        Map<String, String> parameters = HttpRequestUtils.parseQueryString(HttpRequestParser.bodyOf(br));
        log.debug(parameters.get("userId") + " " + parameters.get("password"));
        User user = DataBase.findUserById(parameters.get("userId"));
        try {
            return Objects.equals(user.getPassword(), parameters.get("password"));
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static void create(BufferedReader br) {
        try {
            Map<String, String> parameters = HttpRequestUtils.parseQueryString(HttpRequestParser.bodyOf(br));
            DataBase.addUser(new User(parameters.get("userId"), parameters.get("password"), parameters.get("name"), parameters.get("email")));
            log.debug("ID: " + parameters.get("userId") + " made");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean list(BufferedReader br) throws IOException {
        if (HttpRequestParser.getLogin(br)) {
            return true;
        }
        return false;
    }
}
