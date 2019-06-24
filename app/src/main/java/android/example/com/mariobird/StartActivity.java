package android.example.com.mariobird;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.android.gms.ads.MobileAds;

public class StartActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        MobileAds.initialize(this, "ca-app-pub-8453977966536256~1200656674");
        setContentView(R.layout.activity_start);

        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithResourceId(R.drawable.giphy).build();


        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) findViewById(R.id.gif);
        simpleDraweeView.setController(
                Fresco.newDraweeControllerBuilder()
                        .setImageRequest(imageRequest)
                        .setAutoPlayAnimations(true)
                        .build());

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(StartActivity.this,MainActivity.class);
                StartActivity.this.startActivity(mainIntent);
                StartActivity.this.finish();
            }
        }, 2000);
    }
}
