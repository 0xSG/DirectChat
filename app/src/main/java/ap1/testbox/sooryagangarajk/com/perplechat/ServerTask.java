package ap1.testbox.sooryagangarajk.com.perplechat;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import static android.support.v4.content.ContextCompat.startActivity;
import static ap1.testbox.sooryagangarajk.com.perplechat.WiFiDirectBroadcastReceiver.disconnect;

/**
 * Created by suhas on 23/5/17.
 */

public class ServerTask extends AsyncTask<Void,Void,String> {
    public static final String EXTRA_MESSAGE = "MESSAGE";

    public static final String DMAC = "dMac";
    public static final String DNAME = "dname";
    private static final String TAG2 = "sgk";
    Context context;
    ServerTask(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
             ServerSocket serverSocket = new ServerSocket(8888);
            Socket client = serverSocket.accept();//Waits till client connects
            Log.d(TAG2,"ServerTask "+"Client has connected");
            DataInputStream DIS = new DataInputStream(client.getInputStream());
            String msg_received = DIS.readUTF();
            client.close();
            serverSocket.close();
            Log.d(TAG2,"ServerTask"+" Server has received the message: "+ msg_received);
            return msg_received;
        } catch (BindException e2){
            Log.d("sgk","BindException");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG2,"ServerTask"+" Server failed",e);
            try {
                Thread.sleep(50);
            }catch (Exception e1){

            }
        }


        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Intent i = new Intent(context,ChatActivity.class);
        i.putExtra(EXTRA_MESSAGE,result);
        i.putExtra(DMAC,MainActivity.DMAC);
        i.putExtra(DNAME,MainActivity.DNAME);
        ///// S G K /////
        Log.d(TAG2,"DNAME:"+MainActivity.DNAME+"  DMAC:"+MainActivity.DMAC+"  EXTRA_MESSAGE"+result);
        context.startActivity(i);
    }
}

