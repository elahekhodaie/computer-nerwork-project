import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.sql.Time;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;



public class ServerRequestHandler implements Runnable {
    Constants constants = new Constants();
    int time_to_wait = constants.TIME_TO_WAIT;



    public ServerRequestHandler(Socket server, String url , HashMap<String, String> headers){


        // handling the Connection header part
        String connection_state = headers.get("Connection");

        if (!headers.containsKey("Connection")){
            // the default if we don't have a connection header is close
            try{
                //TODO return file
                server.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }

        }
        if (connection_state.equals("close")){
            try{
                //TODO return file
                server.close();
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
        else if (connection_state.equals("keep-alive")){
            //if the keep alive doesn't exist in the headers
            if(!headers.containsKey("Keep-alive")){

                try{
                    TimeUnit.SECONDS.sleep(constants.TIME_TO_WAIT);
                    server.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else {
                String time_to_keep_alive = headers.get("Keep-alive");
                time_to_wait = Integer.parseInt(time_to_keep_alive);
                try{
                    TimeUnit.SECONDS.sleep(time_to_wait);
                    server.close();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        //check the encoding header for gzip format

        String encoding_value = headers.get("Accept-Encoding");
        if (encoding_value.equals("gzip")){
            try{
                String source_filepath = url;
                String destination_zip_file = "/Users/ellykhodaie/Desktop/CN_project/test.gzip";
                byte [] buffer = new byte[1024];
                FileOutputStream fileOutputStream = new FileOutputStream(destination_zip_file);
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
                FileInputStream fileInput = new FileInputStream(source_filepath);
                int bytes_read;
                while ((bytes_read = fileInput.read(buffer)) > 0) {
                    gzipOutputStream.write(buffer, 0, bytes_read);
                    fileInput.close();

                    gzipOutputStream .finish();
                    gzipOutputStream .close();
                    System.out.println("The file was compressed successfully!");
                }
                //TODO check if this works

            }catch (Exception e){
                e.printStackTrace();
            }

        }







    }


    @Override
    public void run() {

    }
}
