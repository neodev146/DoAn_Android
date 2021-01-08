package com.example.doan_android.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.loader.content.AsyncTaskLoader;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.doan_android.Adapter.AdapterPlayerMusic;
import com.example.doan_android.Adapter.AdapterSong;
import com.example.doan_android.Fragment.music_player;
import com.example.doan_android.Model.Album;
import com.example.doan_android.Model.Baihat;
import com.example.doan_android.Model.Banner;
import com.example.doan_android.Model.Playlist;
import com.example.doan_android.R;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.http.Tag;

public class play_music extends AppCompatActivity {


    //Add view
    private TabLayout tabPlayerMusic;
    private ViewPager view_pager_music;
    //public static ArrayList<Baihat> baihatArrayList = new ArrayList<>();
    ArrayList<Baihat> baihatArrayList = new ArrayList<>();
    //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    AdapterSong adapterSong;
    //AdapterPlayerMusic adapterPlayerMusic;
    AdapterPlayerMusic adaptermusic = new AdapterPlayerMusic(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

    Baihat baihat;

    TextView txtMusicTenBaiHat, txtMusicTenNgheSi, txtTimeSong, txtTotalTimeSong;
    SeekBar barTime;
    ImageView btnPlayPre, btnPlay, btnPlayNext, imgMusicPlayer;
    Button btnBack;

    MediaPlayer mediaPlayer;
    music_player musicplayer;

    boolean next = false;
    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        //Init
        LinkViews();
        EventClick();
        GetData();

        //txtMusicTenBaiHat.setText(baihatArrayList.get(0).getTenBaihat());
        //System.out.print( "LOG: " + baihatArrayList.get(0).getTenBaihat());

        if (baihatArrayList.size() > 0){
            //musicplayer.imageURL=baihatArrayList.get(0).getHinhBaihat();
            Picasso.get().load(baihatArrayList.get(0).getHinhBaihat()).into(imgMusicPlayer);
            txtMusicTenBaiHat.setText(baihatArrayList.get(0).getTenBaihat());
            txtMusicTenNgheSi.setText(baihatArrayList.get(0).getTenCasi());
            new PlayMp3File().execute(baihatArrayList.get(0).getUrlBaihat());
            System.out.println("LOG URL: " + baihatArrayList.get(0).getUrlBaihat());
            System.out.println("LOG CA SI: " + baihatArrayList.get(0).getTenCasi());
            btnPlay.setImageResource(R.drawable.ic_baseline_pause_24);
        }

        //BACK
        btnBack.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              Intent intent = new Intent(play_music.this, MainActivity.class);
              startActivity(intent);
          }
        });



    }

    private void LinkViews(){
        //tabPlayerMusic = findViewById(R.id.tabPlayerMusic);
        //view_pager_music = findViewById(R.id.view_pager_music);
        btnBack = findViewById(R.id.btnBack);
        txtMusicTenBaiHat = findViewById(R.id.txtMusicTenBaiHat);
        txtMusicTenNgheSi = findViewById(R.id.txtMusicTenNgheSi);
        txtTimeSong = findViewById(R.id.txtTimeSong);
        txtTotalTimeSong = findViewById(R.id.txtTotalTimeSong);
        btnPlay = findViewById(R.id.btnPlay);
        btnPlayPre = findViewById(R.id.btnPlayPre);
        btnPlayNext = findViewById(R.id.btnPlayNext);
        barTime = findViewById(R.id.barTime);
        imgMusicPlayer = findViewById(R.id.imgMusicPlayer);
        //view_pager_music.setAdapter(adaptermusic);
        //tabPlayerMusic.setupWithViewPager(view_pager_music);
        musicplayer = (music_player) adaptermusic.getItem(0);

        //Set icon
        //tabPlayerMusic.getTabAt(0).setIcon(R.drawable.ic_baseline_music_note_24).select();
        //tabPlayerMusic.getTabAt(1).setIcon(R.drawable.ic_baseline_text_fields_24);
    }

    class PlayMp3File extends AsyncTask<String, Void, String> {

          @Override
          protected String doInBackground(String... strings) {
              return strings[0];
          }

          @Override
          protected void onPostExecute(String baihat) {
              super.onPostExecute(baihat);
              try {
                  mediaPlayer = new MediaPlayer();
                  mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                  mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                      @Override
                      public void onCompletion(MediaPlayer mp) {
                          mediaPlayer.stop();
                          mediaPlayer.reset();
                      }
                  });
                  mediaPlayer.setDataSource(baihat);
                  mediaPlayer.prepare();
              } catch (IOException e) {
                  e.printStackTrace();
              }
              mediaPlayer.start();
              TimeSong();
              UpdateTime();
          }
      }

      private void TimeSong() {
          SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
          txtTotalTimeSong.setText(simpleDateFormat.format(mediaPlayer.getDuration()));
          barTime.setMax(mediaPlayer.getDuration());
      }

      private void UpdateTime(){
          Handler handler = new Handler();
          handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null){
                    barTime.setProgress(mediaPlayer.getCurrentPosition());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                    txtTimeSong.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                    handler.postDelayed(this, 1000);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                      @Override
                      public void onCompletion(MediaPlayer mp) {
                          next = true;
                      }
                    });
                }
            }
          },1000);
          Handler handler1 = new Handler();
          handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
              if (next == true){
                if(position < (baihatArrayList.size())){
                  btnPlay.setImageResource(R.drawable.ic_baseline_pause_24);
                  position++;
                  if(position > (baihatArrayList.size() - 1)){
                    position = 0;
                  }
                  new PlayMp3File().execute(baihatArrayList.get(position).getUrlBaihat());
                  //musicplayer.imageURL = (baihatArrayList.get(position).getHinhBaihat());
                  //NEXT LOG
                  Picasso.get().load(baihatArrayList.get(position).getHinhBaihat()).into(imgMusicPlayer);
                  System.out.println("NEXT LOG: " + baihatArrayList.get(position).getHinhBaihat());
                  txtMusicTenBaiHat.setText(baihatArrayList.get(position).getTenBaihat());
                  txtMusicTenNgheSi.setText(baihatArrayList.get(position).getTenCasi());
                  //musicplayer.setRefreshing

                }
              btnPlayPre.setClickable(false);
              btnPlayNext.setClickable(false);
              Handler handler1 = new Handler();
              handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                  btnPlayPre.setClickable(true);
                  btnPlayNext.setClickable(true);
                }
              }, 1000);
              next = false;
              }
              else{
                  handler1.postDelayed(this, 1000);
              }

            }
          }, 1000);
      }

    private void GetData() {
        Intent intent = getIntent();
        baihatArrayList.clear();
        if (intent != null) {
            if (intent.hasExtra("baihats")) {
                baihat = (Baihat) intent.getParcelableExtra("baihats");
                baihatArrayList.add(baihat);
            }
            if (intent.hasExtra("danhsach") ) {
                ArrayList<Baihat> mangbaihat= intent.getParcelableArrayListExtra("danhsach");
                for( Baihat item : mangbaihat)
                {
                    // tất cả các bài hát được aadd vào bài hát array liostK
                     baihatArrayList.add(item);
                }
            }
        }
    }

    private void EventClick(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
          @Override
          public void run() {
              if (adaptermusic.getItem(1) != null){
                if(baihatArrayList.size() > 0){
                    //musicplayer.imageURL=baihatArrayList.get(0).getHinhBaihat();
                    Picasso.get().load(baihatArrayList.get(position).getHinhBaihat()).into(imgMusicPlayer);
                    handler.removeCallbacks(this);
                }
                else{
                      handler.postDelayed(this, 300);
                }
              }
          }
        }, 500);
        btnPlay.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                btnPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
              }
              else
              {
                mediaPlayer.start();
                btnPlay.setImageResource(R.drawable.ic_baseline_pause_24);
              }
          }
        });
        barTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
          @Override
          public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

          }

          @Override
          public void onStartTrackingTouch(SeekBar seekBar) {

          }

          @Override
          public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
          }
        });

        btnPlayNext.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
                if(baihatArrayList.size() > 0){
                    if(mediaPlayer.isPlaying() || mediaPlayer != null){
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    if(position < (baihatArrayList.size())){
                        btnPlay.setImageResource(R.drawable.ic_baseline_pause_24);
                        position++;
                        if(position > (baihatArrayList.size() - 1)){
                          position = 0;
                        }
                        new PlayMp3File().execute(baihatArrayList.get(position).getUrlBaihat());
                        musicplayer.imageURL = (baihatArrayList.get(position).getHinhBaihat());
                        //NEXT LOG
                        //System.out.println("NEXT LOG: " + baihatArrayList.get(position).getHinhBaihat());
                        Picasso.get().load(baihatArrayList.get(position).getHinhBaihat()).into(imgMusicPlayer);
                        txtMusicTenBaiHat.setText(baihatArrayList.get(position).getTenBaihat());
                        txtMusicTenNgheSi.setText(baihatArrayList.get(position).getTenCasi());
                        UpdateTime();
                        //musicplayer.setRefreshing
                    }
                }
                btnPlayPre.setClickable(false);
                btnPlayNext.setClickable(false);
                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                    btnPlayPre.setClickable(true);
                    btnPlayNext.setClickable(true);
                  }
                }, 1000);
          }
        });

        //PRE
        btnPlayPre.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if(baihatArrayList.size() > 0){
              if(mediaPlayer.isPlaying() || mediaPlayer != null){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
              }
              if(position < (baihatArrayList.size())){
                btnPlay.setImageResource(R.drawable.ic_baseline_pause_24);
                position--;

                if(position < 0){
                  position = baihatArrayList.size() - 1;
                }

                new PlayMp3File().execute(baihatArrayList.get(position).getUrlBaihat());
                //musicplayer.imageURL = (baihatArrayList.get(position).getHinhBaihat());
                Picasso.get().load(baihatArrayList.get(position).getHinhBaihat()).into(imgMusicPlayer);
                txtMusicTenBaiHat.setText(baihatArrayList.get(position).getTenBaihat());
                txtMusicTenNgheSi.setText(baihatArrayList.get(position).getTenCasi());
                UpdateTime();
              }
            }
            btnPlayPre.setClickable(false);
            btnPlayNext.setClickable(false);
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
              @Override
              public void run() {
                btnPlayPre.setClickable(true);
                btnPlayNext.setClickable(true);
              }
            }, 1000);

          }
        });

    }
}
