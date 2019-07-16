package com.example.com.mariobird;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.CallbackManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.Random;


public class GameView extends View{

    Handler handler;
    Runnable runnable;
    Bitmap background;
    Bitmap ready;
    Bitmap gameover;
    Bitmap topTube, bottomTube;
    Bitmap enemyflowerBottom, enemyflowerTop;
    Bitmap star;
    Bitmap enemyMus;
    Display display;
    Point point;
    int width, height;
    Rect rect;
    Bitmap[] birds;
    int birdFrame=0;
    int velocity=0,gravity=3;
    int birdX, birdY;
    boolean gameState = false;
    int gap = 400;
    int minTube, maxTube;
    int numberTubes = 6;
    int distanceTubes;
    int[] tubeY = new int[numberTubes];
    int[] tubeX = new int[numberTubes];
    int starX;
    int starY;
    int flowerTopX, flowerTopY, flowerBottomX, flowerBottomY;
    int enemyMusX, enemyMusY;
    Bitmap[] numbers = new Bitmap[10];
    Random random;
    int tubeVelocity = 8;
    int score = 0;
    boolean lose = false;
    MediaPlayer coinMedia;
    MediaPlayer screamMedia;
    MediaPlayer slapMedia;
    MediaPlayer starMedia;
    MediaPlayer biteMedia;
    MediaPlayer enemyMusMedia;
    MediaPlayer wing;
    boolean starPower=false;
    boolean starAppear=true;
    boolean enemyMusCena = false;
    boolean alreadyEntried = true;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CallbackManager mCallbackManager;
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private DatabaseHelper databaseHelper;


    public GameView(Context context){
        super(context);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        databaseHelper = new DatabaseHelper(getContext());

        coinMedia = MediaPlayer.create(context, R.raw.coin);
        screamMedia = MediaPlayer.create(context, R.raw.scream);
        slapMedia = MediaPlayer.create(context, R.raw.slap);
        starMedia = MediaPlayer.create(this.getContext(), R.raw.star);
        biteMedia = MediaPlayer.create(this.getContext(), R.raw.bite);
        enemyMusMedia =  MediaPlayer.create(this.getContext(), R.raw.laugh);
        wing =  MediaPlayer.create(this.getContext(), R.raw.wing);


        background = BitmapFactory.decodeResource(getResources(),R.drawable.background);
        ready = BitmapFactory.decodeResource(getResources(),R.drawable.background_ready);
        gameover = BitmapFactory.decodeResource(getResources(),R.drawable.gameover);
        display = ((Activity)getContext()).getWindowManager().getDefaultDisplay();
        point = new Point();
        display.getSize(point);
        width = point.x;
        height = point.y;
        rect = new Rect(0,0,width,height);
        birds = new Bitmap[6];
        birds[0] = BitmapFactory.decodeResource(getResources(),R.drawable.mario11);
        birds[1] = BitmapFactory.decodeResource(getResources(),R.drawable.mario22);
        birds[2] = BitmapFactory.decodeResource(getResources(),R.drawable.mario33);
        birds[3] = BitmapFactory.decodeResource(getResources(),R.drawable.starmario11);
        birds[4] = BitmapFactory.decodeResource(getResources(),R.drawable.starmario22);
        birds[5] = BitmapFactory.decodeResource(getResources(),R.drawable.starmario33);


        numbers[0] = BitmapFactory.decodeResource(getResources(),R.drawable.n0);
        numbers[1] = BitmapFactory.decodeResource(getResources(),R.drawable.n1);
        numbers[2] = BitmapFactory.decodeResource(getResources(),R.drawable.n2);
        numbers[3] = BitmapFactory.decodeResource(getResources(),R.drawable.n3);
        numbers[4] = BitmapFactory.decodeResource(getResources(),R.drawable.n4);
        numbers[5] = BitmapFactory.decodeResource(getResources(),R.drawable.n5);
        numbers[6] = BitmapFactory.decodeResource(getResources(),R.drawable.n6);
        numbers[7] = BitmapFactory.decodeResource(getResources(),R.drawable.n7);
        numbers[8] = BitmapFactory.decodeResource(getResources(),R.drawable.n8);
        numbers[9] = BitmapFactory.decodeResource(getResources(),R.drawable.n9);

        topTube = BitmapFactory.decodeResource(getResources(),R.drawable.pipe_top);
        bottomTube = BitmapFactory.decodeResource(getResources(),R.drawable.pipe_bottom);

        star = BitmapFactory.decodeResource(getResources(),R.drawable.star);

        enemyMus = BitmapFactory.decodeResource(getResources(),R.drawable.enemymus);

        enemyflowerBottom = BitmapFactory.decodeResource(getResources(),R.drawable.enemy_flower_bottom);
        enemyflowerTop = BitmapFactory.decodeResource(getResources(),R.drawable.enemy_flower_top);



        birdX = width/2 - birds[0].getWidth()/2;
        birdY = height/2 - birds[0].getWidth()/2;

        distanceTubes = width*3/6;
        minTube = gap/2;
        maxTube = height - minTube - gap;

        random = new Random();

        for(int i =0;i<numberTubes;i++){
            tubeX[i] = width +i*distanceTubes;
            tubeY[i] = minTube + random.nextInt(maxTube-minTube+100);

        }

    }


    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.drawBitmap(background,null,rect,null);

        if(!starPower) {
            if(birdFrame>2)
                birdFrame=0;
            else {
                if (birdFrame == 0)
                    birdFrame = 1;
                else if (birdFrame == 1)
                    birdFrame = 2;
                else if (birdFrame == 2)
                    birdFrame = 0;
            }
        }
        else{
            if(birdFrame<3){
                birdFrame=3;
            }
            else {
                if (birdFrame == 3)
                    birdFrame = 4;
                else if (birdFrame == 4)
                    birdFrame = 5;
                else if (birdFrame == 5)
                    birdFrame = 3;
            }
        }

        for(int i=0;i<numberTubes;i++) {

            // if mario touches top tube
            if (birdY < tubeY[i] + (height / 2) - gap - 500 && birdX + 70 > tubeX[i] && birdX < tubeX[i] + bottomTube.getWidth() -50) {
                if(!lose&&!starPower) {
                    slapMedia.start();
                    screamMedia.start();
                    gameState = false;
                    lose = true;
                }
            }

            // if mario touches bottom tube
            if (birdY > tubeY[i] + gap - 100 && birdX + 70 > tubeX[i] && birdX < tubeX[i] + bottomTube.getWidth() -50) {
                if(!lose&&!starPower) {
                    slapMedia.start();
                    screamMedia.start();
                    gameState = false;
                    lose = true;
                }
            }

            //if mario pass tubes
            if(tubeX[i]<= birdX&&tubeX[i]>= birdX-7) {
                if(!lose) {
                    score += 1;
                    coinMedia.start();
                }
            }
        }

        //if mario falls down
        if (!(birdY < (height - 3 * birds[0].getHeight() + 90) || velocity < 0)) {
            if(!lose) {
                starMedia.stop();
                screamMedia.start();
                gameState = false;
                lose = true;
            }

        }

        //if mario touches star
        if(starX-birdX<= 60&&starX-birdX>= -60&&starY-birdY<=60&&starY-birdY>=-60) {
            starPower=true;


            if(starAppear) {
                starMedia.start();
                starAppear = false;
            }
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            starAppear=true;
                            starPower=false;
                        }
                    },
                    5000
            );
        }

        if(score>20) {
            //if mario touches enemyFlowerBottom
            if (flowerBottomX - birdX <= 60 && flowerBottomX - birdX >= -60 && flowerBottomY - birdY <= 100 && flowerBottomY - birdY >= -100) {
                if (!lose && !starPower) {
                    biteMedia.start();
                    starMedia.stop();
                    screamMedia.start();
                    gameState = false;
                    lose = true;
                }
            }
        }

        if(score>30) {
            //if mario touches enemyFlowerTop
            if (flowerTopX - birdX <= 60 && flowerTopX - birdX >= -60 && flowerTopY - birdY <= 100 && flowerTopY - birdY >= -100) {
                if (!lose && !starPower) {
                    biteMedia.start();
                    starMedia.stop();
                    screamMedia.start();
                    gameState = false;
                    lose = true;
                }
            }
        }

        if(score>10) {
            //if mario touches enemyMus
            if (enemyMusX - birdX <= 70 && enemyMusX - birdX >= -70 && enemyMusY - birdY <= 60 && enemyMusY - birdY >= -60) {
                if (!lose && !starPower) {
                    enemyMusCena = true;
                    enemyMusMedia.start();
                    screamMedia.start();
                    starMedia.stop();
                    gameState = false;
                    lose = true;
                }
            }
        }

        if(gameState) {
            if (birdY < (height - 3 * birds[0].getHeight() + 90) || velocity < 0) {
                velocity += gravity;
                birdY += velocity;
            }

            for(int i=0;i<numberTubes;i++){
                tubeX[i] -= tubeVelocity;
                if(tubeX[i]< -topTube.getWidth()){
                    tubeX[i] += numberTubes * distanceTubes;
                    tubeY[i] = minTube + random.nextInt(maxTube-minTube+1);
                }

                canvas.drawBitmap(topTube,tubeX[i],tubeY[i]- topTube.getHeight(),null);
                canvas.drawBitmap(bottomTube,tubeX[i],tubeY[i]+gap,null);

            }

            flowerBottomX = tubeX[4]+30;
            flowerBottomY = tubeY[4]+gap-125;
            flowerTopX = tubeX[1]+30;
            flowerTopY = tubeY[1]+gap-400;

            enemyMusX = tubeX[2]+300;
            enemyMusY = tubeY[2]+gap-200;

            if(score>20) {
                starX = tubeX[1]+200;
                starY = tubeY[1] + 500;
            }
            else if(score>10){
                starX = tubeX[1]+300;
                starY = tubeY[1] - 200;
            }
            else{
                starX = tubeX[1]+300;
                starY = tubeY[1] + 100;
            }

            if(score>50){
                tubeVelocity=10;
            }

        }
        else if(lose){
            velocity += gravity;
            birdY += velocity;
            if (enemyMusCena)
                enemyMusY += velocity;
            for (int i = 0; i < numberTubes; i++) {
                if (tubeX[i] < -topTube.getWidth()) {
                    tubeX[i] += numberTubes * distanceTubes;
                    tubeY[i] = minTube + random.nextInt(maxTube - minTube + 1);
                }

                canvas.drawBitmap(topTube, tubeX[i], tubeY[i] - topTube.getHeight(), null);
                canvas.drawBitmap(bottomTube, tubeX[i], tubeY[i] + gap, null);
            }

            if(alreadyEntried) {
                alreadyEntried = false;
                if(currentUser!=null) {
                    final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                final String scorefire = snapshot.child("score").getValue(String.class);
                                assert scorefire != null;
                                if(Integer.parseInt(scorefire)<score) {
                                    rootRef.child("score").setValue(String.valueOf(score));
                                }

                                if(databaseHelper.userExists("1")) {
                                    Cursor data = databaseHelper.getData();
                                    data.moveToPosition(databaseHelper.getPositionUser("1"));
                                    String highscore = data.getString(3);
                                    if (score > Integer.parseInt(highscore)) {
                                        databaseHelper.updateScore("1", String.valueOf(score));
                                    }
                                    databaseHelper.close();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    Cursor data = databaseHelper.getData();
                    data.moveToPosition(databaseHelper.getPositionUser("1"));
                    String highscore = data.getString(3);
                    if (score > Integer.parseInt(highscore)) {
                        databaseHelper.updateScore("1", String.valueOf(score));
                    }
                    databaseHelper.close();
                }

                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                            finish();
                        }
                    },
                    3000
                );
            }

            canvas.drawBitmap(gameover, null, rect, null);
        }

        //draw all the characters
        if(gameState||lose) {

            if (starAppear)
                canvas.drawBitmap(star, starX, starY, null);

            if(score>20)
                canvas.drawBitmap(enemyflowerBottom, flowerBottomX, flowerBottomY, null);

            if(score>30)
                canvas.drawBitmap(enemyflowerTop, flowerTopX, flowerTopY, null);

            if(score>10)
                canvas.drawBitmap(enemyMus, enemyMusX, enemyMusY, null);

            if(score<10)
                canvas.drawBitmap(numbers[score],birdX,height/2 -800,null);
            else if(score<100){
                String score1=Integer.toString(score).substring(0, 1);
                String score2=Integer.toString(score).substring(1, 2);
                canvas.drawBitmap(numbers[Integer.valueOf(score1)],birdX-25,height/2 -800,null);
                canvas.drawBitmap(numbers[Integer.valueOf(score2)],birdX+35,height/2 -800,null);
            }
            else if(score<1000){
                String score1=Integer.toString(score).substring(0, 1);
                String score2=Integer.toString(score).substring(1, 2);
                String score3=Integer.toString(score).substring(2, 3);
                canvas.drawBitmap(numbers[Integer.valueOf(score1)],birdX-50,height/2 -800,null);
                canvas.drawBitmap(numbers[Integer.valueOf(score2)],birdX,height/2 -800,null);
                canvas.drawBitmap(numbers[Integer.valueOf(score3)],birdX+70,height/2 -800,null);
            }
        }


        //mario
        canvas.drawBitmap(birds[birdFrame], birdX, birdY, null);

        if(!gameState&&!lose)
            canvas.drawBitmap(ready,null,rect,null);

        handler.postDelayed(runnable,20);


    }

    private void finish() {
        Activity activity = (Activity)getContext();
        activity.finish();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        if(action == MotionEvent.ACTION_DOWN){

            if(birdY>(height/15)&&!lose) {
                wing.start();
                velocity = -30;
                gameState=true;
            }
        }

        return true;
    }
}
