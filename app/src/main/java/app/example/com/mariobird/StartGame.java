package app.example.com.mariobird;

import android.app.Activity;
import android.os.Bundle;

public class StartGame extends Activity {

    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

        gameView = new GameView(this);
        setContentView(gameView);
    }
}
