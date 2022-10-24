package model;

import db.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class UserService {
    private static Logger log = LoggerFactory.getLogger(UserService.class);

    public static boolean signIn(HttpRequest request) throws IOException {
        log.debug(request.getParameters("userId") + " " + request.getParameters("password"));
        try {
            return Objects.equals(DataBase.findUserById(request.getParameters("userId")).getPassword()
                    , request.getParameters("password"));
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static void create(HttpRequest request) throws IOException {
        DataBase.addUser(new User(request.getParameters("userId")
                                , request.getParameters("password")
                                , request.getParameters("name")
                                , request.getParameters("email")));
        log.debug("ID: " + request.getParameters("userId") + " made");
    }
}
