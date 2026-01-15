package bootstrap;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import config.AppConfig;
import config.DatabaseConfig;
import config.FilterConfig;
import config.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.dispatch.ConnectionHandler;

public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;
    private static final AppConfig LOADER = new AppConfig();
    private static final SecurityConfig securityConfig = new SecurityConfig();
    private static final FilterConfig filterConfig = new FilterConfig();
    private static final DatabaseConfig databaseConfig = new DatabaseConfig();
    private static final ExecutorService executor = Executors.newFixedThreadPool(32);

    public static void main(String args[]) throws Exception {
        int port = 0;
        if (args == null || args.length == 0) {
            port = DEFAULT_PORT;
        } else {
            port = Integer.parseInt(args[0]);
        }
        config();

        // 서버소켓을 생성한다. 웹서버는 기본적으로 8080번 포트를 사용한다.
        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            // 클라이언트가 연결될때까지 대기한다.
            Socket connection;
            while ((connection = listenSocket.accept()) != null) {
                Socket singleConnection = connection;
                executor.submit(() -> {
                    ConnectionHandler connectionHandler = new ConnectionHandler(
                            LOADER.filterChainContainer(),
                            LOADER.exceptionHandlerMapping(),
                            LOADER.httpResponseConverter(),
                            LOADER.httpRequestConverter(),
                            singleConnection);
                    connectionHandler.run();
                });
            }
        }
    }

    private static void config(){
        securityConfig.config();
        filterConfig.config();
        databaseConfig.config();
    }
}
