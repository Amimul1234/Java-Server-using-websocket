package org.socket_handeler;

import org.dboperation.UserDbControl;
import org.entities.AllUserAndRollEntity;
import sharedClasses.LoginReq;
import sharedClasses.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerSocketHandler {

    private static ServerSocketHandler serverSocketHandler = null;
    private final UserDbControl userDbControl;
    private static final String server_image_directory = "H:/sever_image/";
    private static List<ClientHandlerUserListUpdate> clientHandlerUserListUpdateArrayList = new ArrayList<>();

    private ServerSocketHandler()
    {

        userDbControl = UserDbControl.getInstance();

        try {

            ServerSocket serverSocket = new ServerSocket(50000); //This is for normal operation handle
            ServerSocket serverSocket1 = new ServerSocket(45555); //This port for updating user list
            ServerSocket serverSocket2 = new ServerSocket(58555); //This port for updating car list

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true)
                    {
                        Socket socket = null;
                        try {
                            Socket socket1 = serverSocket1.accept();
                            ClientHandlerUserListUpdate clientHandlerUserListUpdate = new ClientHandlerUserListUpdate(socket1);
                            clientHandlerUserListUpdateArrayList.add(clientHandlerUserListUpdate);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();


            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true)
                    {
                        Socket socket = null;
                        try {
                            Socket socket2 = serverSocket2.accept();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            while (true) //First one can run on this thread gracefully
            {
                Socket socket = null;
                try {
                    socket = serverSocket.accept();
                    necessaryActionTaker(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ServerSocketHandler getInstance()
    {
        if(serverSocketHandler == null)
            serverSocketHandler = new ServerSocketHandler();

        return serverSocketHandler;
    }

    private void necessaryActionTaker(Socket socket)
    {
        new Thread()
        {
            @Override
            public void run() {

                try {

                    ClientHandlerNormal clientHandlerNormal = new ClientHandlerNormal(socket);

                    String role = null;

                    while(role == null)
                    {
                        role = credentialChecker(userDbControl, clientHandlerNormal); //Checking for credentials

                        if(role != null)
                        {
                            if(role.equals("Admin"))
                            {
                                adminControls(userDbControl, clientHandlerNormal);
                            }
                            else if(role.equals("Viewer"))
                            {
                                viewerControls(socket); //Viewer controls
                            }
                            else
                            {
                                manufacturerControls(socket); //manufacturer controls
                            }
                        }

                        else
                        {
                            role =  credentialChecker(userDbControl, clientHandlerNormal);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
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

    private static void viewerControls(Socket socket)
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

    private static void adminControls(UserDbControl userDbControl, ClientHandlerNormal clientHandlerNormal) //This is on different thread for every connection
    {
        AllUserAndRollEntity allUserAndRollEntity = new AllUserAndRollEntity();
        String admin_req_from_client;

        try {

            ObjectInputStream objectInputStream = clientHandlerNormal.getObjectInputStream();
            admin_req_from_client = (String) objectInputStream.readUnshared();

            switch (admin_req_from_client) {

                case "1" -> {
                    ObjectInputStream objectInputStream1 = clientHandlerNormal.getObjectInputStream();
                    User user = (User) objectInputStream1.readObject();

                    String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                    String directory = server_image_directory + timeStamp + ".jpg"; //Giving unique name by using timestamp

                    FileOutputStream fileOutputStream = new FileOutputStream(directory);
                    fileOutputStream.write(user.getImage(), 0, user.getImage().length);
                    fileOutputStream.close();

                    allUserAndRollEntity.setName(user.getName());
                    allUserAndRollEntity.setRole(user.getRole());
                    allUserAndRollEntity.setImage(directory);
                    allUserAndRollEntity.setPassword(user.getPassword());

                    if (userDbControl.createNewUser(allUserAndRollEntity).equals("success")) {

                        ObjectOutputStream objectOutputStream = clientHandlerNormal.getObjectOutputStream();
                        String response = "success";
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();

                        notify_client_about_updated_user_list(userDbControl);

                        adminControls(userDbControl, clientHandlerNormal);

                    } else {
                        ObjectOutputStream objectOutputStream = clientHandlerNormal.getObjectOutputStream();
                        String response = "failed";
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();

                        adminControls(userDbControl, clientHandlerNormal);
                    }
                }

                case "2" -> {

                    List<User> userList = new ArrayList<>(userDbControl.getAllUser());
                    ObjectOutputStream objectOutputStream = clientHandlerNormal.getObjectOutputStream();
                    objectOutputStream.writeObject(userList);
                    objectOutputStream.flush();

                    ObjectInputStream objectInputStream1 = clientHandlerNormal.getObjectInputStream();
                    try {
                        User user = (User) objectInputStream1.readObject();
                        AllUserAndRollEntity allUserAndRollEntity1 = new AllUserAndRollEntity();
                        allUserAndRollEntity1.setRole(user.getRole());
                        allUserAndRollEntity1.setName(user.getName());
                        allUserAndRollEntity1.setPassword(user.getPassword());
                        allUserAndRollEntity1.setId(user.getUser_id());
                        userDbControl.updateUser(allUserAndRollEntity1);
                    } catch (IOException | ClassNotFoundException e) {
                        //t.cancel();
                        e.printStackTrace();
                    }

                    adminControls(userDbControl, clientHandlerNormal);//Going to main menu again

                }
                case "3" -> {

                    List<User> userList = new ArrayList<>(userDbControl.getAllUser());
                    ObjectOutputStream objectOutputStream = clientHandlerNormal.getObjectOutputStream();
                    objectOutputStream.writeObject(userList);
                    objectOutputStream.flush();

                    ObjectInputStream objectInputStream1 = clientHandlerNormal.getObjectInputStream();
                    try {
                        User user = (User) objectInputStream1.readObject();
                        AllUserAndRollEntity allUserAndRollEntity1 = new AllUserAndRollEntity();
                        allUserAndRollEntity1.setId(user.getUser_id());
                        userDbControl.removeUser(allUserAndRollEntity1);
                    } catch (IOException | ClassNotFoundException e) {
                        //t.cancel();
                        e.printStackTrace();
                    }

                    adminControls(userDbControl, clientHandlerNormal);//Going to main menu again

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void notify_client_about_updated_user_list(UserDbControl userDbControl)
    {
        List<User> userList = new ArrayList<>(userDbControl.getAllUser()); //Getting the updated list of client
        List<ClientHandlerUserListUpdate> auxList = new ArrayList<>();

        auxList.addAll(clientHandlerUserListUpdateArrayList);

        int size = clientHandlerUserListUpdateArrayList.size();

        for(int i=0; i<size; i++)
        {
            try
            {
                clientHandlerUserListUpdateArrayList.get(i).getObjectOutputStream().writeObject(userList);
                clientHandlerUserListUpdateArrayList.get(i).getObjectOutputStream().flush();
            }catch (Exception e)
            {
                auxList.remove(i);
            }
        }

        clientHandlerUserListUpdateArrayList.clear();
        clientHandlerUserListUpdateArrayList.addAll(auxList);
    }

    private static String  credentialChecker(UserDbControl userDbControl, ClientHandlerNormal socket)
    {
        try {
            ObjectInputStream objectInputStream = socket.getObjectInputStream();
            LoginReq loginReq = (LoginReq) objectInputStream.readObject();//Getting loginReq object from user

            User user = userDbControl.userLoginChecker(loginReq);

            ObjectOutputStream objectOutputStream = socket.getObjectOutputStream();
            objectOutputStream.writeObject(user);
            objectOutputStream.flush();//Writing response to the user

            if (user.isSuccessful())
                return user.getRole();
            else
                return null;

        } catch (Exception e) {
            e.printStackTrace();
            return "connection reset";
        }
    }
}

