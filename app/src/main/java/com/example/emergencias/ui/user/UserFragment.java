package com.example.emergencias.ui.user;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emergencias.R;
import com.example.emergencias.model.User;

public class UserFragment extends Fragment {
    TextView name_user;
    EditText edit_name;
    EditText edit_pass;
    Button bt_save;
    CheckBox check_stay_connected;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name_user = (TextView) requireActivity().findViewById(R.id.name_user);
        edit_name = (EditText) requireActivity().findViewById(R.id.username_profile_user);
        edit_pass = (EditText) requireActivity().findViewById(R.id.password_profile_user);
        bt_save = (Button) requireActivity().findViewById(R.id.save_profile_user);
        check_stay_connected = (CheckBox) requireActivity().findViewById(R.id.stay_connected_profile_user);

        User user = recoverUser();
        name_user.setText(user.getName());
        edit_name.setText(user.getName());
        edit_pass.setText(user.getPassword());
        check_stay_connected.setChecked(user.isStay_connected());

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edit_name.getText().toString();
                String password = edit_pass.getText().toString();
                boolean stay_connected = check_stay_connected.isChecked();

                if (name.equals("") || password.equals(""))
                    Toast.makeText(UserFragment.this.getContext(), R.string.error_empty_fields, Toast.LENGTH_LONG).show();
                else {
                    boolean success = saveUser(name, password, stay_connected);
                    if (success)
                        Toast.makeText(UserFragment.this.getContext(), R.string.update_success, Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(UserFragment.this.getContext(), R.string.update_error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private User recoverUser() {
        SharedPreferences user_logged = requireActivity().getSharedPreferences("default_user", Activity.MODE_PRIVATE);
        String name = user_logged.getString("name","");
        String password = user_logged.getString("password","");
        boolean stay_connected = user_logged.getBoolean("stay_connected",false);

        return new User(name, password, stay_connected);
    }

    private boolean saveUser(String name, String password, boolean stay_connected) {
        SharedPreferences user_data = requireActivity().getSharedPreferences("default_user", Activity.MODE_PRIVATE);
        SharedPreferences.Editor edit_user_data = user_data.edit();

        edit_user_data.putString("name", name);
        edit_user_data.putString("password", password);
        edit_user_data.putBoolean("stay_connected", stay_connected);

        return edit_user_data.commit();
    }
}
