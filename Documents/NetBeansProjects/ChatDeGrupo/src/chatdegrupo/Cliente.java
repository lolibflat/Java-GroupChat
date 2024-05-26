package chatdegrupo;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 *
 * @author pedro e bárbara
 */
public class Cliente {
    
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    
    public Cliente(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e) {
            fecharTudo(socket, bufferedReader, bufferedWriter);
        }
    }
    
    public void enviarMensagem() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            
            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String mensagemParaEnviar = scanner.nextLine();
                bufferedWriter.write(username + ": " + mensagemParaEnviar);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            fecharTudo(socket, bufferedReader, bufferedWriter);
        }
    }
    
    // cada cliente tem uma thread que espera receber uma mensagem para escrever na tela
    public void esperarMensagem() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String mensagemDoGrupo;
                
                while (socket.isConnected()) {
                    try {
                        mensagemDoGrupo = bufferedReader.readLine();
                        System.out.println(mensagemDoGrupo);
                    } catch (IOException e) {
                        fecharTudo(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite seu nome de usuário para entrar no chat de grupo: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1337);
        Cliente cliente = new Cliente(socket, username);
        cliente.esperarMensagem();
        cliente.enviarMensagem();
    }
}