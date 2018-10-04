package com.sensingchange.monitoringprobe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.sensingchange.monitoringprobe.model.User;
import com.sensingchange.monitoringprobe.model.UserModel;
import com.sensingchange.monitoringprobe.remote.ApiUtils;
import com.sensingchange.monitoringprobe.remote.UserService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    Button btnLogin;
    EditText edtPassword;
    EditText edtUsername;
    RelativeLayout signUp;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        signUp = findViewById(R.id.signUp);
        userService = ApiUtils.getUserService();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = MainActivity.this.edtUsername.getText().toString();
                String password = MainActivity.this.edtPassword.getText().toString();
                if (validateLogin(username, password)) {
                    doLogin(username, password);
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
//                startActivity(intent);
            }
        });
    }

    private boolean validateLogin(String username, String password) {
        if (username != null) {
            if (username.trim().length() != 0) {
                if (password != null) {
                    if (password.trim().length() != 0) {
                        return true;
                    }
                }
                Toast.makeText(MainActivity.this, "Password is required", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        Toast.makeText(MainActivity.this, "Email is required", 0).show();
        return false;
    }

    private void doLogin(final String username, String password) {
        ((UserService) new Builder().baseUrl("http://sensing-change.herokuapp.com/api/v1/").addConverterFactory(GsonConverterFactory.create()).build().create(UserService.class)).auth("application/json", new UserModel(new User(username, password))).enqueue(new Callback<User>() {
            public void onResponse(Call<User> call, Response<User> response) {
                Integer status = Integer.valueOf(response.code());
                if (status.equals(Integer.valueOf(ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION))) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra("username", username);
                    MainActivity.this.startActivity(intent);
                } else if (status.equals(Integer.valueOf(401))) {
                    Toast.makeText(MainActivity.this, "Invalid email or password", 0).show();
                } else {
                    Toast.makeText(MainActivity.this, "Unknown error", 0).show();
                }
            }

            public void onFailure(Call<User> call, Throwable t) {
                Context context = MainActivity.this;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Error: ");
                stringBuilder.append(t.toString());
                Toast.makeText(context, stringBuilder.toString(), 0).show();
            }
        });
    }
}
