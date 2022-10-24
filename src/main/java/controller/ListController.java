package controller;

import model.HttpRequest;
import model.HttpResponse;

import java.io.IOException;

public class ListController extends Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (request.isSignedIn()) response.list();
        else response.redirect(LOGIN_PATH);
    }
}
