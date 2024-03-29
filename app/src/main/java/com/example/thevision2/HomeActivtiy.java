package com.example.thevision2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.example.thevision2.Battery.BatteryActivity;
import com.example.thevision2.Messages.MessageActivity;
import com.example.thevision2.Search.SearchActivity;
import com.example.thevision2.Study.StudyActivity;
import com.pedromassango.doubleclick.DoubleClick;
import com.pedromassango.doubleclick.DoubleClickListener;
import java.util.Locale;

public class HomeActivtiy extends AppCompatActivity {

    private RelativeLayout messagesBod,batteryStatus,search,study;
    private TextToSpeech tts;
    private String stringText;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_activtiy);
        intent = new Intent();
        changeStatusBarColor();
        setReference();
        setButton();
    }

    @Override
    protected void onStart() {
        stringText = "Welcome User";
        textToSpeech();
        super.onStart();
    }

    //change statusbar color
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.nevi_light_blue));
        }
    }

    //set reference value
    public void setReference() {
        messagesBod = findViewById(R.id.message_1);
        batteryStatus = findViewById(R.id.battery_1);
        search = findViewById(R.id.search_1);
        study = findViewById(R.id.study_1);
    }

    private void setButton(){
        setMessage();
        setBattery();
        setSearch();
        setStudy();
    }

    //convert text into speech
    private void textToSpeech() {
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });
        final Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {

                tts.setSpeechRate(0.8f);
                tts.setPitch(1.0f);
                tts.speak(stringText, TextToSpeech.QUEUE_FLUSH, null, null);

            }
        }, 500);
    }

    //message
    private void setMessage(){
        messagesBod.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                stringText = "Messages";
                textToSpeech();
            }

            @Override
            public void onDoubleClick(View view) {
              startActivity(new Intent(HomeActivtiy.this, MessageActivity.class));
            }
        }));

    }

    //battery
    private void setBattery(){
        batteryStatus.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                stringText = "Battery level and status";
                textToSpeech();
            }

            @Override
            public void onDoubleClick(View view) {
                startActivity(new Intent(HomeActivtiy.this, BatteryActivity.class));
            }
        }));
    }

    //Search
    private void setSearch(){
        search.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                stringText = "Search and Other thing";
                textToSpeech();
            }

            @Override
            public void onDoubleClick(View view) {
                startActivity(new Intent(HomeActivtiy.this, SearchActivity.class));
            }
        }));
    }

    //Study
    private void setStudy(){
        study.setOnClickListener(new DoubleClick(new DoubleClickListener() {
            @Override
            public void onSingleClick(View view) {
                stringText = "Study";
                textToSpeech();
            }

            @Override
            public void onDoubleClick(View view) {
                startActivity(new Intent(HomeActivtiy.this, StudyActivity.class));
            }
        }));
    }

    //On go back
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent = new Intent(HomeActivtiy.this,SignOutAndExitActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onPause() {
        if (tts != null){
            tts.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onRestart() {
        textToSpeech();
        super.onRestart();
    }
}