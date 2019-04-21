package com.cuhk.travelligent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import io.swagger.client.apis.AuthApi;
import io.swagger.client.apis.UserApi;
import io.swagger.client.models.AuthenticateInput;
import io.swagger.client.models.AuthenticateOutput;
import io.swagger.client.models.CheckAccountInput;
import io.swagger.client.models.CheckAccountOutput;
import io.swagger.client.models.GetMyUserOutput;
import io.swagger.client.models.MyUserDto;
import io.swagger.client.models.SignUpInput;

public class MainActivity extends AppCompatActivity {

    AuthApi authApi = new AuthApi();
    UserApi userApi = new UserApi();

    private void logInAsAdmin() {
        AuthenticateInput authenticateInput = new AuthenticateInput("choimankin@gmail.com", "12345678");
        AuthenticateOutput authenticateOutput = authApi.apiAuthAuthenticate(authenticateInput, "");

        GetMyUserOutput getMyUserOutput = userApi.apiUserGetMyUser("Bearer " + authenticateOutput.getAccessToken());
        MyUserDto myUser = getMyUserOutput.getMyUser();

        SharedPreferences.Editor editor = getSharedPreferences(Configs.PREFS, MODE_PRIVATE).edit();
        editor.putInt(Configs.PREFS_USER_ID, myUser.getId());
        editor.putString(Configs.PREFS_EMAIL_ADDRESS, myUser.getEmailAddress());
        editor.putString(Configs.PREFS_FIRST_NAME, myUser.getFirstName());
        editor.putString(Configs.PREFS_LAST_NAME, myUser.getLastName());
        editor.putString(Configs.PREFS_ACCESS_TOKEN, authenticateOutput.getAccessToken());
        editor.apply();
    }

    private void logInAsNormalUser() {
        CheckAccountInput checkAccountInput = new CheckAccountInput("choimankin@outlook.com");
        CheckAccountOutput checkAccountOutput = authApi.apiAuthCheckAccount(checkAccountInput, "");

        if (checkAccountOutput.getAccountStatus() == 0) {
            SignUpInput signUpInput = new SignUpInput("choimankin@outlook.com", "12345678", "Leo", "Choi", 1000);
            authApi.apiAuthSignUp(signUpInput, "");
        }

        AuthenticateInput authenticateInput = new AuthenticateInput("choimankin@outlook.com", "12345678");
        AuthenticateOutput authenticateOutput = authApi.apiAuthAuthenticate(authenticateInput, "");

        GetMyUserOutput getMyUserOutput = userApi.apiUserGetMyUser("Bearer " + authenticateOutput.getAccessToken());
        MyUserDto myUser = getMyUserOutput.getMyUser();

        SharedPreferences.Editor editor = getSharedPreferences(Configs.PREFS, MODE_PRIVATE).edit();
        editor.putInt(Configs.PREFS_USER_ID, myUser.getId());
        editor.putString(Configs.PREFS_EMAIL_ADDRESS, myUser.getEmailAddress());
        editor.putString(Configs.PREFS_FIRST_NAME, myUser.getFirstName());
        editor.putString(Configs.PREFS_LAST_NAME, myUser.getLastName());
        editor.putString(Configs.PREFS_ACCESS_TOKEN, authenticateOutput.getAccessToken());
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    logInAsNormalUser();

                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

}
