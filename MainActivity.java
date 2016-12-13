package alexyou.tetrisplus;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize gameView and set it as the view
        gameView = new GameView(this);
        setContentView(gameView);

    }

    // GameView class will go here

    // Here is our implementation of GameView
    // It is an inner class.
    // Note how the final closing curly brace }
    // is inside SimpleGameEngine

    // Notice we implement runnable so we have
    // A thread and can override the run method.
    class GameView extends SurfaceView implements Runnable {

        // This is our thread
        Thread gameThread = null;

        // This is new. We need a SurfaceHolder
        // When we use Paint and Canvas in a thread
        // We will see it in action in the draw method soon.
        SurfaceHolder ourHolder;

        // A boolean which we will set and unset
        // when the game is running- or not.
        volatile boolean playing;

        // A Canvas and a Paint object
        Canvas canvas;
        Paint paint;

        // This variable tracks the game frame rate
        long fps;

        // This is used to help calculate the fps
        private long timeThisFrame;

        long secondsThisFrame;
        long timeCounter;

        int backgroundColor;

        int score;
        int randomPos;
        Piece piece;
        Piece[] next;
        Piece[] pieces;
        int numPieces;
        Gameboard board;
        Timer timer;

        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public GameView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            // How kind.
            super(context);

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            // Set our boolean to true - game on!
            playing = true;
            backgroundColor = Color.argb(255,255,255,255);
            int randomPos = (int)(Math.random() * 8) * getScreenWidth(getContext())/14;
            piece = new Piece(getScreenWidth(getContext())*2/14 + randomPos, getScreenWidth(getContext())*1/14, getScreenWidth(getContext()));
            next = new Piece[3];
            for(int i=0;i<3;i++) {
                randomPos = (int)(Math.random() * 8) * getScreenWidth(getContext())/14;
                next[i] = new Piece(getScreenWidth(getContext())*2/14 + randomPos, getScreenWidth(getContext())*1/14, getScreenWidth(getContext()));
            }
            pieces = new Piece[100];
            board = new Gameboard(getScreenHeight(getContext()),getScreenWidth(getContext()));
            timer = new Timer();
            score = 0;
        }
                @Override
                public void run() {
                    // Capture the current time in milliseconds in startFrameTime
                    long startFrameTime = System.currentTimeMillis();

                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (playing) {
                                // Draw the frame
                                draw();
                                // Update the frame
                                update();
                            }
                        }
                    },3000,500);

                    // Calculate the fps this frame
                    // We can then use the result to
                    // time animations and more.
                    timeThisFrame = System.currentTimeMillis() - startFrameTime;
                    secondsThisFrame = TimeUnit.MILLISECONDS.toSeconds(timeThisFrame);
                    timeCounter += secondsThisFrame;
                    //System.out.println("Time: " + secondsThisFrame);
                    if (timeThisFrame > 0) {
                        fps = 1000 / timeThisFrame;
                    }
                }


        // Everything that needs to be updated goes in here
        // In later projects we will have dozens (arrays) of objects.
        // We will also do other things like collision detection.
        public void update() {
            if(board.lose()) {
                playing = false;
            }
            //Collision
            if (piece.isCollision(board)) {
                backgroundColor = piece.getColor();
                board.fillChart(piece);
                pieces[numPieces] = piece;
                numPieces++;
                randomPos = (int)(Math.random() * 8) * getScreenWidth(getContext())/14;
                piece = next[0];
                next[0] = next[1];
                next[1] = next[2];
                next[2] = new Piece(getScreenWidth(gameView.getContext()) * 2 / 14 + randomPos, getScreenWidth(gameView.getContext()) * 1 / 14, getScreenWidth(gameView.getContext()));
            }
            if(!piece.isCollision(board)) {
                piece.fall();
            }
            score += board.clearLine();
        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                //canvas.drawColor(Color.argb(255,255,255,255));
                canvas.drawColor(backgroundColor);

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 249, 129, 0));

                // Make the text a bit bigger
                paint.setTextSize(45);

                // Display the current fps on the screen
                canvas.drawText("FPS:" + fps, 20, 40, paint);

                paint.setColor(Color.argb(255,255,255,255));
                canvas.drawRect(1*getScreenWidth(gameView.getContext())/14,1*getScreenWidth(gameView.getContext()) /14,11*getScreenWidth(gameView.getContext()) /14,16*getScreenWidth(gameView.getContext()) /14,paint);

                board.projection(piece, canvas, paint);

                piece.drawRect(canvas, paint);

                board.drawBoard(canvas, paint);

                board.drawButtons(canvas, paint);

                canvas.drawText("Score:" + score, getScreenWidth(gameView.getContext()) * 11/14, 40, paint);

                board.drawPreview(next, canvas, paint);

                // Draw everything to the screen
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }
        public int getScreenWidth(Context context) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(dm);
            return Math.round(dm.widthPixels);
        }

        public int getScreenHeight(Context context) {
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager windowManager = (WindowManager) context.getSystemService(WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getMetrics(dm);
            return Math.round(dm.heightPixels);
        }

        // If SimpleGameEngine Activity is paused/stopped
        // shutdown our thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        // If SimpleGameEngine Activity is started then
        // start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            int x = (int)motionEvent.getX();
            int y = (int)motionEvent.getY();
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                // Player has touched the screen

                case MotionEvent.ACTION_DOWN:
                    if(y>17*(getScreenWidth(getContext())/14) && y<19*(getScreenWidth(getContext())/14)) {
                        if (x > 6 * (getScreenWidth(getContext()) / 14) && x < 8 * (getScreenWidth(getContext()) / 14)) {
                            piece.rotate(board);
                        }
                        if (x > 2 * (getScreenWidth(getContext()) / 14) && x < 4 * (getScreenWidth(getContext()) / 14)) {
                            piece.setPosition(piece.getX() - (getScreenWidth(getContext()) / 14), board);
                        }
                        if (x > 10 * (getScreenWidth(getContext()) / 14) && x < 12 * (getScreenWidth(getContext()) / 14)) {
                            piece.setPosition(piece.getX() + (getScreenWidth(getContext()) / 14), board);
                        }
                    }
                    else if(y>20*(getScreenWidth(getContext())/14) && y<21*(getScreenWidth(getContext())/14)) {
                        if (x > 2 * (getScreenWidth(getContext()) / 14) && x < 12 * (getScreenWidth(getContext()) / 14)) {
                            while(!piece.isCollision(board)) {
                                score += 10;
                                piece.fall();
                            }
                        }
                    }
                    else {
                        piece.setPosition(x, board);
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    piece.rotate(board);
                    System.out.println("rotate");
                    break;
                case MotionEvent.ACTION_MOVE:
                    piece.setPosition(x, board);
                    break;
                // Player has removed finger from screen
                /*case MotionEvent.ACTION_UP:

                    // Set isMoving so Bob does not move
                    isMoving = false;

                    break;*/
            }
            return true;
        }

    }
    // This is the end of our GameView inner class

    // More SimpleGameEngine methods will go here

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        gameView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        gameView.pause();
    }

}