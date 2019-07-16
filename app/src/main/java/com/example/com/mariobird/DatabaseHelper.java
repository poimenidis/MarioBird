package com.example.com.mariobird;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by User on 2/28/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "people_table";
    private static final String ID = "ID";
    private static final String NAME = "name";
    private static final String IMAGE = "image";
    private static final String SCORE = "score";



    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID TEXT PRIMARY KEY, " +
                NAME +" TEXT, " + IMAGE +" TEXT, "+ SCORE + " TEXT )";
        db.execSQL(createTable);


    }

    public void addElements(String id ,String name, String imageUrl, String score){
        Cursor data = getData();
        ArrayList<String> listData = new ArrayList<>();

        while(data.moveToNext()){
            //get the value from the database in every column
            //then add it to the ArrayList
            listData.add(data.getString(0));
        }

        /*If the lisData doesn't have user's id then add user*/
        if(!listData.contains(id)) {
            addData(id ,name, imageUrl, score);

        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    private void addData(String id, String name, String imageUrl,String score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, id);
        contentValues.put(NAME, name);
        contentValues.put(IMAGE, imageUrl);
        contentValues.put(SCORE, score);

        Log.d(TAG, "addData: Adding " + id + " to " + TABLE_NAME);
        Log.d(TAG, "addData: Adding " + name + " to " + TABLE_NAME);
        Log.d(TAG, "addData: Adding " + imageUrl + " to " + TABLE_NAME);
        Log.d(TAG, "addData: Adding " + score + " to " + TABLE_NAME);

        db.insert(TABLE_NAME, null, contentValues);

        //if date as inserted incorrectly it will return -1
    }


    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return db.rawQuery(query, null);
    }


//    public Cursor getItemID(String name){
//        SQLiteDatabase db = this.getWritableDatabase();
//        String query = "SELECT " + ID + " FROM " + TABLE_NAME +
//                " WHERE " + NAME + " = '" + name + "'";
//        return db.rawQuery(query, null);
//    }


    public void updateScore(String id , String score){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + SCORE +
                " = '" + score + "' WHERE " + ID + " = '" + id + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting image to " + score);
        db.execSQL(query);
    }

    public void updateImage(String id , String image){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + IMAGE +
                " = '" + image + "' WHERE " + ID + " = '" + id + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting image to " + image);
        db.execSQL(query);
    }

    public void updateName(String id , String name){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME + " SET " + NAME +
                " = '" + name + "' WHERE " + ID + " = '" + id + "'";
        Log.d(TAG, "updateName: query: " + query);
        Log.d(TAG, "updateName: Setting image to " + name);
        db.execSQL(query);
    }

    public int getPositionUser(String id){

        Cursor data = getData();
        int count=0;
        int position=-1;
        while(data.moveToNext()){
            if(data.getString(0).equals(id))
                position=count;
            count++;
        }

        return position;

    }

    public void deleteData(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE "
                + ID + " = '" + id + "'";
        Log.d(TAG, "deleteData: query: " + query);
        Log.d(TAG, "deleteData: Deleting " + id + " from database.");
        db.execSQL(query);
    }


    public boolean userExists(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + ID + " FROM " + TABLE_NAME +
                " WHERE " + ID + " = '" + id + "'";
        Cursor cursor = db.rawQuery(query,null);
        if (cursor.getCount()<=0){
            /*cursors NEED to be closed*/
            cursor.close();
            return false;
        }
         cursor.close();
        return true;

    }

}