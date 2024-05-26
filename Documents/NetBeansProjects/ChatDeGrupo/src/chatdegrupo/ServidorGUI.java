package chatdegrupo;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;

public class ServidorGUI extends JFrame {

    private ServerSocket serverSocket;
    private JTextArea textArea;
    private JButton startButton;
    private boolean isRunning;

    public ServidorGUI() {
        setTitle("Servidor de Chat de Grupo");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        startButton = new JButton("Iniciar Servidor");
        buttonPanel.add(startButton);
        add(buttonPanel, BorderLayout.SOUTH);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarServidor();
            }
        });
        setVisible(true);
    }

    private void iniciarServidor() {
        try {
            serverSocket = new ServerSocket(1337);
            textArea.append("Servidor iniciado na porta 1337\n");
            isRunning = true;
            startButton.setEnabled(false);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRunning) {
                        try {
                            Socket socket = serverSocket.accept();
                            textArea.append("Um usuário se conectou ao chat!\n");
                            ClientHandler clientHandler = new ClientHandler(socket);
                            Thread thread = new Thread(clientHandler);
                            thread.start();
                        } catch (IOException e) {
                            if (isRunning) {
                                textArea.append("Erro ao aceitar conexão: " + e.getMessage() + "\n");
                            }
                        }
                    }
                }
            }).start();
        } catch (IOException e) {
            textArea.append("Erro ao iniciar o servidor: " + e.getMessage() + "\n");
        }
    }

    public static void main(String[] args) {
        new ServidorGUI();
    }
}