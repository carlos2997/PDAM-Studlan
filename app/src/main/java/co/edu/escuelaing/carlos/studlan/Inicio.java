package co.edu.escuelaing.carlos.studlan;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageClickListener;
import com.synnapps.carouselview.ViewListener;

import java.util.ArrayList;
import java.util.List;

import co.edu.escuelaing.carlos.database.DBAdapterAsig;

public class Inicio extends AppCompatActivity implements View.OnClickListener{

    private ImageView imgUsuario;
    private TextView txtNombreUsuario;
    private Button btnConfiguracion;
    private SharedPreferences infoUsuario;

    private List<String> titulosAsignaturas;
    private List<Integer> imagenesAsignaturas;

    private CarouselView carouselView;
    private DBAdapterAsig dbAdapterAsig;

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

        dbAdapterAsig = new DBAdapterAsig(this);

        titulosAsignaturas = new ArrayList<>();
        imagenesAsignaturas = new ArrayList<>();

        cargarAsignaturas();

        btnConfiguracion.setOnClickListener(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        txtNombreUsuario.setText(infoUsuario.getString("NombreUsuario","Anonimo"));

        int cantAsig = dbAdapterAsig.getAsignatures(infoUsuario.getInt("Carnet",0)).size();
        if(cantAsig > titulosAsignaturas.size()){
            List<String> asignaturas = dbAdapterAsig.getAsignatures(infoUsuario.getInt("Carnet",0));
            for(int i = 0 ; i < cantAsig ; i++){
                if(!titulosAsignaturas.contains(asignaturas.get(i))){
                    titulosAsignaturas.add(asignaturas.get(i));
                    if((i%2)+1==1){
                        imagenesAsignaturas.add(R.drawable.image_1);
                    }else{
                        imagenesAsignaturas.add(R.drawable.image_2);
                    }
                }
            }
        }
        carouselView.setPageCount(cantAsig);
        carouselView.setViewListener(viewListener);

        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Intent ingresarAsignatura = new Intent(Inicio.this, Materia.class);
                startActivity(ingresarAsignatura);
            }
        });
    }

    private void cargarAsignaturas(){
        int cantAsig = dbAdapterAsig.getAsignatures(infoUsuario.getInt("Carnet",0)).size();
        if(cantAsig>0){
            List<String> asignaturas = dbAdapterAsig.getAsignatures(infoUsuario.getInt("Carnet",0));
            for(int i = 0 ; i < cantAsig ; i++){
                titulosAsignaturas.add(asignaturas.get(i));
                if((i%2)+1==1){
                    imagenesAsignaturas.add(R.drawable.image_1);
                }else{
                    imagenesAsignaturas.add(R.drawable.image_2);
                }
            }
        }
        carouselView.setPageCount(cantAsig);
        carouselView.setViewListener(viewListener);

        carouselView.setImageClickListener(new ImageClickListener() {
            @Override
            public void onClick(int position) {
                Intent ingresarAsignatura = new Intent(Inicio.this, Materia.class);
                startActivity(ingresarAsignatura);
            }
        });
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
