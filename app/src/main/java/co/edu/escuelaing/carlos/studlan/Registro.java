package co.edu.escuelaing.carlos.studlan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class Registro extends AppCompatActivity implements View.OnClickListener{

    private EditText edtNombre,edtCarnet,edtCorreo,edtClave,edtConfirmarClave;
    private CheckBox cboxTerminos;
    private Button btnRegistrarse;

    private class POSTRegister extends AsyncTask<String[],Void,Void>{

        private Context context;

        public POSTRegister(Context context){
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
                    System.out.println("https://worknitor.herokuapp.com/Profesor/nuevoUsuario/" + datos[0][0]);
                    URL url = new URL("https://worknitor.herokuapp.com/Profesor/nuevoUsuario/" + datos[0][0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.connect();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("nombre", datos[0][1]);
                    jsonObject.put("correo", datos[0][2]);
                    jsonObject.put("clave", datos[0][3]);

                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(jsonObject.toString());
                    wr.flush();
                    wr.close();
                    int response = httpURLConnection.getResponseCode();
                    if (response >= 200 && response <= 399) {
                        Message.message(context, "Acaba de registrarse correctamente!!");
                        Intent regreso = new Intent();
                        setResult(Activity.RESULT_OK, regreso);
                        finish();
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
    public void onClick(View v) {
        if(v.getId() == btnRegistrarse.getId()) {
            String nombre = edtNombre.getText().toString();
            String correo = edtCorreo.getText().toString();
            String clave = edtClave.getText().toString();
            String confirmarClave = edtConfirmarClave.getText().toString();

            if (nombre.equals("")) {
                Message.message(this, "El nombre del usuario es inválido!!");
            } else{
                int carnet = 0;
                try {
                    carnet = Integer.parseInt(edtCarnet.getText().toString());
                }catch(NumberFormatException ex){
                    Message.message(this, "Debe escribir un carnet numerico válido!!");
                }
                if ((carnet < 1000000 || carnet > 9999999)) {
                    Message.message(this, "El carnet no es válido!!");
                } else if (!correo.contains(".")) {
                    Message.message(this, "El correo no es válido!!");
                }else if(clave.length()<6 || clave.length()>12){
                    Message.message(this, "La clave debe tener entre 6 y 12 caracteres!!");
                }else if(!clave.equals(confirmarClave)){
                    Message.message(this, "Las claves no coinciden!!");
                }else if(!cboxTerminos.isChecked()){
                    Message.message(this,"Debe Aceptar los términos y condiciones!!");
                }else{
                    String[] datos = {edtCarnet.getText().toString(),nombre,correo,clave};
                    new POSTRegister(this).doInBackground(datos);
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        edtNombre = (EditText) findViewById(R.id.edtNombre);
        edtCarnet = (EditText) findViewById(R.id.edtCarnet);
        edtCorreo = (EditText) findViewById(R.id.edtCorreo);
        edtClave = (EditText) findViewById(R.id.edtClave);
        edtConfirmarClave = (EditText) findViewById(R.id.edtConfirmarClave);
        cboxTerminos = (CheckBox) findViewById(R.id.cboxTerminos);
        btnRegistrarse = (Button) findViewById(R.id.btnRegistrar);

        btnRegistrarse.setOnClickListener(this);

    }


}
