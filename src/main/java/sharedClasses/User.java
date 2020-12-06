package sharedClasses;

public class User {
    private String name;
    private byte[] image = new byte[20000];
    private String role;
    private String password;

    public User() {
    }

    public User(String name, byte[] image, String role, String password) {
        this.name = name;
        this.image = image;
        this.role = role;
        this.password = password;
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
}
