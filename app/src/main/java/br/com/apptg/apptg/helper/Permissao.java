package br.com.apptg.apptg.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissao {

    public static boolean validaPermissoes(int requestCode, Activity activity, String[] permissoes){

        //verificar qual é a versao da SDK que o usuario utiliza
        if(Build.VERSION.SDK_INT >= 23){

            List<String> listaPermissoes = new ArrayList<String>();

            /*Percorre as permissões passadas para verificar uma a uma se já tem permissao liberada*/
            for (String permissao : permissoes){
                Boolean validaPermissao = ContextCompat.checkSelfPermission(activity, permissao) == PackageManager.PERMISSION_GRANTED;
                if( !validaPermissao ) listaPermissoes.add(permissao); //caso nao tenha permissao
            }

            /*Caso a lista esteja vazia, não é necessário solicitar permissao*/
            if(listaPermissoes.isEmpty()) return true;

            String[] novasPermissoes = new String[listaPermissoes.size()];
            listaPermissoes.toArray(novasPermissoes);

            //Solicita permissão
            ActivityCompat.requestPermissions(activity, novasPermissoes, requestCode);
        }
        return true;
    } //tipo boolean tem retorno
    //VOLTAR NA AULA 173 SOBRE PERMISSÕES
}