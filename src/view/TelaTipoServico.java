package view;

import dao.TipoServicoDAO;
import model.TipoServico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.util.List;

public class TelaTipoServico extends JFrame {

    private JTextField txtDescricao, txtPreco, txtBusca;
    private JTable tabela;
    private DefaultTableModel modelo;
    private int idSelecionado = -1;
    private final TipoServicoDAO dao = new TipoServicoDAO();

    public TelaTipoServico() {
        setTitle("Tipos de Serviço");
        setSize(600, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ── Formulário ────────────────────────────────────────────────────────
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Dados do Tipo de Serviço"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; pForm.add(new JLabel("Descrição: *"), g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        txtDescricao = new JTextField(25); pForm.add(txtDescricao, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        pForm.add(new JLabel("Preço (R$): *"), g);
        g.gridx = 1; g.weightx = 0.4; g.fill = GridBagConstraints.HORIZONTAL;
        txtPreco = new JTextField(10); pForm.add(txtPreco, g);

        // ── Botões ────────────────────────────────────────────────────────────
        JPanel pBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        JButton btnNovo    = new JButton("Novo");
        JButton btnSalvar  = new JButton("Salvar");
        JButton btnAlterar = new JButton("Alterar");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnLimpar  = new JButton("Limpar");
        pBotoes.add(btnNovo); pBotoes.add(btnSalvar); pBotoes.add(btnAlterar);
        pBotoes.add(btnExcluir); pBotoes.add(btnLimpar);

        // ── Busca ─────────────────────────────────────────────────────────────
        JPanel pBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        txtBusca = new JTextField(20);
        JButton btnBuscar = new JButton("Buscar");
        pBusca.add(new JLabel("Buscar por descrição:"));
        pBusca.add(txtBusca);
        pBusca.add(btnBuscar);

        // ── Tabela ────────────────────────────────────────────────────────────
        modelo = new DefaultTableModel(new String[]{"ID", "Descrição", "Preço (R$)"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.getColumnModel().getColumn(0).setMaxWidth(50);
        tabela.getColumnModel().getColumn(2).setMaxWidth(120);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabela);

        // ── Layout ────────────────────────────────────────────────────────────
        JPanel topo = new JPanel(new BorderLayout());
        topo.add(pForm,   BorderLayout.CENTER);
        topo.add(pBotoes, BorderLayout.SOUTH);

        setLayout(new BorderLayout(0, 4));
        add(topo,   BorderLayout.NORTH);
        add(pBusca, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);
        scroll.setPreferredSize(new Dimension(0, 220));

        // ── Eventos ───────────────────────────────────────────────────────────
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
            double preco = Double.parseDouble(txtPreco.getText().trim().replace(",", "."));
            dao.inserir(new TipoServico(txtDescricao.getText().trim(), preco));
            JOptionPane.showMessageDialog(this, "Tipo de serviço salvo com sucesso!");
            limpar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço inválido! Use o formato: 35.00");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterar() {
        if (idSelecionado < 0) { JOptionPane.showMessageDialog(this, "Selecione um registro na tabela."); return; }
        if (!validar()) return;
        try {
            double preco = Double.parseDouble(txtPreco.getText().trim().replace(",", "."));
            dao.alterar(new TipoServico(idSelecionado, txtDescricao.getText().trim(), preco));
            JOptionPane.showMessageDialog(this, "Tipo de serviço alterado com sucesso!");
            limpar();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço inválido! Use o formato: 35.00");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (idSelecionado < 0) { JOptionPane.showMessageDialog(this, "Selecione um registro na tabela."); return; }
        int r = JOptionPane.showConfirmDialog(this, "Confirma exclusão?", "Excluir", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;
        try {
            dao.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Tipo de serviço excluído!");
            limpar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        try {
            String t = txtBusca.getText().trim();
            preencher(t.isEmpty() ? dao.listarTodos() : dao.buscarPorDescricao(t));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selecionarLinha() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        idSelecionado = (int) modelo.getValueAt(row, 0);
        txtDescricao.setText((String) modelo.getValueAt(row, 1));
        // remove "R$ " prefix if present
        String precoStr = modelo.getValueAt(row, 2).toString().replace("R$ ", "");
        txtPreco.setText(precoStr);
    }

    private void carregarTabela() {
        try { preencher(dao.listarTodos()); }
        catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Erro ao carregar: " + ex.getMessage()); }
    }

    private void preencher(List<TipoServico> lista) {
        modelo.setRowCount(0);
        for (TipoServico ts : lista)
            modelo.addRow(new Object[]{ts.getIdTipoServico(), ts.getDescricao(),
                String.format("R$ %.2f", ts.getPreco())});
    }

    private void limpar() {
        idSelecionado = -1;
        txtDescricao.setText(""); txtPreco.setText(""); txtBusca.setText("");
        tabela.clearSelection();
        carregarTabela();
    }

    private boolean validar() {
        if (txtDescricao.getText().trim().isEmpty()) { JOptionPane.showMessageDialog(this, "Descrição é obrigatória!"); return false; }
        if (txtPreco.getText().trim().isEmpty())     { JOptionPane.showMessageDialog(this, "Preço é obrigatório!");     return false; }
        return true;
    }
}
