package com.nullpx.irremote;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class FeedbackActivity extends AppCompatActivity {

    private String twitterId = "@IrRemote";
    private String phoneNumber = "+989173811418";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Spinner spFeedback = (Spinner) findViewById(R.id.spinner_feedback);
        ArrayList<String> list = new ArrayList<>();
        list.add("Twitter");
        list.add("Telegram");
        list.add("Wire");
        list.add("Facebook");
        list.add("WhatsApp");
        list.add("Email");
        list.add("Sms");
        list.add("Call");
        list.add("");

        ArrayAdapter<String> spItems = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spFeedback.setAdapter(spItems);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String selectedItem = String.valueOf(((Spinner) findViewById(R.id.spinner_feedback)).getSelectedItem());
                String msg = ((TextView) findViewById(R.id.tv_feedback)).getText().toString();
                switch (selectedItem) {
                    case "Twitter":
                        shareViaTwitter(msg);
                        break;
                    case "Telegram":
                        shareViaTelegram(msg);
                        break;
                    case "Wire":
                        shareViaWire(msg);
                        break;
                    case "Facebook":
                        shareViaFacebook(msg);
                        break;
                    case "WhatsApp":
                        shareViaWhatsApp(msg);
                        break;
                    case "Email":
                        shareViaEmail(msg);
                        break;
                    case "Sms":
                        shareViaSms(msg);
                        break;
                    case "Call":
                        shareViaCall(msg);
                        break;

                    default:
                        shareVia(msg);
                }

                Snackbar.make(view, "Sending Feedback...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void shareVia(String msg) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, msg);
//                startActivity(intent);
        startActivity(Intent.createChooser(intent, "Share with"));
//                shareViaTwitter(((TextView) findViewById(R.id.tv_feedback)).getText().toString());
    }

    private void shareViaCall(String msg) {
        Intent callIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + phoneNumber));
        startActivity(callIntent);
    }

    private void shareViaSms(String msg) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", msg);
        startActivity(intent);
    }

    private void shareViaEmail(String msg) {

        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Feedback");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, msg);
//        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        PackageManager pm = getApplicationContext().getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.name).contains("android.gm")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                startActivity(shareIntent);
                break;
            }
        }
    }

    private void shareViaWire(String msg) {
        Toast.makeText(this, "Wire app is not Installed", Toast.LENGTH_LONG).show();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.putExtra(Intent.EXTRA_TEXT, twitterId + " " + msg);
        String fullUrl = "https://app.wire.com/sajjad_gerami/";
        i.setData(Uri.parse(fullUrl));
        startActivity(i);
    }

    private void shareViaTelegram(String msg) {
        Toast.makeText(this, "Telegram app is not Installed", Toast.LENGTH_LONG).show();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.putExtra(Intent.EXTRA_TEXT, twitterId + " " + msg);
        String fullUrl = "https://t.me/sajjad_gerami/";
        i.setData(Uri.parse(fullUrl));
        startActivity(i);
    }

    public void shareViaFacebook(String message) {

        String fullUrl = "https://www.facebook.com/dialog/share?app_id=145634995501895&display" +
                "=popup&href=https%3A%2F%2Fdevelopers.facebook.com%2Fdocs%2F&redirect_uri=" +
                "https://developers.facebook.com/tools/explorer";
        try {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setClassName("com.facebook.katana",
                    "com.facebook.katana.ShareLinkActivity");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(sharingIntent);

        } catch (Exception e) {
            Toast.makeText(this, "Facebook app is not Installed", Toast.LENGTH_LONG).show();
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.putExtra(Intent.EXTRA_TEXT, twitterId + " " + message);
            i.setData(Uri.parse(fullUrl));
            startActivity(i);
        }
    }

    //    For Twitter.
    public void shareViaTwitter(String message) {
        try {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setClassName("com.twitter.android", "com.twitter.android.PostActivity");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(sharingIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Twitter app is not Installed", Toast.LENGTH_LONG).show();
            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, twitterId + " " + message);
            i.setAction(Intent.ACTION_VIEW);
            try {
                i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + URLEncoder.encode(twitterId + " " + message, "UTF-8")));
            } catch (UnsupportedEncodingException e1) {
                i.setData(Uri.parse("https://mobile.twitter.com/compose/tweet"));
                e1.printStackTrace();
            }
            startActivity(i);
        }
    }

    public void shareViaWhatsApp(String message) {

        PackageManager pm = getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this, "WhatsApp not Installed", Toast.LENGTH_LONG).show();
        }
    }
}
