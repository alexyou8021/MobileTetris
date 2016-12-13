package alexyou.tetrisplus;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Piece {
    Block[] blocks = new Block[4];
    int boxSize;
    int color;
    int xLoc;
    int yLoc;
    int numBlocks;
    int type;

    public Piece(int x, int y, int width) {
        boxSize = width/14;
        color = Color.argb(255,(int)(Math.random()*150+50),(int)(Math.random()*150+50),(int)(Math.random()*150+50));
        xLoc = x;
        yLoc = y;

        type = (int) Math.floor((Math.random()*5));
        switch(type) {
            case 0: //Box
                numBlocks=4;
                blocks[0] = new Block(x, y, width, color);
                blocks[1] = new Block(x+boxSize, y, width, color);
                blocks[2] = new Block(x, y+boxSize, width, color);
                blocks[3] = new Block(x+boxSize, y+boxSize, width, color);
                break;
            case 1: // Line
                numBlocks=4;
                blocks[0] = new Block(x, y, width, color);
                blocks[1] = new Block(x, y+boxSize, width, color);
                blocks[2] = new Block(x, y+(boxSize*2), width, color);
                blocks[3] = new Block(x, y+(boxSize*3), width, color);
                break;
            case 2: //L
                numBlocks=4;
                blocks[0] = new Block(x, y, width, color);
                blocks[1] = new Block(x, y+boxSize, width, color);
                blocks[2] = new Block(x, y+(boxSize*2), width, color);
                blocks[3] = new Block(x+boxSize, y, width, color);
                break;
            case 3://Z
                numBlocks=4;
                blocks[0] = new Block(x, y, width, color);
                blocks[1] = new Block(x-boxSize, y, width, color);
                blocks[2] = new Block(x, y+boxSize, width, color);
                blocks[3] = new Block(x+boxSize, y+boxSize, width, color);
                break;
            case 4://T
                numBlocks=4;
                blocks[0] = new Block(x, y, width, color);
                blocks[1] = new Block(x-boxSize, y, width, color);
                blocks[2] = new Block(x+boxSize, y, width, color);
                blocks[3] = new Block(x, y+boxSize, width, color);
                break;
            default:
                numBlocks=4;
                blocks[0] = new Block(x, y, width, color);
                blocks[1] = new Block(x+boxSize, y, width, color);
                blocks[2] = new Block(x, y+boxSize, width, color);
                blocks[3] = new Block(x+boxSize, y+boxSize, width, color);
                break;
        }

    }
    public void setPosition(int a, Gameboard board) {
        int position;
        int rightBound;
        int index;
        int posDiff;
        int indexDiff;
        boolean collide = false;
        switch (type) {
            case 0:
                rightBound = boxSize * 10;
            case 1:
                rightBound = boxSize * 11;
                break;
            case 2:
                rightBound = boxSize * 10;
                break;
            case 3:
                rightBound = boxSize * 9;
                break;
            default:
                rightBound = boxSize * 10;
                break;
        }
        if (a < (boxSize)) {
            position = boxSize;
            index = 0;
        } else if (a > rightBound) {
            return;
            //position = rightBound-(2*boxSize);
            //index = 9;
        } else {
            position = (int) (Math.floor(a / boxSize) * boxSize);
            index = position / boxSize - 1;
        }

        posDiff = position - blocks[0].getX();
        indexDiff = index - blocks[0].getXIndex();
        if (board.checkFilled(index, blocks[0].getYIndex())) {
            collide = true;
        }
        else if (indexDiff + blocks[1].getXIndex() < 0 || indexDiff + blocks[1].getXIndex() >= 10 || board.checkFilled(indexDiff + blocks[1].getXIndex(), blocks[1].getYIndex())) {
            collide = true;
        }
        else if (indexDiff + blocks[2].getXIndex() < 0 || indexDiff + blocks[2].getXIndex() >= 10 || board.checkFilled(indexDiff + blocks[2].getXIndex(), blocks[2].getYIndex())) {
            collide = true;
        }
        else if (indexDiff + blocks[3].getXIndex() < 0 || indexDiff + blocks[3].getXIndex() >= 10 || board.checkFilled(indexDiff + blocks[3].getXIndex(), blocks[3].getYIndex())) {
            collide = true;
        }
        if (!collide) {
            blocks[0].move(position);
            blocks[1].move(posDiff + blocks[1].getX());
            blocks[2].move(posDiff + blocks[2].getX());
            blocks[3].move(posDiff + blocks[3].getX());
            xLoc = a;
        }
    }
    public int getX() {
        return xLoc;
    }
    public int getY() {
        return yLoc;
    }
    public void drawRect(Canvas canvas, Paint paint) {
        for(int i=0;i<numBlocks;i++) {
            blocks[i].drawRect(canvas, paint);
        }
    }
    public void fall() {
        yLoc+=boxSize;
        for(int i=0;i<4;i++) {
            blocks[i].fall();
        }
    }
    public int getNumBlocks() {
        return numBlocks;
    }
    public boolean isCollision(Gameboard board) {
        boolean collide = false;
        if(blocks[0].getYIndex() == 14) {
            collide = true;
        }
        else if(blocks[1].getYIndex() == 14) {
            collide = true;
        }
        else if(blocks[2].getYIndex() == 14) {
            collide = true;
        }
        else if(blocks[3].getYIndex() == 14) {
            collide = true;
        }
        else {
            for (int i = 0; i < 4; i++) {
                if(blocks[i].getXIndex()<10) {
                    if (board.boxChart[blocks[i].getXIndex()][blocks[i].getYIndex() + 1].exist) {
                        collide = true;
                    }
                }
            }
        }

        return collide;
    }
    public int getColor() {
        return color;
    }
    public void rotate(Gameboard board) {
        boolean collide = false;
        for(int j=1;j<numBlocks;j++) {
            int xIndex = blocks[0].getXIndex() - blocks[j].getYIndex() + blocks[0].getYIndex();
            int yIndex = blocks[0].getYIndex() + blocks[j].getXIndex() - blocks[0].getXIndex();
            if(xIndex < 0 || xIndex >=10 || yIndex < 0 || yIndex >= 15) {
                collide = true;
            }
            else if(board.checkFilled(blocks[0].getXIndex() - blocks[j].getYIndex() + blocks[0].getYIndex(),blocks[0].getYIndex() + blocks[j].getXIndex() - blocks[0].getXIndex())) {
                collide = true;
            }
        }
        if(!collide) {
            for (int i = 0; i < numBlocks; i++) {
                blocks[i].rotate(blocks[0].getXIndex(), blocks[0].getYIndex());
            }
        }
    }
}
