package org.example;

import org.example.DBOperationClass.UserDbControl;
import org.example.entities.AllUserAndRollEntity;
import sharedClasses.User;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class App
{

    private static final String server_image_directory = "H:/sever_image/";

    public static void main( String[] args )
    {
        UserDbControl userDbControl = UserDbControl.getInstance();

        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(50000);
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

                                String role = null;

                                while(role == null)
                                {
                                    role = credentialChecker(userDbControl, socket); //Checking for credentials

                                    if(role != null)
                                    {
                                        if(role.equals("Admin"))
                                        {
                                            adminControls(userDbControl, socket);
                                        }
                                        else if(role.equals("Viewer"))
                                        {
                                            viewControls(socket); //Viewer controls
                                        }
                                        else
                                        {
                                            manufacturerControls(socket); //manufacturer controls
                                        }
                                    }

                                    else
                                    {
                                        role =  credentialChecker(userDbControl, socket);
                                    }
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

    private static void manufacturerControls(Socket socket)
    {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            List<String> clientResponse = (List<String>) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void viewControls(Socket socket)
    {
        try
        {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            List<String> clientResponse = (List<String>) objectInputStream.readObject();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void adminControls(UserDbControl userDbControl, Socket socket)
    {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            List<String> clientResponse = (List<String>) objectInputStream.readObject();

            if(clientResponse.get(0).equals("1"))
            {
                ObjectInputStream objectInputStream1 = new ObjectInputStream(socket.getInputStream());

                User user = (User) objectInputStream1.readObject();

                String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

                String directory = server_image_directory + timeStamp + ".jpg"; //Giving unique name through timestamp

                FileOutputStream fileOutputStream = new FileOutputStream(directory);
                fileOutputStream.write(user.getImage(), 0, user.getImage().length);
                fileOutputStream.close();

                AllUserAndRollEntity allUserAndRollEntity = new AllUserAndRollEntity();

                allUserAndRollEntity.setName(user.getName());
                allUserAndRollEntity.setRole(user.getRole());
                allUserAndRollEntity.setImage(directory);
                allUserAndRollEntity.setPassword(user.getPassword());

                if(userDbControl.createNewUser(allUserAndRollEntity).equals("success"))
                {

                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    String response = "success";
                    objectOutputStream.writeObject(response);
                    objectOutputStream.flush();

                }else {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    String response = "failed";
                    objectOutputStream.writeObject(response);
                    objectOutputStream.flush();
                }

            }

            else if(clientResponse.get(0).equals("2"))
            {
                AllUserAndRollEntity allUserAndRollEntity = new AllUserAndRollEntity();
                allUserAndRollEntity.setName(clientResponse.get(1));
                allUserAndRollEntity.setImage(clientResponse.get(2));
                allUserAndRollEntity.setRole(clientResponse.get(3));
                allUserAndRollEntity.setPassword(clientResponse.get(4));
                allUserAndRollEntity.setId(Integer.parseInt(clientResponse.get(5)));

                userDbControl.updateUser(allUserAndRollEntity);
            }

            else if(clientResponse.get(0).equals("3"))
            {
                AllUserAndRollEntity allUserAndRollEntity = new AllUserAndRollEntity();

                allUserAndRollEntity.setName(clientResponse.get(1));
                allUserAndRollEntity.setImage(clientResponse.get(2));
                allUserAndRollEntity.setRole(clientResponse.get(3));
                allUserAndRollEntity.setPassword(clientResponse.get(4));

                allUserAndRollEntity.setId(Integer.parseInt(clientResponse.get(5)));

                userDbControl.removeUser(allUserAndRollEntity);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String  credentialChecker(UserDbControl userDbControl, Socket socket)
    {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            List<String> credential  = (List<String>) objectInputStream.readObject();

            List<String> response;

            if(credential.get(1).equals(""))
            {
                response = userDbControl.findUser(credential.get(0), null);
            }
            else
            {
                response = userDbControl.findUser(credential.get(0), credential.get(1));
            }


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

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            objectOutputStream.writeObject(response);
            objectOutputStream.flush();

            if (response.get(0).equals("false"))
                return response.get(3);
            else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            return "connection reset";
        }
    }
}
