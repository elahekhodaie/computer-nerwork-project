import java.net.*;
import java.io.*;
import java.util.*;

public class RequestPacket {
    public HttpRequest method;
    public String url;
    public HashMap<String, String> headers;
    public StatusCode responseCode;

    public RequestPacket(HttpRequest m, String u) {
        headers = new HashMap<>();
        this.method = m;
        this.url = u;
    }

    public RequestPacket() {
        headers = new HashMap<>();
    }

    public void addHeader(String key, String value) {
        if (key.equals("Accept-Encoding")) {
            if (value.contains("gzip"))
                headers.put(key, "gzip");
            return;
        }
        headers.put(key, value);
    }

    public String toString() {
        String result = "";
        result += method + " " + url + " " + "HTTP/1.1" + "\n";
        for (Map.Entry mapElement : headers.entrySet()) {
            result += mapElement.getKey() + ": " + mapElement.getValue() + "\n";
        }
        return result;
    }

    public static RequestPacket fromString(String packetString) {
        RequestPacket p = new RequestPacket();
        String[] lines = packetString.split("\n");
        String[] first_line = lines[0].split(" ");
        if (first_line.length != 3) {
            p.responseCode = StatusCode.BAD_REQUEST_400;
            return p;
        }
        boolean methodIsValid = false;
        for (HttpRequest s : HttpRequest.values()) {
            if (s.name().equals(first_line[0])) {
                methodIsValid = true;
            }
        }
        if (!methodIsValid) {
            p.responseCode = StatusCode.NOT_IMPLEMENTED_501;
            return p;
        }
        if (HttpRequest.valueOf(first_line[0]) != HttpRequest.GET) {
            p.responseCode = StatusCode.METHOD_NOT_ALLOWED_405;
            return p;
        }
        p.method = HttpRequest.GET;

        String path = "../root";
        path += first_line[1];
        File f = new File(path);
        System.out.println("path: " + path);
        if (!f.exists()) {
            p.responseCode = StatusCode.NOT_FOUND_404;
            return p;
        }

        p.url = first_line[1];
        for (int i = 1; i < lines.length; i++) {
            String[] line = lines[i].split(": ");
            if (line.length != 2) {
                p.responseCode = StatusCode.BAD_REQUEST_400;
                return p;
            }
            p.addHeader(line[0], line[1]);
        }
        p.responseCode = StatusCode.OK_200;
        return p;
    }

}
