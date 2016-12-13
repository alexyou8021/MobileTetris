package alexyou.tetrisplus;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Gameboard {
    int height;
    int width;
    int boxSize;
    Block[][] boxChart = new Block[10][15];
    Gameboard(int h, int w) {
        height = h;
        width = w;
        boxSize = width/14;

        for(int i=0;i<10;i++) {
            for(int j=0;j<15;j++) {
                boxChart[i][j] = new Block();
            }
        }
    }
    public void drawBoard(Canvas canvas, Paint paint) {
        paint.setStrokeWidth(5);

        for(int i=0;i<10;i++) {
            for(int j=0;j<15;j++) {
                if(boxChart[i][j].exist) {
                    boxChart[i][j].drawRect(canvas, paint);
                }
            }
        }

        paint.setColor(Color.argb(255, 0, 0, 0));

        //Vertical
        for(int i=1;i<12;i++) {
            canvas.drawLine(i*boxSize,boxSize,i*boxSize, 16*boxSize, paint);
        }
        //Horizontal
        for(int i=1;i<17;i++) {
            canvas.drawLine(boxSize,i*boxSize,11*boxSize, i*boxSize, paint);
        }
    }
    public void fillChart(Piece piece) {
        boxChart[piece.blocks[0].getXIndex()][piece.blocks[0].getYIndex()] = new Block(piece.blocks[0].getX(), piece.blocks[0].getY(), width, piece.getColor());
        boxChart[piece.blocks[1].getXIndex()][piece.blocks[1].getYIndex()] = new Block(piece.blocks[1].getX(), piece.blocks[1].getY(), width, piece.getColor());
        boxChart[piece.blocks[2].getXIndex()][piece.blocks[2].getYIndex()] = new Block(piece.blocks[2].getX(), piece.blocks[2].getY(), width, piece.getColor());
        boxChart[piece.blocks[3].getXIndex()][piece.blocks[3].getYIndex()] = new Block(piece.blocks[3].getX(), piece.blocks[3].getY(), width, piece.getColor());

    }
    public int clearLine() {
        int score = 0;
        boolean complete;
        for(int j=14;j>1;j--) {
            complete = true;
            for(int i=0;i<10;i++) {
                if(!boxChart[i][j].exist) {
                    complete = false;
                }
            }
            if(complete) {
                score += 1000;
                for(int k=j;k>1;k--) {
                    for(int i=0;i<10;i++) {
                        boxChart[i][k] = boxChart[i][k-1];
                        if(boxChart[i][k].exist) {
                            boxChart[i][k].setLocation((i+1)*boxSize,(k+1)*boxSize);
                        }
                    }
                }
                j++;
            }
        }
        return score;
    }
    public boolean lose() {
        boolean lost = false;
        for(int i=0;i<10;i++) {
            if(boxChart[i][0].exist){
                lost = true;
            }
        }
        return lost;
    }
    public boolean checkFilled(int x, int y) {
        return boxChart[x][y].exist;
    }
    public void projection(Piece piece, Canvas canvas, Paint paint) {
        for(int i=0;i<15-((int)Math.floor(piece.getY()/boxSize)-1);i++) {
            boolean collide = false;
            for(int j=0;j<piece.getNumBlocks();j++){
                if(i + piece.blocks[j].getYIndex() >= 15) {
                    collide = true;
                    break;
                }
                else if(piece.blocks[j].getXIndex() < 10 && i + piece.blocks[j].getYIndex() < 15 && checkFilled(piece.blocks[j].getXIndex(), i + piece.blocks[j].getYIndex())) {
                    collide = true;
                    break;
                }
            }
            if(collide) {
                for(int j=0;j<piece.getNumBlocks();j++){
                    piece.blocks[j].projection((i-1)*boxSize, canvas, paint);
                }
                i=15;
                return;
            }
        }
    }
    public void drawButtons(Canvas canvas, Paint paint) {
        paint.setColor(Color.argb(255, 0, 0, 0));
        canvas.drawRect(2*boxSize, 17*boxSize, 4*boxSize, 19*boxSize, paint);
        canvas.drawRect(6*boxSize, 17*boxSize, 8*boxSize, 19*boxSize, paint);
        canvas.drawRect(10*boxSize, 17*boxSize, 12*boxSize, 19*boxSize, paint);
        canvas.drawRect(2*boxSize, 20*boxSize, 12*boxSize, 21*boxSize, paint);
    }
    public void drawPreview(Piece[] pieces, Canvas canvas, Paint paint) {
        paint.setColor(Color.argb(255, 255, 255, 255));
        canvas.drawRect((float)11.5*boxSize, boxSize, (float)13.5*boxSize, 3*boxSize, paint);
        canvas.drawRect((float)11.5*boxSize, 4*boxSize, (float)13.5*boxSize, 6*boxSize, paint);
        canvas.drawRect((float)11.5*boxSize, 7*boxSize, (float)13.5*boxSize, 9*boxSize, paint);

        paint.setColor(Color.argb(255, 0, 0, 0));
        canvas.drawLine((float)11.5*boxSize, boxSize, (float)11.5*boxSize,3*boxSize, paint);
        canvas.drawLine((float)13.5*boxSize, boxSize, (float)13.5*boxSize,3*boxSize, paint);
        canvas.drawLine((float)11.5*boxSize, boxSize, (float)13.5*boxSize,boxSize, paint);
        canvas.drawLine((float)11.5*boxSize, 3*boxSize, (float)13.5*boxSize,3*boxSize, paint);

        canvas.drawLine((float)11.5*boxSize, 4*boxSize, (float)11.5*boxSize,6*boxSize, paint);
        canvas.drawLine((float)13.5*boxSize, 4*boxSize, (float)13.5*boxSize,6*boxSize, paint);
        canvas.drawLine((float)11.5*boxSize, 4*boxSize, (float)13.5*boxSize,4*boxSize, paint);
        canvas.drawLine((float)11.5*boxSize, 6*boxSize, (float)13.5*boxSize,6*boxSize, paint);

        canvas.drawLine((float)11.5*boxSize, 7*boxSize, (float)11.5*boxSize,9*boxSize, paint);
        canvas.drawLine((float)13.5*boxSize, 7*boxSize, (float)13.5*boxSize,9*boxSize, paint);
        canvas.drawLine((float)11.5*boxSize, 7*boxSize, (float)13.5*boxSize,7*boxSize, paint);
        canvas.drawLine((float)11.5*boxSize, 9*boxSize, (float)13.5*boxSize,9*boxSize, paint);

        for(int i=0;i<3;i++) {
            paint.setColor(pieces[i].getColor());
            switch(pieces[i].type) {
                case 0: //Box
                    canvas.drawRect((float)12*boxSize,(float)1.5*boxSize + 3*i*boxSize,(float)13*boxSize,(float)2.5*boxSize + 3*i*boxSize, paint);
                    break;
                case 1: // Line
                    canvas.drawRect((float)12.375*boxSize,(float)1.25*boxSize + 3*i*boxSize,(float)12.625*boxSize,(float)2.75*boxSize + 3*i*boxSize, paint);
                    break;
                case 2: //L
                    canvas.drawRect((float)12.1*boxSize,(float)1.4*boxSize + 3*i*boxSize,(float)12.5*boxSize,(float)2.6*boxSize + 3*i*boxSize, paint);
                    canvas.drawRect((float)12.5*boxSize,(float)2.2*boxSize + 3*i*boxSize,(float)12.9*boxSize,(float)2.6*boxSize + 3*i*boxSize, paint);
                    break;
                case 3://Z
                    canvas.drawRect((float)11.9*boxSize,(float)1.6*boxSize + 3*i*boxSize,(float)12.7*boxSize,(float)2*boxSize + 3*i*boxSize, paint);
                    canvas.drawRect((float)12.3*boxSize,(float)2*boxSize + 3*i*boxSize,(float)13.1*boxSize,(float)2.4*boxSize + 3*i*boxSize, paint);
                    break;
                case 4://T
                    canvas.drawRect((float)11.9*boxSize,(float)1.6*boxSize + 3*i*boxSize,(float)13.1*boxSize,(float)2*boxSize + 3*i*boxSize, paint);
                    canvas.drawRect((float)12.3*boxSize,(float)2*boxSize + 3*i*boxSize,(float)12.7*boxSize,(float)2.4*boxSize + 3*i*boxSize, paint);
                    break;
            }
        }


    }
}
