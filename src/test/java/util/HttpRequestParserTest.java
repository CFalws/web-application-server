package util;


import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

import util.*;
public class HttpRequestParserTest {
    @Test
    public void header() {
        BufferedReader br = new BufferedReader(new StringReader(
                "Content-Length: 15\r\n"
                + "Cookie: logined=true\r\n"
                + "\r\nHello world: 14"
        ));
        HttpRequestParser parser = new HttpRequestParser(br);
        try {
            assertThat(parser.header("Content-Length")).isEqualTo("15");
            assertThat(parser.header("Cookie")).isEqualTo("logined=true");
            assertThat(parser.header("Hello world")).isNull();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}