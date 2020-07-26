public class LogClass {

    public void print_HTTPRequest(String utc_datetime, String clientPort, String hostPort, String reqLine){
        System.out.println("Request: " + "[" + utc_datetime + "] " + "[" + clientPort + "] " + "[" + hostPort+ "] " +
                reqLine);
    }

    public void print_HTTPResponse(String utc_datetime, String clientPort, String hostPort, String reqLine, String statusLine){
        System.out.println("Request: " + "[" + utc_datetime + "] " + "[" + clientPort + "] " + "[" + hostPort+ "] " +
             statusLine + " for " + reqLine );
    }


    public void print_for_httpRequest(String utc_datetime, String request_line, String response_status_code){
        System.out.println("[" + utc_datetime + "] " + request_line + " " + response_status_code );
    }


}
