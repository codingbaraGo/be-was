package http.request;

import exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class BufferedReaderHttpRequestConverter implements HttpRequestConverter {
    private static final Logger logger = LoggerFactory.getLogger(BufferedReaderHttpRequestConverter.class);
    public HttpRequest parseRequest(Socket connection){

        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));

            HttpRequest request = HttpRequest.from(bufferedReader.readLine());
            request.setRequestAddress(connection.getInetAddress());

            setHeaders(bufferedReader, request);

            //TODO: Body 파싱 추가

            return request;

        } catch (IOException e) {
            throw new ErrorException("HttpRequestParseError: IO Exception", e);
        }
    }

    private void setHeaders(BufferedReader bufferedReader, HttpRequest request) throws IOException {
        String line;
        while ((line = bufferedReader.readLine())!= null) {
            if(line.isEmpty()) break;
            int idx = line.indexOf(':');
            if(idx<0) throw new ErrorException("HttpRequestHeaderParseError");
            request.setHeader(line.substring(0, idx).strip(), line.substring(idx+1).strip());
        }
    }
}
