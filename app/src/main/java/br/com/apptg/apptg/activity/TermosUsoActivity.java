package br.com.apptg.apptg.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ScrollView;
import android.widget.TextView;

import br.com.apptg.apptg.R;

public class TermosUsoActivity extends AppCompatActivity {

    private TextView termosUso;
    private TextView titulo;
    private ScrollView rolagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termos_uso);

        termosUso = findViewById(R.id.textTermosUso);
        titulo = findViewById(R.id.textTitulo);
        rolagem = findViewById(R.id.barraRolagem);

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Termos de Uso");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }

    @Override
    public boolean onSupportNavigateUp() {

        finish();
        return false;

    }
}
