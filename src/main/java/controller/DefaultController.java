package controller;

import model.HttpRequest;
import model.HttpResponse;

import java.io.IOException;

public class DefaultController extends Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        response.forward(request.getPath());
    }
}
