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

    public void print_error_message(StatusCode statusCode){
        if (statusCode == StatusCode.BAD_REQUEST_400)
            System.out.println("Simple HTML message describing that the request is not accepted !");
        else if (statusCode == StatusCode.METHOD_NOT_ALLOWED_405)
            System.out.println("Simple HTML message describing that the method is not supported !");
        else if (statusCode == StatusCode.NOT_IMPLEMENTED_501)
            System.out.println("Simple HTML message describing that the method is not recognized !");
        else if (statusCode == StatusCode.NOT_FOUND_404)
            System.out.println("HTML message describing a file was not found !");
    }


}
