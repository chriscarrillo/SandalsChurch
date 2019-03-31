package com.chriscarrillo.android.sandalschurch;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
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
                            // Set the text, thumbnail, and URI from the response
                            sermonTitle.setText(response.getString("title"));
                            sermonDescription.setText(response.getString("desc"));
                            sermonDate.setText(response.getString("date"));
                            Picasso.get().load(response.getString("image_sd")).into(sermonVideoThumbnail);
                            sermonVideo.setVideoURI(Uri.parse(response.getString("mp4_hd")));

                            sermonTitle.setAllCaps(true);
                        } catch (JSONException e) {
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
