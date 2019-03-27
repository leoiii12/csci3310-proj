package com.cuhk.travelligent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.swagger.client.apis.AuthApi;
import io.swagger.client.models.AuthenticateInput;
import io.swagger.client.models.AuthenticateOutput;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                AuthApi apiInstance = new AuthApi();
                AuthenticateInput input = new AuthenticateInput("choimankin@gmail.com", "12345678");

                try {
                    AuthenticateOutput result = apiInstance.apiAuthAuthenticate(input);
                    System.out.println(result.getAccessToken());
                } catch (Exception e) {
                    System.err.println("Exception when calling AuthApi#apiAuthAuthenticate");
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

}
