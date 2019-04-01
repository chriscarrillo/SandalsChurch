package com.chriscarrillo.android.sandalschurch;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView sermonTitle = (TextView) findViewById(R.id.sermon_title);
        final TextView sermonDescription = (TextView) findViewById(R.id.sermon_description);
        final TextView sermonDate = (TextView) findViewById(R.id.sermon_date);
        final VideoView sermonVideo = (VideoView) findViewById(R.id.sermon_video);
        final ImageView sermonVideoThumbnail = (ImageView) findViewById(R.id.sermon_thumbnail);

        // Set the media controller of the video
        MediaController mediaController = new MediaController(this);
        sermonVideo.setMediaController(mediaController);
        mediaController.setAnchorView(sermonVideo);

        // When the video starts playing, hide the thumbnail
        sermonVideo.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    sermonVideoThumbnail.setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://sandalschurch.com/api/latest_sermon";

        JsonObjectRequest sermonRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Picasso.get().load(response.getString("image_hd"))
                                    .into(sermonVideoThumbnail);
                            sermonVideo.setVideoURI(Uri.parse(response.getString("mp4_hd")));

                            // Set the text, thumbnail, and URI from the response
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
}
