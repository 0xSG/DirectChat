package ap1.testbox.sooryagangarajk.com.perplechat;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import static ap1.testbox.sooryagangarajk.com.perplechat.ChatActivity.chatArrayAdapter;
import static ap1.testbox.sooryagangarajk.com.perplechat.ChatActivity.dbHelper;

/**
 * Created by suhas on 26/5/17.
 */

public class MessageServer extends ServerTask {
    private static final String TAG2 = "sgk";
    MessageServer(Context context) {
        super(context);
    }

    @Override
    protected String doInBackground(Void... params){

        String msg = super.doInBackground();

        Log.d(TAG2,"MessageServer "+"MessageServer started");
        Log.d(TAG2,"MessageServer "+"MessageServer received: " + msg);
        return msg;
    }

    @Override
    protected void onPostExecute(String result){
        Log.d(TAG2,"MessageServer "+"MessageServer received message: "+result);
        if(result!=null)
        if(!result.equals("")){
            if(result.length()==29 && result.contains("sgk.macTable=")){
                String tablename=result.substring(13,29);

                ChatActivity.macTableName=tablename;
            }else if(result.contains("sgk.device.myName="))
            {

                String otherDevice=result.substring(18,result.length());
                //Toast.makeText(context,otherDevice,Toast.LENGTH_SHORT).show();
                ChatActivity.setOtherDeviceName(otherDevice);
                Log.d("sgk","sgk.device.myName="+otherDevice+"=----post");
            }
            else {
        chatArrayAdapter.add(new ChatMessage(false, result));
        dbHelper.addMsg(ChatActivity.macTableName,result,false);}
        }
        /*Toast.makeText(context,result,Toast.LENGTH_LONG).show();*/

        MessageServer server = new MessageServer(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            server.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[])null);
        else
            server.execute((Void[])null);
    }
}
