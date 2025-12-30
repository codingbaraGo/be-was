package webserver.http.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class HttpBufferedReaderRequestConverter implements HttpRequestConverter {
    private static final Logger logger = LoggerFactory.getLogger(HttpBufferedReaderRequestConverter.class);
    public HttpRequest parseRequest(Socket connection){

        try (InputStream in = connection.getInputStream()) {
            InputStreamReader reader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String firstLine = bufferedReader.readLine();
            HttpRequest request = HttpRequest.from(firstLine);


            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine().strip();
                if(line.length() <= 1) break;
                int idx = line.indexOf(':');

                request.setHeader(line.substring(0, idx), line.substring(idx));
                logger.debug("New Header Added:{} - {}", line.substring(0, idx), line.substring(idx));
            }

            return request;

        } catch (IOException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            logger.error(String.valueOf(e.getClass()));
        }
        //TODO: Change return (to throw webserver.exception)
        return null;
    }
}
