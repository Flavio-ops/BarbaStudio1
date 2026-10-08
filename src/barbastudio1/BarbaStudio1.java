package barbastudio1;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class BarbaStudio1 extends JFrame {

    private static final Color COLOR_BG = new Color(18, 18, 20);
    private static final Color COLOR_CARD = new Color(28, 29, 33);
    private static final Color COLOR_ACCENT = new Color(212, 175, 55);
    private static final Color COLOR_ACCENT_HOVER = new Color(235, 195, 70);
    private static final Color COLOR_DANGER = new Color(210, 65, 65);
    private static final Color COLOR_DANGER_HOVER = new Color(230, 85, 85);
    private static final Color COLOR_TEXT = new Color(245, 245, 245);
    private static final Color COLOR_TEXT_MUTED = new Color(150, 153, 160);

    private CardLayout cardLayout;
    private JPanel painelPrincipal;

    private JPanel formCard;
    private JComboBox<BarbeiroItem> cbBarbeiros;
    private JComboBox<String> cbHorarios;
    private JTextField txtCliente;
    private JTextField txtData;
    private JLabel lblStatusLogin;
    private JButton btnConfirmarAgendamento;
    private JPanel painelBoasVindas;

    private JTable tabela;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtPesquisa;
    private JLabel lblTotalAgendamentos, lblTotalHoje, lblTotalBarbeiros;

    private Integer clienteLogadoId = null;
    private String clienteLogadoNome = null;

    private Image fundoImage;
    private Image logoImage;

    public BarbaStudio1() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException ex) {
                System.err.println("Driver MySQL não encontrado!");
            }
        }

        carregarImagens();
        configurarUIManager();

        setTitle("Barba Studio");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 650));

        cardLayout = new CardLayout();
        painelPrincipal = new JPanel(cardLayout);
        painelPrincipal.add(criarTelaCliente(), "cliente");
        painelPrincipal.add(criarTelaAdmin(), "admin");

        add(painelPrincipal);
        cardLayout.show(painelPrincipal, "cliente");

        carregarBarbeiros();
    }

    private void carregarImagens() {
        String[] caminhosFundo = {
            "src/imagens/fundo.png",
            "imagens/fundo.png",
            System.getProperty("user.dir") + "/src/imagens/fundo.png",
            System.getProperty("user.dir") + "/imagens/fundo.png"
        };
        String[] caminhosLogo = {
            "src/imagens/logo.png",
            "imagens/logo.png",
            System.getProperty("user.dir") + "/src/imagens/logo.png",
            System.getProperty("user.dir") + "/imagens/logo.png"
        };

        for (String caminho : caminhosFundo) {
            try {
                File f = new File(caminho);
                if (f.exists()) {
                    fundoImage = ImageIO.read(f);
                    break;
                }
            } catch (Exception ignored) {}
        }
        for (String caminho : caminhosLogo) {
            try {
                File f = new File(caminho);
                if (f.exists()) {
                    logoImage = ImageIO.read(f);
                    break;
                }
            } catch (Exception ignored) {}
        }

        try {
            if (fundoImage == null) {
                java.net.URL url = getClass().getResource("/imagens/fundo.png");
                if (url != null) fundoImage = ImageIO.read(url);
            }
            if (logoImage == null) {
                java.net.URL url = getClass().getResource("/imagens/logo.png");
                if (url != null) logoImage = ImageIO.read(url);
            }
        } catch (Exception ignored) {}
    }

    private void configurarUIManager() {
        UIManager.put("OptionPane.background", COLOR_CARD);
        UIManager.put("Panel.background", COLOR_CARD);
        UIManager.put("OptionPane.messageForeground", COLOR_TEXT);
        UIManager.put("Button.background", COLOR_ACCENT);
        UIManager.put("Button.foreground", Color.BLACK);
    }

    // =========================================================
    //                    TELA DO CLIENTE
    // =========================================================
    private JPanel criarTelaCliente() {
        JPanel tela = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                if (fundoImage != null) {
                    g2.drawImage(fundoImage, 0, 0, getWidth(), getHeight(), this);
                    g2.setColor(new Color(0, 0, 0, 140));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                } else {
                    g2.setColor(COLOR_BG);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(10, 25, 5, 25));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        if (logoImage != null) {
            Image scaled = logoImage.getScaledInstance(110, 110, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(scaled));
            left.add(lblLogo);
        }

        JPanel centerTitle = new JPanel();
        centerTitle.setLayout(new BoxLayout(centerTitle, BoxLayout.Y_AXIS));
        centerTitle.setOpaque(false);

        JLabel lblTitle = new JLabel("BARBA STUDIO");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(COLOR_ACCENT);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("ESTILO  •  RESPEITO  •  VOCÊ");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(COLOR_TEXT_MUTED);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        centerTitle.add(lblTitle);
        centerTitle.add(Box.createVerticalStrut(4));
        centerTitle.add(lblSub);

        JButton btnAdmin = criarBotaoAnimado("Admin", new Color(40, 40, 45), new Color(60, 60, 65), COLOR_TEXT);
        btnAdmin.setPreferredSize(new Dimension(110, 40));
        btnAdmin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdmin.addActionListener(e -> abrirLoginAdmin());

        header.add(left, BorderLayout.WEST);
        header.add(centerTitle, BorderLayout.CENTER);
        header.add(btnAdmin, BorderLayout.EAST);

        // ===== ÁREA CENTRAL =====
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(10, 40, 30, 40));

        // --- Painel de Boas-vindas ---
        painelBoasVindas = new JPanel();
        painelBoasVindas.setLayout(new BoxLayout(painelBoasVindas, BoxLayout.Y_AXIS));
        painelBoasVindas.setBackground(new Color(15, 15, 18, 230));
        painelBoasVindas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 2),
                new EmptyBorder(30, 35, 30, 35)
        ));
        painelBoasVindas.setPreferredSize(new Dimension(400, 310));

        JLabel lblBemVindo = new JLabel("Bem-vindo ao Barba Studio");
        lblBemVindo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblBemVindo.setForeground(COLOR_TEXT);
        lblBemVindo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMsg = new JLabel("<html><div style='text-align:center;width:300px;'>"
                + "Para realizar um agendamento você precisa<br>criar uma conta e fazer login."
                + "</div></html>");
        lblMsg.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMsg.setForeground(COLOR_TEXT_MUTED);
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        painelBoasVindas.add(lblBemVindo);
        painelBoasVindas.add(Box.createVerticalStrut(12));
        painelBoasVindas.add(lblMsg);
        painelBoasVindas.add(Box.createVerticalStrut(22));

        JPanel botoesConta = new JPanel(new GridLayout(2, 2, 10, 10));
        botoesConta.setOpaque(false);
        botoesConta.setMaximumSize(new Dimension(320, 90));
        botoesConta.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnCriar = criarBotaoAnimado("Criar Conta", new Color(45, 47, 54), new Color(65, 68, 76), COLOR_TEXT);
        JButton btnEntrar = criarBotaoAnimado("Entrar", COLOR_ACCENT, COLOR_ACCENT_HOVER, Color.BLACK);
        JButton btnEsqueci = criarBotaoAnimado("Esqueci Senha", new Color(45, 47, 54), new Color(65, 68, 76), COLOR_TEXT);
        JButton btnSair = criarBotaoAnimado("Sair", new Color(70, 35, 35), new Color(95, 45, 45), COLOR_TEXT);

        btnCriar.addActionListener(e -> abrirCadastroCliente());
        btnEntrar.addActionListener(e -> abrirLoginCliente());
        btnEsqueci.addActionListener(e -> abrirRecuperarSenha());
        btnSair.addActionListener(e -> fazerLogoutCliente());

        botoesConta.add(btnCriar);
        botoesConta.add(btnEntrar);
        botoesConta.add(btnEsqueci);
        botoesConta.add(btnSair);
        painelBoasVindas.add(botoesConta);

        // --- Formulário de Agendamento ---
        formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(new Color(15, 15, 18, 230));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT, 2),
                new EmptyBorder(20, 25, 20, 25)
        ));
        formCard.setPreferredSize(new Dimension(400, 480));
        formCard.setVisible(false);

        JLabel lblFormTitle = new JLabel("Novo Agendamento");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblFormTitle.setForeground(COLOR_TEXT);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblStatusLogin = new JLabel("");
        lblStatusLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatusLogin.setForeground(new Color(80, 200, 120));
        lblStatusLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        formCard.add(lblFormTitle);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(lblStatusLogin);
        formCard.add(Box.createVerticalStrut(14));

        cbBarbeiros = new JComboBox<>();
        txtCliente = criarTextField();
        txtCliente.setEditable(false);
        txtData = criarTextField();
        // Data no formato brasileiro
        txtData.setText(new SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date()));
        cbHorarios = new JComboBox<>(gerarHorarios());

        adicionarCampo(formCard, "Barbeiro Responsável:", cbBarbeiros);
        adicionarCampo(formCard, "Nome do Cliente:", txtCliente);
        adicionarCampo(formCard, "Data (dd/MM/aaaa):", txtData);
        adicionarCampo(formCard, "Horário de Atendimento:", cbHorarios);

        formCard.add(Box.createVerticalStrut(8));

        btnConfirmarAgendamento = criarBotaoAnimado("CONFIRMAR AGENDAMENTO", COLOR_ACCENT, COLOR_ACCENT_HOVER, Color.BLACK);
        btnConfirmarAgendamento.addActionListener(e -> agendarHorario());
        formCard.add(btnConfirmarAgendamento);

        formCard.add(Box.createVerticalStrut(12));

        JButton btnLogout = criarBotaoAnimado("Sair da Conta", new Color(70, 35, 35), new Color(95, 45, 45), COLOR_TEXT);
        btnLogout.addActionListener(e -> fazerLogoutCliente());
        formCard.add(btnLogout);

        center.add(painelBoasVindas);
        center.add(formCard);

        tela.add(header, BorderLayout.NORTH);
        tela.add(center, BorderLayout.CENTER);

        return tela;
    }

    // =========================================================
    //                    TELA DO ADMIN
    // =========================================================
    private JPanel criarTelaAdmin() {
        JPanel tela = new JPanel(new BorderLayout(12, 12));
        tela.setBackground(COLOR_BG);
        tela.setBorder(new EmptyBorder(10, 15, 15, 15));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_CARD);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_ACCENT),
                new EmptyBorder(12, 20, 12, 20)
        ));

        JLabel lblTitle = new JLabel("BARBA STUDIO  •  Painel Administrativo");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(COLOR_ACCENT);

        JPanel pnlStats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlStats.setBackground(COLOR_CARD);
        lblTotalAgendamentos = criarStatCard("Total Geral", "0", pnlStats);
        lblTotalHoje = criarStatCard("Hoje", "0", pnlStats);
        lblTotalBarbeiros = criarStatCard("Barbeiros", "0", pnlStats);

        JButton btnVoltar = criarBotaoAnimado("← Voltar", new Color(50, 50, 55), new Color(70, 70, 75), COLOR_TEXT);
        btnVoltar.setPreferredSize(new Dimension(100, 34));
        btnVoltar.addActionListener(e -> cardLayout.show(painelPrincipal, "cliente"));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(COLOR_CARD);
        right.add(pnlStats);
        right.add(btnVoltar);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        JPanel center = new JPanel(new BorderLayout(15, 0));
        center.setBackground(COLOR_BG);

        JPanel painelBarbeiros = new JPanel();
        painelBarbeiros.setLayout(new BoxLayout(painelBarbeiros, BoxLayout.Y_AXIS));
        painelBarbeiros.setBackground(COLOR_CARD);
        painelBarbeiros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 47, 54), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));
        painelBarbeiros.setPreferredSize(new Dimension(320, 0));

        JLabel lblBarbeiros = new JLabel("Gestão de Barbeiros");
        lblBarbeiros.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblBarbeiros.setForeground(COLOR_TEXT);
        lblBarbeiros.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelBarbeiros.add(lblBarbeiros);
        painelBarbeiros.add(Box.createVerticalStrut(15));

        JComboBox<BarbeiroItem> cbBarbeirosAdmin = new JComboBox<>();
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nome FROM profissionais ORDER BY nome")) {
            while (rs.next()) {
                cbBarbeirosAdmin.addItem(new BarbeiroItem(rs.getInt("id"), rs.getString("nome")));
            }
        } catch (SQLException ignored) {}

        JLabel lblLista = new JLabel("Barbeiros cadastrados:");
        lblLista.setForeground(COLOR_TEXT_MUTED);
        lblLista.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelBarbeiros.add(lblLista);
        painelBarbeiros.add(Box.createVerticalStrut(5));

        cbBarbeirosAdmin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        cbBarbeirosAdmin.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelBarbeiros.add(cbBarbeirosAdmin);
        painelBarbeiros.add(Box.createVerticalStrut(20));

        JButton btnCad = criarBotaoAnimado("+ Cadastrar Novo Barbeiro", new Color(45, 47, 54), new Color(65, 68, 76), COLOR_TEXT);
        btnCad.addActionListener(e -> {
            cadastrarNovoBarbeiro();
            cbBarbeirosAdmin.removeAllItems();
            try (Connection conn = Conexao.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT id, nome FROM profissionais ORDER BY nome")) {
                while (rs.next()) {
                    cbBarbeirosAdmin.addItem(new BarbeiroItem(rs.getInt("id"), rs.getString("nome")));
                }
            } catch (SQLException ignored) {}
        });

        JButton btnExc = criarBotaoAnimado("Excluir Barbeiro Selecionado", COLOR_DANGER, COLOR_DANGER_HOVER, Color.WHITE);
        btnExc.addActionListener(e -> excluirBarbeiro(cbBarbeirosAdmin));

        painelBarbeiros.add(btnCad);
        painelBarbeiros.add(Box.createVerticalStrut(10));
        painelBarbeiros.add(btnExc);

        center.add(painelBarbeiros, BorderLayout.WEST);
        center.add(criarPainelAdminTabela(), BorderLayout.CENTER);

        tela.add(header, BorderLayout.NORTH);
        tela.add(center, BorderLayout.CENTER);
        return tela;
    }

    private JPanel criarPainelAdminTabela() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(45, 47, 54), 1),
                new EmptyBorder(12, 12, 12, 12)
        ));

        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setBackground(COLOR_CARD);

        JLabel title = new JLabel("Gestão de Agendamentos");
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(COLOR_TEXT);

        txtPesquisa = criarTextField();
        txtPesquisa.setPreferredSize(new Dimension(170, 28));
        txtPesquisa.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String f = txtPesquisa.getText();
                sorter.setRowFilter(f.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + f));
            }
        });

        top.add(title, BorderLayout.WEST);
        top.add(txtPesquisa, BorderLayout.EAST);

        tableModel = new DefaultTableModel(new String[]{"ID", "Barbeiro", "Cliente", "Data", "Hora"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabela = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        tabela.setRowSorter(sorter);
        tabela.setBackground(COLOR_CARD);
        tabela.setForeground(COLOR_TEXT);
        tabela.setGridColor(new Color(45, 47, 54));
        tabela.setRowHeight(28);
        tabela.setSelectionBackground(new Color(60, 50, 20));
        tabela.setSelectionForeground(COLOR_ACCENT);
        tabela.getTableHeader().setBackground(COLOR_BG);
        tabela.getTableHeader().setForeground(COLOR_ACCENT);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(COLOR_CARD);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(45, 47, 54)));

        JButton btnCancelar = criarBotaoAnimado("CANCELAR AGENDAMENTO SELECIONADO", COLOR_DANGER, COLOR_DANGER_HOVER, Color.WHITE);
        btnCancelar.addActionListener(e -> cancelarAgendamento());

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btnCancelar, BorderLayout.SOUTH);
        return panel;
    }

    // =========================================================
    //                    LOGIN / CADASTRO / RECUPERAR
    // =========================================================
    private void abrirLoginAdmin() {
        JPasswordField pf = new JPasswordField();
        int op = JOptionPane.showConfirmDialog(this, pf, "Senha do Administrador", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (op == JOptionPane.OK_OPTION) {
            if ("1978".equals(new String(pf.getPassword()))) {
                cardLayout.show(painelPrincipal, "admin");
                atualizarTabela();
                atualizarMetricasAdmin();
            } else {
                JOptionPane.showMessageDialog(this, "Senha incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void abrirCadastroCliente() {
        JTextField nome = new JTextField(20);
        JTextField email = new JTextField(20);
        JTextField telefone = new JTextField(20);
        JPasswordField senha = new JPasswordField(20);
        JPasswordField confirma = new JPasswordField(20);

        JTextField[] campos = {nome, email, telefone, senha, confirma};
        for (JTextField tf : campos) {
            tf.setBackground(COLOR_BG);
            tf.setForeground(COLOR_TEXT);
            tf.setCaretColor(COLOR_ACCENT);
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 70, 75)),
                    BorderFactory.createEmptyBorder(7, 10, 7, 10)));
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_CARD);
        p.setBorder(new EmptyBorder(15, 20, 10, 20));

        p.add(criarLabelDialog("NOME COMPLETO"));
        p.add(Box.createVerticalStrut(3));
        p.add(nome);
        p.add(Box.createVerticalStrut(10));
        p.add(criarLabelDialog("E-MAIL"));
        p.add(Box.createVerticalStrut(3));
        p.add(email);
        p.add(Box.createVerticalStrut(10));
        p.add(criarLabelDialog("TELEFONE / CELULAR"));
        p.add(Box.createVerticalStrut(3));
        p.add(telefone);
        p.add(Box.createVerticalStrut(10));
        p.add(criarLabelDialog("SENHA"));
        p.add(Box.createVerticalStrut(3));
        p.add(senha);
        p.add(Box.createVerticalStrut(10));
        p.add(criarLabelDialog("CONFIRMAR SENHA"));
        p.add(Box.createVerticalStrut(3));
        p.add(confirma);

        int op = JOptionPane.showConfirmDialog(this, p, "Criar Conta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (op == JOptionPane.OK_OPTION) {
            String n = nome.getText().trim();
            String em = email.getText().trim();
            String tel = telefone.getText().trim();
            String s = new String(senha.getPassword());
            String conf = new String(confirma.getPassword());

            if (n.isEmpty() || (em.isEmpty() && tel.isEmpty()) || s.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha nome, senha e pelo menos e-mail ou telefone!");
                return;
            }
            if (!s.equals(conf)) {
                JOptionPane.showMessageDialog(this, "As senhas não coincidem!");
                return;
            }

            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO clientes (nome, email, telefone, senha, ativo) VALUES (?, ?, ?, ?, 1)")) {
                stmt.setString(1, n);
                stmt.setString(2, em.isEmpty() ? null : em);
                stmt.setString(3, tel.isEmpty() ? null : tel);
                stmt.setString(4, s);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Conta criada com sucesso!\nAgora faça login.");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao criar conta:\n" + ex.getMessage());
            }
        }
    }

    private void abrirLoginCliente() {
        JTextField contato = new JTextField(20);
        JPasswordField senha = new JPasswordField(20);

        contato.setBackground(COLOR_BG);
        contato.setForeground(COLOR_TEXT);
        contato.setCaretColor(COLOR_ACCENT);
        contato.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 75)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));

        senha.setBackground(COLOR_BG);
        senha.setForeground(COLOR_TEXT);
        senha.setCaretColor(COLOR_ACCENT);
        senha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 70, 75)),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)));

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_CARD);
        p.setBorder(new EmptyBorder(15, 20, 10, 20));

        p.add(criarLabelDialog("E-MAIL OU TELEFONE"));
        p.add(Box.createVerticalStrut(3));
        p.add(contato);
        p.add(Box.createVerticalStrut(12));
        p.add(criarLabelDialog("SENHA"));
        p.add(Box.createVerticalStrut(3));
        p.add(senha);

        int op = JOptionPane.showConfirmDialog(this, p, "Entrar", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (op == JOptionPane.OK_OPTION) {
            String c = contato.getText().trim();
            String s = new String(senha.getPassword());

            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "SELECT id, nome FROM clientes WHERE (email = ? OR telefone = ?) AND senha = ? AND ativo = 1")) {
                stmt.setString(1, c);
                stmt.setString(2, c);
                stmt.setString(3, s);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    clienteLogadoId = rs.getInt("id");
                    clienteLogadoNome = rs.getString("nome");

                    // Garante que o nome não fique null
                    if (clienteLogadoNome == null || clienteLogadoNome.trim().isEmpty()) {
                        clienteLogadoNome = "Cliente";
                    }

                    painelBoasVindas.setVisible(false);
                    formCard.setVisible(true);
                    txtCliente.setText(clienteLogadoNome);
                    lblStatusLogin.setText("✅  Logado como: " + clienteLogadoNome);

                    JOptionPane.showMessageDialog(this, "Login realizado com sucesso!");
                } else {
                    JOptionPane.showMessageDialog(this, "E-mail/Telefone ou senha incorretos!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }

    private void abrirRecuperarSenha() {
        JTextField contato = new JTextField(20);
        JPasswordField novaSenha = new JPasswordField(20);
        JPasswordField confirma = new JPasswordField(20);

        JTextField[] campos = {contato, novaSenha, confirma};
        for (JTextField tf : campos) {
            tf.setBackground(COLOR_BG);
            tf.setForeground(COLOR_TEXT);
            tf.setCaretColor(COLOR_ACCENT);
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(70, 70, 75)),
                    BorderFactory.createEmptyBorder(7, 10, 7, 10)));
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        }

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(COLOR_CARD);
        p.setBorder(new EmptyBorder(15, 20, 10, 20));

        p.add(criarLabelDialog("E-MAIL OU TELEFONE CADASTRADO"));
        p.add(Box.createVerticalStrut(3));
        p.add(contato);
        p.add(Box.createVerticalStrut(12));
        p.add(criarLabelDialog("NOVA SENHA"));
        p.add(Box.createVerticalStrut(3));
        p.add(novaSenha);
        p.add(Box.createVerticalStrut(12));
        p.add(criarLabelDialog("CONFIRMAR NOVA SENHA"));
        p.add(Box.createVerticalStrut(3));
        p.add(confirma);

        int op = JOptionPane.showConfirmDialog(this, p, "Recuperar Senha", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (op == JOptionPane.OK_OPTION) {
            String c = contato.getText().trim();
            String s = new String(novaSenha.getPassword());
            String conf = new String(confirma.getPassword());

            if (c.isEmpty() || s.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
                return;
            }
            if (!s.equals(conf)) {
                JOptionPane.showMessageDialog(this, "As senhas não coincidem!");
                return;
            }

            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE clientes SET senha = ? WHERE email = ? OR telefone = ?")) {
                stmt.setString(1, s);
                stmt.setString(2, c);
                stmt.setString(3, c);
                int rows = stmt.executeUpdate();

                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Senha alterada com sucesso!\nAgora faça login com a nova senha.");
                } else {
                    JOptionPane.showMessageDialog(this, "E-mail ou telefone não encontrado no sistema!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao recuperar senha:\n" + ex.getMessage());
            }
        }
    }

    private void fazerLogoutCliente() {
        clienteLogadoId = null;
        clienteLogadoNome = null;
        txtCliente.setText("");
        formCard.setVisible(false);
        painelBoasVindas.setVisible(true);
    }

    private JLabel criarLabelDialog(String texto) {
        JLabel l = new JLabel(texto);
        l.setForeground(COLOR_ACCENT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // =========================================================
    //                    AUXILIARES
    // =========================================================
    private void adicionarCampo(JPanel parent, String label, JComponent comp) {
        JLabel lbl = new JLabel(label);
        lbl.setForeground(COLOR_TEXT_MUTED);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        comp.setAlignmentX(Component.LEFT_ALIGNMENT);
        parent.add(lbl);
        parent.add(Box.createVerticalStrut(3));
        parent.add(comp);
        parent.add(Box.createVerticalStrut(9));
    }

    private JTextField criarTextField() {
        JTextField tf = new JTextField();
        tf.setBackground(COLOR_BG);
        tf.setForeground(COLOR_TEXT);
        tf.setCaretColor(COLOR_ACCENT);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 58, 66)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        return tf;
    }

    private JButton criarBotaoAnimado(String texto, Color bgBase, Color bgHover, Color fgText) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgBase);
        btn.setForeground(fgText);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bgHover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(bgBase); }
        });
        return btn;
    }

    private JLabel criarStatCard(String titulo, String valor, JPanel parent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 52, 58), 1),
                new EmptyBorder(4, 10, 4, 10)));
        JLabel t = new JLabel(titulo.toUpperCase());
        t.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        t.setForeground(COLOR_TEXT_MUTED);
        JLabel v = new JLabel(valor);
        v.setFont(new Font("Segoe UI", Font.BOLD, 14));
        v.setForeground(COLOR_ACCENT);
        card.add(t);
        card.add(v);
        parent.add(card);
        return v;
    }

    // Horários de 1 em 1 hora (09:00 até 21:00)
    private String[] gerarHorarios() {
        List<String> lista = new ArrayList<>();
        for (int h = 9; h <= 21; h++) {
            lista.add(String.format("%02d:00", h));
        }
        return lista.toArray(new String[0]);
    }

    private void carregarBarbeiros() {
        cbBarbeiros.removeAllItems();
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, nome FROM profissionais ORDER BY nome")) {
            while (rs.next()) {
                cbBarbeiros.addItem(new BarbeiroItem(rs.getInt("id"), rs.getString("nome")));
            }
        } catch (SQLException ignored) {}
    }

    
    
    
    private void agendarHorario() {
    if (clienteLogadoId == null) {
        JOptionPane.showMessageDialog(this, "Você precisa estar logado para agendar!");
        return;
    }

    BarbeiroItem barbeiro = (BarbeiroItem) cbBarbeiros.getSelectedItem();
    String dataDigitada = txtData.getText().trim();
    String hora = (String) cbHorarios.getSelectedItem();

    if (barbeiro == null || dataDigitada.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Preencha todos os campos!");
        return;
    }

    // Converte dd/MM/yyyy → yyyy-MM-dd
    String dataBanco;
    try {
        SimpleDateFormat formatoBR = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatoBanco = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date data = formatoBR.parse(dataDigitada);
        dataBanco = formatoBanco.format(data);
    } catch (ParseException e) {
        JOptionPane.showMessageDialog(this, "Data inválida! Use o formato dd/MM/aaaa\nExemplo: 08/09/2026");
        return;
    }

    try (Connection conn = Conexao.getConnection();
         PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO agendamentos (profissional_id, cliente_id, cliente_nome, data_agendamento, hora_agendamento, status) " +
                 "VALUES (?, ?, ?, ?, ?, 'AGENDADO')")) {
        
        stmt.setInt(1, barbeiro.getId());
        stmt.setInt(2, clienteLogadoId);
        stmt.setString(3, clienteLogadoNome);      // ← nome do cliente (obrigatório)
        stmt.setString(4, dataBanco);
        stmt.setString(5, hora + ":00");
        
        stmt.executeUpdate();
        JOptionPane.showMessageDialog(this, "Agendamento realizado com sucesso!");
        
    } catch (SQLException ex) {
        if (ex.getErrorCode() == 1062) {
            JOptionPane.showMessageDialog(this, "Este horário já está ocupado!", "Conflito", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }
}

    private void cancelarAgendamento() {
        int row = tabela.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um agendamento para cancelar.");
            return;
        }
        int id = (int) tableModel.getValueAt(tabela.convertRowIndexToModel(row), 0);
        int conf = JOptionPane.showConfirmDialog(this, "Cancelar agendamento #" + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (conf == JOptionPane.YES_OPTION) {
            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM agendamentos WHERE id = ?")) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
                atualizarTabela();
                atualizarMetricasAdmin();
                JOptionPane.showMessageDialog(this, "Agendamento cancelado!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }

    private void cadastrarNovoBarbeiro() {
        String nome = JOptionPane.showInputDialog(this, "Nome do novo Barbeiro:");
        if (nome != null && !nome.trim().isEmpty()) {
            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO profissionais (nome) VALUES (?)")) {
                stmt.setString(1, nome.trim());
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Barbeiro cadastrado!");
                carregarBarbeiros();
                atualizarMetricasAdmin();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }

    private void excluirBarbeiro(JComboBox<BarbeiroItem> combo) {
        BarbeiroItem item = (BarbeiroItem) combo.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Selecione um barbeiro no formulário.");
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this,
                "Excluir o barbeiro \"" + item + "\"?", "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (conf == JOptionPane.YES_OPTION) {
            try (Connection conn = Conexao.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM profissionais WHERE id = ?")) {
                stmt.setInt(1, item.getId());
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Barbeiro excluído!");
                carregarBarbeiros();
                combo.removeAllItems();
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT id, nome FROM profissionais ORDER BY nome")) {
                    while (rs.next()) combo.addItem(new BarbeiroItem(rs.getInt("id"), rs.getString("nome")));
                }
                atualizarMetricasAdmin();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }

    private void atualizarTabela() {
        tableModel.setRowCount(0);
        String sql = "SELECT a.id, p.nome AS barbeiro, c.nome AS cliente, a.data_agendamento, a.hora_agendamento " +
                     "FROM agendamentos a " +
                     "JOIN profissionais p ON a.profissional_id = p.id " +
                     "JOIN clientes c ON a.cliente_id = c.id " +
                     "ORDER BY a.data_agendamento DESC, a.hora_agendamento ASC";
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("barbeiro"), rs.getString("cliente"),
                        rs.getString("data_agendamento"), rs.getString("hora_agendamento")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar lista: " + ex.getMessage());
        }
    }

    private void atualizarMetricasAdmin() {
        try (Connection conn = Conexao.getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM agendamentos");
            if (rs1.next()) lblTotalAgendamentos.setText(rs1.getString(1));

            String hoje = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM agendamentos WHERE data_agendamento = '" + hoje + "'");
            if (rs2.next()) lblTotalHoje.setText(rs2.getString(1));

            ResultSet rs3 = stmt.executeQuery("SELECT COUNT(*) FROM profissionais");
            if (rs3.next()) lblTotalBarbeiros.setText(rs3.getString(1));
        } catch (SQLException ignored) {}
    }

    private static class BarbeiroItem {
        private final int id;
        private final String nome;
        public BarbeiroItem(int id, String nome) { this.id = id; this.nome = nome; }
        public int getId() { return id; }
        public String toString() { return nome; }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BarbaStudio1().setVisible(true));
    }
}