package co.edu.escuelaing.carlos.studlan;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class RestauroClave extends AppCompatActivity implements View.OnClickListener{

    private EditText edtCorreo;
    private Button btnRegistrarClave;
    private SharedPreferences infoUsuario;
    private Session session;

    private class MensajeEmail extends AsyncTask<String,Void,Void>{


        @Override
        protected Void doInBackground(String... email) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.googlemail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            try {
                session = Session.getDefaultInstance(props, new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(ConfiguracionEmail.EMAIL, ConfiguracionEmail.PASSWORD);
                    }
                });

                if (session != null) {
                    Message mm = new MimeMessage(session);
                    mm.setFrom(new InternetAddress(ConfiguracionEmail.EMAIL));
                    mm.setSubject("Reestablecer contraseña Worknitor");
                    mm.setRecipients(Message.RecipientType.TO,InternetAddress.parse(email[0]+"@mail.escuelaing.edu.co"));
                    mm.setContent("Su clave de acceso es: "+infoUsuario.getString("Clave","ASD"),"text/html; charset=utf-8");
                    Transport.send(mm);
                }
            }catch(Exception ex){
                System.out.println("Error: "+ex.getMessage());
            }
            return null;
        }

    }


    @Override
    public void onClick(View v) {
        if(v.getId() == btnRegistrarClave.getId()){
            //Consultar Correo en el sistema
            String correo = edtCorreo.getText().toString();
            if(correo.equals("") || !correo.contains(".")){
                co.edu.escuelaing.carlos.studlan.Message.message(this,"El correo no cumple con las condiciones validas!!");
            }else{
                new MensajeEmail().execute(correo);
                co.edu.escuelaing.carlos.studlan.Message.message(this,"Se ha enviado un correo para reestablecer la clave!!");
                Intent regreso = new Intent();
                setResult(Activity.RESULT_OK,regreso);
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restauroclave);
        edtCorreo = (EditText) findViewById(R.id.edtCorreo);
        btnRegistrarClave = (Button) findViewById(R.id.btnRestaurarClave);
        infoUsuario = this.getSharedPreferences("co.edu.escuelaing.carlos.studlan", Context.MODE_PRIVATE);
        btnRegistrarClave.setOnClickListener(this);
    }

}
