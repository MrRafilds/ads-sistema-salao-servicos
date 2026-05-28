package model;

public class TipoServico {

    private int    idTipoServico;
    private String descricao;
    private double preco;

    public TipoServico() {}

    public TipoServico(String descricao, double preco) {
        this.descricao = descricao;
        this.preco     = preco;
    }

    public TipoServico(int idTipoServico, String descricao, double preco) {
        this.idTipoServico = idTipoServico;
        this.descricao     = descricao;
        this.preco         = preco;
    }

    public int    getIdTipoServico()           { return idTipoServico; }
    public void   setIdTipoServico(int id)     { this.idTipoServico = id; }

    public String getDescricao()               { return descricao; }
    public void   setDescricao(String desc)    { this.descricao = desc; }

    public double getPreco()                   { return preco; }
    public void   setPreco(double preco)       { this.preco = preco; }

    @Override
    public String toString() {
        return descricao + "  —  R$ " + String.format("%.2f", preco);
    }
}
