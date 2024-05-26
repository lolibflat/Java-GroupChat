package chatdegrupo;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JFrame;

/**
 *
 * @author pedro e bárbara
 */

// inicialmente criamos nossa classe do Servidor
// vamos usar termos como usuários e clientes como sinônimos dentro do código
public class Servidor {
    // declaração do objeto que será o listener para novas conexões/clientes, para criar um socket e realizar a comunicação
    private ServerSocket serverSocket;

    public Servidor(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    
    
    public void inciarServidor() {
        try {
            // para que o servidor continue rodando até o socket ser fechado criamos um while loop
            while (!serverSocket.isClosed()) {
                
                /* o método accept() fará o programa esperar até que um cliente se conecte para retornar um objeto socket pque pode ser usado
                para comunicar com ele */
                Socket socket = serverSocket.accept();
                System.out.println("Um usuário se conectou ao chat!");
                
                /* cada objeto da classe ClientHandler será responsável por se comunicar com o cliente
                Esta classe também implementará a interface runnable() para cada thread ser responsável por um cliente individualmente
                Em seguida criamos a thread que será responsável pelo handler
                */
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            
        }
    }
    
    // se ocorrer algum erro, iremos fechar o socket com o seguinte método
    public void fecharServerSocket() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws IOException {
        new ServidorGUI();
    }
}