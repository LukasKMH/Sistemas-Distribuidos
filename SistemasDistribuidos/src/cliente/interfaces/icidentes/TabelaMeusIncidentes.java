package cliente.interfaces.icidentes;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TabelaMeusIncidentes extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTable table;
	private DefaultTableModel tableModel;
	private JButton editarButton;
	private JButton excluirButton;

	public TabelaMeusIncidentes(Socket echoSocket, JsonArray lista_incidentes, JsonObject login) {
		// Configurações básicas do JFrame
		setTitle("Meus incidentes");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Cria um DefaultTableModel para a tabela
		tableModel = new DefaultTableModel();
		tableModel.addColumn("ID");
		tableModel.addColumn("Rodovia");
		tableModel.addColumn("Data");
		tableModel.addColumn("Km");
		tableModel.addColumn("Horário");

		// Cria a tabela e adiciona o modelo
		table = new JTable(tableModel);
		table.setFont(new Font("Arial", Font.PLAIN, 14));
		table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		table.setDefaultRenderer(Object.class, centerRenderer);
		getContentPane().setLayout(null);

		// Adiciona a tabela em um JScrollPane
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBounds(0, 0, 386, 156);
		getContentPane().add(scrollPane);

		// Define a largura preferencial das colunas
		TableColumn idColumn = table.getColumnModel().getColumn(0);
		TableColumn rodoviaColumn = table.getColumnModel().getColumn(1);
		TableColumn dataColumn = table.getColumnModel().getColumn(2);
		TableColumn kmColumn = table.getColumnModel().getColumn(3);
		TableColumn horarioColumn = table.getColumnModel().getColumn(4);

		idColumn.setPreferredWidth(50); // Define a largura preferencial da coluna ID
		rodoviaColumn.setPreferredWidth(100); // Define a largura preferencial da coluna Rodovia
		dataColumn.setPreferredWidth(120); // Define a largura preferencial da coluna Data
		kmColumn.setPreferredWidth(80); // Define a largura preferencial da coluna Km
		horarioColumn.setPreferredWidth(100); // Define a largura preferencial da coluna Horário

		// Cria o botão de edição
		editarButton = new JButton("Editar");
		editarButton.setFont(new Font("Arial", Font.PLAIN, 14));
		editarButton.setBounds(239, 167, 100, 30);
		editarButton.setPreferredSize(new Dimension(80, 30)); // Define o tamanho do botão "Editar"
		editarButton.setEnabled(false); // Desabilita o botão inicialmente
		editarButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Lógica para editar o incidente
				int row = table.getSelectedRow();
				if (row != -1) {
					// Obtém o ID do incidente selecionado
					String idIncidente = table.getValueAt(row, 0).toString();
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								login.addProperty("id_incidente", idIncidente);
								pegarDados(lista_incidentes, login);
								EditarIncidente frame = new EditarIncidente(echoSocket, login);
								frame.setVisible(true);
								frame.setLocationRelativeTo(null);
								dispose();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

				}
			}
		});
		getContentPane().add(editarButton); // Adiciona o botão à esquerda

		// Cria o botão de exclusão
		excluirButton = new JButton("Excluir");
		excluirButton.setFont(new Font("Arial", Font.PLAIN, 14));
		excluirButton.setBounds(53, 167, 100, 30);
		excluirButton.setPreferredSize(new Dimension(80, 30)); // Define o tamanho do botão "Excluir"
		excluirButton.setEnabled(false);
		excluirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Lógica para excluir o incidente
				int row = table.getSelectedRow();
				if (row != -1) {
					// Obtém o ID do incidente selecionado
					String idIncidente = table.getValueAt(row, 0).toString();
					EventQueue.invokeLater(new Runnable() {
						public void run() {
							try {
								login.addProperty("id_operacao", 7);
								login.addProperty("id_incidente", idIncidente);
								login.remove("codigo");
								excluirIncidente(echoSocket, login);
								// dispose();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

				}
			}
		});
		getContentPane().add(excluirButton); // Adiciona o botão à direita

		setSize(400, 251);
		setVisible(true);

		mostrarIncidentes(lista_incidentes);
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				// Verifica se há alguma linha selecionada na tabela
				if (table.getSelectedRow() != -1) {
					// Habilita o botão de edição
					editarButton.setEnabled(true);
					excluirButton.setEnabled(true);
				} else {
					// Desabilita o botão de edição
					editarButton.setEnabled(false);
					excluirButton.setEnabled(false);
				}
			}
		});
	}

	public void mostrarIncidentes(JsonArray lista_incidentes) {
		// Limpa o modelo da tabela
		tableModel.setRowCount(0);

		// Percorre os incidentes e adiciona ao modelo da tabela
		for (int i = 0; i < lista_incidentes.size(); i++) {
			JsonObject incidente = lista_incidentes.get(i).getAsJsonObject();

			String id = incidente.get("id_incidente").getAsString();
			String rodovia = incidente.get("rodovia").getAsString();

			// Formatar a data para o formato certo
			LocalDateTime dataResult = LocalDateTime.parse(incidente.get("data").getAsString(),
					DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
			String data = dataResult.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			String km = incidente.get("km").getAsString();
			String horario = dataResult.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

			Object[] rowData = { id, rodovia, data, km, horario };
			tableModel.addRow(rowData);
		}
	}

	public void pegarDados(JsonArray lista_incidentes, JsonObject login) {
		int id = Integer.parseInt(login.get("id_incidente").getAsString());

		for (int i = 0; i < lista_incidentes.size(); i++) {
			JsonObject incidente = lista_incidentes.get(i).getAsJsonObject();
			String incidenteId = incidente.get("id_incidente").getAsString();

			if (incidenteId.equals(Integer.toString(id))) {
				String rodovia = incidente.get("rodovia").getAsString();

				login.addProperty("id_incidente", incidenteId);
				login.addProperty("rodovia", rodovia);
				login.addProperty("data", incidente.get("data").getAsString());
				login.addProperty("km", incidente.get("km").getAsString());
				login.addProperty("tipo_incidente", incidente.get("tipo_incidente").getAsString());

			}
		}

	}

	public void excluirIncidente(Socket echoSocket, JsonObject login) {
	    int confirm = JOptionPane.showConfirmDialog(null, "Deseja excluir o incidente?");
	    if (confirm == JOptionPane.YES_OPTION) {
	        try {
	            PrintWriter saida = new PrintWriter(echoSocket.getOutputStream(), true);
	            BufferedReader entrada = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
	            saida.println(login);
	            System.out.println("ENVIADO: " + login);

	            Gson gson = new Gson();
	            JsonObject resposta_servidor = gson.fromJson(entrada.readLine(), JsonObject.class);
	            if (resposta_servidor != null)
	                System.out.println("\nRESPOSTA: " + resposta_servidor);
	            System.out.println("************************************************************************\n");

	            // Mensagem
	            if (Integer.parseInt(resposta_servidor.get("codigo").getAsString()) == 200) {
	                JOptionPane.showMessageDialog(null, "Incidente excluído!");

	                // Remove a linha correspondente ao incidente excluído do modelo da tabela
	                int row = table.getSelectedRow();
	                if (row != -1) {
	                    tableModel.removeRow(row);
	                }

	            } else {
	                JOptionPane.showMessageDialog(null, "Erro ao excluir incidente.", "Erro",
	                        JOptionPane.ERROR_MESSAGE);
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}




}
