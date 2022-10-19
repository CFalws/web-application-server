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
    private static final String REDIRECT_LOCATION = "http://localhost:8080";

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
                makeHttpResp(DEFAULT_PATH, 302, dos, "");
                break;
            case "/user/login":
                if (UserManager.signIn(br)) loginResp(DEFAULT_PATH, dos, true);
                else loginResp("/user/login_failed.html", dos, false);
                break;
            case "/user/list":
                if (UserManager.list(br)) listResp(dos);
                else makeHttpResp("/user/login.html", 302, dos, "");
                break;
            case "/css/style.css": makeCssResp(path, dos); break;
            case "/css/bootstrap.min.css": makeCssResp(path, dos); break;
            default:
                makeHttpResp(path, 200, dos, "");
                break;
        }
    }

    private void makeCssResp(String path, DataOutputStream dos) throws IOException {
        response200Header(dos);
        byte[] body = getBytes(path);
        responseCssHeader(dos, body.length);
        responseBody(dos, body);
    }

    private void listResp(DataOutputStream dos) throws IOException {
        response200Header(dos);
        byte[] body = DataBase.findAll().toString().getBytes();
        responseHttpHeader(dos, body.length);
        responseBody(dos, body);
    }

    private void loginResp(String path, DataOutputStream dos, boolean logined) throws IOException {
        if (logined)
            makeHttpResp(path, 302, dos, "Set-Cookie: logined=true");
        else
            makeHttpResp(path, 302, dos, "Set-Cookie: logined=false");
    }

    private void makeHttpResp(String path, int statusCode, DataOutputStream dos, String header) throws IOException {
        switch (statusCode) {
            case 200: response200Header(dos); break;
            case 302: response302Header(path, dos); break;
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
            return Files.readAllBytes(new File("./webapp" + path).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void response200Header(DataOutputStream dos) throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
    }

    private void response302Header(String path, DataOutputStream dos) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        dos.writeBytes("Location: " + REDIRECT_LOCATION + path + "\r\n");
    }

    private void responseHttpHeader(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
        dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }

    private void responseCssHeader(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
        dos.writeBytes("Content-Type: text/css\r\n");
        dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
        dos.writeBytes("\r\n");
    }


    private void responseBody(DataOutputStream dos, byte[] body) throws IOException {
         dos.write(body, 0, body.length);
         dos.flush();
    }
}
