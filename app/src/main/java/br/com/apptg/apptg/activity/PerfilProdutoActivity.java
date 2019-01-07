package br.com.apptg.apptg.activity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.config.ConfiguracaoFirebase;
import br.com.apptg.apptg.model.Produto;

public class PerfilProdutoActivity extends AppCompatActivity {

    private DatabaseReference produtosRef;
    private DatabaseReference referencia;
    private Produto produtoSelecionado;
    private ImageView imagemProduto;
    private TextView nomeProduto, precoProduto, disponibilidadeProduto, descricaoProduto;
    private ValueEventListener valueEventListenerPerfilProduto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_produto);

        //Inicialicar componentes
        inicialicarComponentes();

        //Configuracoes iniciais
        produtosRef = ConfiguracaoFirebase.getFirebase().child("produtos");

        //Recuperar produto selecionado
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            produtoSelecionado = (Produto) bundle.getSerializable("produto selecionado");
        }

        //Recuperar imagem do produto
        String caminhoFoto = produtoSelecionado.getImagem();
        if (caminhoFoto != null) {
            Uri url = Uri.parse(caminhoFoto);
            Glide.with(PerfilProdutoActivity.this).load(url).into(imagemProduto);
        }

    }

    //load Data into recyclerview onStart
    @Override
    public void onStart() {
        super.onStart();

        recuperarDadosProduto();

    }

    @Override
    protected void onStop() { //remover o Listener quando ele não estiver sendo executado
        super.onStop();
        referencia.removeEventListener(valueEventListenerPerfilProduto);
    }

    private void recuperarDadosProduto(){

        referencia = produtosRef.child(produtoSelecionado.getId());
        valueEventListenerPerfilProduto = referencia.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Produto model = dataSnapshot.getValue(Produto.class);

                        String title = model.getTitulo();
                        Double preco = model.getPreco();
                        //String disponibilidade = model.getDisponibilidade();
                        String descricao = model.getDescricao();

                        //Configura valores recuperados
                        nomeProduto.setText(title);
                        precoProduto.setText(Double.toString(preco));
                        //disponibilidadeProduto.setText(disponibilidade);
                        descricaoProduto.setText(descricao);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );

    }

    private void inicialicarComponentes(){

        imagemProduto = findViewById(R.id.imagemProduto);
        nomeProduto = findViewById(R.id.nomeProduto);
        descricaoProduto = findViewById(R.id.descricaoProduto);
        precoProduto = findViewById(R.id.precoProduto);
        disponibilidadeProduto = findViewById(R.id.disponibilidadeProduto);
    }


}
