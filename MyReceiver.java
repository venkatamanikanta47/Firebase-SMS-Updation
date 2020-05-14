package com.example.receivemessage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import java.util.StringTokenizer;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MyReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED="android.provider.Telephony.SMS_RECEIVED";
    private static final String TAG="SmsBroadcastReceiver";
    String msg,phoneNo="";
    String[] values = new String[5];
    int i=0;


    private Firebase mref;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"Intent Received : "+intent.getAction());
        if (intent.getAction()==SMS_RECEIVED)
        {
            Bundle dataBundle=intent.getExtras();
            if(dataBundle !=null)
            {
                Object[] mypdu =(Object [])dataBundle.get("pdus");
                final SmsMessage[] message =new SmsMessage[mypdu.length];

                for(int i=0;i<mypdu.length;i++)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    {
                        String format= dataBundle.getString("format");
                        message[i]=SmsMessage.createFromPdu((byte[])mypdu[i],format);
                    }
                    else
                    {
                        message[i]=SmsMessage.createFromPdu((byte[]) mypdu[i]);
                    }
                    msg =message[i].getMessageBody();
                    phoneNo =message[i].getOriginatingAddress();
                }
                Toast.makeText(context, "Message: "+msg+ "\nNumber: "+phoneNo, Toast.LENGTH_SHORT).show();




                StringTokenizer strtok=new StringTokenizer(msg," ");
                while (strtok.hasMoreTokens())
               {
                    values[i++]=strtok.nextToken();
                }


                String urlstr="https://receive-message.firebaseio.com/"+values[0];

                mref=new Firebase(urlstr);

                Firebase childref1=mref.child("Crop_code");
                childref1.setValue(values[1]);
                Firebase childref2=mref.child("Quantity");
                childref2.setValue(values[2]);
                Firebase childref3=mref.child("Price");
                childref3.setValue(values[3]);
                Firebase childref4=mref.child("Farmer_id");
                childref4.setValue(values[0]);




            }
        }

    }
}
