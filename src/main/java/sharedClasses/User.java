package sharedClasses;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
     @Serial
     private static final long serialVersionUID = 6529685098267757690L;
     private boolean isSuccessful;
     private int user_id;
     private String name;
     private byte[] image;
     private String role;
     private String password;
     private List<String> actions;

    public User() {
        actions = new ArrayList<>();
        isSuccessful = false;
    }

    public User(String name, byte[] image, String role, String password, int user_id) {
        this.name = name;
        this.image = image;
        this.role = role;
        this.password = password;
        this.actions = new ArrayList<>();
        isSuccessful = false;
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setByteArraySize(int size)
    {
        image = new byte[size];
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<String> getActions() {
        return actions;
    }

    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public void setSuccessful(boolean successful) {
        isSuccessful = successful;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
