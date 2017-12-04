package co.edu.escuelaing.carlos.studlan;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ViewListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class Inicio extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgUsuario;
    private TextView txtNombreUsuario;
    private Button btnConfiguracion;
    private SharedPreferences infoUsuario;

    private List<String> titulosAsignaturas;
    private List<Integer> imagenesAsignaturas;
    private String[] materias;

    private CarouselView carouselView;

    class GetTaskAsig extends AsyncTask<String[],Void,Void> {

        private Context context;
        private String[] materias;

        public GetTaskAsig(Context context){
            this.context = context;
        }

        public String[] getMaterias(){
            return materias;
        }

        @Override
        protected Void doInBackground(String[]... datos) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = connMgr.getActiveNetworkInfo();
            if(network != null && network.isConnected()) {
                URL url = null;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL("https://worknitor.herokuapp.com/Profesor/Asignaturas/" + datos[0][0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;
                    StringBuilder sb = new StringBuilder();
                    try {
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append('\n');
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        String mat = sb.toString();
                        if(mat.length()==3){
                            this.materias = new String[0];
                        }else{
                            this.materias = mat.substring(1,mat.length()-2).split(",");
                        }
                        in.close();
                        urlConnection.disconnect();
                    }

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

    @Override
    public void onClick(View v) {
        if(v.getId() == btnConfiguracion.getId()){
            Intent next2 = new Intent(Inicio.this,Configuracion.class);
            startActivity(next2);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        btnConfiguracion = (Button) findViewById(R.id.btnConfiguracion);

        carouselView = (CarouselView) findViewById(R.id.customCarouselView);

        imgUsuario = (ImageView) findViewById(R.id.imgUsuario);
        imgUsuario.setImageResource(R.mipmap.ic_launcher);

        infoUsuario = this.getSharedPreferences("co.edu.escuelaing.carlos.studlan", Context.MODE_PRIVATE);

        txtNombreUsuario = (TextView) findViewById(R.id.txtNombreUsuario);
        txtNombreUsuario.setText(infoUsuario.getString("NombreUsuario","Anonimo"));

        titulosAsignaturas = new ArrayList<>();
        imagenesAsignaturas = new ArrayList<>();

        cargarAsignaturas();

        btnConfiguracion.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        titulosAsignaturas.clear();
        imagenesAsignaturas.clear();

        cargarAsignaturas();
    }

    private void cargarAsignaturas() {
        String[] datos = {Integer.toString(infoUsuario.getInt("Carnet",0))};
        GetTaskAsig arcs = new GetTaskAsig(this);
        try {
            arcs.execute(datos).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        materias = arcs.getMaterias();
        if(materias.length>0){
            for(int i = 0 ; i < materias.length ; i++){
                titulosAsignaturas.add(materias[i].substring(1,materias[i].length()-1));
                if((i%2)+1==1){
                    imagenesAsignaturas.add(R.drawable.image_1);
                }else{
                    imagenesAsignaturas.add(R.drawable.image_2);
                }
            }
        }

        carouselView.setPageCount(materias.length);
        carouselView.setViewListener(viewListener);

        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Intent ingresarAsignatura = new Intent(Inicio.this, Materia.class);
                ingresarAsignatura.putExtra("Asignatura",titulosAsignaturas.get(position));
                startActivity(ingresarAsignatura);
            }
        });
        materias = null;
    }


    /**
     * ViewListener para el carousel, cambio de imagen al deslizar
     */
    private ViewListener viewListener = new ViewListener() {

        @Override
        public View setViewForPosition(int position) {
            View customView = getLayoutInflater().inflate(R.layout.view_custom, null);

            TextView labelTextView = (TextView) customView.findViewById(R.id.labelTextView);
            ImageView AsigImageView = (ImageView) customView.findViewById(R.id.fruitImageView);

            AsigImageView.setImageResource(imagenesAsignaturas.get(position));
            labelTextView.setText(titulosAsignaturas.get(position));

            carouselView.setIndicatorGravity(Gravity.CENTER_HORIZONTAL| Gravity.TOP);

            return customView;
        }
    };

}
