package br.com.apptg.apptg.fragment;


import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.RecyclerItemClickListener;
import br.com.apptg.apptg.activity.DetalhesProdutoActivity;
import br.com.apptg.apptg.activity.PedidosActivity;
import br.com.apptg.apptg.activity.PerfilProdutoActivity;
import br.com.apptg.apptg.adapter.Adapter;
import br.com.apptg.apptg.adapter.AdapterPesquisa;
import br.com.apptg.apptg.config.ConfiguracaoFirebase;
import br.com.apptg.apptg.model.Produto;
import br.com.apptg.apptg.model.Usuario;

/**
 * A simple {@link Fragment} subclass.
 */
public class PesquisaFragment extends Fragment {

    //atributos: Widget
    private android.support.v7.widget.SearchView searchViewPesquisa;
    private RecyclerView recyclerPesquisa;

    //Criar lista para armazenar os produtos
    private List<Produto> listaProdutos = new ArrayList<>();

    private DatabaseReference produtosRef;

    private AdapterPesquisa adapterPesquisa;

    public PesquisaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_pesquisa, container, false);

        searchViewPesquisa = view.findViewById(R.id.searchViewPesquisa);
        recyclerPesquisa = view.findViewById(R.id.recyclerPesquisa);

        //Configuracoes iniciais
        listaProdutos = new ArrayList<>();
        produtosRef = ConfiguracaoFirebase.getFirebase().child("produtos");

        //Configurar recyclerView
        recyclerPesquisa.setHasFixedSize(true);
        recyclerPesquisa.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterPesquisa = new AdapterPesquisa(listaProdutos, getActivity());
        recyclerPesquisa.setAdapter(adapterPesquisa);

        //Configurar searchView
        searchViewPesquisa.setQueryHint("Buscar produtos");

        //Configurar evento de clique
        recyclerPesquisa.addOnItemTouchListener(new RecyclerItemClickListener(
                getActivity(),
                recyclerPesquisa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Produto produtoProcurado = listaProdutos.get(position); //recuperou o produto que foi clicado
                        Intent i = new Intent(getActivity(), DetalhesProdutoActivity.class);
                        i.putExtra("anuncioSelecionado", produtoProcurado);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                }

        ));

        //capturar o que o usuario digitou
        searchViewPesquisa.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            //costuma-se utilizar apenas um dos metodos abaixo, nunca os dois simultaneamente

            @Override
            //os resultados só aparecem quando o usuario escreve e aperta a lupa (enter)
            public boolean onQueryTextSubmit(String query) {
                //teste para verificar o funcionamento da busca
                //Log.d("onQueryTextSubmit", "texto digitado: " + query);
                return false;
                //alterar para true antes de testar
            }

            @Override
            //os resultados vao aparecendo conforme o usuario digita o que procura
            public boolean onQueryTextChange(String newText) {
                //String textoDigitado = searchViewPesquisa.getQuery().toString();
                String textoDigitado = newText.toUpperCase();

                //Usar metodo para pesquisar produtos e carregar dentro do recyclerView
                pesquisarProdutos(textoDigitado);
                return true;
            }
        });

        return view;
    }

    //Criar metodo para pesquisar produtos com passagem de parametro
    private void pesquisarProdutos(String texto){

        //limpar lista
        listaProdutos.clear();

        //pesquisar produtos caso tenha texto na pesquisa
        if(texto.length() > 2){ //verifica se o tamanho da string texto eh maior que zero

            Query query = produtosRef.orderByChild("pesquisa").startAt(texto).endAt(texto + "\uf8ff"); // codigo para expandir a pesquisa

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) { //recuperar os filhos do parametro passado

                                listaProdutos.add(ds.getValue(Produto.class));
                    }

                    adapterPesquisa.notifyDataSetChanged();

                    //int total = listaProdutos.size();
                    //Log.i("totalProdutos", "total " + total);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }


    }

}
