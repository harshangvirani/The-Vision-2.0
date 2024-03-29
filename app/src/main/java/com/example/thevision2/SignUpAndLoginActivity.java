package com.example.thevision2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthActionCodeException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class SignUpAndLoginActivity extends AppCompatActivity{

    //Define
    private Button login,signup;
    private ProgressBar progressBar;
    private Boolean loginFlag = false;
    private Boolean signupFlag = false;
    private int flag = 0;
    private String stringText;
    private TextToSpeech tts;
    private String tag = "Activity";
    private String email,password = "";
    private FirebaseAuth mAuth;
    private boolean wifi = false;
    private boolean mobilData = false;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_and_login);


        //Check user is login or not
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null){
            stringText = "Click Upper part of your screen for Login and Down part of your screen for SignUp";
            textToSpeech();
        }else{
            finish();
            startActivity(new Intent(SignUpAndLoginActivity.this,HomeActivtiy.class));
        }
        changeStatusBarColor();
        setReference();
        setButton();
    }

    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.light_blue));
        }
    }

    private void setReference(){
        login = findViewById(R.id.login_btn);
        signup = findViewById(R.id.sign_up_btn);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setButton(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()){
                    loginFlag = true;
                    data();
                    Toast.makeText(getApplicationContext(),"Internet is on",Toast.LENGTH_SHORT).show();
                }else {
                    stringText = "Please turn on internet";
                    textToSpeech();
                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()){
                    signupFlag = true;
                    data();
                    Toast.makeText(getApplicationContext(),"Internet is on",Toast.LENGTH_SHORT).show();
                }else {
                    stringText = "Please turn on internet";
                    textToSpeech();
                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Internet Connection check
    private boolean isConnected(){
        boolean connected = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo !=null && networkInfo.isAvailable() && networkInfo.isConnected();
            return connected;
        }catch (Exception e){
            e.printStackTrace();
            Log.d("Connection Exception",e.getMessage());
        }
        return connected;
    }

    private void data(){
        if (flag == 0){
            stringText = "Speak Your Email";
            textToSpeech();
            setHandelar();
        }
        if (flag == 1){
            stringText = "Speak Your Password";
            textToSpeech();
            setHandelar();
        }
    }

    private void setHandelar() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                speechToText();
            }
        },7000);
    }

    //Text to speech
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

    //Speech to text
    private void speechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        Log.d(tag, "Inside speech here");
        try {
            startActivityForResult(intent, 1000);
        } catch (ActivityNotFoundException e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Here", "Inside onActivityResult");
        switch (requestCode) {
            case 1000: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    result.trimToSize();

                    if (flag == 0) {
                        email = result.get(0).toLowerCase().trim();
                        Log.d(tag,email);
                        email = email.replaceAll(" ", "");
                        Log.d(tag,email);
                        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            stringText = "Your Email is invalid";
                            textToSpeech();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    recreate();
                                }
                            }, 3000);
                            return;
                        }

                        flag++;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                signup.performClick();
                            }
                        }, 2000);

                        return;
                    }
                    if (flag == 1) {
                        password = result.get(0).toLowerCase().trim();
                        password = password.replaceAll(" ", "");
                        Log.d(tag,password);
                        //Login
                        if (loginFlag){
                            progressBar.setVisibility(View.VISIBLE);
                            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(SignUpAndLoginActivity.this,HomeActivtiy.class);
                                       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        stringText = "Your email or password is incorrect";
                                        textToSpeech();
                                        HandlerSignup();
                                    }
                                }
                            });
                            loginFlag = false;
                            return;
                        }

                        //Sign up
                        if (signupFlag) {
                            progressBar.setVisibility(View.VISIBLE);
                            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        stringText = "Sign up successfully";
                                        textToSpeech();
                                        HandlerSignup();
                                        SendDataToRealTimeDatabase(email,password);
                                    }
                                    else {
                                        if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                            stringText = "User is already register";
                                            textToSpeech();
                                            HandlerSignup();
                                        }else {
                                            stringText = "Something is wrong";
                                            textToSpeech();
                                            HandlerSignup();
                                        }
                                    }
                                }
                            });
                            signupFlag = false;
                            return;
                        }
                    }

                }
            }
            break;
        }
    }

    private void HandlerSignup() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recreate();
            }
        }, 3000);
    }

    //Send data to firebase realtime database
    private void SendDataToRealTimeDatabase(String email, String password){
        try {
            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("Students");
            String id = databaseReference.push().getKey();

            UserHelperClass userHelperClass = new UserHelperClass(email,password);

            databaseReference.child(id).setValue(userHelperClass);


        }catch (Exception e){
            e.printStackTrace();
        }
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