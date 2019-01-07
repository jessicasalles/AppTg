package br.com.apptg.apptg.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.config.ConfiguracaoFirebase;
import br.com.apptg.apptg.config.UsuarioFirebase;
import br.com.apptg.apptg.model.Usuario;

import static br.com.apptg.apptg.config.UsuarioFirebase.getUsuarioAtual;

public class ExcluirActivity extends AppCompatActivity {

    private Button excluir;
    private FirebaseAuth autenticacao;
    private Usuario usuario;
    private DatabaseReference removerDados;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private Usuario usuarioLogado2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excluir);

        excluir = findViewById(R.id.botaoExcluir);

        final FirebaseUser usuarioLogado = getUsuarioAtual();

        //Recuperar os dados do usuario para poder apaga-los do banco de dados
        usuarioLogado2 = UsuarioFirebase.getDadosUsuarioLogado();

        //Recuperar id usuario logado
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //Configuracoes iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        removerDados = firebaseRef.child("usuarios");

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Excluir conta");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);


        excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Mensagem de confirmacao
                AlertDialog.Builder dialog = new AlertDialog.Builder(ExcluirActivity.this,  R.style.DialogStyle);

                //Configurar titulo e mensagem
                dialog.setTitle("Excluir");
                dialog.setMessage("Tem certeza que você quer excluir sua conta?");

                //Configurar cancelamento (a Dialog só fecha ao clicar em algum botao)
                dialog.setCancelable(false);

                //Configurar icone
                dialog.setIcon(R.drawable.ic_close_black_24dp);

                //Configura acoes para botao sim e nao
                dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        removerFirebase(usuarioLogado2);    //deleta informacoes do usuario do banco de dados do Firebase

                        //deleta a conta do usuario
                        AuthCredential credential = EmailAuthProvider.getCredential("user@example.com", "password1234");
                        usuarioLogado.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                usuarioLogado.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            //removerFirebase(usuarioLogado2);

                                            //Toast.makeText(ExcluirActivity.this, "Conta excluída com sucesso!", Toast.LENGTH_SHORT).show();

                                        } else {

                                            Toast.makeText(ExcluirActivity.this, "Deu ruim!!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                        sair();

                        /*autenticacao.signOut();
                        Intent in = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(in);*/
                    }
                });
                dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                //Criar e exibir AlertDialog
                dialog.create();
                dialog.show();
            }
        });
    }

    //Metodo para deletar as informacoes do usuario do banco de dados
    public void removerFirebase(Usuario usuarioLogado2){

        DatabaseReference usuarioRef = removerDados.child(usuarioLogado2.getId());
        usuarioRef.removeValue();

    }

    @Override
    public boolean onSupportNavigateUp(){

        finish();
        return false;

    }

    public void sair(){

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

}

