package model;

import db.DataBase;
import util.HttpRequestParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ResponseRenderer {
    private static final String DEFAULT_PATH = "/index.html";
    private static final String WEBAPP_PATH = "./webapp";
    private static final String LOGIN_FAIL_PATH = "/user/login_failed.html";
    private static final String LOGIN_PATH = "/user/login.html";
    private BufferedReader request;
    private DataOutputStream response;
    public ResponseRenderer(BufferedReader request, DataOutputStream response) {
        this.request = request;
        this.response = response;
    }

    public void render() throws IOException {
        HttpRequestParser requestParser = new HttpRequestParser(request);
        String resourcePath = requestParser.getPath();
        switch (resourcePath) {
            case "/user/create":
                UserManager.create(requestParser);
                resp302(DEFAULT_PATH);
                break;
            case "/user/login":
                if (UserManager.signIn(requestParser)) resp302SignInSuccess(DEFAULT_PATH, true);
                else resp302SignInSuccess(LOGIN_FAIL_PATH, false);
                break;
            case "/user/list":
                if (requestParser.isSignedIn()) resp200AllUser();
                else resp302(LOGIN_PATH);
                break;
            default:
                resp200(resourcePath);
                break;
        }
    }

    private void resp302SignInSuccess(String location, Boolean success) throws IOException {
        resp302(location);
        response.writeBytes("Set-Cookie: logined=" + String.valueOf(success) + "\r\n\r\n");
    }

    private void resp302(String location) throws IOException {
        response.writeBytes("HTTP/1.1 302 Found \r\n");
        response.writeBytes("Location: " + location + "\r\n");
    }

    private void resp200AllUser() throws IOException {
        byte[] body = DataBase.findAll().toString().getBytes();
        response.writeBytes("HTTP/1.1 200 OK \r\n");
        response.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        response.writeBytes("Content-Length: " + body.length + "\r\n");
        response.writeBytes("\r\n");
        responseBody(body);
    }


    private void resp200(String path) throws IOException {
        byte[] body = getBytes(path);
        response.writeBytes("HTTP/1.1 200 OK \r\n");
        response.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        response.writeBytes("Content-Length: " + body.length + "\r\n");
        response.writeBytes("\r\n");
        responseBody(body);
    }

    private byte[] getBytes(String path) throws IOException {
        if (path.equals("/")) {
            path = DEFAULT_PATH;
        }
        return Files.readAllBytes(new File(WEBAPP_PATH + path).toPath());
    }

    private void responseBody(byte[] body) throws IOException {
        response.write(body, 0, body.length);
        response.flush();
    }
}
