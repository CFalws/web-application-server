package controller;

import db.DataBase;
import model.HttpRequest;
import model.HttpResponse;

import java.io.IOException;

public class ListController extends Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        if (request.isSignedIn()) {
            byte[] body = DataBase.findAll().toString().getBytes();
            response.forward(body);
        }
        else response.redirect(LOGIN_PATH);
    }
}
