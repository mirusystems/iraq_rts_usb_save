package com.mirusystems.usbsave;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;



public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    NavController navController;
    @IdRes
    int currentFragmentId;
    @IdRes
    int previousFragmentId;
    private NavController.OnDestinationChangedListener destinationChangedListener = new NavController.OnDestinationChangedListener() {
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
            Log.v(TAG, "onDestinationChanged: destination = " + destination + ", arguments = " + arguments);
            previousFragmentId = currentFragmentId;
            currentFragmentId = destination.getId();
            getSupportActionBar().setTitle(destination.getLabel());
            if (currentFragmentId == R.id.initFragment || currentFragmentId == R.id.mainFragment || currentFragmentId == R.id.splashFragment) {
                setDisplayHomeAsUpEnabled(false);
            } else {
                setDisplayHomeAsUpEnabled(true);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.addOnDestinationChangedListener(destinationChangedListener);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
    }

    @Override
    public void setTitle(int titleId) {
        getSupportActionBar().setTitle(titleId);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (navController != null) {
                    return navController.navigateUp() || super.onOptionsItemSelected(item);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void setDisplayHomeAsUpEnabled(boolean enabled) {
        Log.v(TAG, "setDisplayHomeAsUpEnabled: " + enabled);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(enabled);
        actionBar.setHomeButtonEnabled(enabled);
    }
}
