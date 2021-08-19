package com.hoony.mediaplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import com.hoony.mediaplayer.utils.Logs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    // creating a variable for exoplayerview
    SimpleExoPlayerView exoPlayerView;

    // creating a variable for exoplayer
    SimpleExoPlayer exoPlayer;
    AutoCompleteTextView addressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logs.d("player START");
        exoPlayerView = findViewById(R.id.idExoPlayerView);
        addressView = findViewById(R.id.address_view);
        List<String> suggestList = new ArrayList<>();
        suggestList.add("https://media.geeksforgeeks.org/wp-content/uploads/20201217163353/Screenrecorder-2020-12-17-16-32-03-350.mp4");
        suggestList.add("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4");

        SuggestAdapter adapter = new SuggestAdapter(MainActivity.this, suggestList);
        addressView.setAdapter(adapter);
        addressView.setImeOptions(EditorInfo.IME_ACTION_GO);
        addressView.setInputType(InputType.TYPE_CLASS_TEXT);
        /*
        addressView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //Enter key Action
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    //키패드 내리기
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(binding.editPass.getWindowToken(), 0);

                    return true;
                }
                return false;
            }
        });
        */
        addressView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    if(TextUtils.isEmpty(addressView.getText().toString())) {
                        handled = false;
                    } else {
                        play(addressView.getText().toString());
                        handled = true;
                    }
                }
                return handled;
            }
        });
    }

    private void play(String videoURL) {

        try {
            // bandwisthmeter is used for
            // getting default bandwidth
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

            // track selector is used to navigate between
            // video using a default seekbar.
            TrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory(bandwidthMeter));

            // we are adding our track selector to exoplayer.
            if(null != exoPlayer) {
                exoPlayer.stop();
                exoPlayer.release();
                exoPlayer = null;
            }

            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);


            // we are parsing a video url
            // and parsing its video uri.
            Uri videouri = Uri.parse(videoURL);

            // we are creating a variable for datasource factory
            // and setting its user agent as 'exoplayer_view'
            DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("exoplayer_video");

            // we are creating a variable for extractor factory
            // and setting it to default extractor factory.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            // we are creating a media source with above variables
            // and passing our event handler as null,
            MediaSource mediaSource = new ExtractorMediaSource(videouri, dataSourceFactory, extractorsFactory, null, null);

            // inside our exoplayer view
            // we are setting our player
            exoPlayerView.setPlayer(exoPlayer);


            // we are preparing our exoplayer
            // with media source.
            exoPlayer.prepare(mediaSource);

            // we are setting our exoplayerc
            // when it is ready.
            exoPlayer.setPlayWhenReady(true);
            //exoPlayer.play();
        } catch (Exception e) {
            // below line is used for
            // handling our errors.
            Logs.e("Error : " + e.toString());
        }
    }

    public class SuggestAdapter extends ArrayAdapter<String> implements SpinnerAdapter {
        private LayoutInflater mInflator;
        private List<String> mItems;

        SuggestAdapter(Context context, List<String> objects) {
            super(context, R.layout.item_suggest, objects);
            mInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mItems = objects;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public String getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.item_suggest, null, false);
                holder = new ViewHolder();
                holder.textView = (TextView) convertView.findViewById(R.id.spinnerText);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.textView.setText(mItems.get(position));

            return convertView;
        }

        private class ViewHolder {
            TextView textView;
        }
    }
}






