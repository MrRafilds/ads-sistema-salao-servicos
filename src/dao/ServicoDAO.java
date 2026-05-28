package dao;

import connection.ConnectionFactory;
import model.Cliente;
import model.Servico;
import model.TipoServico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServicoDAO {

    public void inserir(Servico s) throws SQLException {
        String sql = "INSERT INTO servico (cliente, tipoServico, detalhe, valor, data) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getCliente().getIdCliente());
            ps.setInt(2, s.getTipoServico().getIdTipoServico());
            ps.setString(3, s.getDetalhe());
            ps.setDouble(4, s.getValor());
            ps.setDate(5, new java.sql.Date(s.getData().getTime()));
            ps.executeUpdate();
        }
    }

    public void alterar(Servico s) throws SQLException {
        String sql = "UPDATE servico SET cliente=?, tipoServico=?, detalhe=?, valor=?, data=? "
                   + "WHERE idServico=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, s.getCliente().getIdCliente());
            ps.setInt(2, s.getTipoServico().getIdTipoServico());
            ps.setString(3, s.getDetalhe());
            ps.setDouble(4, s.getValor());
            ps.setDate(5, new java.sql.Date(s.getData().getTime()));
            ps.setInt(6, s.getIdServico());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM servico WHERE idServico=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Servico> listarTodos() throws SQLException {
        String sql =
            "SELECT s.idServico, s.detalhe, s.valor, s.data, " +
            "       c.idCliente, c.nome, c.cpf, c.telefone, " +
            "       t.idTipoServico, t.descricao, t.preco " +
            "FROM servico s " +
            "JOIN cliente c     ON s.cliente     = c.idCliente " +
            "JOIN tipoServico t ON s.tipoServico = t.idTipoServico " +
            "ORDER BY s.data DESC";
        List<Servico> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Servico> buscarPorCliente(String nomeCliente) throws SQLException {
        String sql =
            "SELECT s.idServico, s.detalhe, s.valor, s.data, " +
            "       c.idCliente, c.nome, c.cpf, c.telefone, " +
            "       t.idTipoServico, t.descricao, t.preco " +
            "FROM servico s " +
            "JOIN cliente c     ON s.cliente     = c.idCliente " +
            "JOIN tipoServico t ON s.tipoServico = t.idTipoServico " +
            "WHERE c.nome LIKE ? ORDER BY s.data DESC";
        List<Servico> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nomeCliente + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private Servico mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente(
            rs.getInt("idCliente"),
            rs.getString("nome"),
            rs.getString("cpf"),
            rs.getString("telefone")
        );
        TipoServico ts = new TipoServico(
            rs.getInt("idTipoServico"),
            rs.getString("descricao"),
            rs.getDouble("preco")
        );
        return new Servico(
            rs.getInt("idServico"),
            c, ts,
            rs.getString("detalhe"),
            rs.getDouble("valor"),
            rs.getDate("data")
        );
    }
}
