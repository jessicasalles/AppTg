package br.com.apptg.apptg.model;

import com.google.firebase.database.DatabaseReference;

import br.com.apptg.apptg.config.ConfiguracaoFirebase;

public class Carrinho {

    private String idUsuario;
    private String idPedido;
    private String nomeUsuario;
    private String nomeProduto;
    private String categoria;
    private String status = "pendente";
    private int metodoPagamento;

    public Carrinho() {
    }

    public Carrinho(String idUsu) {

        setIdUsuario(idUsu);

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("carrinhoCompras").child(idUsu);
        setIdPedido(pedidoRef.push().getKey());

    }

    public void confimar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference pedidoRef = firebaseRef.child("comprasEfetuadas").child(idUsuario).child("informaçõesUsuario");
        pedidoRef.setValue( this );

    }

    public void remover(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        //DatabaseReference pedidoRef = firebaseRef.child("carrinhoCompras").child(idUsuario).child("itensCompra");
        DatabaseReference pedidoRef = firebaseRef.child("carrinhoCompras").child(idUsuario);
        pedidoRef.removeValue();

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getNomeProduto() {
        return nomeProduto;
    }

    public void setNomeProduto(String nomeProduto) {
        this.nomeProduto = nomeProduto;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(int metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }
}
