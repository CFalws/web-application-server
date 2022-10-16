package db;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Map;

import model.User;
import org.junit.Test;

import util.HttpRequestUtils.Pair;

public class DataBaseTest {
    @Test
    public void testFindAll() {
        DataBase.addUser(new User("df", "123", "oo", "abc@abc.com"));
        System.out.println(DataBase.findAll());
    }
}