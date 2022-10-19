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
    private BufferedReader br;
    private DataOutputStream dos;
    public ResponseRenderer(BufferedReader br, DataOutputStream dos) {
        this.br = br;
        this.dos = dos;
    }

    public void render() throws IOException {
        String path = HttpRequestParser.requestLine(br);
        switch (path) {
            case "/user/create":
                UserManager.create(br);
                resp302(HOST + DEFAULT_PATH);
                break;
            case "/user/login":
                if (UserManager.signIn(br)) resp302SignInSuccess(HOST + DEFAULT_PATH, true);
                else resp302SignInSuccess(HOST + "/user/login_failed.html", false);
                break;
            case "/user/list":
                if (HttpRequestParser.isSignedIn(br)) resp200AllUser();
                else resp302(HOST + "/user/login.html");
                break;
            default:
                resp200(path);
                break;
        }
    }

    private void resp302SignInSuccess(String location, Boolean success) throws IOException {
        resp302(location);
        dos.writeBytes("Set-Cookie: logined=" + String.valueOf(success) + "\r\n\r\n");
    }
    private void resp302SignInSuccess(String location) throws IOException {
        resp302(location);
        dos.writeBytes("Set-Cookie: logined=true\r\n\r\n");
    }

    private void resp302SignInFail(String location) throws IOException {
        resp302(location);
        dos.writeBytes("Set-Cookie: logined=false\r\n\r\n");
    }

    private void resp302(String location) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        dos.writeBytes("Location: " + location + "\r\n");
    }

    private void resp200AllUser() throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        byte[] body = DataBase.findAll().toString().getBytes();
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        responseBody(body);
    }


    private void resp200(String path) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        byte[] body = getBytes(path);
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");
        responseBody(body);
    }

    private byte[] getBytes(String path) throws IOException {
        if (path.equals("/")) {
            path = "/index.html";
        }
        return Files.readAllBytes(new File(WEBAPP_PATH + path).toPath());
    }

    private void responseBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.flush();
    }
}
