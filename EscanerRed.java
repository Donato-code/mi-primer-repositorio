import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;

public class EscanerRed extends JFrame {

    private JTable table;
    private JTextField ipInicioField;
    private JTextField ipFinField;
    private JButton scanButton, clearButton, saveButton, netstatButton;
    private JProgressBar progressBar;
    private JTextArea netstatOutput;

    private DefaultTableModel tableModel;

    public EscanerRed() {
        setTitle("Escáner de Red - Proyecto Final");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        add(panel);

        // Panel de entrada
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Rango de IPs"));

        ipInicioField = new JTextField("192.168.0.1");
        ipFinField = new JTextField("192.168.0.10");

        inputPanel.add(new JLabel("IP inicio:"));
        inputPanel.add(ipInicioField);
        inputPanel.add(new JLabel("IP fin:"));
        inputPanel.add(ipFinField);

        panel.add(inputPanel, BorderLayout.NORTH);

        // Tabla de resultados
        tableModel = new DefaultTableModel(new String[]{"IP", "Hostname", "Estado", "Tiempo (ms)"}, 0);
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Resultados"));
        panel.add(scroll, BorderLayout.CENTER);

        // Panel inferior con botones y barra de progreso
        JPanel bottomPanel = new JPanel(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        scanButton = new JButton("Escanear");
        clearButton = new JButton("Limpiar");
        saveButton = new JButton("Guardar");
        netstatButton = new JButton("Netstat");

        buttonPanel.add(scanButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(netstatButton);

        bottomPanel.add(buttonPanel, BorderLayout.NORTH);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // Panel lateral para salida de netstat
        netstatOutput = new JTextArea();
        netstatOutput.setEditable(false);
        JScrollPane netstatScroll = new JScrollPane(netstatOutput);
        netstatScroll.setBorder(BorderFactory.createTitledBorder("Salida de Netstat"));
        netstatScroll.setPreferredSize(new Dimension(400, 0));
        panel.add(netstatScroll, BorderLayout.EAST);

        // Acciones de botones
        scanButton.addActionListener(e -> escanearRed());
        clearButton.addActionListener(e -> limpiarTabla());
        saveButton.addActionListener(e -> guardarResultados());
        netstatButton.addActionListener(e -> ejecutarNetstat());
    }

    private void escanearRed() {
        String ipInicio = ipInicioField.getText().trim();
        String ipFin = ipFinField.getText().trim();

        if (!esIpValida(ipInicio) || !esIpValida(ipFin)) {
            JOptionPane.showMessageDialog(this, "Formato de IP inválido");
            return;
        }

        tableModel.setRowCount(0);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                long ipInicioLong = ipToLong(InetAddress.getByName(ipInicio));
                long ipFinLong = ipToLong(InetAddress.getByName(ipFin));

                progressBar.setMinimum(0);
                progressBar.setMaximum((int) (ipFinLong - ipInicioLong + 1));
                int progreso = 0;

                for (long ip = ipInicioLong; ip <= ipFinLong; ip++) {
                    String ipStr = longToIp(ip);
                    String estado;
                    String hostName = "";

                    try {
                        InetAddress inet = InetAddress.getByName(ipStr);
                        long startTime = System.currentTimeMillis();
                        boolean reachable = inet.isReachable(500);
                        long endTime = System.currentTimeMillis();

                        if (reachable) {
                            estado = "ACTIVA ✅";
                            hostName = inet.getHostName();
                            tableModel.addRow(new Object[]{ipStr, hostName, estado, endTime - startTime});
                        } else {
                            estado = "No responde ❌";
                            tableModel.addRow(new Object[]{ipStr, "-", estado, "-"});
                        }

                    } catch (IOException ex) {
                        estado = "Error ❌";
                        tableModel.addRow(new Object[]{ipStr, "-", estado, "-"});
                    }

                    progreso++;
                    progressBar.setValue(progreso);
                }

                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(null, "Escaneo finalizado");
            }
        };

        worker.execute();
    }

    private void limpiarTabla() {
        tableModel.setRowCount(0);
        progressBar.setValue(0);
        netstatOutput.setText("");
    }

    private void guardarResultados() {
        try (FileWriter writer = new FileWriter("resultados.txt")) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                writer.write(
                        tableModel.getValueAt(i, 0) + "\t" +
                        tableModel.getValueAt(i, 1) + "\t" +
                        tableModel.getValueAt(i, 2) + "\t" +
                        tableModel.getValueAt(i, 3) + "\n"
                );
            }
            JOptionPane.showMessageDialog(this, "Resultados guardados en resultados.txt");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al guardar: " + e.getMessage());
        }
    }

    private void ejecutarNetstat() {
        // Aquí usamos netstat con 3 modificadores: -a, -n, -o
        String[] comandos = {"netstat -a", "netstat -n", "netstat -o"};
        netstatOutput.setText("");

        for (String comando : comandos) {
            netstatOutput.append(">> " + comando + "\n");
            try {
                Process proceso = Runtime.getRuntime().exec(comando);
                BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
                String linea;
                while ((linea = lector.readLine()) != null) {
                    netstatOutput.append(linea + "\n");
                }
                lector.close();
                netstatOutput.append("\n");
            } catch (IOException e) {
                netstatOutput.append("Error ejecutando " + comando + ": " + e.getMessage() + "\n\n");
            }
        }
    }

    private boolean esIpValida(String ip) {
        String[] octetos = ip.split("\\.");
        if (octetos.length != 4) return false;
        try {
            for (String o : octetos) {
                int n = Integer.parseInt(o);
                if (n < 0 || n > 255) return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private long ipToLong(InetAddress ip) {
        byte[] octetos = ip.getAddress();
        long resultado = 0;
        for (byte octeto : octetos) {
            resultado = resultado << 8 | (octeto & 0xFF);
        }
        return resultado;
    }

    private String longToIp(long ip) {
        return String.format("%d.%d.%d.%d",
                (ip >> 24) & 0xFF,
                (ip >> 16) & 0xFF,
                (ip >> 8) & 0xFF,
                ip & 0xFF);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EscanerRed().setVisible(true));
    }
}
