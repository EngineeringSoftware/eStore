package org.estore;

import java.io.*;
import java.io.BufferedReader;
import java.net.*;
import org.estore.planner.util.Table;

public class Main {

    public static void main(String[] args) {
        boolean exit = false;
        int port = 1234;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (Exception e) {
                System.err.println("Invalid argument");
                System.exit(1);
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            Estore estore = new Estore("testDb");
            while (!exit) {
                try (Socket clientSocket = serverSocket.accept();
                        BufferedReader in =
                                new BufferedReader(
                                        new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String query;
                    while ((query = in.readLine()) != null) {
                        if (query.equals("q")) {
                            exit = true;
                            out.println("Exited");
                            break;
                        }
                        Table result = estore.query(query);
                        if (result != null) {
                            // print result on both server and client
                            // result.print();
                            out.println(result);
                        } else {
                            out.println("Result is null");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
