package com.elprog.middleman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;



import java.util.ArrayList;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
    String struser=intent.getStringExtra("userData");


        /*Intent i = new Intent();
        i.setClassName("com.elprog.middleman", "com.elprog.middleman.MainActivity");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);*/
    }

}
