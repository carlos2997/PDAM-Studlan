package co.edu.escuelaing.carlos.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import co.edu.escuelaing.carlos.studlan.Message;


/**
 * Created by carlos on 25/11/17.
 */

public class DBAdapter {
    myDbHelper myhelper;

    public DBAdapter(Context context){
        myhelper = new myDbHelper(context);
    }

    public String changeName(String nombre){
        String[] nombreUsuario = nombre.split(" ");
        for(int i = 0 ; i < nombreUsuario.length ; i++){
            nombreUsuario[i] = nombreUsuario[i].substring(0,1).toUpperCase()+nombreUsuario[i].substring(1).toLowerCase();
        }
        if(nombreUsuario.length==1){
            nombre = nombreUsuario[0];
        }else if(nombreUsuario.length==2){
            nombre = nombreUsuario[0]+" "+nombreUsuario[1];
        }else if(nombreUsuario.length>2){
            nombre = nombreUsuario[0]+" "+nombreUsuario[2];
        }
        return nombre;
    }

    public long insertDataTeach(int carnet, String nombre, String correo, String pass){
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.CARNET, carnet);
        contentValues.put(myDbHelper.NOMBRE, nombre);
        contentValues.put(myDbHelper.CORREO, correo);
        contentValues.put(myDbHelper.MyPASSWORD, pass);
        contentValues.put(myDbHelper.IMGPROFESOR, "Null");
        long id = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return id;
    }

    public String getDataTeach(int carnet){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.MyPASSWORD};
        Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,"carnetID=?",Integer.toString(carnet).split(" "),null,null,null);
        while (cursor.moveToNext()) {
            String  password = cursor.getString(cursor.getColumnIndex(myDbHelper.MyPASSWORD));
            return password;
        }
        return null;
    }

    public String getNameTeach(int carnet){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.NOMBRE};
        String[] whereArgs = {Integer.toString(carnet)};
        Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,"carnetID=?",whereArgs,null,null,null);
        while (cursor.moveToNext()) {
            String  nombre = cursor.getString(cursor.getColumnIndex(myDbHelper.NOMBRE));
            return nombre;
        }
        return null;
    }

    public int updateUserPasTeach(int carnet, String nombre, String password){
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.NOMBRE,nombre);
        contentValues.put(myDbHelper.MyPASSWORD,password);
        String[] whereArgs = {Integer.toString(carnet)};
        int count = db.update(myDbHelper.TABLE_NAME,contentValues,myDbHelper.CARNET+" = ?",whereArgs);
        return count;
    }


    static class myDbHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "myDatabase";    // Database Name
        private static final String TABLE_NAME = "Profesor";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version
        private static final String CARNET="carnetID";     // Column I (Primary Key)
        private static final String NOMBRE = "Nombre";    //Column II
        private static final String CORREO = "Correo";
        private static final String MyPASSWORD= "Password";    // Column III
        private static final String IMGPROFESOR= "ImgProfesor";
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+CARNET+" INTEGER PRIMARY KEY, "+NOMBRE+" VARCHAR(255) ,"+CORREO+" VARCHAR2(255) ,"+ MyPASSWORD+" VARCHAR(225),"+IMGPROFESOR+" BLOB);";
        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
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
