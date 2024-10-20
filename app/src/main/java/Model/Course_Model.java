package Model;

import androidx.recyclerview.widget.RecyclerView;

public class Course_Model  {

    private String name;
    private int img;

    public Course_Model(String name, int img) {
        this.name = name;
        this.img = img;
    }
    //For Firebase Activities
    public Course_Model() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
