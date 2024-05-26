package chatdegrupo;
import java.util.ArrayList;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.IOException;

/**
 *
 * @author pedro e bárbara
 */
public class ClientHandler implements Runnable {
    
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;  // este é o socket que será passado pela classe Servidor
    private BufferedReader bufferedReader;  // lê as mensagens enviadas pelo cliente
    private BufferedWriter bufferedWriter;  // envia as mensagens para o cliente
    private String clientUsername;  // nome do usuário
    
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;   // recebe o socket da classe servidor e o torna local
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // o que o usuário está enviando fica guardado neste buffer
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));   // o que o usuário está recebendo fica guardado neste buffer
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);
            mensagemDoServidor("SERVIDOR: " + clientUsername + " conectou-se ao chat.");
        } catch (IOException e) {
            fecharTudo(socket, bufferedReader, bufferedWriter);
        }
    }
    
    @Override
    public void run(){
        String mensagemDoCliente;
        
        while (socket.isConnected()) {
            try {
                mensagemDoCliente = bufferedReader.readLine();
                mensagemDoServidor(mensagemDoCliente);
            } catch (IOException e) {
                fecharTudo(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
    
    public void mensagemDoServidor(String mensagemParaEnviar) {
        for (ClientHandler clientHandler : clientHandlers) {    // percorre todos clientHandlers
            try {
                if (!clientHandler.clientUsername.equals(clientUsername)) { // a mensagem dizendo que o usuário se conectou ao chat deve aparecer para todos menos para o usuário
                    clientHandler.bufferedWriter.write(mensagemParaEnviar);
                    clientHandler.bufferedWriter.newLine();
                    // para o buffer enviar uma mensagem ele precisa estar cheio, como a mensagem provavelmente nao será
                    // suficiente para enchê-lo, nós fazemos um flush manualmente
                    clientHandler.bufferedWriter.flush();
                } 
            } catch (IOException e) {
                fecharTudo(socket, bufferedReader, bufferedWriter);
            }
        }
    }
    
    public void removerClientHandler() {
        clientHandlers.remove(this);
        mensagemDoServidor("SERVIDOR: " + clientUsername + " saiu do chat.");
    }
    
    public void fecharTudo(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removerClientHandler();
        try {
            // para não recebermos um null pointer exception quando executamos removerClientHandler, fazemos estas proximas checagens
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
}