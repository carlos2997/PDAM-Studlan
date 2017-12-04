package co.edu.escuelaing.carlos.studlan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Configuracion extends AppCompatActivity implements View.OnClickListener{

    private EditText edtNombreConfig,edtClaveAntCnfig,edtClaveNuevaConfig,edtConfirmarClaveConfig;
    private ImageButton imgbUsuarioConfig;
    private Button btnGuardarConfig;
    private SharedPreferences infoUsuario;
    private static final int SELECT_FILE = 1;

    public void abrirGaleria(View v){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Seleccione la foto de perfil"),SELECT_FILE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Uri selectedImageUri = null;
        Uri selectedImage;

        String filePath = null;
        switch (requestCode) {
            case SELECT_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    String selectedPath=selectedImage.getPath();
                    if (requestCode == SELECT_FILE) {

                        if (selectedPath != null) {
                            InputStream imageStream = null;
                            try {
                                imageStream = getContentResolver().openInputStream(selectedImage);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            Bitmap bmp = BitmapFactory.decodeStream(imageStream);
                            ImageView mImg = (ImageView) findViewById(R.id.imgbUsuarioConfig);
                            mImg.setImageBitmap(bmp);

                        }
                    }
                }
                break;
        }
    }

    private class POSTRegister extends AsyncTask<String[],Void,Void> {

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
                    System.out.println("https://worknitor.herokuapp.com/Profesor/nuevosDatos/" + datos[0][0]);
                    URL url = new URL("https://worknitor.herokuapp.com/Profesor/nuevosDatos/" + datos[0][0]);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestMethod("PUT");
                    httpURLConnection.setRequestProperty("Content-Type", "application/json");
                    httpURLConnection.connect();

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("NombreUsuario", datos[0][1]);
                    jsonObject.put("Clave", datos[0][2]);

                    DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
                    wr.writeBytes(jsonObject.toString());
                    wr.flush();
                    wr.close();
                    int response = httpURLConnection.getResponseCode();
                    if (response >= 200 && response <= 399) {
                        Message.message(context, "Actualización exitosa!!");
                        Intent regreso = new Intent();
                        setResult(Activity.RESULT_OK, regreso);
                        finish();
                    } else {
                        Message.message(context, "No fue posible realizar la actualización!!");
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
        if(v.getId() == imgbUsuarioConfig.getId()){
            abrirGaleria(v);
        }else if(v.getId() == btnGuardarConfig.getId()){
            String nombreConfig = edtNombreConfig.getText().toString();
            String claveAntConfig = edtClaveAntCnfig.getText().toString();
            String claveNuevaConfig = edtClaveNuevaConfig.getText().toString();
            String confirmarClave = edtConfirmarClaveConfig.getText().toString();
            if(nombreConfig.equals("")){
                Message.message(this,"Nombre de usuario inválido!!");
            }else if(claveNuevaConfig.equals("") || !claveNuevaConfig.equals(confirmarClave) || (claveNuevaConfig.length()<6 || claveNuevaConfig.length()>12)){
                Message.message(this,"La clave no es apta o no coincide con su validación!!");
            }else{
                if(!claveAntConfig.equals(infoUsuario.getString("Clave","ABC"))){
                    Message.message(this,"La clave antigua no coincide con la registrada!!");
                }else{

                    String[] datos = {Integer.toString(infoUsuario.getInt("Carnet",0)),nombreConfig,claveNuevaConfig};

                    new POSTRegister(this).doInBackground(datos);

                    SharedPreferences.Editor datosGuardados;
                    datosGuardados = infoUsuario.edit();
                    datosGuardados.putString("NombreUsuario",nombreConfig);
                    datosGuardados.putString("Clave",claveNuevaConfig);
                    datosGuardados.commit();

                    Message.message(this,"Datos actualizados correctamente!!");
                    Intent regreso = new Intent();
                    setResult(Activity.RESULT_OK,regreso);
                    finish();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        edtNombreConfig = (EditText) findViewById(R.id.edtNombreConfig);
        edtClaveAntCnfig = (EditText) findViewById(R.id.edtClaveAntConfig);
        edtClaveNuevaConfig = (EditText) findViewById(R.id.edtClaveNuevaConfig);
        edtConfirmarClaveConfig = (EditText) findViewById(R.id.edtConfirmarClaveConfig);
        imgbUsuarioConfig = (ImageButton) findViewById(R.id.imgbUsuarioConfig);
        btnGuardarConfig = (Button) findViewById(R.id.btnGuardarConfig);

        imgbUsuarioConfig.setOnClickListener(this);
        btnGuardarConfig.setOnClickListener(this);

        infoUsuario = this.getSharedPreferences("co.edu.escuelaing.carlos.studlan", Context.MODE_PRIVATE);
    }

}