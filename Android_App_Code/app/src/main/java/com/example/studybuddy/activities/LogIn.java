package com.example.studybuddy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studybuddy.R;
import com.example.studybuddy.model.BASE_URL;
import com.example.studybuddy.model.LogInResponse;
import com.example.studybuddy.model.LoginRequest;
import com.example.studybuddy.network.APIService;
import com.google.android.material.snackbar.Snackbar;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LogIn extends AppCompatActivity {

    private Dialog dialog;
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String TEXT = "token";

    RelativeLayout loginLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        dialog = new Dialog(this);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation;
    }

    public void f_pass(View view) {
    }


    public void signIn_lS(View view) {
        // Extract the email and password from the UI
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);
        String email_str = email.getText().toString();
        String password_str = password.getText().toString();

        String fieldsValidated = validateFields(email_str, password_str);

        if (fieldsValidated.equals("OK")){
            LoginRequest loginRequest = new LoginRequest(email_str, password_str);
            loginRequest.setEmail(email_str);
            loginRequest.setPassword(password_str);
            loading();
            logInUser(loginRequest);
        }

        else {
            show_err_snackBar(fieldsValidated);
        }
    }

    private void logInUser(LoginRequest loginRequest) {
        Call<LogInResponse> logInResponseCall = getAPIService().userLogin(loginRequest);
        logInResponseCall.enqueue(new Callback<LogInResponse>() {
            @Override
            public void onResponse(@NonNull Call<LogInResponse> call, @NonNull Response<LogInResponse> response) {
                LogInResponse lr = new LogInResponse();
                int code = response.code();
                lr.setCode(code);
                dialog.dismiss();
                if (response.isSuccessful()){
                    assert response.body() != null;
                    String token = response.body().getToken();
                    saveData(token);
                }
                else {
                    String message = code + " -1 " + ((code == 401) ? "Not Verified" : "User doesn't exist");
                    makeToast(message);
                }
            }

            @Override
            public void onFailure(Call<LogInResponse> call, Throwable t) {
                dialog.dismiss();
                makeToast("null");
            }
        });

    }

    private void saveData(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TEXT, token);
        editor.apply();
    }

    private static Retrofit getRetrofit(){

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();

        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL.getBaseUrl())
                .client(okHttpClient)
                .build();
    }

    public static APIService getAPIService(){
        return getRetrofit().create(APIService.class);
    }

    public void sup_lS(View view) {
        //Intent from LogIn to SignUp
        Intent intent = new Intent(getApplicationContext(), SignUp.class);
        startActivity(intent);
    }
    public void makeToast(String message){
        Toast.makeText(LogIn.this, message, Toast.LENGTH_LONG).show();
    }

    private void loading(){
        dialog.setContentView(R.layout.loading_message_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    void show_err_snackBar(String err_message){
        loginLayout = findViewById(R.id.login_layout);

        Snackbar err_snackbar = Snackbar.make(loginLayout, "", Snackbar.LENGTH_INDEFINITE);
        View custom_snackbar_view = getLayoutInflater().inflate(R.layout.err_snackbar, null);
        err_snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout =(Snackbar.SnackbarLayout) err_snackbar.getView();
        snackbarLayout.setPadding(0,0,0,0);
        TextView errText = custom_snackbar_view.findViewById(R.id.sb_error_text);
        errText.setText(err_message);
        (custom_snackbar_view.findViewById(R.id.submit_sb)).setOnClickListener(view -> err_snackbar.dismiss());
        snackbarLayout.addView(custom_snackbar_view,0);
        err_snackbar.show();

    }

    private String validateFields(String email, String password){
        String output = "OK";
        if (!(email.isEmpty() && password.isEmpty())){
            loading();
            dialog.dismiss();
        }
        else {
            output = getString(R.string.empty_fields);
        }
        return output;
    }
}