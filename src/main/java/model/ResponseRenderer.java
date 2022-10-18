package model;

import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

public class ResponseRenderer {
    private BufferedReader br;
    private DataOutputStream dos;
    public ResponseRenderer(BufferedReader br, DataOutputStream dos) {
        this.br = br;
        this.dos = dos;
    }

    public void render(RequestHandler handler) throws IOException {
        handler.makeResp(br, dos);
    }
}
