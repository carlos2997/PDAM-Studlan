package co.edu.escuelaing.carlos.studlan;

/**
 * Created by 2105537 on 11/30/17.
 */

import android.content.Context;
import android.widget.Toast;

public class Message {
    public static void message(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}