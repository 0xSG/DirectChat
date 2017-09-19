package ap1.testbox.sooryagangarajk.com.perplechat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import java.net.InetAddress;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    public static final String EXTRA_MESSAGE = "MESSAGE";
    private static final String TAG2 = "sgk";
    static private WifiP2pManager mManager;
    static  private WifiP2pManager.Channel mChannel;
    private Activity mActivity;
    WifiP2pManager.PeerListListener peerListListener;
    Context context;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       Activity activity, WifiP2pManager.PeerListListener peerListListener,Context context) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        this.peerListListener = peerListListener;
        this.context = context;
    }

    public static void disconnect(){
        mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.d(TAG2,"removeGroup Called.......");
            }

            @Override
            public void onFailure(int i) {

            }
        });
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi P2P is enabled
                } else {
                    // Wi-Fi P2P is not enabled
                    Toast.makeText(mActivity,"Wifi p2p is not enabled",Toast.LENGTH_SHORT).show();
                }
            }

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
                Log.d(TAG2,"BroadcastReceiver:"+"requestPeers() has been called");
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            if (mManager == null)
                return;

            NetworkInfo netwinfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (netwinfo.isConnected()){
                Log.d(TAG2,"BroadcastReceiver:"+"Connection is successful");
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        InetAddress groupOwnerAddress =  info.groupOwnerAddress;

                        // After the group negotiation, we can determine the group owner
                        // (server).
                        if (info.groupFormed && info.isGroupOwner) {
                            // Do whatever tasks are specific to the group owner.
                            // One common case is creating a group owner thread and accepting
                            // incoming connections.
                            Log.d(TAG2,"BroadcastReceiver:"+"Starting server thread");
                            ServerTask server = new ServerTask(context);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                                server.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
                            else
                                server.execute((Void[])null);
                            Log.d(TAG2,"BroadcastReceiver:"+"Server is running");

                        } else if (info.groupFormed) {
                            // The other device acts as the peer (client). In this case,
                            // you'll want to create a peer thread that connects
                            // to the group owner.
                            Log.d(TAG2,"BroadcastReceiver:"+"Starting client thread");
                            String clientIP = ClientClass.getLocalIpAddress();
                            User.sendMessage(clientIP,groupOwnerAddress);
                            Intent i = new Intent(context,ChatActivity.class);
                            i.putExtra(EXTRA_MESSAGE,groupOwnerAddress.getHostAddress());
                            Log.d(TAG2,"groupOwnerAddress");
                            context.startActivity(i);
                            Log.d(TAG2,"BroadcastReceiver:"+"Started client thread\n"+"groupOwnerAddress:"+groupOwnerAddress);

                        }
                }
                });
            }
            else{
                Log.d(TAG2,"BroadcastReceiver:"+"Connection was not successful");

            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }

}
