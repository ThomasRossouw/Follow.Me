package com.example.heremapss;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper
{

//create the db

    public static final String DATABASE_NAME = "Categories.db";
    //create the table in the db
    public static final String TABLE_NAME = "Category_table";
    // public static final String TABLE_NAME2 = "Items_table";


    //add in the columns
    public static final String COL_1 = "ID";
    public static final String COL_2 = "NAME";




    public void querydata(String sql){
        SQLiteDatabase sd = getWritableDatabase();
        sd.execSQL(sql);

    }







    //constructor
    public DatabaseHelper(Context context, String s, Object o, int i) {

        super(context, DATABASE_NAME, null, 1);
        //SQLiteDatabase db = this.getWritableDatabase();
        SQLiteDatabase db = this.getWritableDatabase();

    }





    @Override
    public void onCreate(SQLiteDatabase db) {
        //execute a query
        db.execSQL("create table " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," + "NAME TEXT)");
        // db.execSQL("create table " + TABLE_NAME2 + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," + "NAME TEXT, DESCRIPTION TEXT, DATE TEXT, IMAGE BLOB)");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);

        onCreate(db);

    }

    public boolean insertData(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        //to put info into the columns
        ContentValues contentvalues = new ContentValues();
        //put option

        contentvalues.put(COL_2, name);


        // long result = db.insert(TABLE_NAME, null, content values);
        long result = db.insert(TABLE_NAME,null,contentvalues);
        return result != -1;
    }



    //get all data from the db
    public Cursor getData(String sql){
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(sql, null);

    }


    //gathers data from database
    public Cursor ViewData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from "+TABLE_NAME;
        Cursor cursor = db.rawQuery(query,null);
        return cursor;
    }
}




