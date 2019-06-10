package android.example.com.mariobird;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends Activity {

    private Button login;
    private DatabaseHelper databaseHelper;
    ImageButton highscoreButton;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fresco.initialize(this);

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

                    DialogInterface.OnClickListener dialogClickListener1 = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    if (checkInternetConnection()) {                //replacement could be "" (empty character)
                                        FirebaseAuth.getInstance().signOut();
                                        LoginManager.getInstance().logOut();
                                        login.setText(getString(R.string.log_in));
                                    } else {
                                        Toast.makeText(MainActivity.this, R.string.no_internet_connection, Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }

                        }
                    };

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle(R.string.log_out)
                            .setMessage(R.string.sure_log_out)
                            .setNegativeButton(R.string.no_option, dialogClickListener1).setPositiveButton(R.string.yes_option, dialogClickListener1).show();

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
            databaseHelper.addElements("1","Mario","1","0");

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

    /*a simple method that checks if there is internet connection*/
    public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) Objects.requireNonNull(this).getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
