package ap1.testbox.sooryagangarajk.com.perplechat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v4.util.LogWriter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String TAG2 = "sgk";
    static WifiP2pManager managerObj;
    Boolean exit = false;///// S G K /////
    static WifiP2pManager.Channel channelObj;
    WiFiDirectBroadcastReceiver receiverObj;
    IntentFilter filterObj;

    SwipeRefreshLayout swipeRefreshLayout;
    ListView lv;
    List<Ingredient> ingredientsList = new ArrayList<Ingredient>();
    final List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    public static String DMAC, DNAME;
    public static ArrayAdapter<Ingredient> adapter;
    public static EditText myName;



    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();

            if (!refreshedPeers.equals(peers)) {
                peers.clear();
                peers.addAll(refreshedPeers);
                ingredientsList.clear();


                for (WifiP2pDevice d1 : peers)

                {
                    ingredientsList.add(new Ingredient(d1.deviceName, d1.deviceAddress));
                }

            }

            if (peers.size() == 0) {
                Log.d("MainActivity", "No peers found");

                // no peers found
            }


        }
    };

    @Override
    public void onBackPressed() {

        if (exit) {
            // appStarted = false;
            Toast.makeText(this, "Bye",
                    Toast.LENGTH_SHORT).show();
            for (int i=0;i<=5;i++){
            System.exit(0);}
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000); // SG - This will wait for 3sec and then set exit as false

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {


        super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        overridePendingTransition(R.anim.transition_anim_r,R.anim.transition_anim_1_r);
        setContentView(R.layout.activity_main);

        ///// S G K /////

        //TO TURN ON THE WIFI

        WifiManager wifiManager = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(!wifiManager.isWifiEnabled()){///// S G K /////
            wifiManager.setWifiEnabled(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            },1500);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.slayout);
        lv = (ListView) findViewById(R.id.FndListId);
        myName=(EditText) findViewById(R.id.mtNameId);
        TextView ok=(TextView)findViewById(R.id.okId);


        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );



        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                discover();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {



                        adapter = new ArrayAdapter<Ingredient>(MainActivity.this, android.R.layout.simple_list_item_1, ingredientsList);

                        lv.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);

                    }
                }, 4 * 1000);

            }
        });



        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String devAddress = ingredientsList.get(i).mac;
                WifiP2pConfig configDevice = new WifiP2pConfig();
                configDevice.deviceAddress = devAddress;

                DMAC = devAddress;

                //ChatActivity.dName=ingredientsList.get(i).name;
                DNAME = ingredientsList.get(i).name;
                /*macTable=macToString(devAddress);*/

                Log.d(TAG2, "------------macTable--:"+macToString(DMAC)+"------------");
                //ChatActivity.macTableName=macToString(DMAC);
                managerObj.connect(channelObj, configDevice, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {///// S G K /////
                        /*Intent intent =new Intent(MainActivity.this,ChatActivity.class);
                        MainActivity.this.startActivity(intent);*/
                        Log.d(TAG2, "Connection initiated successfully");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.d(TAG2, "Connection failed: " + reason);
                    }
                });

            }
        });


        managerObj = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channelObj = managerObj.initialize(this, getMainLooper(), null);
        receiverObj = new WiFiDirectBroadcastReceiver(managerObj, channelObj, this, peerListListener, this.getApplicationContext());

        filterObj = new IntentFilter();///// S G K /////
        filterObj.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        filterObj.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        filterObj.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        filterObj.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        discover();


        adapter = new ArrayAdapter<Ingredient>(this, android.R.layout.simple_list_item_1, ingredientsList);
        lv.setAdapter(adapter);

        /*View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }*/

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Your name set",Toast.LENGTH_SHORT).show();
                ChatActivity.myNameString=myName.getText().toString();

            }
        });





    }

    void discover() {
        managerObj.discoverPeers(channelObj, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {

                ///// S G K /////
                Log.d(TAG2, "discover onSuccess called");

            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG2, "discover onSuccess called");
            }
        });
    }

    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();///// S G K /////
        registerReceiver(receiverObj, filterObj);
    }

    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiverObj);
    }

    public String macToString(String macAdd) {
        Log.d("sgk", "macToString() macAdd:" + macAdd);
        String temp = "SG";///// S G K /////
        Log.d("sgk",macAdd);
        StringTokenizer stringTokenizer = new StringTokenizer(macAdd,":");
        while(stringTokenizer.hasMoreTokens()){
            temp+=stringTokenizer.nextToken();

        }

        Log.d("sgk", "macToString() temp:" + temp);
        return temp;
    }

}



class Ingredient {
    public String mac;
    public String name;

    public Ingredient(String name, String mac) {
        this.name = name;
        this.mac = mac;///// S G K /////
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return this.name.toString();
    }


}