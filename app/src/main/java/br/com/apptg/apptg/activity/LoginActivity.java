package br.com.apptg.apptg.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.config.ConfiguracaoFirebase;
import br.com.apptg.apptg.helper.Permissao;
import br.com.apptg.apptg.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class LoginActivity extends AppCompatActivity {

    //atributos
    //private TextInputLayout email;
    private TextInputEditText email;
    //private TextInputLayout senha;
    private TextInputEditText senha;
    private Button botaoLogin;
    private ImageView botaoSair;
    private Usuario usuario;
    private ProgressBar progressBar;
    private String emailUsuario, senhaUsuario;

    private FirebaseAuth autenticacao;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Permissao.validaPermissoes(1,this, permissoesNecessarias);

        verificarUsuarioLogado();

        inicializarComponentes();

        botaoSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                abrirTelaPrincipal();
            }
        });

        /*Definir Mascaras*/
        /*SimpleMaskFormatter simpleMaskSenha = new SimpleMaskFormatter("AAAAAAAAAAAA"); // "A" aceita letras e números. "L" aceita só letras e "N" só números
        MaskTextWatcher maskSenha = new MaskTextWatcher(senha, simpleMaskSenha);
        senha.addTextChangedListener(maskSenha);*/

        //esconder a progressBar até clicar no botao login
        progressBar.setVisibility(View.GONE);

        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //emailUsuario = email.getEditText().getText().toString();
                emailUsuario = email.getText().toString();
                //senhaUsuario = senha.getEditText().getText().toString();
                senhaUsuario = senha.getText().toString();

                //pegar as informaçoes do usuario
                usuario = new Usuario();
                usuario.setEmail(emailUsuario);
                usuario.setSenha(senhaUsuario);

                if(validarCampos() == true){
                    validarLogin();
                }
            }
        });
    }

    public void inicializarComponentes(){

        //email = findViewById(R.id.editEmail);
        email = findViewById(R.id.textEditEmail);
        //senha = findViewById(R.id.editSenha);
        senha = findViewById(R.id.textEditSenha);
        botaoLogin = findViewById(R.id.botaoLogin);
        botaoSair = findViewById(R.id.botaoSair);
        progressBar = findViewById(R.id.progressBarLogin);

        email.requestFocus();

    }

    //metodo criado para verificar se o usuario ja esta logado a fim de direcionar para a tela principal sem precisar passar pelo login se o usuario ja estiver logado
    private void verificarUsuarioLogado(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser() != null){
         abrirTelaPrincipal();
        }
    }

    private void validarLogin(){

        /*ProgressDialog mDialog = new ProgressDialog(LoginActivity.this, R.style.Dialog);
        mDialog.setTitle("Login");
        mDialog.setMessage("Carregando...");
        mDialog.show();*/

        progressBar.setVisibility(View.VISIBLE);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    //Toast.makeText(LoginActivity.this, "Sucesso ao fazer login.", Toast.LENGTH_SHORT).show();
                    abrirTelaPrincipal();
                }else{

                    progressBar.setVisibility(View.GONE);

                    String erroExcecao = " ";

                    try {
                        throw task.getException();

                    } catch (FirebaseAuthInvalidUserException e) {
                        erroExcecao = "E-mail incorreto ou inexistente.";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "Senha incorreta.";
                    } catch (Exception e) { //precisa manter esse catch para o caso de nao cair em uma das exceçoes anteriores, precisa manter uma exceçao generica
                        erroExcecao = "Erro ao efetuar o login!";
                        e.printStackTrace();
                    }

                    Toast.makeText(LoginActivity.this, "Erro: " + erroExcecao, Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void abrirTelaPrincipal(){

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void abrirCadastroUsuario(View view){
            Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
            startActivity(intent);
    }

    public void esqueciSenha(View view){
        Intent intent = new Intent(LoginActivity.this, SenhaActivity.class);
        startActivity(intent);
    }

    //VOLTAR AULA 174
    //tratamento para quando o usuario nega a permissao
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for(int resultado: grantResults){

            if(resultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para utilizar esse app, é necessário utilizar as  permissões");

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean validarCampos(){
        boolean retorno = true;

        if (emailUsuario.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailUsuario).matches()){
            email.setError("Por favor, entre com um e-mail válido.");
            retorno = false;
        }
        if (senhaUsuario.isEmpty()){
            senha.setError("Por favor, entre com uma senha válida.");
            retorno = false;
        }

        return retorno;
    }
}
