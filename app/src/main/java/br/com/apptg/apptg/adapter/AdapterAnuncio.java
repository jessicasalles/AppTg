package br.com.apptg.apptg.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.model.Produto;

public class AdapterAnuncio extends RecyclerView.Adapter<AdapterAnuncio.MyViwHolder> {

    private List<Produto> anuncios;
    private Context context;

    public AdapterAnuncio(List<Produto> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @Override
    public MyViwHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.postagem_detalhe, parent, false);

        return new MyViwHolder(item);
    }

    @Override
    public void onBindViewHolder(MyViwHolder holder, int position) {

        Produto anuncio = anuncios.get(position);
        holder.titulo.setText(anuncio.getTitulo());
        holder.valor.setText("R$ " + Double.toString(anuncio.getPreco()));

        String urlFotos = anuncio.getImagem();
        Picasso.get().load(urlFotos).into(holder.foto);

    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }

    public class MyViwHolder extends RecyclerView.ViewHolder{

        TextView titulo;
        TextView valor;
        ImageView foto;

        public MyViwHolder(View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.textNome);
            valor = itemView.findViewById(R.id.textPostagem);
            foto = itemView.findViewById(R.id.imagePostagem);
        }
    }
}
