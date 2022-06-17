package com.mycompany.brickconqueror;

class BallNode extends Node { 
    public String dir;
    public BallNode(int x, int y) {
        super(x, y);
        dir = null;
    }
    public void move() {
        if (dir.equals("ur")){
            x += 1; y -= 1;
        } else if (this.dir.equals("ul")){
            x -= 1; y -= 1;
        } else if (this.dir.equals("lr")){
            x += 1; y += 1;
        } else if (this.dir.equals("ll")){
            x -= 1; y += 1;
        }
    }
}
