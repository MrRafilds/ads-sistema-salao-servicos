package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactory {

    private static final String URL     = "jdbc:mysql://localhost:3306/aulapoo"
                                        + "?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=America/Sao_Paulo";
    private static final String USUARIO = "fatec";
    private static final String SENHA   = "fatec2021";   

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USUARIO, SENHA);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "Driver MySQL não encontrado! Adicione o mysql-connector-j.jar em /lib", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                "Falha ao conectar no banco de dados.\n" +
                "Verifique: MySQL rodando, banco 'trabalhoPoo' criado, usuário/senha corretos.\n" +
                "Erro: " + e.getMessage(), e);
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try { connection.close(); }
            catch (SQLException e) { System.err.println("Erro ao fechar conexão: " + e.getMessage()); }
        }
    }
}
