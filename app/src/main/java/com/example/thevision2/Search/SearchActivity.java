package com.example.thevision2.Search;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.thevision2.R;

import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private LinearLayout searchBtn,textRecogBtn;
    private TextToSpeech tts;
    private String stringText;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        stringText = "Click Upper part of your screen for search and Down part of your screen for text recognition";
        textToSpeech();

        setReference();
        setButton();
    }

    private void setReference() {
        searchBtn = findViewById(R.id.search_layout);
        textRecogBtn = findViewById(R.id.text_reco_layout);
    }

    private void setButton() {
        //search btn
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(SearchActivity.this,SearchMainActivity.class);
                startActivity(intent);
            }
        });

        //text recognition btn
        textRecogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Text Recognition",Toast.LENGTH_SHORT).show();
            }
        });
    }

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

    @Override
    protected void onPause() {
        tts.shutdown();
        super.onPause();
    }
}