package com.nullpx.irremote;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DeviceSelectionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = DeviceSelectionActivity.class.getName();
    private String device;
    private String brand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_device);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.btnSelect).setEnabled(false);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void btnDeviceOnClick(View view) {
        Button imageButton = (Button) view;
        populateBrands(imageButton.getText().toString());
//        imageButton.setBackgroundColor(200);
        findViewById(R.id.btnSelect).setEnabled(false);
    }

    public void btnSelectOnClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("device", device);
        intent.putExtra("brand", brand);
        setResult(RESULT_OK, intent);
        finish();
        //onBackPressed();
    }

    private void populateBrands(String device) {

        this.device = device;

        List<String> categories = new ArrayList<>();
        if (device.equals("TV")) {

            categories.add("LG");
            categories.add("Sony");
            categories.add("Samsung");
            categories.add("Toshiba");
            categories.add("Sharp");
            categories.add("Philips");
            categories.add("Panasonic");
            categories.add("X.Vision");

        } else if (device.equals("AC")) {
            Toast.makeText(this, "AC is not supported yet. contact Sajjad Gerami", Toast.LENGTH_LONG).show();
        } else if (device.equals("Fan")) {
            Toast.makeText(this, "Fan is not supported yet. contact Sajjad Gerami", Toast.LENGTH_LONG).show();
        } else if (device.equals("Projector")) {
            Toast.makeText(this, "Fan is not supported yet. contact Sajjad Gerami", Toast.LENGTH_LONG).show();
        } else if (device.equals("Receiver")) {

            Toast.makeText(this, "Receiver is not supported yet. contact Sajjad Gerami", Toast.LENGTH_LONG).show();
            categories.add("MediaStar");
            categories.add("Xcruiser");
            categories.add("StarMax");
            categories.add("StarSat");
        }

        LinearLayout group = (LinearLayout) findViewById(R.id.rg_brands);
        group.removeAllViews();

        // remove the items we already have
        ArrayList<String> list = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            list = extras.getStringArrayList("device_list");
        }
        if (list != null) {
            categories.removeAll(list);
        }

        if (categories.isEmpty()) {
            Log.d(TAG, "We are out of supported brands for now :-|");
            final Button button = new Button(getApplicationContext());
            button.setText("+ Add new brand");
//                button.setBackgroundDrawable(R.drawable.ic_menu_camera);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                        brand = ((Button) v).getText().toString();
//                        findViewById(R.id.btnSelect).setEnabled(true);
                    Toast.makeText(DeviceSelectionActivity.this, "Adding new brand is not supported yet. contact Sajjad Gerami", Toast.LENGTH_LONG).show();
                    button.setEnabled(false);
                }
            });

            group.addView(button);

        } else {
            for (String s : categories) {
                final Button button = new Button(getApplicationContext());
                button.setText(s);
//                button.setBackgroundDrawable(R.drawable.ic_menu_camera);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        brand = ((Button) v).getText().toString();
                        findViewById(R.id.btnSelect).setEnabled(true);
                    }
                });

                group.addView(button);
            }
        }
    }
}
