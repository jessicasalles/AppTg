package br.com.apptg.apptg.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.config.ConfiguracaoFirebase;
import br.com.apptg.apptg.config.UsuarioFirebase;
import br.com.apptg.apptg.helper.Base64Custom;
import br.com.apptg.apptg.model.Usuario;

import static br.com.apptg.apptg.config.UsuarioFirebase.getUsuarioAtual;

public class CadastroActivity extends AppCompatActivity {

    //private TextInputLayout nome;
    private TextInputEditText nome;
    //private TextInputLayout email;
    private TextInputEditText email;
    //private TextInputLayout senha;
    private TextInputEditText senha;
    private ProgressBar progressBar;
    private Button botaoCadastrar;
    private Usuario usuario;
    private String nomeUsuario, emailUsuario, senhaUsuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inicializarComponentes();

        /*Definir Mascaras*/
       /* SimpleMaskFormatter simpleMaskSenha = new SimpleMaskFormatter("AAAAAAAAAAAA"); // "A" aceita letras e números. "L" aceita só letras e "N" só números
        MaskTextWatcher maskSenha = new MaskTextWatcher(senha, simpleMaskSenha);
        senha.addTextChangedListener(maskSenha);*/

        //esconder a progressBar até clicar no botao cadastrar
        progressBar.setVisibility(View.GONE);

        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //nomeUsuario  = nome.getEditText().getText().toString().trim();
                nomeUsuario  = nome.getText().toString().trim();
               // emailUsuario = email.getEditText().getText().toString().trim();
                emailUsuario  = email.getText().toString().trim();
               // senhaUsuario = senha.getEditText().getText().toString().trim();
                senhaUsuario  = senha.getText().toString().trim();

                //apos instanciar a classe devemos setar os atributos dessa classe
                usuario = new Usuario();
                usuario.setNome(nomeUsuario);
                usuario.setEmail(emailUsuario);
                usuario.setSenha(senhaUsuario);

                if(validarCampos()){
                    cadastrarUsuario(usuario);
                }
            }
        });

    }

    public void inicializarComponentes(){

        //nome = findViewById(R.id.textNome);
        nome = findViewById(R.id.textEditNome);
        //email = findViewById(R.id.textEmail);
        email = findViewById(R.id.textEditEmail);
        //senha = findViewById(R.id.textSenha);
        senha = findViewById(R.id.textEditSenha);
        botaoCadastrar = findViewById(R.id.botaoCadastrar);
        progressBar = findViewById(R.id.progressBarCadastro);

        nome.requestFocus();

    }

    private void cadastrarUsuario(final Usuario usuario){

        progressBar.setVisibility(View.VISIBLE);

        //nesse metodo faremos a conexao com o Firebase e salvaremos os dados la
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                //esse metodo é responsavel por verificar se o cadastro do usuario foi realmente feito
                //o objeto task sera o retorno

                if (task.isSuccessful()) { //verificar se deu certo o cadastro
                    try{

                        Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuário.", Toast.LENGTH_LONG).show();

                        //Salvar dados no Firebase
                        //String identificadorUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                        String idUsuario = task.getResult().getUser().getUid();
                        usuario.setId(idUsuario);
                        usuario.salvar();

                        //Salvar dados no profile do Firebase
                        UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());

                        //Enviar um e-mail de verificacao
                        FirebaseUser usuarioLogado = getUsuarioAtual();

                        usuarioLogado.sendEmailVerification()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(CadastroActivity.this, "E-mail enviado com sucesso", Toast.LENGTH_SHORT).show();

                                    }else{
                                        Toast.makeText(CadastroActivity.this, "Erro ao fazer o cadastro.", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });

                        //após realizar o cadastro deve-se mandar o usuario direto para a MainActivity
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                        //autenticacao.signOut(); //a ideia é que o usuario faça o cadastro e depois faça o login no app (nao é para direcionar
                                            //para a tela inicial após o cadastro)
                        //finish();     //as activities ficam empilhadas, então ao finalizar uma activity, quem aparece é a que estava embaixo (foi aberta antes da activity atual)

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{

                    progressBar.setVisibility(View.GONE);

                    String falha = " ";

                    try {
                        throw task.getException();

                    } catch (FirebaseAuthWeakPasswordException e) {
                        falha = "Digite uma senha mais forte contendo mais caracteres, letras e números!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        falha = "O e-mail digitado é inválido. Digite um novo e-mail!";
                    } catch (FirebaseAuthUserCollisionException e) {
                        falha = "E-mail já cadastrado.";
                    } catch (Exception e) { //precisa manter esse catch para o caso de nao cair em uma das exceçoes anteriores, precisa manter uma exceçao generica
                        falha = "Erro ao efetuar o cadastro!";
                        e.printStackTrace();
                    }

                    Toast.makeText(CadastroActivity.this, "Erro: " + falha, Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    public void abrirLoginUsuario(View view){
        Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean validarCampos(){
        boolean retorno = true;

        if (nomeUsuario.isEmpty() || nomeUsuario.length() > 40){
            nome.setError("Por favor, entre com um nome válido.");
            retorno = false;
        }

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
