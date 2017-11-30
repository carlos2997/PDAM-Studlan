package co.edu.escuelaing.carlos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import co.edu.escuelaing.carlos.studlan.Message;


/**
 * Created by carlos on 25/11/17.
 */

public class DBAdapterAsig {
    myDbHelperAsig myhelperAsig;

    public DBAdapterAsig(Context context){
        myhelperAsig = new myDbHelperAsig(context);
    }

    public String changeAsig(String asignatura){
        return asignatura.toUpperCase();
    }

    public long insertDataAsig(int carnet, String nombreAsig, String pass){
        SQLiteDatabase dbb = myhelperAsig.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelperAsig.CARNET, carnet);
        contentValues.put(myDbHelperAsig.NOMBREASIG, nombreAsig);
        contentValues.put(myDbHelperAsig.PASSWORD, pass);
        long id = dbb.insert(myDbHelperAsig.TABLE_NAME, null , contentValues);
        return id;
    }

    public int getCountAsignatures(int carnet){
        System.out.println("ESTE ES EL CARNET1: "+carnet);
        SQLiteDatabase db = myhelperAsig.getWritableDatabase();
        String[] columns = {myDbHelperAsig.NOMBREASIG};
        Cursor cursor =db.query(myDbHelperAsig.TABLE_NAME,columns,"carnetID=?",Integer.toString(carnet).split(" "),null,null,null);
        int count = 0;
        while(cursor.moveToNext()){
            count++;
        }
        return cursor.getColumnCount();
    }

    public List<String> getAsignatures(int carnet){
        SQLiteDatabase db = myhelperAsig.getWritableDatabase();
        String[] columns = {myDbHelperAsig.NOMBREASIG};
        Cursor cursor =db.query(myDbHelperAsig.TABLE_NAME,columns,"carnetID=?",Integer.toString(carnet).split(" "),null,null,null);
        List<String> asignaturas = new ArrayList<>();
        while(cursor.moveToNext()){
            asignaturas.add(cursor.getString(cursor.getColumnIndex(myDbHelperAsig.NOMBREASIG)));
        }
        return asignaturas;
    }

    static class myDbHelperAsig extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "myDatabase1";    // Database Name
        private static final String TABLE_NAME = "Asignaturas";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version
        private static final String CARNET ="carnetID";     // Column I (Primary Key)
        private static final String NOMBREASIG = "NombreAsig";    //Column II
        private static final String PASSWORD = "Password";    // Column III
        private static final String IMGASIG = "ImgAsignatura";
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+CARNET+" INTEGER, "+NOMBREASIG+" VARCHAR(255) ,"+ PASSWORD+" VARCHAR(225),"+IMGASIG+" BLOB, PRIMARY KEY ("+CARNET+","+NOMBREASIG+"));";
        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public myDbHelperAsig(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                Message.message(context,""+e);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Message.message(context,"OnUpgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {
                Message.message(context,""+e);
            }
        }
    }
}
