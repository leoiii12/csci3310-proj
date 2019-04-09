package com.cuhk.travelligent;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import io.swagger.client.apis.AuthApi;
import io.swagger.client.apis.CommentApi;
import io.swagger.client.apis.RatingApi;
import io.swagger.client.apis.SightApi;
import io.swagger.client.apis.UserApi;
import io.swagger.client.models.AuthenticateInput;
import io.swagger.client.models.AuthenticateOutput;
import io.swagger.client.models.CheckAccountInput;
import io.swagger.client.models.CheckAccountOutput;
import io.swagger.client.models.CreateCommentInput;
import io.swagger.client.models.CreateCommentOutput;
import io.swagger.client.models.CreateRatingInput;
import io.swagger.client.models.CreateRatingOutput;
import io.swagger.client.models.CreateSightInput;
import io.swagger.client.models.CreateSightOutput;
import io.swagger.client.models.GetMyUserOutput;
import io.swagger.client.models.GetSightInput;
import io.swagger.client.models.GetSightOutput;
import io.swagger.client.models.ListSightsOutput;
import io.swagger.client.models.SignUpInput;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    AuthApi authApi = new AuthApi();
                    AuthenticateInput authenticateInput = new AuthenticateInput("choimankin@gmail.com", "12345678");
                    AuthenticateOutput authenticateOutput = authApi.apiAuthAuthenticate(authenticateInput, "");

                    UserApi userApi = new UserApi();
                    GetMyUserOutput getMyUserOutput = userApi.apiUserGetMyUser("Bearer " + authenticateOutput.getAccessToken());

                    CheckAccountInput checkAccountInput = new CheckAccountInput("choimankin@outlook.com");
                    CheckAccountOutput checkAccountOutput = authApi.apiAuthCheckAccount(checkAccountInput, "");

                    if (checkAccountOutput.getAccountStatus() == 0) {
                        SignUpInput signUpInput = new SignUpInput("choimankin@outlook.com", "12345678", "Leo", "Choi", 1000);
                        authApi.apiAuthSignUp(signUpInput, "");
                    }

                    authenticateInput = new AuthenticateInput("choimankin@outlook.com", "12345678");
                    authenticateOutput = authApi.apiAuthAuthenticate(authenticateInput, "");

                    SightApi sightApi = new SightApi();
                    ListSightsOutput listSightsOutput = sightApi.apiSightList("Bearer " + authenticateOutput.getAccessToken());

                    if (listSightsOutput.getSights().length == 0) {
                        CreateSightInput createSightInput = new CreateSightInput("天一合一", 22.4215355, 114.2076754);
                        CreateSightOutput createSightOutput = sightApi.apiSightCreate(createSightInput, "Bearer " + authenticateOutput.getAccessToken());

                        System.out.println(createSightOutput);
                    }

                    GetSightInput getSightInput = new GetSightInput(1);
                    GetSightOutput getSightOutput = sightApi.apiSightGet(getSightInput, "Bearer " + authenticateOutput.getAccessToken());

                    System.out.println(getSightOutput);

                    CommentApi commentApi = new CommentApi();
                    CreateCommentInput createCommentInput = new CreateCommentInput("Hello World", null, 1, null);
                    CreateCommentOutput createCommentOutput = commentApi.apiCommentCreate(createCommentInput, "Bearer " + authenticateOutput.getAccessToken());

                    System.out.println(createCommentOutput);

                    RatingApi ratingApi = new RatingApi();
                    CreateRatingInput createRatingInput = new CreateRatingInput(4.9, null, 1, null);
                    CreateRatingOutput createRatingOutput = ratingApi.apiRatingCreate(createRatingInput, "Bearer " + authenticateOutput.getAccessToken());

                    System.out.println(createRatingOutput);

                    getSightInput = new GetSightInput(1);
                    getSightOutput = sightApi.apiSightGet(getSightInput, "Bearer " + authenticateOutput.getAccessToken());

                    System.out.println(getSightOutput);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);


    }

}
