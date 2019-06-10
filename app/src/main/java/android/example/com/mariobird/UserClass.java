package android.example.com.mariobird;

public class UserClass {

    private String name;
    private String id;
    private String image;
    private String score;

    public UserClass(String name, String id, String image, String score){
        this.name = name;
        this.id = id;
        this.image = image;
        this.score = score;
    }

    public UserClass(){

    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(String score) {
        this.score = score;
    }
}


