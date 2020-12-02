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

                                String role = credentialChecker(userDbControl, inputStream, outputStream); //Checking for credentials

                                if(role != null)
                                {
                                    if(role.equals("Admin"))
                                    {
                                        adminControls(); //Controls of admin
                                    }
                                    else if(role.equals("Viewer"))
                                    {
                                        viewControls(); //Viewer controls
                                    }
                                    else
                                    {
                                        manufacturerControls(); //manufacturer controls
                                    }
                                }
                                else
                                {
                                    credentialChecker(userDbControl, inputStream, outputStream);
                                }

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

    private static void manufacturerControls()
    {

    }

    private static void viewControls()
    {

    }

    private static void adminControls() {
    }

    private static String  credentialChecker(UserDbControl userDbControl, InputStream inputStream, OutputStream outputStream)
    {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

            List<String> credential  = (List<String>) objectInputStream.readObject();

            List<String> response = userDbControl.findUser(credential.get(0), credential.get(1));

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

            if(response.get(0).equals("false"))
            {
                if(response.get(3).equals("Admin"))
                {
                    response.add("Create a new user");
                    response.add("Update existence user");
                    response.add("Remove existence user");
                }
                else if(response.get(3).equals("Viewer"))
                {
                    response.add("View all cars");
                    response.add("Search car by registration number");
                    response.add("Search car by make and model");
                    response.add("Buy a car");
                }
                else if(response.get(3).equals("Manufacturer"))
                {
                    response.add("View all cars");
                    response.add("Add a car");
                    response.add("Edit a car");
                    response.add("Delete a car");
                }
            }

            objectOutputStream.writeObject(response);
            objectOutputStream.flush();

            if (response.get(0).equals("false"))
                return response.get(3);
            else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
