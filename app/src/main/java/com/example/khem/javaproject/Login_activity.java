package com.example.khem.javaproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

public class Login_activity extends AppCompatActivity {

    CallbackManager callbackManager;
    LoginButton loginButton;
    PrefUtil prefUtil;
    Bundle parameters;
    Button show;
    EditText email;
    EditText pass;
    int showclick = 0;

    Button sigin_button;
    Button login_button;

    public void Login(){}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.login_activity);

        show = (Button)findViewById(R.id.eye);
        email = (EditText)findViewById(R.id.email_main);
        pass = (EditText)findViewById(R.id.password_main);
        login_button = (Button)findViewById(R.id.login_main);

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        callbackManager = CallbackManager.Factory.create();
        loginwithFB();
        sigin();

        login_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {



                Intent mainpage = new Intent(Login_activity.this,main.class);
                mainpage.putExtra("email",email.getText().toString());
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(showclick == 0){
                    pass.setInputType(InputType.TYPE_CLASS_TEXT);
                    showclick = 1;
                }
                else{
                    pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showclick = 0;
                }
            }
        });

    }
    private Bundle getFacebookData(JSONObject object) {
        Bundle bundle = new Bundle();

        try {
            String id = object.getString("id");
            URL profile_pic;
            try {
                profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?type=large");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));



            prefUtil.saveFacebookUserInfo(object.getString("first_name"),
                    object.getString("last_name"), object.getString("email")
                    , profile_pic.toString());

        } catch (Exception e) {
            Log.d("getFacebookData", "BUNDLE Exception : "+e.toString());
        }

        return bundle;

    }
    private void loginwithFB(){
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        prefUtil = new PrefUtil(Login_activity.this);

                        String accessToken = loginResult.getAccessToken().getToken();

                        // save accessToken to SharedPreference
                        prefUtil.saveAccessToken(accessToken);

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject jsonObject,
                                                            GraphResponse response) {

                                        // Getting FB User Data
                                        Bundle facebookData = getFacebookData(jsonObject);

                                        Intent newpage = new Intent(Login_activity.this, Register.class);
                                        newpage.putExtras(facebookData);
                                        startActivity(newpage);

                                    }
                                });

                        parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }


                    @Override
                    public void onCancel () {
                    }

                    @Override
                    public void onError (FacebookException e){
                        e.printStackTrace();
                        deleteAccessToken();
                    }
                }
        );
    }


    public void sigin(){
        sigin_button = (Button)findViewById(R.id.signin);
        sigin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newpage = new Intent(Login_activity.this, Register.class);

                startActivity(newpage);
            }
        });


    }
    private void deleteAccessToken() {
        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {

                if (currentAccessToken == null){
                    //User logged out
                    prefUtil.clearToken();
                    LoginManager.getInstance().logOut();
                }
            }
        };
    }


    protected boolean check_user(String e,String p){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference();

        ref.child("User").addValueEventListener()

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }



}
