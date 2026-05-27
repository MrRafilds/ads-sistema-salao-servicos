package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        setTitle("Salão de Corte - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 220);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel painel = new JPanel(new GridLayout(3, 1, 8, 8));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JButton btnClientes    = new JButton("Clientes");
        JButton btnTipos       = new JButton("Tipos de Serviço");
        JButton btnServicos    = new JButton("Serviços Prestados");

        btnClientes.addActionListener(e -> new TelaCliente().setVisible(true));
        btnTipos.addActionListener(e    -> new TelaTipoServico().setVisible(true));
        btnServicos.addActionListener(e -> new TelaServico().setVisible(true));

        painel.add(btnClientes);
        painel.add(btnTipos);
        painel.add(btnServicos);

        add(painel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TelaPrincipal().setVisible(true));
    }
}
