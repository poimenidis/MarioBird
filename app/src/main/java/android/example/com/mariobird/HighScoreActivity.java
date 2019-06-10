package android.example.com.mariobird;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HighScoreActivity extends Activity {

    private ListView listView;
    private HighscoreAdapter customAdapter;
    private SwipeRefreshLayout swip;
    private UserClass user;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ArrayList<UserClass> users;
    private ValueEventListener valueEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        users = new ArrayList<>();

        swip = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swip.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);

        swip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mAuth.getUid() != null) {
                    retrieveUsers();
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
                else {
                    if (swip.isRefreshing()) {
                        swip.setRefreshing(false);
                    }
                }
            }

        });

        listView = findViewById(R.id.highscore_list);
        customAdapter = new HighscoreAdapter(this);
        user = new UserClass();
        users.add(user);
        customAdapter.setDatas(users);
        listView.setAdapter(customAdapter);
        customAdapter.clear();
        users.clear();

        if(mAuth.getUid()==null) {
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            if (databaseHelper.userExists("1")) {
                Cursor data = databaseHelper.getData();
                data.moveToPosition(databaseHelper.getPositionUser("1"));
                String id = data.getString(0);
                String name = data.getString(1);
                String image = data.getString(2);
                String highscore = data.getString(3);
                databaseHelper.close();

                user = new UserClass(name, id, image, highscore);
                users.add(user);

                customAdapter.setDatas(users);
            }
        }
        else{
            retrieveUsers();
        }


    }

    public void retrieveUsers(){
        swip.setRefreshing(true);
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Users");
        valueEventListener = ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                users.clear();
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    users.add(snap.getValue(UserClass.class));
                }

                customAdapter.setDatas(users);
                Collections.sort(users, new Comparator<UserClass>() {
                    @Override
                    public int compare(UserClass o1, UserClass o2) {
                        return (o1.getScore().compareTo(o2.getScore()));
                    }
                });
                Collections.reverse(users);
                swip.setRefreshing(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(valueEventListener!=null) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
            ref.removeEventListener(valueEventListener);
        }
    }
}
