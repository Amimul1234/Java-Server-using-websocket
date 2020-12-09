package org.socket_handeler;

import org.DBOperation.UserDbControl;
import org.entities.AllUserAndRollEntity;
import sharedClasses.LoginReq;
import sharedClasses.User;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SocketHandler {

    private static SocketHandler socketHandler = null;
    private final UserDbControl userDbControl;
    private static final String server_image_directory = "H:/sever_image/";

    private SocketHandler()
    {
        userDbControl = UserDbControl.getInstance();
        try {
            ServerSocket serverSocket = new ServerSocket(50000);

            while(true)
            {
                Socket socket = serverSocket.accept();
                necessaryActionTaker(socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SocketHandler getInstance()
    {
        if(socketHandler == null)
            socketHandler = new SocketHandler();

        return socketHandler;
    }

    private void necessaryActionTaker(Socket socket)
    {
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
                                viewerControls(socket); //Viewer controls
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

    private static void adminControls(UserDbControl userDbControl, Socket socket)
    {
        AllUserAndRollEntity allUserAndRollEntity = new AllUserAndRollEntity();
        String admin_req_from_client;

        try {

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

            admin_req_from_client = (String) objectInputStream.readObject();

            switch (admin_req_from_client) {

                case "1" -> {
                    ObjectInputStream objectInputStream1 = new ObjectInputStream(socket.getInputStream());
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

                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        String response = "success";
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();

                    } else {
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        String response = "failed";
                        objectOutputStream.writeObject(response);
                        objectOutputStream.flush();
                    }
                }
                case "2" -> {
                    /*
                    allUserAndRollEntity.setName(clientResponse.get(1));
                    allUserAndRollEntity.setImage(clientResponse.get(2));
                    allUserAndRollEntity.setRole(clientResponse.get(3));
                    allUserAndRollEntity.setPassword(clientResponse.get(4));
                    allUserAndRollEntity.setId(Integer.parseInt(clientResponse.get(5)));
                    userDbControl.updateUser(allUserAndRollEntity);

                     */
                }
                case "3" -> {
                    /*
                    allUserAndRollEntity.setName(clientResponse.get(1));
                    allUserAndRollEntity.setImage(clientResponse.get(2));
                    allUserAndRollEntity.setRole(clientResponse.get(3));
                    allUserAndRollEntity.setPassword(clientResponse.get(4));
                    allUserAndRollEntity.setId(Integer.parseInt(clientResponse.get(5)));
                    userDbControl.removeUser(allUserAndRollEntity);

                     */
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String  credentialChecker(UserDbControl userDbControl, Socket socket)
    {
        try {

            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            LoginReq loginReq = (LoginReq) objectInputStream.readObject();//Getting loginReq object from user

            User user = userDbControl.userLoginChecker(loginReq);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
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
