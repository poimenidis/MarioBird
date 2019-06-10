package android.example.com.mariobird;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

public class LoginActivity extends Activity {

    private LoginButton loginButton;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private CallbackManager mCallbackManager;
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private String userId;
    private String userName;
    private boolean canWrite=false;
    private DatabaseHelper databaseHelper;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100;
    private ProgressBar progressBar;
    private SignInButton googleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        googleButton = findViewById(R.id.google_button);

        databaseHelper = new DatabaseHelper(this);

        loginButton = findViewById(R.id.login_button);
        progressBar = findViewById(R.id.progressBar);

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions(Arrays
                .asList("public_profile","email"));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                loginButton.setEnabled(false);
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code
                                try {
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    // [START auth_with_facebook]
    private void handleFacebookAccessToken (final AccessToken token){

        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            canWrite = isWriteStoragePermissionGranted();
//                            if(canWrite)
                            handleFbData();

                        } else {

                        }
                    }
                });
    }

    public void handleFbData() {
        /*First we need to check if user already exists in the database. If the user doesn't exist, the flag
         * returns true and we create the user in database. */
        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    /*Get user's fb profile and take his photo url.*/
                    Profile userProfile = Profile.getCurrentProfile();
                    final String userPhotoUrl = userProfile.getProfilePictureUri(761, 761).toString();
                    final String userName = userProfile.getName();
                    final UserClass fbUser = new UserClass(userName,mAuth.getUid(),userPhotoUrl,"0");
                    rootRef.setValue(fbUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
//                           new DownloadImage(mAuth.getCurrentUser().getUid(),userName,todos,aboutMe).execute(userPhotoUrl);
                        }
                    });
                }
                else{
                    /*If user exists in FireBase but it doesn't exist in SQLite, then
                     * take his attributes from Firebase and make the SQLite entry.*/
                    final FirebaseUser user = mAuth.getCurrentUser();
                    if(!databaseHelper.userExists("1")){
                        /*Get appropriate user info from FireBase*/

                        /*userName*/
                        final String userName = snapshot.child("name").getValue(String.class);
                        final String id = snapshot.child("id").getValue(String.class);
                        final String image = snapshot.child("image").getValue(String.class);
                        final String score = snapshot.child("score").getValue(String.class);

                        databaseHelper.addElements("1",userName,image,score);

                    }/*If user exists both in FireBase and SQLite, just update UI*/
                    else{
                        final String scorefire = snapshot.child("score").getValue(String.class);
                        Cursor data = databaseHelper.getData();
                        data.moveToPosition(databaseHelper.getPositionUser("1"));
                        String highscore = data.getString(3);
                        if(Integer.parseInt(scorefire)>Integer.parseInt(highscore)){
                            databaseHelper.updateScore("1",scorefire);
                        }
                        else {
                            rootRef.child("score").setValue(highscore);
                        }

                        final String userName = snapshot.child("name").getValue(String.class);
                        final String image = snapshot.child("image").getValue(String.class);

                        databaseHelper.updateImage("1", image);
                        databaseHelper.updateName("1", userName);
                    }
                }

                finish();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /*A method that checks if WRITE_EXTERNAL_STORAGE permission is granted.
     * If it is then return true.
     * else return false and prompt user to grant that permission by showing a dialog.
     * onRequestPermissionResult method handles the result.
     * If user grands that permission then continue normally (call handleFbData() )
     * else show a Toast message and do nothing.*/
    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

}
