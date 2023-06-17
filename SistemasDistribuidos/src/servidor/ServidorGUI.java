package servidor;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import servidor.dao.BancoDados;
import servidor.dao.ClienteDao;

public class ServidorGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtPorta;
	private JButton btnMostrarUsuarios;
	private JButton btnIniciarServidor;
	private JTable table;
	private DefaultTableModel tableModel;
	Connection conexao = null;
	JsonArray listaClientes;
	ServerSocket socketServidor = null;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
				    ServidorGUI frame = new ServidorGUI();
				    frame.setVisible(true);
				} catch (Exception e) {
				    JOptionPane.showMessageDialog(null, "Erro ao iniciar o servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
				}

			}
		});
	}

	public ServidorGUI() throws IOException {
		setTitle("Servidor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		// Cria um DefaultTableModel para a tabela
		tableModel = new DefaultTableModel();
		tableModel.addColumn("ID");
		tableModel.addColumn("Nome");
		tableModel.addColumn("Email");

		// Cria a tabela e adiciona o modelo
		table = new JTable(tableModel);
		table.setFont(new Font("Arial", Font.PLAIN, 14));
		table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		table.setDefaultRenderer(Object.class, centerRenderer);

		// Adiciona a tabela em um JScrollPane
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(6, 38, 425, 220);
		contentPane.add(scrollPane);

		// Define a largura preferencial das colunas
		TableColumn idColumn = table.getColumnModel().getColumn(0);
		TableColumn nomeColumn = table.getColumnModel().getColumn(1);
		TableColumn emailColumn = table.getColumnModel().getColumn(2);

		idColumn.setPreferredWidth(50); // Define a largura preferencial da coluna ID
		nomeColumn.setPreferredWidth(145); // Define a largura preferencial da coluna Nome
		emailColumn.setPreferredWidth(240); // Define a largura preferencial da coluna Email

		JLabel lblPorta = new JLabel("Porta:");
		lblPorta.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPorta.setBounds(10, 9, 46, 17);
		contentPane.add(lblPorta);

		txtPorta = new JTextField();
		txtPorta.setBounds(55, 7, 86, 25);
		contentPane.add(txtPorta);
		txtPorta.setColumns(10);

		btnIniciarServidor = new JButton("Conectar");
		btnIniciarServidor.setFont(new Font("Arial", Font.PLAIN, 12));
		btnIniciarServidor.setBounds(162, 7, 90, 25);
		contentPane.add(btnIniciarServidor);
		btnIniciarServidor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					mostrarClientesLogados();
					iniciarServidor(Integer.parseInt(txtPorta.getText()));
				} catch (NumberFormatException | IOException | SQLException e1) {
					JOptionPane.showMessageDialog(null, "Erro ao inicar servidor.", "Erro", JOptionPane.ERROR_MESSAGE);
				}

			}
		});

		btnMostrarUsuarios = new JButton("Mostrar Usuários");
		btnMostrarUsuarios.setFont(new Font("Arial", Font.PLAIN, 12));
		btnMostrarUsuarios.setBounds(288, 7, 143, 25);
		contentPane.add(btnMostrarUsuarios);
		btnMostrarUsuarios.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mostrarClientesLogados();
			}
		});

	}

	public void iniciarServidor(int porta) throws IOException, SQLException {
		Thread serverThread = new Thread(() -> {
			try (ServerSocket socketServidor = new ServerSocket(porta)) {
				System.out.println("Socket de conexao criado.");
				System.out.println("Aguardando conexao...");
				while (true) {
					mostrarClientesLogados();
					new Servidor(socketServidor.accept());
				}
			} catch (IOException e) {
				System.err.println("Erro ao iniciar o servidor: " + e.getMessage());

			}
		});
		serverThread.start();
	}

	public void mostrarClientesLogados() {
		try {
			conexao = BancoDados.conectar();
			listaClientes = new ClienteDao(conexao).listarClientesLogados();
			tableModel.setRowCount(0);
			for (int i = 0; i < listaClientes.size(); i++) {
				JsonObject cliente = listaClientes.get(i).getAsJsonObject();
				String id = cliente.get("id_usuario").getAsString();
				String nome = cliente.get("nome").getAsString();
				String email = cliente.get("email").getAsString();
				Object[] rowData = { id, nome, email };
				tableModel.addRow(rowData);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Ocorreu um erro ao exibir os clientes.", "Erro ao Exibir Incidentes",
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
