package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Objects;

import model.ResponseRenderer;
import model.UserManager;
import db.DataBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestParser;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String DEFAULT_PATH = "/index.html";
    private static final String HOST = "http://localhost:8080";
    public static final String WEBAPP_PATH = "./webapp";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            new ResponseRenderer(new BufferedReader(new InputStreamReader(in))
                    , new DataOutputStream(out)).render(this);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void makeResp(BufferedReader br, DataOutputStream dos) throws IOException {
        String path = HttpRequestParser.requestLine(br);
        switch (path) {
            case "/user/create":
                UserManager.create(br);
                resp302(HOST + DEFAULT_PATH, dos);
                break;
            case "/user/login":
                if (UserManager.signIn(br)) resp302SignInSuccess(HOST + DEFAULT_PATH, dos);
                else resp302SignInFail(HOST + "/user/login_failed.html", dos);
                break;
            case "/user/list":
                if (UserManager.list(br)) listResp(dos);
                else resp302(HOST + "/user/login.html", dos);
                break;
            default:
                resp200(path, 200, dos, "");
                break;
        }
    }

    private void resp302SignInSuccess(String location, DataOutputStream dos) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        dos.writeBytes("Set-Cookie: logined=true\r\n");
        dos.writeBytes("Location: " + location + "\r\n");
    }

    private void resp302SignInFail(String location, DataOutputStream dos) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        dos.writeBytes("Set-Cookie: logined=false\r\n");
        dos.writeBytes("Location: " + location + "\r\n");
    }

    private void resp302(String location, DataOutputStream dos) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        dos.writeBytes("Location: " + location + "\r\n");
    }

    private void listResp(DataOutputStream dos) throws IOException {
        response200Header(dos);
        byte[] body = DataBase.findAll().toString().getBytes();
        responseHttpHeader(dos, body.length);
        responseBody(dos, body);
    }


    private void resp200(String path, int statusCode, DataOutputStream dos, String header) throws IOException {
        switch (statusCode) {
            case 200: response200Header(dos); break;
            default: break;
        }
        byte[] body = getBytes(path);
        if (!(header == null) && !Objects.equals(header, "")) {
            log.info("yes");
            dos.writeBytes(header + " \r\n");
        }
        responseHttpHeader(dos, body.length);

        responseBody(dos, body);
    }

    private byte[] getBytes(String path) {
        if (path.equals("/")) {
            return "hello world".getBytes();
        }
        try {
            return Files.readAllBytes(new File(WEBAPP_PATH + path).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void response200Header(DataOutputStream dos) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
    }

    private void responseHttpHeader(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }

    private void responseBody(DataOutputStream dos, byte[] body) throws IOException {
         dos.write(body, 0, body.length);
         dos.flush();
    }
}
