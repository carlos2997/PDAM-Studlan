package co.edu.escuelaing.carlos.studlan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import co.edu.escuelaing.carlos.database.DBAdapter;

public class Registro extends AppCompatActivity implements View.OnClickListener{

    private EditText edtNombre,edtCarnet,edtCorreo,edtClave,edtConfirmarClave;
    private CheckBox cboxTerminos;
    private Button btnRegistrarse;
    private DBAdapter dbAdapter;
    private SharedPreferences infoUsuario;

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

                    dbAdapter = new DBAdapter(this);
                    dbAdapter.insertDataTeach(carnet, dbAdapter.changeName(nombre), correo, clave);

                    Intent regreso = new Intent();
                    setResult(Activity.RESULT_OK,regreso);
                    finish();
                    Message.message(this,"Acaba de registrarse correctamente!!");
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

        infoUsuario = this.getSharedPreferences("co.edu.escuelaing.carlos.studlan", Context.MODE_PRIVATE);

    }


}

