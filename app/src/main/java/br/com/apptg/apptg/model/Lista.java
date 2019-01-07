package br.com.apptg.apptg.model;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import java.io.Serializable;

//OBJETO CRIADO DENTRO DE UM ARRAY LIST. O OBJETO TEM AS INFORMACOES QUE QUEREMOS EXIBIR (DADOS DO MENU DO USUARIO)

public class Lista implements Serializable{

    private String titulo;
    private String icone;

    //construtor sem passagem de parametros
    public Lista(){

    }

    //construtor com passagem de parametros
    public Lista(String titulo, String icone) {
        this.titulo = titulo;
        this.icone = icone;
    }

    //metodos usados para configurar os valores dos atributos sem precisar usar os construtores
    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }
}
