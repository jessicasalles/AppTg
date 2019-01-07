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
import br.com.apptg.apptg.model.Produto;

//O ViewHolder é usado para exibir os itens dentro do RecyclerView
public class AdapterPesquisa extends RecyclerView.Adapter<AdapterPesquisa.MyViewHolder> { //usou a classe dentro da classe, isto é, Adapter.MyViewHolder
    //MyViewHolder é uma classe dentro da classe Adapter que está dentro da classe RecyclerView

    private List<Produto> listaProdutos;
    private Context context;


    public AdapterPesquisa(List<Produto> l, Context c) {
        this.listaProdutos = l;
        this.context = c;
    }

    //metodo chamado para criar as visualizacoes (nao exibe, so cria)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //converter a lista criada (arquivo xml) em um arquivo tipo view
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produtos, parent, false);

        return new MyViewHolder(itemLista);
    }

    //EXIBIR cada um dos elementos da lista de visualizacao
    //metodo chamado a quantidade de vezes estipuladas pelo metodo abaixo: getItemCount
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Produto lista  = listaProdutos.get(position);
        holder.titulo.setText(lista.getTitulo());

        if (lista.getImagem() != null){
            Uri uri = Uri.parse(lista.getImagem());
            Glide.with(context).load(uri).into(holder.iconeProduto);
        }else {
            holder.iconeProduto.setImageResource(R.drawable.ic_home); //esta imagem so devera aparecer se der algum erro
        }
    }

    //retorna a quantidade de itens que serao exibidos
    @Override
    public int getItemCount() {

        return listaProdutos.size();
    }

    //criacao de classe interna (Inner Class)
    public class MyViewHolder extends RecyclerView.ViewHolder{

        //criacao dos atributos utilizados na lista
        TextView titulo;
        ImageView iconeProduto;

        //criacao do construtor da classe MyViewHolder
        public MyViewHolder(View itemView) {
            super(itemView);

            //através do itemViewpodemos acessar os id que estao la dentro
            titulo = itemView.findViewById(R.id.textProduto);
            iconeProduto = itemView.findViewById(R.id.iconeProduto);
        }
    }//exibir dados dentro de cada elemento da lista
}

