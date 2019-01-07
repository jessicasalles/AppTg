package br.com.apptg.apptg.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class AdapterCategoria extends BaseExpandableListAdapter {

    private String [] groupNames = { "Higiene", "Maquiagem", "Vestuário" };
    private String [][] childNames = { { "Absorvente", "Desodorante", "Sabonete", "Xampu"}, {"Lábios", "Olhos", "Pele"}, {"Blusa", "Chinelo", "Sutiã"} };

    Context context;

    public AdapterCategoria(Context context) {
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return groupNames.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childNames[groupPosition].length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupNames[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childNames[groupPosition][childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {

        TextView txtView = new TextView(context);
        txtView.setText(groupNames[groupPosition]);
        txtView.setPadding(100,0,0, 0);
        txtView.setTextColor(Color.BLACK);
        txtView.setTextSize(20);
        return txtView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {

        final TextView txtView = new TextView(context);
        txtView.setText(childNames[groupPosition][childPosition]);
        txtView.setPadding(100,0,0,0);
        txtView.setTextColor(Color.RED);
        txtView.setTextSize(15);

        /*txtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, txtView.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });*/

        return txtView;

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }
}

