package com.loginwithfacebook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager=null;


    private TextView textViewInfo;
    private LoginButton loginButton;

    private ImageView imageProfilePic;


    private TextView textViewLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_main);


        textViewInfo = (TextView)findViewById(R.id.textViewInfo);
        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile email");

        textViewLink= (TextView)findViewById(R.id.textViewLink);

        imageProfilePic= (ImageView)findViewById(R.id.imageProfilePic);

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {



                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                       /* textViewInfo.setText("User Name  " +
                                loginResult.getAccessToken().getUserId() + "\n" +
                                "Auth Token: " + loginResult.getAccessToken().getToken());*/

                        requestData();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        textViewInfo.setText("Login attempt cancelled.");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        textViewInfo.setText("Login attempt cancelled.");
                    }

                });




    }

    public void requestData(){
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    JSONObject json = response.getJSONObject();
                    if(json != null){
                        String text = "<b>Name :</b> "+json.getString("name")+"<br><br><b>Email :</b> "+json.getString("email")+"<br><br><b>Profile link :</b> ";
                        textViewInfo.setText(Html.fromHtml(text));
                        Glide.with(MainActivity.this).load(json.getJSONObject("picture").getJSONObject("data").getString("url")).centerCrop().placeholder(R.mipmap.ic_launcher).crossFade().into(imageProfilePic);

                        textViewLink.setText(json.getString("link"));
                        Linkify.addLinks(textViewLink, Linkify.WEB_URLS);


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
