package br.com.apptg.apptg.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.model.ItemPedido;

public class AdapterCompras extends RecyclerView.Adapter<AdapterCompras.MyViewHolder> {

    private List<ItemPedido> itens;
    private Context context;

    public AdapterCompras(List<ItemPedido> itens, Context context) {
        this.itens = itens;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.postagem_compras, parent, false);

        return new AdapterCompras.MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final ItemPedido carrinho = itens.get(position);

        Double total = 0.0;

        int qtd = carrinho.getQuantidade();

        if(qtd == 1){
            Double preco = carrinho.getPreco();
            holder.titulo.setText(carrinho.getNomeProduto());
            holder.quantidade.setText(String.valueOf(qtd));
            holder.preco.setText(Double.toString(preco));
        }

        if(qtd >= 2){
            int qtde = carrinho.getQuantidade();
            Double precoTotal = carrinho.getPreco();
            //total = (qtde * 5.0);
            total = (qtde * precoTotal);

            holder.titulo.setText(carrinho.getNomeProduto());
            holder.quantidade.setText(String.valueOf(qtde));
            holder.preco.setText(Double.toString(total));
        }
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo;
        TextView preco;
        TextView quantidade;
        TextView excluir;

        public MyViewHolder(View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textNomeProduto);
            preco = itemView.findViewById(R.id.textPreco);
            quantidade = itemView.findViewById(R.id.textQuantidade);
            excluir = itemView.findViewById(R.id.textExcluir);
        }
    }
}
