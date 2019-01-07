package br.com.apptg.apptg.model;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import br.com.apptg.apptg.config.ConfiguracaoFirebase;
import br.com.apptg.apptg.config.UsuarioFirebase;

public class ItemPedido {

    private String idUsuario;
    private String idProduto;
    private String nomeProduto;
    private int quantidade;
    private Double preco;

    public ItemPedido() {
    }

    public void remover(){

        idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("carrinhoCompras").child(idUsuario).child("itensCompra").child(getIdProduto());
        pedidoRef.removeValue();
    }

    public void atualizar(List<ItemPedido> x, String idPedido){

        idUsuario = UsuarioFirebase.getIdentificadorUsuario();

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("comprasEfetuadas").child(idUsuario).child("minhasCompras").child(idPedido);
        pedidoRef.setValue( x );

    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}
