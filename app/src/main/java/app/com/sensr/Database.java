package app.com.sensr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by jpalmows on 6/16/2016.
 */
public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Data.db";
    public static final String TABLE1_NAME = "Data_Table";

    public static final String COL_1 = "ID";
    public static final String COL_2 = "SENSORTYPE";
    public static final String COL_3 = "DATA";
    public static final String COL_4 = "TIMESTAMP";

    public static String firstnametoadd;
    public static String lastnametoadd;
    public static String existingrfidnumbertoadd;



    public Database(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE1_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,SENSORTYPE TEXT,DATA TEXT, TIMESTAMP TEXT)");




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE1_NAME);

        onCreate(db);

    }

    public boolean insertData(String sensortype, String data, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentvalues = new ContentValues();
        contentvalues.put(COL_2, sensortype);
        contentvalues.put(COL_3, data);
        contentvalues.put(COL_4, timestamp);

        long result = db.insert(TABLE1_NAME, null, contentvalues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){

            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }


    }

    public ArrayList<String> getSensorTypeList() {
        ArrayList<String> SensorTypeList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + TABLE1_NAME;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    String SensorType = cursor.getString(cursor.getColumnIndex("SENSORTYPE"));
                    SensorTypeList.add(SensorType);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return SensorTypeList;
    }

    public ArrayList<String> getDataList() {
        ArrayList<String> DataList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + TABLE1_NAME;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    String Data = cursor.getString(cursor.getColumnIndex("DATA"));
                    DataList.add(Data);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return DataList;
    }

    public ArrayList<String> getTimeStampList() {
        ArrayList<String> timestampList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + TABLE1_NAME;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    String timestamp = cursor.getString(cursor.getColumnIndex("TIMESTAMP"));
                    timestampList.add(timestamp);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return timestampList;
    }

    public ArrayList<String> getAllData() {
        ArrayList<String> AllDataList = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        db.beginTransaction();
        try {
            String selectQuery = "SELECT * FROM " + TABLE1_NAME;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {

                    String sensorType = cursor.getString(cursor.getColumnIndex("SENSORTYPE"));
                    String data = cursor.getString(cursor.getColumnIndex("DATA"));
                    String timestamp = cursor.getString(cursor.getColumnIndex("TIMESTAMP"));

                    AllDataList.add(sensorType+":" + "    " + data + "    " + timestamp);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return AllDataList;
    }
}

