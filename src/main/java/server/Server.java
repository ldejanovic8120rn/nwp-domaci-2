package server;

import framework.annotations.Controller;
import framework.engine.DIEngine;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class Server {

    public static final int TCP_PORT = 8080;

    public static void main(String[] args) throws IOException {

        try {
            DIEngine diEngine = new DIEngine();

            File dir = new File(System.getProperty("user.dir"));
            diEngine.loadAllClasses(Objects.requireNonNull(dir.listFiles()));

            for(Class<?> c: diEngine.getAllClasses()) {
                if(c.isAnnotationPresent(Controller.class)) {
                    System.out.println("Controller: " + c.getName());
                }
            }


            ServerSocket serverSocket = new ServerSocket(TCP_PORT);
            System.out.println("Server is running at http://localhost:" + TCP_PORT);
            while(true) {
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
