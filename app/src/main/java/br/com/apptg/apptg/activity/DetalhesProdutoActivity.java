package br.com.apptg.apptg.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.config.ConfiguracaoFirebase;
import br.com.apptg.apptg.config.UsuarioFirebase;
import br.com.apptg.apptg.model.Carrinho;
import br.com.apptg.apptg.model.ItemPedido;
import br.com.apptg.apptg.model.Produto;
import br.com.apptg.apptg.model.Usuario;


public class DetalhesProdutoActivity extends AppCompatActivity {

    private ImageView produtoView;
    private TextView titulo, descricao, departamento, categoria, preco;
    private Button botaoCarrinho;
    private String idUsuarioLogado;

    private Usuario usuario;
    private Produto anuncioSelecionado, produtoProcurado;
    private Carrinho pedidoRecuperado;
    //private Produto anuncioSelecionado;

    //private Usuario usuarioLogado;

    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itemCarrinho = new ArrayList<>();


    private DatabaseReference firebaseRef;
    private DatabaseReference usuariosRef;
    private DatabaseReference carrinhoRef;
    private DatabaseReference usuarioLogadoRef;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_produto);

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Detalhes do produto");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);

        //Inicializar componentes de interface
        inicializarComponentes();

        //Recuperar dados do anuncio
        //anuncioSelecionado = (Produto) getIntent().getSerializableExtra("anuncioSelecionado");
        anuncioSelecionado = (Produto) getIntent().getSerializableExtra("anuncioSelecionado");
        produtoProcurado = (Produto) getIntent().getSerializableExtra("anuncioSelecionado");

        //Recuperar id usuario logado
        //idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        if(anuncioSelecionado != null){

            titulo.setText(anuncioSelecionado.getTitulo());
            descricao.setText(anuncioSelecionado.getDescricao());
            preco.setText("R$ " + Double.toString(anuncioSelecionado.getPreco()));
            departamento.setText(anuncioSelecionado.getDepartamento());
            categoria.setText(anuncioSelecionado.getCategoria());

            String urlString = anuncioSelecionado.getImagem();
            Picasso.get().load(urlString).into(produtoView);
        }

        //Configuracoes iniciais
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        //usuariosRef = firebaseRef.child("Usuários");
        //carrinhoRef = firebaseRef.child("Carrinho");

        botaoCarrinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //so eh possivel adicionar produtos se estiver logado
                verificarUsuarioLogado();

                if(autenticacao.getCurrentUser() != null){
                    //recuperar dados do usuario depois que ele estiver logado
                    recuperarDadosUsuario();

                    confirmarQuantidade();
                }

                //salvarProdutoCarrinho(usuarioLogado, anuncioSelecionado);

                //Toast.makeText(getApplicationContext(), "Produto adicionado ao carrinho!", Toast.LENGTH_SHORT).show();


            }
        });
    }

    private void confirmarQuantidade() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogStyle);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade:");

        final EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");

        //Configurar cancelamento (a Dialog só fecha ao clicar em algum botao)
        builder.setCancelable(false);

        builder.setView(editQuantidade);

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //Resgatar a quantidade de itens ditigada pelo usuario
                String quantidade = editQuantidade.getText().toString();

                //Gerar lista com os detalhes de cada produto colocado no carrinho
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto(anuncioSelecionado.getId());
                itemPedido.setNomeProduto(anuncioSelecionado.getTitulo());
                itemPedido.setPreco(anuncioSelecionado.getPreco());
                itemPedido.setQuantidade(Integer.parseInt(quantidade));

                //aAdicionar os detalhes do produto na lista
                itemCarrinho.add( itemPedido );

                //Verificar se o pedido recuperado ja existe
                if(pedidoRecuperado == null){
                    pedidoRecuperado = new Carrinho(idUsuarioLogado);

                }

                //Recuperar dados do usuario e salvar o pedido no carrinho
                pedidoRecuperado.setNomeUsuario(usuario.getNome());
                pedidoRecuperado.setIdUsuario(usuario.getId());

                //Salvar dados do usuario e do carrinho no FIrebase
                DatabaseReference usuarioRef = firebaseRef.child("carrinhoCompras").child(idUsuarioLogado).child("informaçõesUsuario");
                usuarioRef.setValue(pedidoRecuperado);

                DatabaseReference carrinhoRef = firebaseRef.child("carrinhoCompras").child(idUsuarioLogado).child("itensCompra").child(anuncioSelecionado.getId());
                carrinhoRef.setValue(itemPedido);

                Toast.makeText(DetalhesProdutoActivity.this,"Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //Metodo criado para verificar se o usuario ja esta logado a fim de direcionar para a tela principal sem precisar passar pelo login se o usuario ja estiver logado
    private void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser() == null){
            abrirTelaLogin();
        }
    }

    //Metodo criado para abrir a Tela de Login
    private void abrirTelaLogin(){
        Intent intent = new Intent(DetalhesProdutoActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void recuperarDadosUsuario(){
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(idUsuarioLogado);

        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    usuario = dataSnapshot.getValue(Usuario.class); //recuperar os dados do usuario se os campos no Firebase nao estiverem vazios
                }
                //recuperarPedido();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    /*private void recuperarDadosUsuarioLogado (){ //os dados do FirebaseUser nao sao suficientes (so tem nome, e-mail, foto), precisa recuperar outros dados como compras

        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(new ValueEventListener() { //recupera os dados do usuario logado uma unica vez, nao precisa ficar recuperando toda hora
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Recupera dados de usuário logado
                usuarioLogado = dataSnapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }*/

    /*private void salvarProdutoCarrinho(Usuario usuarioLogado, Produto anuncioSelecionado){

        /*
         *carrinho
         * id_usuario
         *  id_produto
         *   nome usuario
         *   dados do produto
         **/

        /*HashMap<String, Object> dadosProduto = new HashMap<>();     //está fazendo um updateChildren
        dadosProduto.put("nome", anuncioSelecionado.getNome());
        dadosProduto.put("pesquisa", anuncioSelecionado.getTitulo());
        dadosProduto.put("preco", anuncioSelecionado.getPreco());
        dadosProduto.put("quantidade", anuncioSelecionado.getId());
        dadosProduto.put("uNome", usuarioLogado.getNome());

        DatabaseReference usuarioRef = usuariosRef.child(usuarioLogado.getId()).child("Compras").child(anuncioSelecionado.getId());
        usuarioRef.setValue(dadosProduto);

       DatabaseReference carrinhosRef = carrinhoRef.child(usuarioLogado.getId()).child("Compras").child(anuncioSelecionado.getId());
       carrinhosRef.setValue(dadosProduto);

    }*/

    private void inicializarComponentes(){

        produtoView = findViewById(R.id.produtoView);
        titulo = findViewById(R.id.textTituloDetalhe);
        descricao = findViewById(R.id.textDescricaoDetalhe);
        departamento = findViewById(R.id.textDepartDetalhe);
        categoria = findViewById(R.id.textCategoriaDetalhe);
        preco = findViewById(R.id.textPrecoDetalhe);
        botaoCarrinho = findViewById(R.id.botaoCarrinho);

    }

    /*@Override
    protected void onStart(){
        super.onStart();

        //Recupera dados do usuario logado sempre que ele clicar em um novo produto. Os dados serao recuperados sempre atualizados
        //Como esse metodo recupera uma unica vez, nao ha necessidade de usar o onStop para finalizar essa busca
        recuperarDadosUsuarioLogado();
    }*/
}

