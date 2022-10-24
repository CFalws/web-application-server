package model;

import db.DataBase;
import service.UserService;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class  HttpResponse {
    private static final String DEFAULT_PATH = "/index.html";
    private static final String WEBAPP_PATH = "./webapp";
    private static final String LOGIN_FAIL_PATH = "/user/login_failed.html";
    private static final String LOGIN_PATH = "/user/login.html";
    private DataOutputStream dos;
    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void signInSuccess(String location, Boolean success) throws IOException {
        redirect(location);
        dos.writeBytes("Set-Cookie: logined=" + String.valueOf(success) + "\r\n\r\n");
    }

    public void redirect(String location) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        dos.writeBytes("Location: " + location + "\r\n");
    }

    public void list() throws IOException {
        byte[] body = DataBase.findAll().toString().getBytes();
        forward(body);
    }


    public void forward(String path) throws IOException {
        byte[] body = getBytes(path);
        forward(body);
    }

    private void forward(byte[] body) throws IOException {
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
