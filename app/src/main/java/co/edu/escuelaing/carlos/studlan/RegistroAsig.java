package co.edu.escuelaing.carlos.studlan;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegistroAsig extends AppCompatActivity implements View.OnClickListener{

    private EditText edtRegitroAsig;
    private Button btnRegitrarAsig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registroasig);

        edtRegitroAsig = (EditText) findViewById(R.id.edtRegitroAsig);
        btnRegitrarAsig = (Button) findViewById(R.id.btnRegistrarAsig);


    }

    @Override
    public void onClick(View v) {
        if(v.getId() == btnRegitrarAsig.getId()){

            /*Falta verificar la asig si existe*/

            Intent next1 = new Intent(RegistroAsig.this,Inicio.class);
            startActivity(next1);
        }
    }
}
