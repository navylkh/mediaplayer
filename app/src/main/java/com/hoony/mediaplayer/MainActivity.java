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

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.ui.PlayerView;

import com.hoony.mediaplayer.utils.Logs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {

    // creating a variable for exoplayerview
    PlayerView exoPlayerView;

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
        suggestList.add("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_175k.mov");

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
            // we are adding our track selector to exoplayer.
            if(null != exoPlayer) {
                exoPlayer.stop();
                exoPlayer.release();
                exoPlayer = null;
            }

            exoPlayer = new SimpleExoPlayer.Builder(MainActivity.this).build();


            // we are parsing a video url
            // and parsing its video uri.
            Uri videouri = Uri.parse(videoURL);

            // we are creating a variable for extractor factory
            // and setting it to default extractor factory.
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

            // inside our exoplayer view
            // we are setting our player
            exoPlayerView.setPlayer(exoPlayer);

            // we are preparing our exoplayer
            // with media source.
            MediaItem mediaItem = MediaItem.fromUri(videoURL);
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();

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






