package br.com.apptg.apptg.activity;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.config.ConfiguracaoFirebase;

public class SenhaActivity extends AppCompatActivity {

    private ImageView cadeado;
    private EditText email;
    private TextView textEmail;
    private Button alterarSenha;
    private String alterarEmail;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senha);

        configuracoesIniciais();

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Alterar Senha");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        alterarSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alterarEmail = email.getText().toString().trim();

                if (alterarEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(alterarEmail).matches()) {
                    email.setError("Por favor, entre com um e-mail válido.");
                    return;
                }


                autenticacao.sendPasswordResetEmail(alterarEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){
                                    Toast.makeText(SenhaActivity.this, "Check email to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SenhaActivity.this, "Fail to send reset password email!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;

    }

    public void configuracoesIniciais(){

        cadeado = findViewById(R.id.imageCadeado);
        email = findViewById(R.id.editEmail);
        textEmail = findViewById(R.id.textSenha);
        alterarSenha = findViewById(R.id.botaoAlterarSenha);
    }


}
