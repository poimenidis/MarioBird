package android.example.com.mariobird;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private Button login;
    private DatabaseHelper databaseHelper;
    ImageButton highscoreButton;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        login = findViewById(R.id.login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAuth.getCurrentUser()==null) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    login.setText(getString(R.string.log_in));
                }
            }
        });

        highscoreButton = findViewById(R.id.highscore);
        highscoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenHighscores();
            }
        });




        if(!databaseHelper.userExists("1"))
            databaseHelper.addElements("1","1","1","0");

    }


    public void StartGame(View view) {
        Intent intent = new Intent(this, StartGame.class);
        startActivity(intent);
    }

    public void OpenHighscores() {
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mAuth.getCurrentUser()==null) {
            login.setText(getString(R.string.log_in));
        }
        else{
            login.setText(getString(R.string.log_out));
        }
    }
}
