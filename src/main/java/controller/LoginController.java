package controller;

import model.HttpRequest;
import model.HttpResponse;
import service.UserService;

import java.io.IOException;

public class LoginController extends Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (UserService.signIn(request)){
            response.addHeader("Set-Cookie", "logined=true");
            response.redirect(DEFAULT_PATH);
        } else {
            response.addHeader("Set-Cookie", "logined=false");
            response.redirect(LOGIN_FAIL_PATH);
        }
    }
}
