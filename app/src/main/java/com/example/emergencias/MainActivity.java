package com.example.emergencias;

import android.Manifest;
import android.os.Bundle;

import com.example.emergencias.ui.call.CallPermission;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.emergencias.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements CallPermission.NoticeDialogListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_call, R.id.navigation_contacts, R.id.navigation_user)
                .build();
        NavHostFragment nhf = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = nhf.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public void onDialogPositiveClick(int codigo) {
        if (codigo==1){
            String[] permissions ={Manifest.permission.CALL_PHONE};
            requestPermissions(permissions, 2222);

        }
    }
}