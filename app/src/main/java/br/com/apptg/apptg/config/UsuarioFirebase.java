package br.com.apptg.apptg.config;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.Exclude;

import br.com.apptg.apptg.model.Usuario;

public class UsuarioFirebase {

    //metodo criado para recuperar o usuario atual
    public static FirebaseUser getUsuarioAtual(){ //retorno do tipo FirebaseUser

        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }

    public static String getIdentificadorUsuario(){

        return getUsuarioAtual().getUid();
    }

    public static void atualizarNomeUsuario(String nome){

        try{

            //recuperar usuario atual, ou seja, quem esta logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //Configurar objeto para alteracao do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();

            usuarioLogado.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( !task.isSuccessful() ){
                        Log.d("Perfil", "Erro ao atualizar nome de perfil.");
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void atualizarFotoUsuario(Uri url){

        try{

            //recuperar usuario atual, ou seja, quem esta logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();

            //Configurar objeto para alteracao do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setPhotoUri(url).build();

            usuarioLogado.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if( !task.isSuccessful() ){
                        Log.d("Perfil", "Erro ao atualizar a foto de perfil.");
                    }
                }
            });

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Usuario getDadosUsuarioLogado(){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());
        usuario.setId(firebaseUser.getUid());

        if(firebaseUser.getPhotoUrl() == null){ //usuario ainda nao configurou uma foto
            usuario.setCaminhoFoto("");
        }else {
            usuario.setCaminhoFoto(firebaseUser.getPhotoUrl().toString());
        }

        return usuario;
    }
}
