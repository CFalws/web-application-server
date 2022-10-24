package model;

import db.DataBase;
import util.HttpRequest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class HttpResponse {
    private static final String DEFAULT_PATH = "/index.html";
    private static final String WEBAPP_PATH = "./webapp";
    private static final String LOGIN_FAIL_PATH = "/user/login_failed.html";
    private static final String LOGIN_PATH = "/user/login.html";
    private BufferedReader bufferedReader;
    private DataOutputStream dos;
    public HttpResponse(BufferedReader bufferedReader, DataOutputStream dos) {
        this.bufferedReader = bufferedReader;
        this.dos = dos;
    }

    public void render() throws IOException {
        HttpRequest request = new HttpRequest(bufferedReader);
        String resourcePath = request.getPath();
        switch (resourcePath) {
            case "/user/create":
                UserManager.create(request);
                redirect(DEFAULT_PATH);
                break;
            case "/user/login":
                if (UserManager.signIn(request)) resp302SignInSuccess(DEFAULT_PATH, true);
                else resp302SignInSuccess(LOGIN_FAIL_PATH, false);
                break;
            case "/user/list":
                if (request.isSignedIn()) resp200AllUser();
                else redirect(LOGIN_PATH);
                break;
            default:
                resp200(resourcePath);
                break;
        }
    }

    private void resp302SignInSuccess(String location, Boolean success) throws IOException {
        redirect(location);
        dos.writeBytes("Set-Cookie: logined=" + String.valueOf(success) + "\r\n\r\n");
    }

    private void redirect(String location) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        dos.writeBytes("Location: " + location + "\r\n");
    }

    private void resp200AllUser() throws IOException {
        byte[] body = DataBase.findAll().toString().getBytes();
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        responseBody(body);
    }


    private void resp200(String path) throws IOException {
        byte[] body = getBytes(path);
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        responseBody(body);
    }

    private byte[] getBytes(String path) throws IOException {
        if (path.equals("/")) {
            path = DEFAULT_PATH;
        }
        return Files.readAllBytes(new File(WEBAPP_PATH + path).toPath());
    }

    private void responseBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.flush();
    }
}
