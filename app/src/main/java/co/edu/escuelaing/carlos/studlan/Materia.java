package co.edu.escuelaing.carlos.studlan;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.widget.Button;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class Materia extends AppCompatActivity{

    private CompactCalendarView compactCalendar;
    private TextView txtMes;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
    private String asignatura;
    private SharedPreferences infoUsuario;

    class GetEventAsig extends AsyncTask<String[],Void,Void> {

        private Context context;

        public GetEventAsig(Context context){
            this.context = context;
        }

        public InfoEvento leerEvento(JsonReader reader) throws IOException {
            String mensajeEvento = null;
            long fecha = 0;

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                switch (name) {
                    case "mensajeEvento":
                        mensajeEvento = reader.nextString();
                        break;
                    case "fecha":
                        fecha = reader.nextLong();
                        break;
                    default:
                        reader.skipValue();
                        break;
                }
            }
            reader.endObject();
            return new InfoEvento(mensajeEvento,fecha);
        }



        @Override
        protected Void doInBackground(String[]... datos) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = connMgr.getActiveNetworkInfo();
            if(network != null && network.isConnected()) {
                URL url = null;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL("https://worknitor.herokuapp.com/Profesor/TodosEventos/" + datos[0][0] + "/"+datos[0][1]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                    JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));

                    List<InfoEvento> listaEeventos = new ArrayList();

                    reader.beginArray();
                    while (reader.hasNext()) {
                        listaEeventos.add(leerEvento(reader));
                    }
                    reader.endArray();

                    for(InfoEvento info : listaEeventos){
                        compactCalendar.addEvent(new Event(Color.RED,info.getFecha(),info.getMensajeEvento()));
                    }

                    in.close();
                    urlConnection.disconnect();


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }else{
                Message.message(context,"No hay conexión a internet!!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
        }
    }

    private class POSTEventRegister extends AsyncTask<String[],Void,Void>{

        private Context context;

        public POSTEventRegister(Context context){
            this.context = context;
        }

        @Override
        protected Void doInBackground(String[]... datos) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = connMgr.getActiveNetworkInfo();
            if(network != null && network.isConnected()) {

                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    URL url = new URL("https://worknitor.herokuapp.com/Profesor/NuevoEventoAsignatura/"+datos[0][0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.connect();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("nombreAsig", datos[0][1]);
                    jsonObject.put("mensajeEvento", datos[0][2]);
                    jsonObject.put("fecha", datos[0][3]);

                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(jsonObject.toString());
                    wr.flush();
                    wr.close();
                    int response = httpURLConnection.getResponseCode();
                    if (response >= 200 && response <= 399) {
                        Message.message(context, "Acaba de registrarse correctamente!!");
                    } else {
                        Message.message(context, "No fue posible realizar el registro!!");
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                Message.message(context,"No hay conexión a internet!!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materia);

        final ActionBar actionbar = getSupportActionBar();

        compactCalendar = (CompactCalendarView) findViewById(R.id.cmpCalendar);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        Date d = new Date();
        txtMes = (TextView) findViewById(R.id.txtMes);
        txtMes.setText(dateFormat.format(d));

        infoUsuario = this.getSharedPreferences("co.edu.escuelaing.carlos.studlan", Context.MODE_PRIVATE);

        Bundle b = getIntent().getExtras();
        asignatura = b.getString("Asignatura");


        String[] datos = {Integer.toString(infoUsuario.getInt("Carnet",0)),asignatura};
        GetEventAsig arcs = new GetEventAsig(this);
        try {
            arcs.execute(datos).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getApplicationContext();

                Calendar cal = Calendar.getInstance();
                cal.setTime(dateClicked);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                boolean fechaEncontrada = false;

                List<Event> events = compactCalendar.getEventsForMonth(dateClicked);
                for (Event evento : events){
                    Calendar calEvent = Calendar.getInstance();
                    calEvent.setTimeInMillis(evento.getTimeInMillis());
                    int yearEvent = calEvent.get(Calendar.YEAR);
                    int monthEvent = calEvent.get(Calendar.MONTH);
                    int dayEvent = calEvent.get(Calendar.DAY_OF_MONTH);

                    if(year==yearEvent && month==monthEvent && day==dayEvent){
                        Message.message(getApplicationContext(),evento.getData().toString());
                        fechaEncontrada = true;
                    }
                }
                if(!fechaEncontrada){
                    Message.messageShort(context,"No hay un evento registardo para esta fecha!!");
                }
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                txtMes.setText(dateFormat.format(firstDayOfNewMonth));
            }
        });


    }

}
