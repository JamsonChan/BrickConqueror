package com.mycompany.brickconqueror;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * JavaFX App
 */
public class App extends Application {

    public int grid = 10;                    // 每格10*10px
    public Node sceneSize = new Node(1280,720);//960, 540);   // 儲存視窗大小
    public Node padSize = new Node(20 * grid, 2 * grid);      // 儲存板子大小
    public Node brickSize = new Node(4 * grid, 2 * grid);    // 儲存磚塊大小
    public Node padPos = null;      // 儲存板子位置
    public ImageView backgroundImage = new ImageView(); // 背景圖片顯示物件
    public BallNode ball = null;      // 球物件，儲存位置和方向
    public ArrayList<BrickNode> bricks = new ArrayList();      // 磚塊串列
    public ImageView padImage = null; // 板子圖片顯示物件
    public Circle ballCir = null;      // 顯示球的Circle物件
    public ArrayList<BallNode> balls =  new ArrayList(); // 技能球串列
    public int countBalls = 0;              // 計算技能球的數量
    public int funcSplitTime = -1;        // 技能Split計時器
    public int funcLongTime = -1;       // 技能Long計時器
    public int funcStrongTime = -1;    // 技能Strong計時器
    public boolean funcSplitIsActivate = false;     // 技能 Split 啟動與否
    public boolean funcLongIsActivate = false;    // 技能 Long 啟動與否
    public boolean funcStrongIsActivate = false; // 技能 Strong 啟動與否
    public String VictoryOrLose = "";   // 輸贏狀態
    public int life = 6;                           // 玩家生命值
    public Text lifeText = new Text();    // 生命值文字
    public MediaPlayer themePlayer = null; // 背景音樂撥放器
    public Group gp = null;
    public Scene scene = null;
    public Timer timer = null;
    public TimerTask task = null;

    @Override
    public void start(Stage stage) {
        createBricks(200); // 生成磚塊

        gp = new Group();
        scene = new Scene(gp, sceneSize.x, sceneSize.y);
        setBackGround();
        stage.setScene(scene);
        stage.setTitle("Brick Conqueror");
        stage.getIcons().add(new Image("file:src\\image\\icon.jpg"));
        stage.show();
        
        gameLoop(stage); // 遊戲主程式
    }
    public void gameLoop(Stage stage){
        timer = new Timer();
        playTheme("theme");
        task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() ->{
                    gp.getChildren().clear(); // 清空畫面
                    gp.getChildren().add(backgroundImage); // 畫上背景
                    serve(); // 發球
                    drawPad(); // 畫出板子
                    drawBricks(); // 畫出所有磚塊
                    checkLife(); // 確認血量剩多少
                    checkGameOver(); // 確認遊戲結束了沒
                    if (VictoryOrLose.equals("")) drawBall(); // 若沒有輸贏，就繼續畫出球的位置
                    else gameOver(stage); // 若有輸贏就進入到GameOver
                    checkBall(ball); // 確認球是否有碰到物件，若碰到就要反彈
                    checkFunc(); // 確認是否有觸發任何技能
                    checkBalls(); // 確認技能球(紅)是否有碰到物件，若碰到就要反彈
                    setLifeText(); // 設定生命值文字
                    windowClose(stage);  // 關閉視窗時關閉程式
                    gp.getChildren().add(lifeText); // 顯示生命值
                    gp.getChildren().add(padImage); // 畫出版子
                    gp.getChildren().add(ballCir); // 畫出球
                });
            }
        };
        timer.schedule(task, 0, 2); // 每2ms刷新一次畫面
    }
    public void checkLife(){
        if (ball == null) return;
        if (ball.y > sceneSize.y + grid){
            playSound("hurt");
            ball = null;
            life --;
        }
    }
    public void checkGameOver(){
        if (bricks.isEmpty()) VictoryOrLose = "victory";
        else if (life == 0){
            VictoryOrLose = "lose";
            ballCir.setCenterX(sceneSize.x/2);
            ballCir.setCenterY(sceneSize.y/2);
        }
    }
    public void gameOver(Stage stage){
        if (ballCir.getRadius() < sceneSize.x) {
            if (VictoryOrLose.equals("victory")) ballCir.setFill(Color.rgb(100, 149, 237));
            else ballCir.setFill(Color.BLACK);
            ballCir.setRadius(ballCir.getRadius() + 3);
            if (ball != null){
                ball.x = 0;
                ball.y = 0;
            }
        }else{
            themePlayer.stop();
            playTheme(VictoryOrLose);
            timer.cancel();
            timer.purge();
            task.cancel();
            GameOver go = new GameOver(gp, scene, sceneSize, VictoryOrLose);
            go.start(stage);
        }
    }
    public void createBricks(int num) {
        boolean pass = true;
        while (bricks.size() != num){
            int x = (int) (Math.random() * sceneSize.x / brickSize.x) * brickSize.x;
            int y = (int) ((int) (Math.random() * sceneSize.y * 0.6 / brickSize.y) * brickSize.y);
            for (int i = 0; i < bricks.size(); i += 1){
                if ((int) bricks.get(i).x == x && (int) bricks.get(i).y == y) pass = false;
            }
            if (pass){
                BrickNode bricknode = new BrickNode(x, y);
                bricks.add(bricknode);
            }
            pass = true;
        }
    }
    public void setBackGround(){
        backgroundImage.setImage(new Image("file:src\\image\\background.jpg"));
        backgroundImage.setFitWidth(sceneSize.x);
        backgroundImage.setFitHeight(sceneSize.y);
        gp.getChildren().add(backgroundImage);
    }
    public void serve(){
        scene.setOnMousePressed(new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            if (ball == null && !event.isMiddleButtonDown()){
                if (padPos.y < sceneSize.y*0.8) padPos.y = (int)(sceneSize.y*0.8);
                ball = new BallNode(padPos.x, padPos.y - grid);
                if (event.isPrimaryButtonDown()) ball.dir = "ul";
                else if (event.isSecondaryButtonDown()) ball.dir = "ur";
            }
        }
    });
}
    public void drawPad(){
        scene.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                int x = (int)event.getX();
                int y = (int)event.getY();
                if (y < sceneSize.y*0.8) y = (int)(sceneSize.y*0.8);
                padPos = new Node(x, y);
                padImage = new ImageView("file:src\\image\\pad.jpg");
                padImage.setX(x-(padSize.x/2));
                padImage.setY(y-(padSize.y/2));
                padImage.setFitWidth(padSize.x);
                padImage.setFitHeight(padSize.y);
            }
        });}
    public void drawBricks(){
        for (int i = 0;i<bricks.size();i++){
            ImageView brickImage = new ImageView(bricks.get(i).image);
            brickImage.setX(bricks.get(i).x);
            brickImage.setY(bricks.get(i).y);
            brickImage.setFitWidth(brickSize.x);
            brickImage.setFitHeight(brickSize.y);
            gp.getChildren().add(brickImage);
        }
    }
    public void drawBall(){
        if (ball == null) return;
        ballCir = new Circle();
        ballCir.setCenterX(ball.x);
        ballCir.setCenterY(ball.y);
        ballCir.setRadius(grid);
        if (funcStrongIsActivate) ballCir.setFill(Color.rgb((int)(Math.random() * 255),(int)(Math.random() * 255),(int)(Math.random() * 255)));
        else ballCir.setFill(Color.WHITE);
    }
    public void checkBall(BallNode ball){
        if (ball == null) return;
        ball.move();
        checkTouchEdge(ball);
        checkTouchPad(ball);
        checkTouchBrick(ball);
    }
    public void checkFunc(){
        if (funcSplitIsActivate) funcSplit();
        if (funcLongIsActivate) funcLong();
        if (funcStrongIsActivate) funcStrong();
    }
    public void checkBalls(){
        if (!balls.isEmpty()){
            for (int i = 0; i< balls.size();i++){
                checkBall(balls.get(i));
            }
            drawSplitBalls();
        }
    }
    public void checkTouchEdge(BallNode ball) {
        int rightlimit = sceneSize.x - grid;
        int lowerlimit = sceneSize.y - grid;
        if (ball.x == 0 || ball.x == rightlimit || ball.y == 0 || ball.y == lowerlimit){
            playSound("touch_edge");
            if (ball.dir.equals("ur")){
                if (ball.x == rightlimit && ball.y == 0)ball.dir = "ll";
                else if (ball.x == rightlimit) ball.dir = "ul";
                else ball.dir = "lr";
            } else if (ball.dir.equals("ul")){
                if (ball.x == 0 && ball.y == 0) ball.dir = "lr";
                else if (ball.x == 0) ball.dir = "ur";
                else ball.dir = "ll";
            } else if (ball.dir.equals("lr")){
                if (ball.x == rightlimit && ball.y == lowerlimit)ball.dir = "ul";
                else if (ball.x == rightlimit) ball.dir = "ll";
            } else if (ball.dir.equals("ll")){
                if (ball.x == 0 && ball.y == lowerlimit) ball.dir = "ur";
                else if (ball.x == 0) ball.dir = "lr";
            }
        }
    }
    public void checkTouchPad(BallNode ball){
        if ((padPos.x - padSize.x/2<= ball.x && ball.x <= padPos.x + padSize.x/2) && (padPos.y - padSize.y/2 <= ball.y && ball.y <= padPos.y + padSize.y/2)){
            if (ball.dir.equals("ll")) ball.dir = "ul";
            else if (ball.dir.equals("lr")) ball.dir = "ur";
        }
    }
    public void checkTouchBrick(BallNode ball) {
        for (int i = 0; i < bricks.size(); i += 1){
            BrickNode b = bricks.get(i);
            Node bs = brickSize;
            if ((b.x <= ball.x && ball.x <= b.x + bs.x) && (b.y <= ball.y && ball.y <= b.y + bs.y)){
                if (!funcStrongIsActivate){
                    if (ball.dir.equals("ur")){
                        if (ball.x == b.x && ball.y == b.y + bs.y)ball.dir = "ll";
                        else if (ball.x == b.x)ball.dir = "ul";
                        else ball.dir = "lr";
                    } else if (ball.dir.equals("ul")){
                        if (ball.x == b.x + bs.x && ball.y == b.y + bs.y) ball.dir = "lr";
                        else if (ball.x == b.x + bs.x) ball.dir = "ur";
                        else ball.dir = "ll";
                    } else if (ball.dir.equals("lr")){
                        if (ball.x == b.x && ball.y == b.y) ball.dir = "ul";
                        else if (ball.x == b.x) ball.dir = "ll";
                        else ball.dir = "ur";
                    } else if (ball.dir.equals("ll")){
                        if (ball.x == b.x + bs.x && ball.y == b.y)ball.dir = "ur";
                        else if (ball.x == b.x + bs.x)ball.dir = "lr";
                        else ball.dir = "ul";
                    }
                }
                if (b.hardness > 0){
                    b.hardness -= 1;
                    b.setImage();
                    playSound("touch_brick");
                }
                else {
                    activateFunc(b);
                    bricks.remove(i);
                    playSound("break_brick");
                }
                return;
            }
        }
    }
    public void drawSplitBalls(){
        if (balls.isEmpty()) return;
        for (int i=0;i<balls.size();i++){
            if (balls.get(i) != null){
                Circle splitBallsCir = new Circle();
                splitBallsCir.setCenterX(balls.get(i).x);
                splitBallsCir.setCenterY(balls.get(i).y);
                splitBallsCir.setRadius(grid);
                splitBallsCir.setFill(Color.BLACK);
                gp.getChildren().add(splitBallsCir);
            }
        }
    }
    public void activateFunc(BrickNode b){
        String func = b.func;
        if (func == null) return;
        else if (func.equals("split")){
            funcSplitIsActivate = true;
        } 
        else if (func.equals("long")){
            funcLongIsActivate = true;
            playSound("func_long");
        }
        else if (func.equals("strong")){
            funcStrongIsActivate = true;
            playSound("func_strong");
        }
    }
    public void funcSplit(){
        if (funcSplitTime == -1){
            funcSplitTime = (int)System.currentTimeMillis();
        }
        int curTime = (int)System.currentTimeMillis();
        int duration = (int)(curTime - funcSplitTime);
        if (countBalls<10){
            if (duration > 100 || balls.isEmpty()){
                funcSplitTime = (int)System.currentTimeMillis();
                BallNode newBall = new BallNode(padPos.x, padPos.y);
                if (countBalls%2 == 0) newBall.dir = "ur";
                else newBall.dir = "ul";
                balls.add(newBall);
                countBalls += 1;
            }
        }
        for (int i = 0; i<balls.size();i++){
            if (balls.get(i).y > sceneSize.y+grid) balls.remove(i);
            }
        if (balls.isEmpty()){
            funcLongTime = -1;
            balls = new ArrayList();
            countBalls = 0;
            funcSplitIsActivate = false;
        }
    }
    public void funcLong(){
        if (funcLongTime == -1){
            funcLongTime = (int)System.currentTimeMillis();
            padSize.x = 60*grid;
        }
        int curTime = (int)System.currentTimeMillis();
        int duration = (int)(curTime - funcLongTime)/1000;
        if (duration > 10){
            funcLongTime = -1;
            padSize.x = 20*grid;
            funcLongIsActivate = false;
        }
    }
    public void funcStrong(){
        if (funcStrongTime == -1){
            funcStrongTime = (int)System.currentTimeMillis();
        }
        int curTime = (int)System.currentTimeMillis();
        int duration = (int)(curTime - funcStrongTime)/1000;
        if (duration > 3){
            funcStrongTime = -1;
            funcStrongIsActivate = false;
        }
    }
    public void setLifeText(){
        String t = "";
        for (int i=1;i<life+1;i++){
            t += "❤";
            if (i%3==0) t += "\n";
            else t += " ";
        }
        lifeText.setText(t);
        lifeText.setX(sceneSize.x*0.885);
        lifeText.setY(sceneSize.y*0.933);
        lifeText.setFill(Color.RED);
        lifeText.setStyle("-fx-font: "+String.valueOf(sceneSize.x*0.02708)+" arial;");
    }
    public void playTheme(String name){
        Media sound = new Media(new File("src\\sound\\" + name + ".mp3").toURI().toString());
        themePlayer = new MediaPlayer(sound);
        themePlayer.play();
    }
    public void playSound(String name){
        Media sound = new Media(new File("src\\sound\\" + name + ".mp3").toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }
    public void windowClose(Stage stage){
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }
    public static void main(String[] args) {
        launch();
    }
}