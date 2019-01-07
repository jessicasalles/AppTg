package br.com.apptg.apptg.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public final class ConfiguracaoFirebase { //"final" significa que a classe nao pode ser extendida (extends). Nao queremos que a classe seja extendida, pois ela será apenas para configuraçao do Firebase

    private static DatabaseReference referenciaFirebase; //static = o valor do atributo nao muda independente das instancias que forem criadas dessa classe
    private static FirebaseAuth autenticacao;
    private static StorageReference storage;

    public static DatabaseReference getFirebase(){ //metodo responsavel por recuperar a instancia do firebase

        if(referenciaFirebase == null){
            referenciaFirebase = FirebaseDatabase.getInstance().getReference(); //retorna essa referencia do Firebase
        }

        return  referenciaFirebase;
    }
    //nao queremos instanciar uma classe o tempo inteiro dentro de um processo de configuracao

    public static FirebaseAuth getFirebaseAutenticacao(){

        //verificar se ja existe autenticacao
        if(autenticacao == null){
            autenticacao = FirebaseAuth.getInstance();
        }
        return autenticacao;
    }

    //Retorna instancia do FirebaseStorage
    public static StorageReference getFirebaseStorage(){
        if (storage == null){
            storage = FirebaseStorage.getInstance().getReference();
        }
        return storage;
    }

}


