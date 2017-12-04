package co.edu.escuelaing.carlos.studlan;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private EditText edtCarnet, edtClave;
    private Button btnIngresar, btnRegistrar, btnRecordarClave;
    private SharedPreferences infoUsuario;


    class GetTask extends AsyncTask<String[],Void,Void>{

        private Context context;
        private String registrado;

        public GetTask(Context context){
            this.context = context;
        }

        @Override
        protected Void doInBackground(String[]... datos) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo network = connMgr.getActiveNetworkInfo();
            if(network != null && network.isConnected()) {
                URL url = null;
                HttpURLConnection urlConnection = null;
                try {
                    url = new URL("https://worknitor.herokuapp.com/Profesor/" + datos[0][0] + "/" + datos[0][1]);
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
                        registrado = sb.toString();
                        in.close();
                        urlConnection.disconnect();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                if(!registrado.contains("Nothing")){
                    SharedPreferences.Editor datosGuardados;
                    datosGuardados = infoUsuario.edit();
                    datosGuardados.putString("NombreUsuario",registrado);
                    datosGuardados.putInt("Carnet",Integer.parseInt(datos[0][0]));
                    datosGuardados.putString("Clave",datos[0][1]);
                    datosGuardados.commit();
                    Intent next1 = new Intent(context,Inicio.class);
                    startActivity(next1);
                }else{
                    Message.message(context,"Usuario o clave inválida!!");
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
        if(v.getId() == btnIngresar.getId()) {
            int carnet = 0;
            try {
                carnet = Integer.parseInt(edtCarnet.getText().toString());
            }catch(Exception ex){
                Message.message(this,"Debe escribir un carnet válido!!");
            }
            String clave = edtClave.getText().toString();
            String[] datos = {Integer.toString(carnet),clave};
            if(clave.length()>1 && carnet>0){
                new GetTask(this).doInBackground(datos);
            }else{
                Message.message(this,"Complete todos los datos!!");
            }
        }else if(v.getId() == btnRegistrar.getId()){
            Intent next2 = new Intent(Login.this,Registro.class);
            startActivity(next2);
        }else if(v.getId() == btnRecordarClave.getId()){
            Intent next3 = new Intent(Login.this,RestauroClave.class);
            startActivity(next3);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtCarnet = (EditText) findViewById(R.id.edtCarnet);
        edtClave = (EditText) findViewById(R.id.edtClave);
        btnIngresar = (Button) findViewById(R.id.btnIngresar);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        btnRecordarClave = (Button) findViewById(R.id.btnRecordarClave);

        btnIngresar.setOnClickListener(this);
        btnRegistrar.setOnClickListener(this);
        btnRecordarClave.setOnClickListener(this);
        infoUsuario = this.getSharedPreferences("co.edu.escuelaing.carlos.studlan", Context.MODE_PRIVATE);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        edtCarnet.setText("");
        edtClave.setText("");
    }

}
