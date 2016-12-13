package alexyou.tetrisplus;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Block {
    int boxSize;
    Rect rect;
    int color;
    int xLoc;
    int yLoc;
    int xIndex;
    int yIndex;
    boolean exist;
    Block() {
        exist = false;
        color = Color.argb(255,255,255,255);
    }
    Block(int x, int y, int width, int col) {
        boxSize = width/14;
        rect = new Rect(x,y,x+boxSize,y+boxSize);
        color = col;
        xLoc = x;
        yLoc = y;
        xIndex = x/boxSize - 1;
        yIndex = y/boxSize - 1;
        exist = true;
    }
    public void move(int a) {
        xLoc = a;
        xIndex = (int)Math.floor(a/boxSize) - 1;
        rect.set(a,rect.top,a+boxSize,rect.bottom);
    }
    public void drawRect(Canvas canvas, Paint paint) {
        if(exist) {
            paint.setColor(color);
            canvas.drawRect(rect, paint);
            paint.getColor();
            double shade = 0.8;
            paint.setColor(Color.argb(255,(int)(Color.red(color)*shade),(int)(Color.green(color)*shade),(int)(Color.blue(color)*shade)));
            canvas.drawRect(rect.left, rect.top, rect.left + 10, rect.bottom, paint);
            canvas.drawRect(rect.left, rect.bottom - 10, rect.right, rect.bottom, paint);
            paint.setColor(Color.argb(255, 255, 255, 255));
            canvas.drawRect(rect.left + boxSize - 20, rect.top + 10, rect.left + boxSize - 10, rect.top + 20, paint);
        }
    }
    public void fall() {
        yLoc += boxSize;
        yIndex = (int)Math.floor(yLoc/boxSize) - 1;
        rect.set(rect.left, rect.top+boxSize, rect.right, rect.bottom+boxSize);
    }
    public void setLocation(int a, int b) {
        rect.set(a,b,a+boxSize,b+boxSize);
        xLoc = a;
        yLoc = b;
        xIndex = (int)Math.floor(a/boxSize) - 1;
        yIndex = (int)Math.floor(b/boxSize) - 1;
    }
    public void rotate(int x, int y) {
        int xDiff = xIndex - x;
        int yDiff = yIndex - y;

        xIndex = x - yDiff;
        yIndex = y + xDiff;
        xLoc = (xIndex + 1) * boxSize;
        yLoc = (yIndex + 1) * boxSize;
        rect.set(xLoc, yLoc, xLoc+boxSize, yLoc+boxSize);
    }
    public void projection(int dist, Canvas canvas, Paint paint) {
        paint.setColor(color);
        canvas.drawRect(rect.left, dist + rect.top, rect.right, dist + rect.bottom, paint);
        paint.setColor(Color.argb(255, 255, 255, 255));
        canvas.drawRect(rect.left + 10, rect.top + dist + 10, rect.right - 10, rect.bottom + dist - 10, paint);
    }
    public int getX() {
        return xLoc;
    }
    public int getY() {
        return yLoc;
    }
    public int getXIndex() { return xIndex; }
    public int getYIndex() { return yIndex; }
}
