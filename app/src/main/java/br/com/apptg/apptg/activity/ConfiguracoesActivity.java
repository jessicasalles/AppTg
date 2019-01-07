package br.com.apptg.apptg.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.RecyclerItemClickListener;
import br.com.apptg.apptg.adapter.Adapter;
import br.com.apptg.apptg.model.Lista;

public class ConfiguracoesActivity extends AppCompatActivity {

   private RecyclerView recyclerConfig;
   private ArrayList<Lista> listaItensUsuarios = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        recyclerConfig = findViewById(R.id.recyclerConfiguracoes);

        //chamar metodo que criou a listagem de itens
        this.criarLista();

        //Configurar adapter (deve-se criar uma classe para configurar o adapter) aula 95
        Adapter adapter = new Adapter(listaItensUsuarios, getApplicationContext()); //passa a lista de itens para o adapter fazer a exibicao

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Configurações");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        //Configurar RecyclerVIew
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager((getApplicationContext()));

        //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext()); //getActivity passa a activity principal como parametro e usa ela como contexto
        recyclerConfig.setLayoutManager(layoutManager);
        recyclerConfig.setHasFixedSize(true); //metodo usado para melhorar a performance do recyclerview (indicaçao do google) TAMANHO FIXO DOS ITENS DA LISTA
        recyclerConfig.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerConfig.setAdapter(adapter); //o adaptador(adapter) ira pegar os dados, montar uma visualizacao e retornar para cada idetem do RecyclerView

        //evento de click
        recyclerConfig.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerConfig,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Lista itemSelecionado = listaItensUsuarios.get(position);

                                switch (position) {
                                    //Termos de uso
                                    case 0:
                                        Intent i = new Intent(getApplicationContext(), TermosUsoActivity.class);
                                        i.putExtra("telaTermos", itemSelecionado);
                                        startActivity(i);
                                        break;

                                    //Fale Conosco
                                    case 1:
                                        Intent ii = new Intent(getApplicationContext(), FaleConoscoActivity.class);
                                        ii.putExtra("telaFale", itemSelecionado);
                                        startActivity(ii);
                                        break;

                                    //Alterar Senha
                                    case 2:
                                        Intent iii = new Intent(getApplicationContext(), SenhaActivity.class);
                                        iii.putExtra("telaSenha", itemSelecionado);
                                        startActivity(iii);
                                        break;

                                    //Excluir conta
                                    case 3:
                                        Intent iiii = new Intent(getApplicationContext(), ExcluirActivity.class);
                                        iiii.putExtra("telaExcluir", itemSelecionado);
                                        startActivity(iiii);
                                        break;
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        })
        );
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;
    }

    //configurar lista usando o atributo criado
    public void criarLista(){

        Lista item = new Lista("Termos de Uso", Integer.toString(R.drawable.ic_description_black_24dp));
        this.listaItensUsuarios.add(item); //adiciona os itens ao arraylist

        item = new Lista("Fale Conosco", Integer.toString(R.drawable.ic_chat_bubble_outline_black_24dp));
        this.listaItensUsuarios.add(item);

        item = new Lista("Alterar senha", Integer.toString(R.drawable.ic_lock_outline_black_24dp));
        this.listaItensUsuarios.add(item);

        item = new Lista("Excluir conta", Integer.toString(R.drawable.ic_thumb_down_black_24dp));
        this.listaItensUsuarios.add(item);


    }
}
