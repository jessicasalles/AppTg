package br.com.apptg.apptg.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.model.Lista;

//O ViewHolder é usado para exibir os itens dentro do RecyclerView
public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> { //usou a classe dentro da classe, isto é, Adapter.MyViewHolder
    //MyViewHolder é uma classe dentro da classe Adapter que está dentro da classe RecyclerView

    private List<Lista> listaItens;
    private Context context;


    public Adapter(ArrayList<Lista> listagem, Context c) {
            this.listaItens = listagem;
            this.context = c;
    }

    //metodo chamado para criar as visualizacoes (nao exibe, so cria)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //converter a lista criada (arquivo xml) em um arquivo tipo view
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lista, parent, false);

        return new MyViewHolder(itemLista);
    }

    //EXIBIR cada um dos elementos da lista de visualizacao
    //metodo chamado a quantidade de vezes estipuladas pelo metodo abaixo: getItemCount
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Lista lista  = listaItens.get(position);
        holder.titulo.setText(lista.getTitulo());

        if (lista.getIcone() != null){
            holder.iconePerfil.setImageResource(Integer.parseInt(lista.getIcone()));
        }else {
            holder.iconePerfil.setImageResource(R.drawable.ic_home); //esta imagem so devera aparecer se der algum erro
        }
    }

    //retorna a quantidade de itens que serao exibidos
    @Override
    public int getItemCount() {

        return listaItens.size();
    }

    //criacao de classe interna (Inner Class)
    public class MyViewHolder extends RecyclerView.ViewHolder{

        //criacao dos atributos utilizados na lista
        TextView titulo;
        ImageView iconePerfil;

        //criacao do construtor da classe MyViewHolder
        public MyViewHolder(View itemView) {
            super(itemView);

            //através do itemViewpodemos acessar os id que estao la dentro
            titulo = itemView.findViewById(R.id.textPerfil);
            iconePerfil = itemView.findViewById(R.id.iconePessoa);
        }
    }//exibir dados dentro de cada elemento da lista


}
