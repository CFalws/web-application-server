package controller;

import model.HttpRequest;
import model.HttpResponse;
import service.UserService;

import java.io.IOException;

public class LoginController extends Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (UserService.signIn(request)) response.signInSuccess(DEFAULT_PATH, true);
        else response.signInSuccess(LOGIN_FAIL_PATH, false);
    }
}
