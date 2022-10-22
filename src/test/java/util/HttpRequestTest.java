package util;


import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequestTest {
    @Test
    public void header() {
        BufferedReader br = new BufferedReader(new StringReader(
                "Content-Length: 15\r\n"
                + "Cookie: logined=true\r\n"
                + "\r\nHello world: 14"
        ));
        HttpRequest parser = new HttpRequest(br);
        try {
            assertThat(parser.header("Content-Length")).isEqualTo("15");
            assertThat(parser.header("Cookie")).isEqualTo("logined=true");
            assertThat(parser.header("Hello world")).isNull();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}