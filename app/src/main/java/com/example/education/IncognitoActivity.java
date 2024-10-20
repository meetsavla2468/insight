package com.example.education;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class IncognitoActivity extends AppCompatActivity {

    Toolbar toolbar;
    ImageView home_btn,mic,cut;
    WebView webView;
    EditText editText;
    ProgressBar progressBar;
    private final int mic_req_code=10;
    private  boolean desktop_mode_flag=false;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incognito);
        home_btn=findViewById(R.id.home_btn);
        toolbar=findViewById(R.id.toolbar);
        webView=findViewById(R.id.webView);
        mic=findViewById(R.id.mic);
        cut=findViewById(R.id.cut);
        progressBar=findViewById(R.id.progressBar);
//        getSupportActionBar().hide();
        setSupportActionBar(toolbar);
        editText=findViewById(R.id.search_editText);
//        findViewById(R.id.search_ll).setBackgroundResource(R.drawable.search_bg);
        WebSettings webSettings= webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebViewClient(new MyWebViewClient());
//        Intent intent=getIntent();
//        String txt=intent.getStringExtra("txt");

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });

        webView.loadUrl("https://google.com");


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(editText.getText().toString().isEmpty()){
                    cut.setVisibility(View.INVISIBLE);
                    mic.setVisibility(View.VISIBLE);
                }else{
                    cut.setVisibility(View.VISIBLE);
                    mic.setVisibility(View.INVISIBLE);}
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editText.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i== EditorInfo.IME_ACTION_GO || i== EditorInfo.IME_ACTION_DONE){
                search_url(editText.getText().toString());
                return true;
            }
            return false;
        });


        home_btn.setOnClickListener(view -> {
                    startActivity(new Intent(this, IncognitoActivity.class));
                    finish();
                }
        );



        cut.setOnClickListener(view -> editText.setText(""));

        mic.setOnClickListener(view -> {
            Intent intent_mic=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent_mic.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent_mic.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());



            startActivityForResult(intent_mic,mic_req_code);

        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK ){
            if(requestCode==mic_req_code && data!=null){
                ArrayList<String> get_mic_input=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                search_url(get_mic_input.get(0));
            }
        }
    }

    public void search_url(String url){
        boolean is_url= Patterns.WEB_URL.matcher(url).matches();
        if(is_url){
            webView.loadUrl(url);
        }
        else{
            webView.loadUrl("https://google.com/search?q="+url);
        }
    }











    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //            setDesktopMode(view,desktop_mode_flag);
            super.onPageStarted(view, url, favicon);
            InputMethodManager go_manager=(InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            go_manager.hideSoftInputFromWindow(editText.getWindowToken(),0);
            editText.clearFocus();
            editText.setText(webView.getUrl());
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            progressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.refresh) {
            webView.reload();
        }
        else {
            desktopDefineFun(item);
        }

        return super.onOptionsItemSelected(item);
    }

    public void desktopDefineFun(MenuItem item) {
        if(!desktop_mode_flag){
            desktop_mode_flag=true;
            setDesktopMode(webView, true);
            item.setTitle("Mobile Mode");
        }
        else{
            desktop_mode_flag=false;
            setDesktopMode(webView, false);
            item.setTitle("Desktop Mode");
        }
    }

    public void setDesktopMode(WebView webView, boolean b) {
        String newUserAgent=webView.getSettings().getUserAgentString();
        if(b) {

            try {
                String uas = webView.getSettings().getUserAgentString();
                String androidDosString = webView.getSettings().getUserAgentString().substring(uas.indexOf("("), uas.indexOf(")")+1);
                newUserAgent = webView.getSettings().getUserAgentString().replace(androidDosString, "(X11; Linux x86_64)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            newUserAgent=null;
        }
        webView.getSettings().setUserAgentString(newUserAgent);
        webView.getSettings().setUseWideViewPort(b);
        webView.getSettings().setLoadWithOverviewMode(b);
        webView.reload();
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
//            ((MenuItem)findViewById(R.id.desktop_mode)).setTitle(R.string.dsk);
//            if(desktop_mode_flag==true){
//                myItem.setTitle("Mobile Mode");
//            }
//            else{
//                myItem.setTitle("Desktop Mode");
//            }
        }
        else{
            super.onBackPressed();
        }
    }
}