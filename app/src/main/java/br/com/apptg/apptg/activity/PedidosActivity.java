package br.com.apptg.apptg.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.adapter.AdapterCompras;
import br.com.apptg.apptg.config.ConfiguracaoFirebase;
import br.com.apptg.apptg.config.UsuarioFirebase;
import br.com.apptg.apptg.model.ItemPedido;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerCompras;
    private AdapterCompras adapterCompras;
    private List<ItemPedido> listaAnuncios;
    private DatabaseReference produtoComprado;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Minhas compras");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        recyclerCompras = findViewById(R.id.recyclerCarrinho);

        //Recuperar id usuario logado
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //Configuracoes iniciais
        listaAnuncios = new ArrayList<>();
        produtoComprado = ConfiguracaoFirebase.getFirebase().child("comprasEfetuadas").child(idUsuarioLogado).child("minhasCompras");

        //Configurar recyclerView
        recyclerCompras.setHasFixedSize(true);
        recyclerCompras.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        adapterCompras = new AdapterCompras(listaAnuncios, getApplicationContext());
        recyclerCompras.setAdapter(adapterCompras);

        //Recuperar os produtos que foram colocados no carrinho
        recuperarCarrinho();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    public void recuperarCarrinho(){

        produtoComprado.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot categoria: dataSnapshot.getChildren()){
                    for(DataSnapshot produto: categoria.getChildren()){
                        //minhasCompras = categoria.getValue(ItemPedido.class);

                        ItemPedido anuncio = produto.getValue(ItemPedido.class);
                        listaAnuncios.add(anuncio);

                        recuperarValoresCarrinho();
                    }
                    adapterCompras.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void recuperarValoresCarrinho(){

        produtoComprado.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Double total = 0.0;

                for(DataSnapshot categoria: dataSnapshot.getChildren()){
                    for(DataSnapshot produto: categoria.getChildren()){

                        ItemPedido anuncio = produto.getValue(ItemPedido.class);

                        int qtde = anuncio.getQuantidade();
                        Double precoTotal = anuncio.getPreco();
                        total += (qtde * precoTotal);

                    }
                }

                DecimalFormat df = new DecimalFormat("0.00");

                //textTotalCompra.setText("R$ " + df.format(total));

                adapterCompras.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
