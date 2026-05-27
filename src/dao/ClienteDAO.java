package dao;

import connection.ConnectionFactory;
import model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO - ClienteDAO
 * CRUD completo para a entidade Cliente.
 */
public class ClienteDAO {

    public void inserir(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nome, cpf, telefone) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getCpf());
            ps.setString(3, c.getTelefone());
            ps.executeUpdate();
        }
    }

    public void alterar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nome=?, cpf=?, telefone=? WHERE idCliente=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getNome());
            ps.setString(2, c.getCpf());
            ps.setString(3, c.getTelefone());
            ps.setInt(4, c.getIdCliente());
            ps.executeUpdate();
        }
    }

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE idCliente=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Cliente> listarTodos() throws SQLException {
        String sql = "SELECT * FROM cliente ORDER BY nome";
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public List<Cliente> buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE nome LIKE ? ORDER BY nome";
        List<Cliente> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + nome + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE idCliente=?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getInt("idCliente"),
            rs.getString("nome"),
            rs.getString("cpf"),
            rs.getString("telefone")
        );
    }
}
