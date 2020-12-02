package org.example;

import org.example.DBOperationClass.UserDbControl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class App
{
    public static void main( String[] args )
    {
        UserDbControl userDbControl = UserDbControl.getInstance();

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(1242);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true)
        {
            try {
                Socket socket;
                if (serverSocket != null) {
                    socket = serverSocket.accept();

                    new Thread()
                    {
                        @Override
                        public void run() {
                            try {
                                InputStream inputStream = socket.getInputStream();
                                OutputStream outputStream = socket.getOutputStream();

                                credentialChecker(userDbControl, inputStream, outputStream); //Checking for role and credentials

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void credentialChecker(UserDbControl userDbControl, InputStream inputStream, OutputStream outputStream)
    {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            List<String> credential  = (List<String>) objectInputStream.readObject();

            List<String> response = userDbControl.findUser(credential.get(0), credential.get(1));

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
