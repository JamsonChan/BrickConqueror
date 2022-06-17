package com.mycompany.brickconqueror;

import java.util.Arrays;
import java.util.List;
import javafx.scene.image.Image;

class BrickNode extends Node {
    public int hardness;
    public String func;
    public Image image;
    public BrickNode(int x, int y) {
        super(x, y);
        hardness = 2;
        func = this.setFunc();
        setImage();
    }
    private String setFunc(){
        if (Math.random()<0.1){
            List<String> funcs = Arrays.asList("split", "long", "strong");
            return funcs.get((int)(Math.random()*3));
        }else return null;
    }
    public void setImage(){
        String image_name = "";
        if (func != null){
            if (hardness == 0) image_name = "qbox1.jpg";
            else if (hardness == 1) image_name = "qbox2.jpg";
            else if (hardness == 2) image_name = "qbox3.jpg";
        }else{
            if (hardness == 0) image_name = "brick1.jpg";
            else if (hardness == 1) image_name = "brick2.jpg";
            else if (hardness == 2) image_name = "brick3.jpg";
        }
        image = new Image("file:src\\image\\" + image_name);
    }
//    public void switchImage(){
//        if (func != null){
//            if (image_name.equals("qbox1.jpg")) image_name = "qbox11.jpg";
//            else if (image_name.equals("qbox2.jpg")) image_name = "qbox22.jpg";
//            else if (image_name.equals("qbox3.jpg")) image_name = "qbox33.jpg";
//            else if (image_name.equals("qbox11.jpg")) image_name = "qbox1.jpg";
//            else if (image_name.equals("qbox22.jpg")) image_name = "qbox2.jpg";
//            else if (image_name.equals("qbox33.jpg")) image_name = "qbox3.jpg";
//        }image = new Image("file:src\\image\\" + image_name + ".jpg");
//    }
}

