package util;


import org.junit.Test;

import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequestParserTest {
    private String dir = "./src/test/resources/";
    @Test
    public void header() {
        BufferedReader br = new BufferedReader(new StringReader(
                "GET /user/create HTTP/1.1\r\n"
                        + "Content-Length: 15\r\n"
                        + "Cookie: logined=true\r\n"
                        + "\r\nHello world: 14"
        ));
        HttpRequestParser parser = new HttpRequestParser(br);
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
            HttpRequestParser parser = new HttpRequestParser(
                    new BufferedReader(new InputStreamReader(in)));

            assertThat("GET").isEqualTo(parser.getMethod());
            assertThat("/user/create").isEqualTo(parser.getResourcePath());
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
            HttpRequestParser parser = new HttpRequestParser(
                    new BufferedReader(new InputStreamReader(in)));

            assertThat("POST").isEqualTo(parser.getMethod());
            assertThat("/user/create").isEqualTo(parser.getResourcePath());
            assertThat("keep-alive").isEqualTo(parser.getHeader("Connection"));
            assertThat("javajigi").isEqualTo(parser.getParameters("userId"));

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}