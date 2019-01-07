package br.com.apptg.apptg.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.RecyclerItemClickListener;
import br.com.apptg.apptg.activity.ConfiguracoesActivity;
import br.com.apptg.apptg.activity.EditarPerfilActivity;
import br.com.apptg.apptg.activity.LoginActivity;
import br.com.apptg.apptg.activity.MainActivity;
import br.com.apptg.apptg.activity.PedidosActivity;
import br.com.apptg.apptg.adapter.Adapter;
import br.com.apptg.apptg.config.ConfiguracaoFirebase;
import br.com.apptg.apptg.config.UsuarioFirebase;
import br.com.apptg.apptg.model.Lista;
import br.com.apptg.apptg.model.Usuario;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class UsuarioFragment extends Fragment {

    private RecyclerView recyclerViewPerfil;
    private ArrayList<Lista> listaItensUsuarios = new ArrayList<>();
    private Usuario usuarioLogado;
    private TextView nome;
    private CircleImageView foto;

    private FirebaseAuth autenticacao;

    public UsuarioFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_usuario, container, false);

        //Configuracoes iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        recyclerViewPerfil = view.findViewById(R.id.recyclerPerfil);
        nome = view.findViewById(R.id.textSaudacao);
        foto = view.findViewById(R.id.editFoto);

        //Recuperar dados usuario
        FirebaseUser usuarioPerfil = UsuarioFirebase.getUsuarioAtual();
        nome.setText("Olá, "+ usuarioPerfil.getDisplayName());

        Uri url = usuarioPerfil.getPhotoUrl();
        if(url != null){

            Glide.with(getActivity()).load(url).into(foto);

        }else{
            foto.setImageResource(R.drawable.avatar);
       }

        //chamar metodo que criou a listagem de itens
        this.criarLista();

        //Configurar adapter (deve-se criar uma classe para configurar o adapter) aula 95
        Adapter adapter = new Adapter(listaItensUsuarios, getActivity()); //passa a lista de itens para o adapter fazer a exibicao

        //Configurar RecyclerVIew
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity()); //getActivity passa a activity principal como parametro e usa ela como contexto
        recyclerViewPerfil.setLayoutManager(layoutManager);
        recyclerViewPerfil.setHasFixedSize(true); //metodo usado para melhorar a performance do recyclerview (indicaçao do google) TAMANHO FIXO DOS ITENS DA LISTA
        recyclerViewPerfil.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL));
        recyclerViewPerfil.setAdapter(adapter); //o adaptador(adapter) ira pegar os dados, montar uma visualizacao e retornar para cada idetem do RecyclerView

        //evento de click
        recyclerViewPerfil.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getActivity(),
                        recyclerViewPerfil,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                                Lista itemSelecionado = listaItensUsuarios.get(position);

                                switch (position){
                                    //Perfil
                                    case 0:
                                        Intent i = new Intent(getActivity(), EditarPerfilActivity.class);
                                        i.putExtra("telaPerfil", itemSelecionado);
                                        startActivity(i);
                                        break;

                                    //Meus pedidos
                                    case 1:
                                        Intent ii = new Intent(getActivity(), PedidosActivity.class);
                                        ii.putExtra("telaPedidos", itemSelecionado);
                                        startActivity(ii);
                                        break;

                                    //Configurações
                                    case 2:
                                        Intent iii = new Intent(getActivity(), ConfiguracoesActivity.class);
                                        iii.putExtra("telaConfiguracao", itemSelecionado);
                                        startActivity(iii);
                                        break;

                                    //Sair
                                    case 3:
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity(), R.style.DialogStyle);

                                        //Configurar titulo e mensagem
                                        dialog.setTitle("Sair");
                                        dialog.setMessage("Você deseja mesmo sair?");

                                        //Configurar cancelamento (a Dialog só fecha ao clicar em algum botao)
                                        dialog.setCancelable(false);

                                        //Configurar icone
                                        dialog.setIcon(R.drawable.ic_close_black_24dp);

                                        //Configura acoes para botao sim e nao
                                        dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                autenticacao.signOut();
                                                Intent in = new Intent(getActivity(), MainActivity.class);
                                                startActivity(in);
                                            }
                                        });
                                        dialog.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });

                                        //Criar e exibir AlertDialog
                                        dialog.create();
                                        dialog.show();
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

        return view;

    }

    //configurar lista usando o atributo criado
    public void criarLista(){

        Lista item = new Lista("Perfil", Integer.toString(R.drawable.ic_person_pin_black_24dp));
        this.listaItensUsuarios.add(item); //adiciona os itens ao arraylist

        item = new Lista("Minhas compras", Integer.toString(R.drawable.ic_shopping_cart_black_24dp));
        this.listaItensUsuarios.add(item);

        item = new Lista("Configurações", Integer.toString(R.drawable.ic_settings_black_24dp));
        this.listaItensUsuarios.add(item);

        item = new Lista("Sair", Integer.toString(R.drawable.ic_exit_to_app_black_24dp));
        this.listaItensUsuarios.add(item);

    }

}
