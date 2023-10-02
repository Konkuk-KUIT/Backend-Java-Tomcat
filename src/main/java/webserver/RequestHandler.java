package webserver;

import http.util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String ROOT_URL = "./webapp";
    private static final String HOME_URL = "/index.html";
    private static final String LOGIN_FAILED_URL = "/user/login_failed.html";
    private static final String LOGIN_URL = "/user/login.html";
    private static final String LIST_URL = "/user/list.html";

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            String startLine = br.readLine();
            String[] startLines = startLine.split(" ") ;
            String httpMethod = startLines[0];
            String requestUrl = startLines[1];

            byte[] body = new byte[0];

            /**
             * 요구사항 1번: index.html 반환하기
             */
            if (httpMethod.equals("GET") && (requestUrl.equals("/") || requestUrl.equals("/index.html"))) {
                body = Files.readAllBytes(Paths.get(ROOT_URL+HOME_URL));
            }

            response200Header(dos, body.length);
            responseBody(dos, body);

        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}