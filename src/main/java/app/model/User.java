package app.model;

public class User {
    private Long userId;
    private String password;
    private String nickname;
    private String email;

    public User(String password, String nickname, String email) {
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId){
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + nickname + ", email=" + email + "]";
    }
}
