package webserver;

import java.io.*;
import java.net.Socket;

import controller.Controller;
import model.HttpRequest;
import model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest request = new HttpRequest(new BufferedReader(new InputStreamReader(in)));
            HttpResponse response = new HttpResponse(new DataOutputStream(out));

            Controller.getController(request.getPath()).service(request, response);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
