package com.cuhk.travelligent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.swagger.client.apis.AuthApi;
import io.swagger.client.apis.UserApi;
import io.swagger.client.models.AuthenticateInput;
import io.swagger.client.models.AuthenticateOutput;
import io.swagger.client.models.GetMyUserOutput;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                AuthApi authApi = new AuthApi();
                AuthenticateInput authenticateInput = new AuthenticateInput("choimankin@gmail.com", "12345678");
                AuthenticateOutput authenticateOutput = authApi.apiAuthAuthenticate(authenticateInput, "");

                // This token should be preserved
                System.out.println(authenticateOutput.getAccessToken());

                UserApi userApi = new UserApi();
                GetMyUserOutput getMyUserOutput = userApi.apiUserGetMyUser("Bearer " + authenticateOutput.getAccessToken());

                System.out.println(getMyUserOutput);
            }
        });

        thread.start();
    }

}
