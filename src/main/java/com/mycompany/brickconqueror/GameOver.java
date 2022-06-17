package com.mycompany.brickconqueror;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GameOver extends Application {
    public Group gp;
    public Scene scene;
    public Node sceneSize;
    public String status;
    
    public GameOver(Group gp, Scene scene, Node sceneSize, String status){
        this.gp = gp;
        this.scene = scene;
        this.sceneSize = sceneSize;
        this.status = status;
    }
    @Override
    public void start(Stage stage) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() ->{
                    ImageView backgroundImage = new ImageView();
                    backgroundImage.setImage(new Image("file:src\\image\\" + status + ".gif"));
                    backgroundImage.setFitWidth(sceneSize.x);
                    backgroundImage.setFitHeight(sceneSize.y);
                    gp.getChildren().add(backgroundImage);
                });
            }
        };
        timer.schedule(task, 0, 10000);
    }
    public static void main(String[] args) {
        launch();
    }
}
