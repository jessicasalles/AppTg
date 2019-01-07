package br.com.apptg.apptg.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.config.ConfiguracaoFirebase;
import br.com.apptg.apptg.config.UsuarioFirebase;
import br.com.apptg.apptg.model.Lista;
import br.com.apptg.apptg.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

public class EditarPerfilActivity extends AppCompatActivity {

    private CircleImageView foto;
    private TextView alterarFoto;
    private Button salvar;
    private TextInputEditText nome, email;
    private Usuario usuarioLogado;
    private StorageReference storageRef;
    private String identificadorUsuario;

    private static final int SELECAO_GALERIA = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        //Configuracoes iniciais
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storageRef = ConfiguracaoFirebase.getFirebaseStorage();
        identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Editar perfil");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        //Inicializar componentes
        inicializarComponentes();

        //Recuperar dados usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        nome.setText(usuarioPerfil.getDisplayName());
        email.setText(usuarioPerfil.getEmail());

        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null){

            Glide.with(EditarPerfilActivity.this).load(url).into(foto);

        }else{
            foto.setImageResource(R.drawable.avatar);
        }

        //Salvar alterações do nome
        salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nomeAtualizado = nome.getText().toString();

                //atualizar nome no perfil do Firebase
                UsuarioFirebase.atualizarNomeUsuario(nomeAtualizado);

                //atualizar nome no banco de dados
                usuarioLogado.setNome(nomeAtualizado);
                usuarioLogado.atualizar(); //nao vai sobrescrever os dados, apenas atualizar

                Toast.makeText(EditarPerfilActivity.this,"Dados atualizados com sucesso!",Toast.LENGTH_SHORT ).show();
            }
        });

        //Adicionar foto
        alterarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //caminho padrao para acessar as fotos dentro do celular
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(i.resolveActivity(getPackageManager()) != null){ //verificacao para ver se existe galeria de fotos
                    startActivityForResult(i, SELECAO_GALERIA); //SELECAO_GALERIA É UM REQUEST CODE (aula de permissoes)
                }
            }
        });

        }

    //Sobrescrever metodo para captuar retorno da imagem da galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) { //verificar se existe alguma imagem
            Bitmap imagem = null;

            try {
                //Selecao apenas da galeria de fotos
                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagemSelecionada = data.getData(); //criacao da Uri e recebendo "data" como parametro
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagemSelecionada);
                        break;
                }

                //Caso tenha sido escolhida uma imagem
                    if (imagem != null) {

                        //Configura imagem na tela
                        foto.setImageBitmap(imagem);

                        //Recuperar dados da imagem para o firebase
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos); //comprimindo a imagem com uma qualidade 70
                        byte[] dadosImagem = baos.toByteArray();

                        //Salvar imagem no firebase
                        StorageReference imagemRef = storageRef //organizar as imagens em pastas e colocar nome com id do usuario
                                .child("imagens")
                                .child("perfil")
                                .child(identificadorUsuario + ".jpeg");
                        UploadTask uploadTask = imagemRef.putBytes(dadosImagem); //deve-se passar um array de bytes da imagem
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditarPerfilActivity.this, "Erro ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                //Recuperar local da foto (URL)
                                Uri url = taskSnapshot.getDownloadUrl();
                                atualizarFotoUsuario(url);

                                Toast.makeText(EditarPerfilActivity.this, "Sucesso ao fazer upload da imagem!", Toast.LENGTH_SHORT).show();
                            }
                        });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Metodo para atualizar a foto do usuario
    private void atualizarFotoUsuario(Uri url){
        //Atualizar foto no perfil
        UsuarioFirebase.atualizarFotoUsuario(url);

        //Atualizar foto no Firebase
        usuarioLogado.setCaminhoFoto(url.toString()); //converteu para string apenas aqui, pois setCaminhoFoto recebe uma String
        usuarioLogado.atualizar();

        Toast.makeText(EditarPerfilActivity.this, "Sua foto foi atualizada!", Toast.LENGTH_SHORT).show();

    }

    //Metodo para inicializar componentes
    public void inicializarComponentes(){

        foto = findViewById(R.id.editFotoPerfil);
        alterarFoto = findViewById(R.id.txtAlterarFoto);
        nome = findViewById(R.id.editNomePerfil);
        email = findViewById(R.id.editEmailPerfil);
        salvar = findViewById(R.id.botaoSalvarAlteracoes);
        email.setFocusable(false); //o usuario não consegue alterar o campo de e-mail
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;

    }
}
