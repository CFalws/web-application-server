package model;

import db.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequest;

import java.io.IOException;
import java.util.Objects;

public class UserService {
    private static Logger log = LoggerFactory.getLogger(UserService.class);

    public static boolean signIn(HttpRequest requestParser) throws IOException {
        log.debug(requestParser.getParameters("userId") + " " + requestParser.getParameters("password"));
        try {
            return Objects.equals(DataBase.findUserById(requestParser.getParameters("userId")).getPassword()
                    , requestParser.getParameters("password"));
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static void create(HttpRequest requestParser) throws IOException {
        DataBase.addUser(new User(requestParser.getParameters("userId")
                                , requestParser.getParameters("password")
                                , requestParser.getParameters("name")
                                , requestParser.getParameters("email")));
        log.debug("ID: " + requestParser.getParameters("userId") + " made");
    }
}
