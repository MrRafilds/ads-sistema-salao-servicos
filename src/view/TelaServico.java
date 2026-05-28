package view;

import dao.ClienteDAO;
import dao.ServicoDAO;
import dao.TipoServicoDAO;
import model.Cliente;
import model.Servico;
import model.TipoServico;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TelaServico extends JFrame {

    private JComboBox<Cliente>     cbCliente;
    private JComboBox<TipoServico> cbTipoServico;
    private JTextField             txtValor, txtData, txtDetalhe, txtBusca;
    private JTable                 tabela;
    private DefaultTableModel      modelo;
    private int                    idSelecionado = -1;

    private final ServicoDAO     daoServico = new ServicoDAO();
    private final ClienteDAO     daoCliente = new ClienteDAO();
    private final TipoServicoDAO daoTipo    = new TipoServicoDAO();
    private final SimpleDateFormat sdf      = new SimpleDateFormat("dd/MM/yyyy");

    public TelaServico() {
        setTitle("Serviços Prestados");
        setSize(800, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // ── Formulário 
        JPanel pForm = new JPanel(new GridBagLayout());
        pForm.setBorder(BorderFactory.createTitledBorder("Dados do Serviço Prestado"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.anchor = GridBagConstraints.WEST;

        // Cliente
        g.gridx = 0; g.gridy = 0; pForm.add(new JLabel("Cliente: *"), g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        cbCliente = new JComboBox<>();
        cbCliente.setPreferredSize(new Dimension(220, 24));
        pForm.add(cbCliente, g);

        // Tipo de Serviço
        g.gridx = 0; g.gridy = 1; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        pForm.add(new JLabel("Tipo de Serviço: *"), g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        cbTipoServico = new JComboBox<>();
        cbTipoServico.setPreferredSize(new Dimension(220, 24));
        pForm.add(cbTipoServico, g);

        // Valor (preenchido automaticamente)
        g.gridx = 0; g.gridy = 2; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        pForm.add(new JLabel("Valor (R$):"), g);
        g.gridx = 1; g.weightx = 0.3; g.fill = GridBagConstraints.HORIZONTAL;
        txtValor = new JTextField(10);
        txtValor.setEditable(false);
        pForm.add(txtValor, g);

        // Data (preenchida automaticamente)
        g.gridx = 0; g.gridy = 3; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        pForm.add(new JLabel("Data:"), g);
        g.gridx = 1; g.weightx = 0.3; g.fill = GridBagConstraints.HORIZONTAL;
        txtData = new JTextField(10);
        txtData.setEditable(false);
        pForm.add(txtData, g);

        // Detalhe
        g.gridx = 0; g.gridy = 4; g.weightx = 0; g.fill = GridBagConstraints.NONE;
        pForm.add(new JLabel("Detalhe:"), g);
        g.gridx = 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        txtDetalhe = new JTextField(30);
        pForm.add(txtDetalhe, g);

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
        pBusca.add(new JLabel("Buscar por cliente:"));
        pBusca.add(txtBusca);
        pBusca.add(btnBuscar);

        // ── Tabela 
        modelo = new DefaultTableModel(
            new String[]{"ID", "Cliente", "Tipo de Serviço", "Detalhe", "Valor (R$)", "Data"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.getColumnModel().getColumn(0).setMaxWidth(40);
        tabela.getColumnModel().getColumn(4).setMaxWidth(100);
        tabela.getColumnModel().getColumn(5).setMaxWidth(90);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabela);

        // ── Layout 
        JPanel topo = new JPanel(new BorderLayout());
        topo.add(pForm,   BorderLayout.CENTER);
        topo.add(pBotoes, BorderLayout.SOUTH);

        setLayout(new BorderLayout(0, 4));
        add(topo,   BorderLayout.NORTH);
        add(pBusca, BorderLayout.CENTER);
        add(scroll, BorderLayout.SOUTH);
        scroll.setPreferredSize(new Dimension(0, 200));

        // ── Eventos 
        // Ao selecionar tipo, preenche o valor automaticamente
        cbTipoServico.addActionListener(e -> preencherValor());

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

        carregarCombos();
        txtData.setText(sdf.format(new Date()));
        carregarTabela();
    }

    private void preencherValor() {
        TipoServico ts = (TipoServico) cbTipoServico.getSelectedItem();
        if (ts != null) txtValor.setText(String.format("%.2f", ts.getPreco()));
    }

    private void salvar() {
        if (!validar()) return;
        try {
            daoServico.inserir(montarServico());
            JOptionPane.showMessageDialog(this, "Serviço registrado com sucesso!");
            limpar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterar() {
        if (idSelecionado < 0) { JOptionPane.showMessageDialog(this, "Selecione um registro na tabela."); return; }
        if (!validar()) return;
        try {
            Servico s = montarServico();
            s.setIdServico(idSelecionado);
            daoServico.alterar(s);
            JOptionPane.showMessageDialog(this, "Serviço alterado com sucesso!");
            limpar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluir() {
        if (idSelecionado < 0) { JOptionPane.showMessageDialog(this, "Selecione um registro na tabela."); return; }
        int r = JOptionPane.showConfirmDialog(this, "Confirma exclusão?", "Excluir", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;
        try {
            daoServico.excluir(idSelecionado);
            JOptionPane.showMessageDialog(this, "Serviço excluído!");
            limpar();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscar() {
        try {
            String t = txtBusca.getText().trim();
            preencher(t.isEmpty() ? daoServico.listarTodos() : daoServico.buscarPorCliente(t));
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void selecionarLinha() {
        int row = tabela.getSelectedRow();
        if (row < 0) return;
        idSelecionado = (int) modelo.getValueAt(row, 0);

        String nomeCliente  = (String) modelo.getValueAt(row, 1);
        String nomeServico  = (String) modelo.getValueAt(row, 2);

        for (int i = 0; i < cbCliente.getItemCount(); i++)
            if (cbCliente.getItemAt(i).getNome().equals(nomeCliente)) { cbCliente.setSelectedIndex(i); break; }

        for (int i = 0; i < cbTipoServico.getItemCount(); i++)
            if (cbTipoServico.getItemAt(i).getDescricao().equals(nomeServico)) { cbTipoServico.setSelectedIndex(i); break; }

        txtDetalhe.setText((String)  modelo.getValueAt(row, 3));
        txtValor.setText(modelo.getValueAt(row, 4).toString().replace("R$ ", ""));
        txtData.setText((String)  modelo.getValueAt(row, 5));
    }

    private Servico montarServico() {
        Cliente     c   = (Cliente)     cbCliente.getSelectedItem();
        TipoServico ts  = (TipoServico) cbTipoServico.getSelectedItem();
        double      val = Double.parseDouble(txtValor.getText().trim().replace(",", "."));
        return new Servico(c, ts, txtDetalhe.getText().trim(), val, new Date());
    }

    private void carregarCombos() {
        try {
            cbCliente.removeAllItems();
            for (Cliente c : daoCliente.listarTodos()) cbCliente.addItem(c);

            cbTipoServico.removeAllItems();
            for (TipoServico ts : daoTipo.listarTodos()) cbTipoServico.addItem(ts);

            preencherValor();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar combos: " + ex.getMessage());
        }
    }

    private void carregarTabela() {
        try { preencher(daoServico.listarTodos()); }
        catch (SQLException ex) { JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage()); }
    }

    private void preencher(List<Servico> lista) {
        modelo.setRowCount(0);
        for (Servico s : lista) {
            String data = s.getData() != null ? sdf.format(s.getData()) : "";
            modelo.addRow(new Object[]{
                s.getIdServico(),
                s.getCliente().getNome(),
                s.getTipoServico().getDescricao(),
                s.getDetalhe(),
                String.format("R$ %.2f", s.getValor()),
                data
            });
        }
    }

    private void limpar() {
        idSelecionado = -1;
        carregarCombos();
        txtDetalhe.setText(""); txtBusca.setText("");
        txtData.setText(sdf.format(new Date()));
        tabela.clearSelection();
        carregarTabela();
    }

    private boolean validar() {
        if (cbCliente.getItemCount()     == 0) { JOptionPane.showMessageDialog(this, "Nenhum cliente cadastrado!"); return false; }
        if (cbTipoServico.getItemCount() == 0) { JOptionPane.showMessageDialog(this, "Nenhum tipo de serviço cadastrado!"); return false; }
        return true;
    }
}
