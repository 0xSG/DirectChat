package ap1.testbox.sooryagangarajk.com.perplechat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by sooryagangarajk on 13/08/17.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static String DB_NAME = "sgBASE.db";
    public static int DB_VER = 1;

    public static String COL_ID = "id";
    public static String COL_MSG = "msg";
    public static String COL_R = "right";
    public static String TAG = "sgk";


    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
        Log.d(TAG, "DATA BASE CREATED");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }



    public void addMsg(String macTable, String msg, Boolean right) {
        Log.d(TAG, "addMsg() Table:"+macTable+" msg:"+msg+" right:"+right);
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MSG, msg);
        values.put(COL_R, right);
        database.insert(macTable, null, values);
        database.close();
    }

    public boolean isTableExists(String tableName) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();


        Cursor cursor = mDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                Log.d(TAG, "isTableExists():true");
                return true;

            }
            cursor.close();
        }
        Log.d(TAG, "isTableExists():false");
        return false;
    }

    public msgNSide[] getAppCategoryDetail(String TABLE_NAME) {
        Log.d(TAG, "getAppCategoryDetail called");
        int count=checkRec(TABLE_NAME),i=0;
        msgNSide[] msgNSides=new msgNSide[count];
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int columnIndexOfMsg;
        String msg;
        int columnIndexOfSide;
        Boolean side;


        if (cursor.moveToFirst()) {
            columnIndexOfMsg = cursor.getColumnIndex(COL_MSG);
            columnIndexOfSide = cursor.getColumnIndex(COL_R);
            do {
                msgNSides[i]=new msgNSide();
                msgNSides[i].msg= cursor.getString(columnIndexOfMsg);
                msgNSides[i].side=cursor.getInt(columnIndexOfSide)>0;
                i++;
                if (i==count)
                    break;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return msgNSides;

    }
    public int checkRec(String TABLE_NAME){

        int count=0;
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        //cursor.moveToFirst();
        if(cursor.moveToNext()){
            count++;

        }
        count=cursor.getCount();
        Log.d(TAG, "checkRec called value:"+count);
        return count;
    }
    public void createTable(String tableName){
        SQLiteDatabase db=this.getWritableDatabase();

        db.execSQL(" CREATE TABLE " + tableName + " ( " + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT , " + COL_MSG + " TEXT," + COL_R + " BOOLEAN ) ");
        Log.d(TAG, "table created");
    }
    public void clearDB(String tableName){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("DELETE FROM "+tableName);

        Log.d(TAG, "table cleared");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
