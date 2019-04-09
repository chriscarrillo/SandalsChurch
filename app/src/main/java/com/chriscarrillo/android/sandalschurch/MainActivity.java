package com.chriscarrillo.android.sandalschurch;

import android.content.res.Configuration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import bg.devlabs.fullscreenvideoview.FullscreenVideoView;

public class MainActivity extends AppCompatActivity {

    /*
        I originally had a thumbnail showing over the video. After
        some debugging, I discovered the FullscreenVideoView library
        sets all of the view visibilities back to visible when the
        orientation is changed back to portrait. There is currently
        a thumbnail feature in development.
     */

    private TextView sermonTitle;
    private TextView sermonDescription;
    private TextView sermonDate;
    private FullscreenVideoView sermonVideo;
//    private ImageView sermonVideoThumbnail;
//    private ImageView sermonVideoPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sermonTitle = (TextView) findViewById(R.id.sermon_title);
        sermonDescription = (TextView) findViewById(R.id.sermon_description);
        sermonDate = (TextView) findViewById(R.id.sermon_date);
        sermonVideo = (FullscreenVideoView) findViewById(R.id.sermon_video);
//        sermonVideoThumbnail = (ImageView) findViewById(R.id.sermon_thumbnail);
//        sermonVideoPlay = (ImageView) findViewById(R.id.play_image);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://sandalschurch.com/api/latest_sermon";

        /*View.OnClickListener playListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sermonVideoThumbnail.setVisibility(View.GONE);
                sermonVideoPlay.setVisibility(View.GONE);
                sermonVideo.enableAutoStart();
            }
        };

        sermonVideoThumbnail.setOnClickListener(playListener);
        sermonVideoPlay.setOnClickListener(playListener);*/

        JsonObjectRequest sermonRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        try {
                            // Set the video URL
                            sermonVideo.videoUrl(response.getString("mp4_hd"))
                                    .addSeekBackwardButton().addSeekForwardButton();
//                            Picasso.get().load(response.getString("image_hd"))
//                                    .into(sermonVideoThumbnail);

                            sermonTitle.setText(response.getString("title"));
                            sermonDescription.setText(response.getString("desc"));

                            // Format the date into a nice, readable format
                            String date = response.getString("date");
                            SimpleDateFormat beforeDateFormat =
                                    new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date formattedDate = beforeDateFormat.parse(date);

                            SimpleDateFormat afterDateFormat =
                                    new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
                            String finalDate = afterDateFormat.format(formattedDate);
                            sermonDate.setText(finalDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", error.toString());
                    }
                }
        );

        queue.add(sermonRequest);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            sermonVideo.enableAutoStart();
//            sermonVideoThumbnail.setVisibility(View.GONE);
//            sermonVideoPlay.setVisibility(View.GONE);
        }
    }
}
