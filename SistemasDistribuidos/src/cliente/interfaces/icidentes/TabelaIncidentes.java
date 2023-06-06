package cliente.interfaces.icidentes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TabelaIncidentes extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTable table;
    private DefaultTableModel tableModel;

    public TabelaIncidentes(JsonArray lista_incidentes) {
        // Configurações básicas do JFrame
        setTitle("Incidentes");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Cria um DefaultTableModel para a tabela
        tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Rodovia");
        tableModel.addColumn("Data");
        tableModel.addColumn("Km");
        tableModel.addColumn("Horário");
        tableModel.addColumn("Tipo");

        // Cria a tabela e adiciona o modelo
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        // Adiciona a tabela em um JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        getContentPane().add(scrollPane);

        // Define a largura preferencial das colunas
        TableColumn idColumn = table.getColumnModel().getColumn(0);
        TableColumn rodoviaColumn = table.getColumnModel().getColumn(1);
        TableColumn dataColumn = table.getColumnModel().getColumn(2);
        TableColumn kmColumn = table.getColumnModel().getColumn(3);
        TableColumn horarioColumn = table.getColumnModel().getColumn(4);
        TableColumn tipoColumn = table.getColumnModel().getColumn(5);

        idColumn.setPreferredWidth(50); // Define a largura preferencial da coluna ID
        rodoviaColumn.setPreferredWidth(100); // Define a largura preferencial da coluna Rodovia
        dataColumn.setPreferredWidth(120); // Define a largura preferencial da coluna Data
        kmColumn.setPreferredWidth(80); // Define a largura preferencial da coluna Km
        horarioColumn.setPreferredWidth(100); // Define a largura preferencial da coluna Horário
        tipoColumn.setPreferredWidth(50); // Define a largura preferencial da coluna Tipo

        setSize(460, 300);
        setVisible(true);

        try {
            mostrarIncidentes(lista_incidentes);
        } catch (Exception e) {
            e.printStackTrace();
            exibirErro("Erro ao exibir incidentes. Verifique os dados fornecidos.");
        }
    }

    public void mostrarIncidentes(JsonArray lista_incidentes) {
        // Limpa o modelo da tabela
        tableModel.setRowCount(0);
        for (int i = 0; i < lista_incidentes.size(); i++) {
            JsonObject incidente = lista_incidentes.get(i).getAsJsonObject();

            try {
                String id = incidente.get("id_incidente").getAsString();
                String rodovia = incidente.get("rodovia").getAsString();

                // Formatar a data para o formato certo
                LocalDateTime dataResult = LocalDateTime.parse(incidente.get("data").getAsString(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String data = dataResult.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String km = incidente.get("km").getAsString();
                String horario = dataResult.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                String tipo = incidente.get("tipo_incidente").getAsString();

                Object[] rowData = { id, rodovia, data, km, horario, tipo };
                tableModel.addRow(rowData);
            } catch (Exception e) {
                e.printStackTrace();
                exibirErro("Erro ao exibir incidente na posição " + i + ". Verifique os dados fornecidos.");
            }
        }
    }

    private void exibirErro(String mensagem) {
        // Exibe uma mensagem de erro em um JOptionPane, ou qualquer outra forma de exibição de erro desejada
        System.err.println("Erro: " + mensagem);
    }
}
