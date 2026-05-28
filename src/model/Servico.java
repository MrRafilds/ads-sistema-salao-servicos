package model;

import java.util.Date;

public class Servico {

    private int         idServico;
    private Cliente     cliente;
    private TipoServico tipoServico;
    private String      detalhe;
    private double      valor;
    private Date        data;

    public Servico() {}

    public Servico(Cliente cliente, TipoServico tipoServico,
                   String detalhe, double valor, Date data) {
        this.cliente     = cliente;
        this.tipoServico = tipoServico;
        this.detalhe     = detalhe;
        this.valor       = valor;
        this.data        = data;
    }

    public Servico(int idServico, Cliente cliente, TipoServico tipoServico,
                   String detalhe, double valor, Date data) {
        this.idServico   = idServico;
        this.cliente     = cliente;
        this.tipoServico = tipoServico;
        this.detalhe     = detalhe;
        this.valor       = valor;
        this.data        = data;
    }

    public int         getIdServico()              { return idServico; }
    public void        setIdServico(int id)        { this.idServico = id; }

    public Cliente     getCliente()                { return cliente; }
    public void        setCliente(Cliente c)       { this.cliente = c; }

    public TipoServico getTipoServico()            { return tipoServico; }
    public void        setTipoServico(TipoServico ts) { this.tipoServico = ts; }

    public String      getDetalhe()                { return detalhe; }
    public void        setDetalhe(String d)        { this.detalhe = d; }

    public double      getValor()                  { return valor; }
    public void        setValor(double v)          { this.valor = v; }

    public Date        getData()                   { return data; }
    public void        setData(Date d)             { this.data = d; }
}
