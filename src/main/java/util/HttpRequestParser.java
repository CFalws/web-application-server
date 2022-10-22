package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpRequestParser {
    private static final Logger log = LoggerFactory.getLogger(HttpRequestParser.class);
    private String method;
    private String path;
    private Map<String, String> header = new HashMap<>();
    private String body;
    private Map<String, String> parameters;

    public HttpRequestParser(BufferedReader request) {
        parse(request);
    }
    private void requestLine(BufferedReader request) throws IOException {
        String[] reqLine = request.readLine().split(" ");
        if (!Objects.equals(reqLine[1].substring(0, 1), "/"))
            throw new IllegalArgumentException();
        method = reqLine[0];
        String[] pathAndParam = reqLine[1].split("\\?");
        path = pathAndParam[0];
        parameters = HttpRequestUtils.parseQueryString(pathAndParam[1]);
    }

    private void header(BufferedReader request) throws IOException {
        String line;
        while ((line = request.readLine()).length() != 0) {
            log.debug(line);
            header.put(HttpRequestUtils.parseHeader(line).getKey()
                    , HttpRequestUtils.parseHeader(line).getValue());
        }
    }

    private void body(BufferedReader request) throws IOException {
        try {
            body = IOUtils.readData(request, Integer.parseInt(header.get("Content-Length")));
            parameters = HttpRequestUtils.parseQueryString(body);
        } catch (NumberFormatException e) {
            // do nothing
            log.debug("NumberFormat Exception!~~!");
        }
    }

    private void parse(BufferedReader request) {
        try {
            requestLine(request);
            header(request);
            body(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean isSignedIn() throws IOException {
        String cookie = header.get("Cookie");
        if (Objects.nonNull(cookie)) {
            return Boolean
                    .parseBoolean(HttpRequestUtils.parseCookies(cookie).get("logined"));
        }
        return false;
    }

    public String getMethod() {
        return method;
    }

    public String getResourcePath() {
        return path;
    }

    public String getHeader(String key) throws IOException {
        return header.get(key);
    }

    public String getBody() {
        return body;
    }

    public String getParameters(String key) {
        return parameters.get(key);
    }
}
