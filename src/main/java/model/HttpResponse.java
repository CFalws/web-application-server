package model;

import db.DataBase;
import service.UserService;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class  HttpResponse {
    private static final String DEFAULT_PATH = "/index.html";
    private static final String WEBAPP_PATH = "./webapp";
    private DataOutputStream dos;
    private Map<String, String> header = new HashMap<>();
    public HttpResponse(DataOutputStream dos) {
        this.dos = dos;
    }

    public void addHeader(String key, String value) {
        if (Objects.isNull(key) || Objects.isNull(value)) throw new NullPointerException();
        header.put(key, value);
    }

    public void forward(String path) throws IOException {
        byte[] body = getBytes(path);
        forward(body);
    }

    public void forward(byte[] body) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        responseBody(body);
    }

    public void redirect(String location) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        dos.writeBytes("Location: " + location + "\r\n");
    }

    public void signInSuccess(String location, Boolean success) throws IOException {
        redirect(location);
        dos.writeBytes("Set-Cookie: logined=" + String.valueOf(success) + "\r\n\r\n");
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
