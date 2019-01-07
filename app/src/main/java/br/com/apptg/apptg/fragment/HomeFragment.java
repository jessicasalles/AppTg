package br.com.apptg.apptg.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.RecyclerItemClickListener;
import br.com.apptg.apptg.activity.DetalhesProdutoActivity;
import br.com.apptg.apptg.adapter.AdapterAnuncio;
import br.com.apptg.apptg.adapter.ViewHolder;
import br.com.apptg.apptg.config.ConfiguracaoFirebase;
import br.com.apptg.apptg.helper.SpacesItemDecoration;

import br.com.apptg.apptg.model.Produto;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference anuncioPublicoRef;
    private AdapterAnuncio adapterAnuncios;
    private List<Produto> listaAnuncios = new ArrayList<>();
    //private List<Produto> listaAnuncios;
    private AlertDialog dialog;
    private Spinner spinnerCategoria;
    private String filtroCategoria = " "; //por padrao vai começar vazio para depois receber o valor do filtro
    private boolean filtrandoPorCategoria = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        //Configurar referencia Firebase
        anuncioPublicoRef = ConfiguracaoFirebase.getFirebase().child("produtos");

        //set layout as LinearLayout
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SpacesItemDecoration(2, -40, false));

        adapterAnuncios = new AdapterAnuncio(listaAnuncios, getContext());
        recyclerView.setAdapter(adapterAnuncios);

        //Aplicar evento de clique para abrir o produto e ver os detalhes
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //recuperar o anuncio selecionado
                        Produto anuncioSelecionado = listaAnuncios.get(position);

                        //Enviar o anuncio selecionado para a activity de detalhes do anuncio
                        Intent i = new Intent(getActivity(), DetalhesProdutoActivity.class);
                        i.putExtra("anuncioSelecionado", anuncioSelecionado);
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

        //Recuperar anuncios
        recuperarAnunciosPublicos();

        return view;
    }

    //Método para criar um menu de filtro apenas na home (página inicial)
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_filtro, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Implementacao do filtro ao clicar no botao
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filtro:

                AlertDialog.Builder dialogFiltro = new AlertDialog.Builder(getContext(), R.style.DialogStyle);
                dialogFiltro.setTitle("Filtrar");

                //Configurar o spinner
                View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null); //inflar o arquivo XML do spinner dentro do AlertDialog
                spinnerCategoria = viewSpinner.findViewById(R.id.spinnerFiltro);

                //Recuperar dados das string-arrays
                String[] categorias = getResources().getStringArray(R.array.categoria);

                //Configurar o spinner de categoria e criar um array adapter (usando layouts proprios do android)
                ArrayAdapter<String> adapterCategoria = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categorias);
                adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCategoria.setAdapter(adapterCategoria);

                dialogFiltro.setView(viewSpinner);

                //botao Filtrar
                dialogFiltro.setPositiveButton("Filtrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        filtroCategoria = spinnerCategoria.getSelectedItem().toString();
                        filtrandoPorCategoria = true;

                        recuperarAnunciosPorCategoria(filtroCategoria);

                    }
                });

                //botao Limpar
                dialogFiltro.setNegativeButton("Limpar filtros", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        recuperarAnunciosIniciais();
                    }
                });

                AlertDialog dialog = dialogFiltro.create();
                dialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    //Metodo para recuperar a tela inicial de anuncios sem filtro
    public void recuperarAnunciosIniciais() {
        //Configura nó por categoria
        anuncioPublicoRef = ConfiguracaoFirebase.getFirebase().child("produtos"); //pega o nó geral e varre os nós secundarios, porem o filtro de categoria ja direciona para o nó filtrado

        anuncioPublicoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaAnuncios.clear(); //limpa a lista antes de adicionar novos itens nela

                for (DataSnapshot categoria : dataSnapshot.getChildren()) {

                            Produto anuncio = categoria.getValue(Produto.class);
                            listaAnuncios.add(anuncio);

                }
                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //Metodo para recuperar todos os dados dos anuncios filtrados pela categoria
    public void recuperarAnunciosPorCategoria(String texto) {

       /* //Configura nó por categoria
        anuncioPublicoRef = ConfiguracaoFirebase.getFirebase()
                .child("Dado") //pega o nó geral e varre os nós secundarios, porem o filtro de categoria ja direciona para o nó filtrado
                .child(filtroCategoria);

        //Recuperar os dados DENTRO da categoria filtrada
        anuncioPublicoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaAnuncios.clear(); //limpa a lista antes de adicionar novos itens nela

                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    for (DataSnapshot anuncios : item.getChildren()) {

                        Produto anuncio = anuncios.getValue(Produto.class);
                        listaAnuncios.add(anuncio);
                    }
                }

                adapterAnuncios.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        //Configura nó por categoria
        Query query = ConfiguracaoFirebase.getFirebase().child("produtos").orderByChild("categoria").equalTo(texto);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaAnuncios.clear(); //limpa a lista antes de adicionar novos itens nela

                for (DataSnapshot categoria : dataSnapshot.getChildren()) { //recuperar os filhos do parametro passado

                    Produto anuncio = categoria.getValue(Produto.class);
                    listaAnuncios.add(anuncio);

                }

                adapterAnuncios.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void recuperarAnunciosPublicos() {

        anuncioPublicoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot categoria : dataSnapshot.getChildren()) {
                    //for (DataSnapshot item : categoria.getChildren()) {
                        //for (DataSnapshot anuncios : item.getChildren()) {

                            Produto anuncio = categoria.getValue(Produto.class);
                            listaAnuncios.add(anuncio);


                        //}

                   // }

                }
                adapterAnuncios.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


