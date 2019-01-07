package br.com.apptg.apptg.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import br.com.apptg.apptg.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    View view;


    public ViewHolder(View itemView) {
        super(itemView);

        view = itemView;
    }

    //set details to RecyclerView postagem_detalhe
    public void setDetails (Context ctx, String title, String preco, String image){

        //Views
        TextView TitleView = view.findViewById(R.id.textNome);
        TextView Detail = view.findViewById(R.id.textPostagem);
        ImageView Image = view.findViewById(R.id.imagePostagem);


        //set data to views
        TitleView.setText(title);
        Detail.setText(preco);
        Glide.with(ctx).load(image).into(Image);
    }
}