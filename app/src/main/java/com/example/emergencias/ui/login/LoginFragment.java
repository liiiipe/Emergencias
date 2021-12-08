package com.example.emergencias.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.emergencias.MainActivity;
import com.example.emergencias.R;
import com.example.emergencias.model.User;

public class LoginFragment extends AppCompatActivity {
    EditText edit_name;
    EditText edit_pass;
    Button bt_login;
    Button bt_register;
    CheckBox check_stay_connected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        edit_name = (EditText) findViewById(R.id.username);
        edit_pass = (EditText) findViewById(R.id.password);
        bt_login = (Button) findViewById(R.id.login);
        bt_register = (Button) findViewById(R.id.register);
        check_stay_connected = (CheckBox) findViewById(R.id.stay_connected);

        if (userIsLogged()) {
            User user = recoverUser();

            // TODO fillContactList(user);

            goToNextPage(user);
        }
        else {
            // ação de clique no botão login
            bt_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = edit_name.getText().toString();
                    String password = edit_pass.getText().toString();
                    if (name.equals("") || password.equals("")) {
                        Toast.makeText(LoginFragment.this, "Todos os campos devem ser preenchidos!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        SharedPreferences saved_user = getSharedPreferences("default_user", Activity.MODE_PRIVATE);
                        String saved_name = saved_user.getString("name", "");
                        String saved_password = saved_user.getString("password", "");

                        if ((saved_name != null) && (saved_password != null)) {
                            if (saved_name.equals(name) && saved_password.equals(password)) {
                                User user = recoverUser();

                                // TODO fillContactList(user);

                                goToNextPage(user);
                            } else {
                                Toast.makeText(LoginFragment.this, "Login e Senha Incorretos!", Toast.LENGTH_LONG).show();
                            }
                        }
                        else {
                            Toast.makeText(LoginFragment.this, "Usuário não registrado!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });

            // ação de clique no botão registrar
            bt_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = edit_name.getText().toString();
                    String password = edit_pass.getText().toString();
                    boolean stay_connected = check_stay_connected.isChecked();

                    SharedPreferences new_user = getSharedPreferences("default_user", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor edit_new_user = new_user.edit();

                    edit_new_user.putString("name", name);
                    edit_new_user.putString("password", password);
                    edit_new_user.putBoolean("stay_connected", stay_connected);

                    edit_new_user.commit();

                    User user = new User(name, password, stay_connected);

                    goToNextPage(user);
                }
            });
        }
    }

    protected void goToNextPage(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", (Parcelable) user);
        startActivity(intent);

        finish();
    }

    private boolean userIsLogged() {
        SharedPreferences user_logged = getSharedPreferences("default_user", Activity.MODE_PRIVATE);
        return user_logged.getBoolean("stay_connected",false);
    }

    private User recoverUser() {
        SharedPreferences user_logged= getSharedPreferences("default_user", Activity.MODE_PRIVATE);
        String name = user_logged.getString("name","");
        String password = user_logged.getString("password","");
        boolean stay_connected = user_logged.getBoolean("stay_connected",false);

        return new User(name, password, stay_connected);
    }
}
