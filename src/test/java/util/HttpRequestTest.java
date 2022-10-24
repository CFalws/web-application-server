package util;


import model.HttpRequest;
import org.junit.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequestTest {
    private String dir = "./src/test/resources/";
    @Test
    public void header() throws IOException {
        BufferedReader br = new BufferedReader(new StringReader(
                "GET /user/create HTTP/1.1\r\n"
                        + "Content-Length: 15\r\n"
                        + "Cookie: logined=true\r\n"
                        + "\r\nHello world: 14"
        ));
        HttpRequest parser = new HttpRequest(br);
        try {
            assertThat(parser.getHeader("Content-Length")).isEqualTo("15");
            assertThat(parser.getHeader("Cookie")).isEqualTo("logined=true");
            assertThat(parser.getHeader("Hello world")).isNull();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getTest() {
        try {
            InputStream in = new FileInputStream(new File(dir + "Http_Get.txt"));
            HttpRequest parser = new HttpRequest(
                    new BufferedReader(new InputStreamReader(in)));

            assertThat("GET").isEqualTo(parser.getMethod());
            assertThat("/user/create").isEqualTo(parser.getPath());
            assertThat("keep-alive").isEqualTo(parser.getHeader("Connection"));
            assertThat("javajigi").isEqualTo(parser.getParameters("userId"));

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void postTest() {
        try {
            InputStream in = new FileInputStream(new File(dir + "Http_POST.txt"));
            HttpRequest parser = new HttpRequest(
                    new BufferedReader(new InputStreamReader(in)));

            assertThat("POST").isEqualTo(parser.getMethod());
            assertThat("/user/create").isEqualTo(parser.getPath());
            assertThat("keep-alive").isEqualTo(parser.getHeader("Connection"));
            assertThat("javajigi").isEqualTo(parser.getParameters("userId"));

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}