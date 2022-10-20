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
    private static final String HOST = "http://localhost:8080";
    private static final String WEBAPP_PATH = "./webapp";
    private BufferedReader request;
    private DataOutputStream response;
    public ResponseRenderer(BufferedReader request, DataOutputStream response) {
        this.request = request;
        this.response = response;
    }

    public void render() throws IOException {
        HttpRequestParser requestParser = new HttpRequestParser(request);
        String path = requestParser.getPath();
        switch (path) {
            case "/user/create":
                UserManager.create(requestParser);
                resp302(HOST + DEFAULT_PATH);
                break;
            case "/user/login":
                if (UserManager.signIn(requestParser)) resp302SignInSuccess(HOST + DEFAULT_PATH, true);
                else resp302SignInSuccess(HOST + "/user/login_failed.html", false);
                break;
            case "/user/list":
                if (requestParser.isSignedIn()) resp200AllUser();
                else resp302(HOST + "/user/login.html");
                break;
            default:
                resp200(path);
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
        response.writeBytes("HTTP/1.1 200 OK \r\n");
        byte[] body = DataBase.findAll().toString().getBytes();
        response.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        response.writeBytes("Content-Length: " + body.length + "\r\n");
        response.writeBytes("\r\n");
        responseBody(body);
    }


    private void resp200(String path) throws IOException {
        response.writeBytes("HTTP/1.1 200 OK \r\n");
        byte[] body = getBytes(path);
        response.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        response.writeBytes("Content-Length: " + body.length + "\r\n");
        response.writeBytes("\r\n");
        responseBody(body);
    }

    private byte[] getBytes(String path) throws IOException {
        if (path.equals("/")) {
            path = "/index.html";
        }
        return Files.readAllBytes(new File(WEBAPP_PATH + path).toPath());
    }

    private void responseBody(byte[] body) throws IOException {
        response.write(body, 0, body.length);
        response.flush();
    }
}
