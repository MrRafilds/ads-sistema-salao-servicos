package dao;

import connection.ConnectionFactory;
import model.TipoServico;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO - TipoServicoDAO
 * CRUD completo para a entidade TipoServico.
 */
public class TipoServicoDAO {

    public void inserir(TipoServico ts) throws SQLException {
        String sql = "INSERT INTO tipoServico (descricao, preco) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ts.getDescricao());
            ps.setDouble(2, ts.getPreco());
            ps.executeUpdate();
        }
    }

    public void alterar(TipoServico ts) throws SQLException {
        String sql = "UPDATE tipoServico SET descricao=?, preco=? WHERE idTipoServico=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ts.getDescricao());
            ps.setDouble(2, ts.getPreco());
            ps.setInt(3, ts.getIdTipoServico());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM tipoServico WHERE idTipoServico=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<TipoServico> listarTodos() throws SQLException {
        String sql = "SELECT * FROM tipoServico ORDER BY descricao";
        List<TipoServico> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<TipoServico> buscarPorDescricao(String desc) throws SQLException {
        String sql = "SELECT * FROM tipoServico WHERE descricao LIKE ? ORDER BY descricao";
        List<TipoServico> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + desc + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public TipoServico buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM tipoServico WHERE idTipoServico=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    private TipoServico mapear(ResultSet rs) throws SQLException {
        return new TipoServico(
            rs.getInt("idTipoServico"),
            rs.getString("descricao"),
            rs.getDouble("preco")
        );
    }
}
