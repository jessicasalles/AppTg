package br.com.apptg.apptg.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import br.com.apptg.apptg.R;
import br.com.apptg.apptg.config.ConfiguracaoFirebase;
import br.com.apptg.apptg.fragment.CarrinhoFragment;
import br.com.apptg.apptg.fragment.HomeFragment;
import br.com.apptg.apptg.fragment.PesquisaFragment;
import br.com.apptg.apptg.fragment.UsuarioFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private FirebaseAuth autenticacao;

       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Configuracao de objetos
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("First Lady's Kit");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        //Configuracao bottom navigation view
           configuraBottomNavigationView();
           FragmentManager fragmentManager = getSupportFragmentManager();
           FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

           fragmentTransaction.replace(R.id.viewPager, new HomeFragment()).commit();
        }

        //Metodo responsavel por cariar a NavigationBottom
    private void configuraBottomNavigationView(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigation);

        //faz as configuracoes iniciais do Bottom Navigation
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);

        //habilitar navegacao
        habilitarNavegacao(bottomNavigationViewEx);

    }

    /**Metodo responsavel por tratar eventos de click na Bottom Navigation
     * @param viewEx
     */
    private void habilitarNavegacao(BottomNavigationViewEx viewEx){

           viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
               @Override
               public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                   FragmentManager fragmentManager = getSupportFragmentManager();
                   FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                   switch (item.getItemId()){
                       case R.id.ic_home:
                           fragmentTransaction.replace(R.id.viewPager, new HomeFragment()).commit();
                           return true;
                       case R.id.ic_pesquisa:
                           fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                           return true;
                       case R.id.ic_carrinho:
                           autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                           if(autenticacao.getCurrentUser() != null){
                           fragmentTransaction.replace(R.id.viewPager, new CarrinhoFragment()).commit();
                           }else {
                               Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                               startActivity(intent);
                               finish();
                           }
                           return true;
                       case R.id.ic_usuario:
                           autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                           if(autenticacao.getCurrentUser() != null){
                               fragmentTransaction.replace(R.id.viewPager, new UsuarioFragment()).commit();
                           }else {
                               Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                               startActivity(intent);
                               finish();
                           }
                           return true;
                   }
                   return false;
               }
           });
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    //fazer os tratamentos necessarios nos menus selecionados
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // getItemId retorna qual menu foi selecionado
        switch (item.getItemId()) {

            case R.id.action_sair:
                deslogarUsuario();
                return true;

            case R.id.action_settings:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deslogarUsuario(){
           autenticacao.signOut();
           Intent intent = new Intent(MainActivity.this, LoginActivity.class);
           startActivity(intent);
           finish();
    }*/
}
