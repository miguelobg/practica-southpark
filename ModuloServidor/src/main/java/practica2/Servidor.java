package practica2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Servidor {
    private static final int PUERTO = 69;
    private static final int MAX_CLIENTES = 3; // Máximo de 3 clientes concurrentes
    private static final Semaphore semaforo = new Semaphore(1, true); // Permite acceso exclusivo a los archivos
    private static final ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTES);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {
            System.out.println("Servidor escuchando en el puerto " + PUERTO);

            while (true) {
                Socket cliente = serverSocket.accept();
                System.out.println("Nuevo cliente conectado: " + cliente.getInetAddress().getHostAddress());
                pool.execute(new ClienteConectado(cliente, semaforo));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}