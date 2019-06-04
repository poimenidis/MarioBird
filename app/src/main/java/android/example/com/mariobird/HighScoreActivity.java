package android.example.com.mariobird;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HighScoreActivity extends AppCompatActivity {

    private ListView listView;
    private HighscoreAdapter customAdapter;
    private String[] d1 = {null};
    private SwipeRefreshLayout swip;
    private UserClass highscoreClass;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();
    private ArrayList<String> scores = new ArrayList<>();
    private ArrayList<UserClass> highscoreClasses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        swip = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swip.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);

        swip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler mh = new Handler();
                mh.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swip.isRefreshing()) {
                            swip.setRefreshing(false);
                        }
                    }
                }, 7000);
            }

        });

        listView = findViewById(R.id.highscore_list);
        customAdapter = new HighscoreAdapter(this);
        List<String> data1 = new ArrayList<>(Arrays.asList(d1));
        customAdapter.setDatas(data1, data1, data1, data1);
        listView.setAdapter(customAdapter);
        customAdapter.clear();

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        if(databaseHelper.userExists("1")) {
            Cursor data = databaseHelper.getData();
            data.moveToPosition(databaseHelper.getPositionUser("1"));
            String id = data.getString(0);
            String name = data.getString(1);
            String image = data.getString(2);
            String highscore = data.getString(3);
            databaseHelper.close();

            highscoreClass = new UserClass(name, id, image, highscore);
            highscoreClasses.add(highscoreClass);

            for (UserClass h : highscoreClasses) {
                names.add(h.getName());
                images.add(h.getImage());
                ids.add(h.getId());
                scores.add(h.getScore());
            }

            customAdapter.setDatas(ids, names, images, scores);
        }


    }
}
