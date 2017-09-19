package ap1.testbox.sooryagangarajk.com.perplechat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import static ap1.testbox.sooryagangarajk.com.perplechat.User.receiveMessage;
import static ap1.testbox.sooryagangarajk.com.perplechat.User.sendMessage;
import static ap1.testbox.sooryagangarajk.com.perplechat.WiFiDirectBroadcastReceiver.EXTRA_MESSAGE;
import static ap1.testbox.sooryagangarajk.com.perplechat.WiFiDirectBroadcastReceiver.disconnect;

public class ChatActivity extends AppCompatActivity {
    public static ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;
    private Button buttonSend;
    private boolean side = false, restoredFlag = false;
    public static String macTableName = "dmac";

    Animation sendBtnAnim;
    private static final String TAG2 = "sgk";
    public int count;
    public static DBHelper dbHelper;
    public msgNSide msgNSideAllRec[];
    public static TextView otherDeviceName;
    public static String myNameString ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.transition_anim_1,R.anim.transition_anim);
        setContentView(R.layout.activity_chat);
        //Intent intent = getIntent();///// S G K /////
        macTableName = getStringMac();

        /*TextView textView =(TextView) findViewById(R.id.textView);
        textView.setText(dName);*/

        otherDeviceName =(TextView) findViewById(R.id.myUserNameId);
        buttonSend = (Button) findViewById(R.id.send);
        sendBtnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.send_btn_anim);
        listView = (ListView) findViewById(R.id.msgview);
        listView.setStackFromBottom(true);///// S G K /////
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right_raw);
        listView.setAdapter(chatArrayAdapter);


        chatText = (EditText) findViewById(R.id.msg);



        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage(macTableName);
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);///// S G K /////


        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        receiveMessage(this.getApplicationContext());
        sendConfig(macTableName);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!restoredFlag) {
                    restoreList(macTableName);
                    restoredFlag = true;
                }
            }
        }, 1 * 1000);




    }
   
    @Override
    public void onBackPressed() {///// S G K /////

        disconnect();

        buttonSend.startAnimation(sendBtnAnim);

        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);


            }
        },1000);
    }



    private boolean sendChatMessage(String tableName) {
        Intent i = getIntent();///// S G K /////
        String serverAddress = i.getStringExtra(EXTRA_MESSAGE);
        Log.d(TAG2, "serverAddress:" + serverAddress);
        try {
            side = true;
            final InetAddress inetAddress = InetAddress.getByName(serverAddress);

            String txt = chatText.getText().toString();
            if (txt.length() != 0) {///// S G K /////
                if (!txt.equals("app.clear.db")) {
                    chatArrayAdapter.add(new ChatMessage(side, txt));
                    dbHelper.addMsg(tableName, txt, side);
                    sendMessage(txt, inetAddress);
                } else {
                    dbHelper.clearDB(tableName);
                    Toast.makeText(this, "Data base cleared", Toast.LENGTH_SHORT).show();
                }

            }
            chatText.setText("");


        } catch (UnknownHostException e) {

        }
        return true;
    }

    private boolean sendConfig(String msg) {
        Intent i = getIntent();///// S G K /////
        String serverAddress = i.getStringExtra(EXTRA_MESSAGE);
        Log.d(TAG2, "serverAddress:" + serverAddress);
        try {
            side = true;
            final InetAddress inetAddress = InetAddress.getByName(serverAddress);

            String txt = "sgk.macTable=" + msg;
            Log.d("sgk","sgk.macTable="+txt+"sendConfig()--------------");
            sendMessage(txt, inetAddress);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String txt1;
                    /*if(myNameString.equals("Your Name")){*/
                        txt1 ="sgk.device.myName="+myNameString;
                    /*}else {
                        txt1 ="sgk.device.myName="+ myNameString;
                    }*/
                    Log.d("sgk","sgk.device.myName="+txt1+"sendConfig()--------------");
                    sendMessage(txt1, inetAddress);

                }
            },800);




        } catch (UnknownHostException e) {

        }
        return true;
    }
    static public void setOtherDeviceName(String string){
        otherDeviceName.setText(string);

    }

    public void restoreList(String tableName) {
        dbHelper = new DBHelper(this);
        if (dbHelper.isTableExists(tableName)) {
            Log.d(TAG2, "if(dbHelper.isTableExists(tableName)) == true");
            count = dbHelper.checkRec(tableName);

            msgNSideAllRec = new msgNSide[count];
            msgNSideAllRec = dbHelper.getAppCategoryDetail(tableName);
            for (msgNSide MNS : msgNSideAllRec) {///// S G K /////
                chatArrayAdapter.add(new ChatMessage(MNS.side, MNS.msg));
                Log.d(TAG2, "if(dbHelper.isTableExists(tableName)) MNS.side:" + MNS.side + " MNS.msg" + MNS.msg);
            }


        } else {
            dbHelper.createTable(tableName);
            Log.d(TAG2, "createTable() table:" + tableName);
        }
    }

    public String getStringMac() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();


        Log.d("sgk", "macToString() macAdd:" + macAddress);
        String temp = "SG";
//       ///// S G K /////
        StringTokenizer stringTokenizer = new StringTokenizer(macAddress, ":");
        while (stringTokenizer.hasMoreTokens()) {
            temp += stringTokenizer.nextToken();

        }
        temp += "SG";
        Log.d("sgk", "macToString() temp:" + temp);
        return temp;
    }


}
