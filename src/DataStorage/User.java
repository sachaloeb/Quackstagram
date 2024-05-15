package src.DataStorage;
import java.util.ArrayList;
import java.util.List;

// Represents a user on Quackstagram
public class User {
    public static String currentUser;
    private String username;
    private String bio;
    private String hashedPassword;
    private int postsCount;
    private int followersCount;
    private int followingCount;
    private List<Picture> pictures;

    public User(String username, String bio, String hashedPassword) {
        this.username = username;
        this.bio = bio;
        this.hashedPassword = hashedPassword;
        this.pictures = new ArrayList<>();
        // Initialize counts to 0
        this.postsCount = 0;
        this.followersCount = 0;
        this.followingCount = 0;
    }

    public User(String username){
        this.username = username;
    }

    // Add a picture to the user's profile
    public void addPicture(Picture picture) {
        pictures.add(picture);
        postsCount++;
    }

    // Getter methods for user details
    public String getUsername() { return username; }
    public String getBio() { return bio; }
    public void setBio(String bio) {this.bio = bio; }
    public int getPostsCount() { return postsCount; }
    public int getFollowersCount() { return followersCount; }
    public int getFollowingCount() { return followingCount; }
    public List<Picture> getPictures() { return pictures; }
    public static String getCurrentUserName(){ return currentUser; }

    // Setter methods for followers and following counts
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }
    public void setPostCount(int postCount) { this.postsCount = postCount;}
    public static void setCurrentUserName(String username){ currentUser = username; }
    // Implement the toString method for saving user information

@Override
public String toString() {
    return username + ":" + bio + ":" + hashedPassword; // Format as needed
}

}