package org.socket_handeler;

import org.dboperation.UserDbControl;
import org.entities.AllUserAndRollEntity;
import sharedClasses.LoginReq;
import sharedClasses.User;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Operation_handler {

    private UserDbControl userDbControl;
    private final String server_image_directory = "H:/sever_image/";

    public Operation_handler(Socket socket, UserDbControl userDbControl) {
        this.userDbControl = userDbControl;
        necessaryActionTaker(socket);
    }

    private void necessaryActionTaker(Socket socket) //User role checker function
    {
        ClientHandlerGeneral clientHandlerGeneral = new ClientHandlerGeneral(socket);

        String role = null;

        while(role == null)
        {
            role = credentialChecker(userDbControl, clientHandlerGeneral); //Checking for credentials

            if(role != null)
            {
                if(role.equals("Admin"))
                {
                    adminControls(userDbControl, clientHandlerGeneral);
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
                role =  credentialChecker(userDbControl, clientHandlerGeneral);
            }
        }
    }

    private void manufacturerControls(Socket socket)
    {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            List<String> clientResponse = (List<String>) objectInputStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void viewerControls(Socket socket)
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

    private void adminControls(UserDbControl userDbControl, ClientHandlerGeneral clientHandlerGeneral) //This is on different thread for every connection
    {
        AllUserAndRollEntity allUserAndRollEntity = new AllUserAndRollEntity();
        String admin_req_from_client;

        try {

            ObjectInputStream objectInputStream = clientHandlerGeneral.getObjectInputStream();
            admin_req_from_client = (String) objectInputStream.readUnshared();

            switch (admin_req_from_client) {

                case "1" -> {
                    ObjectInputStream objectInputStream1 = clientHandlerGeneral.getObjectInputStream();
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

                        ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                        String response = "success";
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();

                        notify_client_about_updated_user_list(userDbControl);

                        adminControls(userDbControl, clientHandlerGeneral);

                    } else {
                        ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                        String response = "failed";
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();

                        adminControls(userDbControl, clientHandlerGeneral);
                    }
                }

                case "2" -> {

                    List<User> userList = new ArrayList<>(userDbControl.getAllUser());
                    ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                    objectOutputStream.writeObject(userList);
                    objectOutputStream.flush();

                    ObjectInputStream objectInputStream1 = clientHandlerGeneral.getObjectInputStream();

                    try {
                        User user = (User) objectInputStream1.readObject();
                        AllUserAndRollEntity allUserAndRollEntity1 = new AllUserAndRollEntity();
                        allUserAndRollEntity1.setRole(user.getRole());
                        allUserAndRollEntity1.setName(user.getName());
                        allUserAndRollEntity1.setPassword(user.getPassword());
                        allUserAndRollEntity1.setId(user.getUser_id());
                        userDbControl.updateUser(allUserAndRollEntity1);
                        notify_client_about_updated_user_list(userDbControl);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    adminControls(userDbControl, clientHandlerGeneral);//Going to main menu again

                }
                case "3" -> {

                    List<User> userList = new ArrayList<>(userDbControl.getAllUser());
                    ObjectOutputStream objectOutputStream = clientHandlerGeneral.getObjectOutputStream();
                    objectOutputStream.writeObject(userList);
                    objectOutputStream.flush();

                    ObjectInputStream objectInputStream1 = clientHandlerGeneral.getObjectInputStream();

                    try {
                        User user = (User) objectInputStream1.readObject();
                        AllUserAndRollEntity allUserAndRollEntity1 = new AllUserAndRollEntity();
                        allUserAndRollEntity1.setId(user.getUser_id());
                        userDbControl.removeUser(allUserAndRollEntity1);

                        notify_client_about_updated_user_list(userDbControl);

                    } catch (IOException | ClassNotFoundException e) {
                        //t.cancel();
                        e.printStackTrace();
                    }

                    adminControls(userDbControl, clientHandlerGeneral);//Going to main menu again

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String  credentialChecker(UserDbControl userDbControl, ClientHandlerGeneral socket)
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

    private void notify_client_about_updated_user_list(UserDbControl userDbControl)
    {
        List<User> userList = new ArrayList<>(userDbControl.getAllUser()); //Getting the updated list of client

        int size = ServerSocketHandler.clientHandlerUserListUpdateArrayList.size();
        System.out.println(String.valueOf(size));

        for(int i=0; i<size; i++)
        {
            try
            {
                ServerSocketHandler.clientHandlerUserListUpdateArrayList.get(i).getObjectOutputStream().writeObject(userList);
                ServerSocketHandler.clientHandlerUserListUpdateArrayList.get(i).getObjectOutputStream().flush();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

}
