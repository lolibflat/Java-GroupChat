package chatdegrupo;
import java.awt.BorderLayout;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import javax.swing.*;

public class ClienteGUI extends JFrame {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private JTextPane textPane;
    private JTextField textField;
    private JPanel messagePanel;
    private JScrollPane scrollPane;

    public ClienteGUI(Socket socket, String username) {
        this.socket = socket;
        this.username = username;

        try {
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            enviarUsername();  // Envia o nome do usuário assim que o cliente é criado
        } catch (IOException e) {
            fecharTudo(socket, bufferedReader, bufferedWriter);
        }

        setTitle("Chat de Grupo - " + username);
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        scrollPane = new JScrollPane(messagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        textField = new JTextField();
        textField.setPreferredSize(new Dimension(0, 30));
        add(textField, BorderLayout.SOUTH);

        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enviarMensagem();
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                esperarMensagem();
            }
        }).start();
    }

    private void enviarUsername() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            fecharTudo(socket, bufferedReader, bufferedWriter);
        }
    }

    public void enviarMensagem() {
        String mensagemParaEnviar = textField.getText();
        if (!mensagemParaEnviar.isEmpty()) {
            try {
                bufferedWriter.write(username + ": " + mensagemParaEnviar);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                mostrarMensagemEnviada(mensagemParaEnviar);
                textField.setText("");
            } catch (IOException e) {
                fecharTudo(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void esperarMensagem() {
        String mensagemDoGrupo;
        while (socket.isConnected()) {
            try {
                mensagemDoGrupo = bufferedReader.readLine();
                mostrarMensagemRecebida(mensagemDoGrupo);
            } catch (IOException e) {
                fecharTudo(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void mostrarMensagemEnviada(String mensagem) {
        addMessageToPanel("<html><div style='text-align: right; background-color: #DFF2BF; border-radius: 10px; padding: 10px; margin: 5px;'>" + username + ": " + mensagem + "</div></html>", true);
    }

    public void mostrarMensagemRecebida(String mensagem) {
        addMessageToPanel("<html><div style='text-align: left; background-color: #BDE5F8; border-radius: 10px; padding: 10px; margin: 5px;'>" + mensagem + "</div></html>", false);
    }

    private void addMessageToPanel(String message, boolean isUserMessage) {
        JLabel messageLabel = new JLabel(message);
        if (isUserMessage) {
            messageLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            messageLabel.setHorizontalAlignment(SwingConstants.LEFT);
        }
        messagePanel.add(messageLabel);
        messagePanel.revalidate();
        messagePanel.repaint();
    }

    public void fecharTudo(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        String username = JOptionPane.showInputDialog("Digite seu nome de usuário:");
        Socket socket = new Socket("localhost", 1337);
        ClienteGUI clienteGUI = new ClienteGUI(socket, username);
        clienteGUI.setVisible(true);
    }
}