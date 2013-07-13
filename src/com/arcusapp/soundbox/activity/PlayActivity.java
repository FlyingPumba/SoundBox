package com.arcusapp.soundbox.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.arcusapp.soundbox.R;
import com.arcusapp.soundbox.SoundBoxApplication;
import com.arcusapp.soundbox.model.BundleExtra;
import com.arcusapp.soundbox.model.MediaPlayerServiceListener;
import com.arcusapp.soundbox.model.RandomState;
import com.arcusapp.soundbox.model.RepeatState;
import com.arcusapp.soundbox.model.Song;
import com.arcusapp.soundbox.player.MediaPlayerService;

public class PlayActivity extends Activity implements OnClickListener, MediaPlayerServiceListener {

    private TextView txtTitle, txtFile, txtArtist, txtAlbum, txtTimeCurrent, txtTimeTotal;
    private Button btnSwitchRandom, btnSwitchRepeat, btnPlayAndPause;
    private SeekBar seekBar;

    private MediaPlayerService mediaService;
    private ServiceConnection myServiceConnection;

    private String currentID;
    private List<String> songsID;

    private Song currentSong;
    private Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        initUI();

        try {
            Bundle bundle = this.getIntent().getExtras();
            currentID = bundle.getString(BundleExtra.CURRENT_ID, BundleExtra.DefaultValues.DEFAULT_ID);
            songsID = bundle.getStringArrayList(BundleExtra.SONGS_ID_LIST);
        } catch (Exception ex) {
        }

        initServiceConnection(savedInstanceState);
    }

    private void initiRunnableSeekBar() {
        myHandler = new Handler();
        myHandler.removeCallbacks(moveSeekBarThread);
        myHandler.postDelayed(moveSeekBarThread, 100);
    }

    private void initUI() {
        txtTitle = (TextView) findViewById(R.id.txtSongTitle);
        txtTitle.setTypeface(null, Typeface.BOLD);
        txtFile = (TextView) findViewById(R.id.txtSongFile);
        txtArtist = (TextView) findViewById(R.id.txtSongArtist);
        txtAlbum = (TextView) findViewById(R.id.txtSongAlbum);
        txtTimeCurrent = (TextView) findViewById(R.id.txtTimeCurrent);
        txtTimeTotal = (TextView) findViewById(R.id.txtTimeTotal);

        btnSwitchRandom = (Button) findViewById(R.id.btnSwitchRandom);
        btnSwitchRandom.setOnClickListener(this);

        btnSwitchRepeat = (Button) findViewById(R.id.btnSwitchRepeat);
        btnSwitchRepeat.setOnClickListener(this);

        btnPlayAndPause = (Button) findViewById(R.id.btnPlayPause);

        // init the seekbar
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaService.seekTo(progress);
                    seekBar.setProgress(progress);
                }
            }
        });
    }

    private void initServiceConnection(final Bundle savedInstanceState) {
        myServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder binder) {
                mediaService = ((MediaPlayerService.MyBinder) binder).getService();
                registerToMediaService();
                playBundleExtraSongs(savedInstanceState);
                updateUI();
                initiRunnableSeekBar();
            }

            public void onServiceDisconnected(ComponentName className) {
                mediaService = null;
            }
        };

        Intent intent = new Intent();
        intent.setAction(SoundBoxApplication.ACTION_MEDIA_PLAYER_SERVICE);
        this.startService(intent);
        this.bindService(intent, myServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.play, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnPlayPause) {
            mediaService.playAndPause();
            if (mediaService.isPlaying()) {
                btnPlayAndPause.setBackgroundResource(R.drawable.icon_pause);
            } else {
                btnPlayAndPause.setBackgroundResource(R.drawable.icon_play);
            }
        }
        else if (v.getId() == R.id.btnPrevSong) {
            mediaService.playPreviousSong();

        }
        else if (v.getId() == R.id.btnNextSong) {
            mediaService.playNextSong();
        }
        else if (v.getId() == R.id.btnSwitchRandom) {
            mediaService.changeRandomState();
            btnSwitchRandom.setBackgroundResource(randomStateIcon(mediaService.getRandomState()));
            Toast.makeText(this, randomStateToText(mediaService.getRandomState()), Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.btnSwitchRepeat) {
            mediaService.changeRepeatState();
            btnSwitchRepeat.setBackgroundResource(repeatStateIcon(mediaService.getRepeatState()));
            Toast.makeText(this, repeatStateToText(mediaService.getRepeatState()), Toast.LENGTH_SHORT).show();
        }
        else if (v.getId() == R.id.btnCurrentPlayList) {
            Intent intent = new Intent();
            intent.setAction(SoundBoxApplication.ACTION_SONGLIST_ACTIVITY);

            Bundle b = new Bundle();
            b.putString(BundleExtra.CURRENT_ID, currentSong.getID());
            b.putStringArrayList(BundleExtra.SONGS_ID_LIST, new ArrayList<String>(mediaService.getSongsIDList()));
            intent.putExtras(b);

            startActivity(intent);
        }
        else if (v.getId() == R.id.btnHomePlayActivity) {
            Intent activityIntent = new Intent();
            activityIntent.setAction(SoundBoxApplication.ACTION_MAIN_ACTIVITY);
            startActivity(activityIntent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaService != null)
            unbindService(myServiceConnection);
    }

    @Override
    public void onSongCompletion() {
        updateUI();
    }

    private void registerToMediaService() {
        mediaService.registerListener(this);
    }

    private void playBundleExtraSongs(Bundle savedInstanceState) {
        if (savedInstanceState == null && songsID != null) {
            mediaService.playSongs(currentID, songsID);
        }
    }

    public void updateUI() {
        currentSong = mediaService.getCurrentSong();

        txtTitle.setText(currentSong.getTitle());
        txtFile.setText(currentSong.getFile().getName());
        txtArtist.setText(currentSong.getArtist());
        txtAlbum.setText(currentSong.getAlbum());

        btnSwitchRandom.setBackgroundResource(randomStateIcon(mediaService.getRandomState()));
        btnSwitchRepeat.setBackgroundResource(repeatStateIcon(mediaService.getRepeatState()));

        if (mediaService.isPlaying()) {
            btnPlayAndPause.setBackgroundResource(R.drawable.icon_pause);
        } else {
            btnPlayAndPause.setBackgroundResource(R.drawable.icon_play);
        }
        int duration = mediaService.getDuration();
        txtTimeTotal.setText(formatDuration(duration));
        seekBar.setMax(duration);
    }

    private int repeatStateIcon(RepeatState state) {
        if (state == RepeatState.Off) {
            return R.drawable.icon_repeat_off;
        } else if (state == RepeatState.All) {
            return R.drawable.icon_repeat_all;
        } else {
            return R.drawable.icon_repeat_one;
        }
    }

    private int randomStateIcon(RandomState state) {
        if (state == RandomState.Off) {
            return R.drawable.icon_random_off;
        } else if (state == RandomState.Shuffled) {
            return R.drawable.icon_random_shuffled;
        } else {
            return R.drawable.icon_random_random;
        }
    }

    private String repeatStateToText(RepeatState state) {
        if (state == RepeatState.Off) {
            return "Repeat mode is Off";
        } else if (state == RepeatState.All) {
            return "Repeat mode is All";
        } else {
            return "Repeat mode is One";
        }
    }

    private String randomStateToText(RandomState state) {
        if (state == RandomState.Off) {
            return "Random mode is Off";
        } else if (state == RandomState.Shuffled) {
            return "Random mode is Shuffled";
        } else {
            return "Random mode is TrueRandom";
        }
    }

    private Runnable moveSeekBarThread = new Runnable() {
        public void run() {
            int position = mediaService.getCurrentPosition();
            txtTimeCurrent.setText(formatDuration(position));
            seekBar.setProgress(position);
            myHandler.postDelayed(this, 100);
        }
    };

    /**
     * Returns a string with the current duration on the mm:ss format (minutes:seconds)
     * 
     * @param duration in miliseconds
     * @return
     */
    private String formatDuration(int duration) {
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
    }
}
