package br.com.apptg.apptg.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import br.com.apptg.apptg.config.ConfiguracaoFirebase;

public class Usuario implements Serializable {

    private String id, nome, email, senha, caminhoFoto;

    //Construtor
    public Usuario(){

    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuariosRef = firebaseRef.child("usuarios").child(getId());
        usuariosRef.setValue(this); //this -> passamos o proprio objeto usuario
    }

    //metodo criado para atualizar dados
    public void atualizar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(getId());

        Map<String, Object> valoresUsuario = converterParaMap();
        usuarioRef.updateChildren(valoresUsuario);
    }

    //Criar um MAP para usar o updateChildren (converteu o usuario em HashMap)
    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("id", getId());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("email", getEmail());
        usuarioMap.put("caminhoFoto", getCaminhoFoto());

        return usuarioMap;
    }

    //getter and setter são utilizados para setar e configurar os atributos da classe

    //nao leva em consideracao o Id quando salvar os dados do usuario ¬¬
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude //nao leva em consideracao a Senha quando salvar os dados do usuario
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) { this.caminhoFoto = caminhoFoto;
    }

}
