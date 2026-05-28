package view;

import dao.ClienteDAO;
import model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class TelaCliente extends JFrame {

    private JTextField txtNome, txtCpf, txtTelefone, txtBusca;
    private JTable tabela;
    private DefaultTableModel modelo;
    private int idSelecionado = -1;
    private final ClienteDAO dao = new ClienteDAO();

    public TelaCliente() {
        setTitle("Clientes");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ── Formulário 
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; pForm.add(new JLabel("Nome: *"), g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        txtNome = new JTextField(25); pForm.add(txtNome, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        pForm.add(new JLabel("CPF: *"), g);
        g.gridx = 1; g.weightx = 0.5; g.fill = GridBagConstraints.HORIZONTAL;
        txtCpf = new JTextField(15); pForm.add(txtCpf, g);

        g.gridx = 0; g.gridy = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        pForm.add(new JLabel("Telefone:"), g);
        g.gridx = 1; g.weightx = 0.5; g.fill = GridBagConstraints.HORIZONTAL;
        txtTelefone = new JTextField(15); pForm.add(txtTelefone, g);

        // ── Botões 
        JPanel pBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        JButton btnNovo    = new JButton("Novo");
        JButton btnSalvar  = new JButton("Salvar");
        JButton btnAlterar = new JButton("Alterar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar  = new JButton("Limpar");
        pBotoes.add(btnNovo); pBotoes.add(btnSalvar); pBotoes.add(btnAlterar);
        pBotoes.add(btnExcluir); pBotoes.add(btnLimpar);

        // ── Busca 
        JPanel pBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        pBusca.add(new JLabel("Buscar por nome:"));
        pBusca.add(txtBusca);
        pBusca.add(btnBuscar);

        // ── Tabela 
        modelo = new DefaultTableModel(new String[]{"ID", "Nome", "CPF", "Telefone"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabela);

        // ── Layout principal
        JPanel topo = new JPanel(new BorderLayout());
        topo.add(pForm,   BorderLayout.CENTER);
        topo.add(pBotoes, BorderLayout.SOUTH);

        setLayout(new BorderLayout(0, 4));
        add(topo,   BorderLayout.NORTH);
        add(pBusca, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);
        scroll.setPreferredSize(new Dimension(0, 220));

        // ── Eventos 
        btnNovo.addActionListener(e    -> limpar());
        btnSalvar.addActionListener(e  -> salvar());
        btnAlterar.addActionListener(e -> alterar());
        btnExcluir.addActionListener(e -> excluir());
        btnLimpar.addActionListener(e  -> limpar());
        btnBuscar.addActionListener(e  -> buscar());
        txtBusca.addActionListener(e   -> buscar());
        tabela.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { selecionarLinha(); }
        });

        carregarTabela();
    }

    private void salvar() {
        if (!validar()) return;
        try {
            dao.inserir(new Cliente(txtNome.getText().trim(), txtCpf.getText().trim(), txtTelefone.getText().trim()));
            JOptionPane.showMessageDialog(this, "Cliente salvo com sucesso!");
            limpar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterar() {
        if (idSelecionado < 0) { JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela."); return; }
        if (!validar()) return;
        try {
            dao.alterar(new Cliente(idSelecionado, txtNome.getText().trim(), txtCpf.getText().trim(), txtTelefone.getText().trim()));
            JOptionPane.showMessageDialog(this, "Cliente alterado com sucesso!");
            limpar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (idSelecionado < 0) { JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela."); return; }
        int r = JOptionPane.showConfirmDialog(this, "Confirma exclusão?", "Excluir", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Cliente excluído!");
            limpar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        try {
            String t = txtBusca.getText().trim();
            preencher(t.isEmpty() ? dao.listarTodos() : dao.buscarPorNome(t));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selecionarLinha() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        idSelecionado = (int) modelo.getValueAt(row, 0);
        txtNome.setText((String) modelo.getValueAt(row, 1));
        txtCpf.setText((String) modelo.getValueAt(row, 2));
        txtTelefone.setText((String) modelo.getValueAt(row, 3));
    }

    private void carregarTabela() {
        try { preencher(dao.listarTodos()); }
        catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Erro ao carregar: " + ex.getMessage()); }
    }

    private void preencher(List<Cliente> lista) {
        modelo.setRowCount(0);
        for (Cliente c : lista)
            modelo.addRow(new Object[]{c.getIdCliente(), c.getNome(), c.getCpf(), c.getTelefone()});
    }

    private void limpar() {
        idSelecionado = -1;
        txtNome.setText(""); txtCpf.setText(""); txtTelefone.setText(""); txtBusca.setText("");
        tabela.clearSelection();
        carregarTabela();
    }

    private boolean validar() {
        if (txtNome.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Nome é obrigatório!"); return false; }
        if (txtCpf.getText().trim().isEmpty())  { JOptionPane.showMessageDialog(this, "CPF é obrigatório!");  return false; }
        return true;
    }
}
