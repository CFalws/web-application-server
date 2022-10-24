package controller;

import model.HttpRequest;
import model.HttpResponse;
import service.UserService;

import java.io.IOException;

public class CreateController extends Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        UserService.create(request);
        response.redirect(DEFAULT_PATH);
    }
}
