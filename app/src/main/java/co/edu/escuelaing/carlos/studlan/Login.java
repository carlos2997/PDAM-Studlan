package co.edu.escuelaing.carlos.studlan;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import co.edu.escuelaing.carlos.database.DBAdapter;

public class Login extends AppCompatActivity implements View.OnClickListener{

    private EditText edtCarnet, edtClave;
    private Button btnIngresar, btnRegistrar, btnRecordarClave;
    private DBAdapter dbAdapter;
    private SharedPreferences infoUsuario;

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

            dbAdapter = new DBAdapter(this);

            String claveAlmacenada = dbAdapter.getDataTeach(carnet);

            if(clave.equals(claveAlmacenada)){
                SharedPreferences.Editor datosGuardados;
                datosGuardados = infoUsuario.edit();
                datosGuardados.putString("NombreUsuario",dbAdapter.getNameTeach(carnet));
                datosGuardados.putInt("Carnet",carnet);
                datosGuardados.putString("Clave",clave);
                datosGuardados.commit();

                Intent next1 = new Intent(Login.this,Inicio.class);
                startActivity(next1);
            }else{
                Message.message(this,"Carnet o Clave Inválida!!");
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

        dbAdapter = new DBAdapter(this);
        dbAdapter.insertDataTeach(2105537, "Carlos Ramirez", "carlos.ramirez-ot", "asd");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        edtCarnet.setText("");
        edtClave.setText("");
    }

}