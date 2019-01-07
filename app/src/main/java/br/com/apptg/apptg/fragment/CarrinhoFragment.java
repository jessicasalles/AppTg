package br.com.apptg.apptg.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.RecyclerItemClickListener;
import br.com.apptg.apptg.activity.MainActivity;
import br.com.apptg.apptg.adapter.AdapterCarrinho;
import br.com.apptg.apptg.config.ConfiguracaoFirebase;
import br.com.apptg.apptg.config.UsuarioFirebase;
import br.com.apptg.apptg.model.Carrinho;
import br.com.apptg.apptg.model.ItemPedido;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarrinhoFragment extends Fragment {

    private RecyclerView recyclerCarrinho;
    private AdapterCarrinho adapterCarrinho;
    private DatabaseReference produtoComprado, dadosCompra;

    private ValueEventListener valueEventListenerCarrinho;

    private String idUsuarioLogado;
    private Button finalizarPedido;
    private Button continuarComprando;
    private TextView textTotalCompra;
    private int metodoPagamento;

    private List<ItemPedido> listaAnuncios;

    private ItemPedido minhasCompras;
    private Carrinho pedidoRecuperado;

    //private List<Produto> listaAnuncios = new ArrayList<>();

    public CarrinhoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_carrinho, container, false);

        recyclerCarrinho = view.findViewById(R.id.recyclerCarrinho);
        finalizarPedido = view.findViewById(R.id.btnFinalizar);
        continuarComprando = view.findViewById(R.id.btnContinuar);
        textTotalCompra = view.findViewById(R.id.textTotalCompra);

        //Recuperar id usuario logado
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        //Configuracoes iniciais
        listaAnuncios = new ArrayList<>();
        produtoComprado = ConfiguracaoFirebase.getFirebase().child("carrinhoCompras").child(idUsuarioLogado).child("itensCompra");
        dadosCompra = ConfiguracaoFirebase.getFirebase().child("carrinhoCompras").child(idUsuarioLogado).child("informaçõesUsuario");

        //Configurar recyclerView
        recyclerCarrinho.setHasFixedSize(true);
        recyclerCarrinho.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterCarrinho = new AdapterCarrinho(listaAnuncios, getActivity());
        recyclerCarrinho.setAdapter(adapterCarrinho);

        //adicionar evento de clique no recyclerView para excluir os produtos do carrinho
        recyclerCarrinho.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                recyclerCarrinho,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        ItemPedido produtoSelecionado = listaAnuncios.get(position);
                        produtoSelecionado.remover();

                        Toast.makeText(getContext(), "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        //Recuperar os produtos que foram colocados no carrinho
        recuperarCarrinho();

        recuperarDadosCompra();

        //adicionar evento de clique no recyclerView para excluir os produtos do carrinho
        recyclerCarrinho.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                recyclerCarrinho,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext(), R.style.DialogStyle);

                        //Configura AlertDialog
                        alertDialog.setTitle("Excluir item do Carrinho");
                        alertDialog.setMessage("Você tem certeza que deseja excluir esse item do carrinho?");
                        alertDialog.setCancelable(false);

                        alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                ItemPedido produtoSelecionado = listaAnuncios.get(position);
                                produtoSelecionado.remover();

                                adapterCarrinho.notifyDataSetChanged();

                                Toast.makeText(getContext(), "Produto excluído com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                adapterCarrinho.notifyDataSetChanged();
                            }
                        });

                        AlertDialog alert = alertDialog.create();
                        alert.show();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }
        ));

        continuarComprando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        finalizarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalizarPedido();
            }
        });

        return view;
    }

    private void finalizarPedido() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DialogStyle);
        builder.setTitle("Selecione um método de pagamento");

        //Configurar cancelamento (a Dialog só fecha ao clicar em algum botao)
        builder.setCancelable(false);

        //Passar um array de opcoes (usa CharSequence, nao ha necessidade de criar um array de strings porque sao poucas opcoes)
        CharSequence[] itens = new CharSequence[]{ //indice 0 eh credito, indice 1 eh debito
                "Cartão de crédito", "Cartão de débito"
        };

        //Exibir opcoes atraves de um RadioButton
        builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                metodoPagamento = which;
            }
        });

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                pedidoRecuperado.setMetodoPagamento( metodoPagamento );
                pedidoRecuperado.setStatus("confirmado");
                pedidoRecuperado.confimar();
                minhasCompras.atualizar(listaAnuncios, pedidoRecuperado.getIdPedido());
                pedidoRecuperado.remover();
                pedidoRecuperado = null;

            }
        });

        builder.setNegativeButton("Cancelar",new DialogInterface.OnClickListener() {
            @Override
            public void onClick (DialogInterface dialog,int which){

            }

        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void recuperarDadosCompra() {

        dadosCompra.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pedidoRecuperado = dataSnapshot.getValue(Carrinho.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Metodo para recuperar os produtos que foram colocados no carrinho
    public void recuperarCarrinho(){

        valueEventListenerCarrinho = produtoComprado.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                listaAnuncios.clear();
                for(DataSnapshot categoria: dataSnapshot.getChildren()){

                        minhasCompras = categoria.getValue(ItemPedido.class);

                        ItemPedido anuncio = categoria.getValue(ItemPedido.class);
                        listaAnuncios.add(anuncio);

                        recuperarValoresCarrinho();
                }
                adapterCarrinho.notifyDataSetChanged();
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

                    ItemPedido anuncio = categoria.getValue(ItemPedido.class);

                    int qtde = anuncio.getQuantidade();
                    Double precoTotal = anuncio.getPreco();
                    total += (qtde * precoTotal);

                }

                DecimalFormat df = new DecimalFormat("0.00");

                textTotalCompra.setText("R$ " + df.format(total));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        recuperarCarrinho();;
    }

    @Override
    public void onStop() {
        super.onStop();
        produtoComprado.removeEventListener( valueEventListenerCarrinho );
    }
}
