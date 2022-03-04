package com.ashwinsaxena.newsapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN;


public class MainActivity extends AppCompatActivity {

    private final int WRITE_STORAGE_PERMISSION_REQUEST_CODE = 40;
    //This should be String country
    private String prevSelectedCountry = "";
    //Check the permission request part again
    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> launchFragment());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Simplify if-else
        //In case of rotation - Permission already checked and Fragment Also launched already
        if (savedInstanceState != null) {
            prevSelectedCountry = savedInstanceState.getString(Constants.PREV_SELECTED_COUNTRY);
            populateCountryCodeList();

        } else if (!checkPermission()) {                  // Having Permission - False
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_STORAGE_PERMISSION_REQUEST_CODE);
        } else {
            launchFragment();
        }
    }

    //Method for checking the WRITE_EXTERNAL_STORAGE Permission
    private boolean checkPermission() {
        int result = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            result = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    //Requesting Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_STORAGE_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchFragment();
                Toast.makeText(this, R.string.permission_granted, Toast.LENGTH_SHORT).show();
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !shouldShowRequestPermissionRationale(permissions[0])) {
                showRationaleUI();
            } else {
                launchFragment();
                Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
            }
        }

    }

    //Why close the app if user denies, we can still show online News
    //Check again & also use method-chaining here
    private void showRationaleUI() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setMessage(R.string.permission_request);
        builder.setPositiveButton(R.string.permit_manually, (dialog, which) -> {
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startForResult.launch(i);
        }).setNegativeButton(R.string.cancel, (dialog, which) -> {
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show();
            launchFragment();
        }).setCancelable(false).show();
    }

    //Method for launching new NewsFragment (News List in a recycler view)
    private void launchFragment() {
        prevSelectedCountry = getCurrentCountry();
        populateCountryCodeList();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,
                NewsFragment.newInstance(), NewsFragment.class.getSimpleName()).commit();
    }

    // Populating the Spinner with the Country Codes and setting up a ItemSelectedListener to it
    private void populateCountryCodeList() {
        Spinner spinner = findViewById(R.id.spinner);
        //Creating Arraylist of Country 2-Char Codes
        ArrayList<String> countryCodes = new ArrayList<>(Arrays.asList(Locale.getISOCountries()));
        countryCodes.add(0, getString(R.string.select_default_item));
        //Creating Adapter for Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, countryCodes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        int currentCountryPosition = adapter.getPosition(prevSelectedCountry);
        spinner.setSelection(currentCountryPosition);
        //Calling method for creating Country/Item Selected Listener
        addListener();
    }

    //try invoking onNothingSelected
    private void addListener() {
        Spinner spinner = findViewById(R.id.spinner);
        //Setting Country Selected Listener for spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NewsFragment fragmentInstance = (NewsFragment) getSupportFragmentManager()
                        .findFragmentByTag(NewsFragment.class.getSimpleName());
                String curCountry = parent.getItemAtPosition(position).toString(); //Select
                //Ensuring fragment existence and new country is selected before requesting news update
                if (fragmentInstance != null && !curCountry.equals(prevSelectedCountry)) {
                    prevSelectedCountry = curCountry;
                    fragmentInstance.updateNews(curCountry);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //If the selected item is removed from the list this will be called
                spinner.setSelection(0);
            }
        });

    }

    public void showDetailNews(ArrayList<DataModel> dataHolderList, int curPos) {
        //Launching Fragment for Single Detailed News
        //Check this
        getSupportFragmentManager().beginTransaction().setTransition(TRANSIT_FRAGMENT_OPEN)
                .add(R.id.fragment, DetailNewsFragment.newInstance(dataHolderList, curPos),
                        DetailNewsFragment.class.getSimpleName())
                .addToBackStack(null) //check this (need to check if we can pop from anyhere with the name here)
                .commit();
    }

    //Check it again

    public String getCurrentCountry() {
        String curCountry = "";
        Configuration configuration = getResources().getConfiguration();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            curCountry = configuration.getLocales().get(0).getCountry();
        } else {
            curCountry = configuration.locale.getCountry();
        }
        return curCountry;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(Constants.PREV_SELECTED_COUNTRY, prevSelectedCountry);
        super.onSaveInstanceState(outState);
    }
}
