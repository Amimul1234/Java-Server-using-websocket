package org.socket_handeler;

import org.dboperation.UserDbControl;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ServerSocketHandler {

    private static UserDbControl userDbControl = null;
    private static ServerSocketHandler serverSocketHandler = null;
    public static List<ClientHandlerUserListUpdate> clientHandlerUserListUpdateArrayList = new ArrayList<>();

    private ServerSocketHandler()
    {
        userDbControl = UserDbControl.getInstance();

        try {

            ServerSocket serverSocket = new ServerSocket(50000); //This is for normal operation handle
            ServerSocket serverSocket1 = new ServerSocket(45555); //This port for updating user list
            ServerSocket serverSocket2 = new ServerSocket(58555); //This port for updating car list

            new Thread(new Runnable() { //Auxiliary port for user update
                @Override
                public void run() {
                    while(true)
                    {
                        try {

                            Socket socket = serverSocket1.accept();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ClientHandlerUserListUpdate clientHandlerUserListUpdate = new ClientHandlerUserListUpdate(socket);
                                    clientHandlerUserListUpdateArrayList.add(clientHandlerUserListUpdate);
                                }
                            }).start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            new Thread(new Runnable() { //Auxiliary port for car update
                @Override
                public void run() {
                    while(true)
                    {
                        try {
                            Socket socket2 = serverSocket2.accept();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            }).start();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            while (true) //Main port for communication
            {
                try {
                    Socket socket = serverSocket.accept();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            new Operation_handler(socket, userDbControl);
                        }
                    }).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void getInstance() //Getting singleton class for application layer
    {
        if(serverSocketHandler == null)
            serverSocketHandler = new ServerSocketHandler();
    }
}

