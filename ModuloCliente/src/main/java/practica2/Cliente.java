package practica2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    private static final String HOST = "localhost";
    private static final int PUERTO = 69;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PUERTO);
             BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            String linea;
            while ((linea = entrada.readLine()) != null) {
                if (linea.equals("<FIN_MENU>"))
                    break;
                System.out.println(linea);
            }

            while (true) {
                System.out.print("Ingrese opción:");
                String opcion = scanner.nextLine();
                salida.println(opcion);

                // Si se elige "5", se lee el mensaje final y se sale
                if (opcion.trim().equals("5")) {
                    linea = entrada.readLine();
                    System.out.println(linea);
                    break;
                }

                // Para opciones que requieren información adicional
                if (opcion.trim().equals("2") || opcion.trim().equals("3") || opcion.trim().equals("4")) {
                    // Se espera el primer prompt, por ejemplo: "Ingrese el nombre del archivo:"
                    linea = entrada.readLine();
                    System.out.println(linea);
                    String respuesta = scanner.nextLine();
                    salida.println(respuesta);

                    if (opcion.trim().equals("3")) {
                        // Para buscar por personaje, se solicita el nombre del personaje
                        linea = entrada.readLine();
                        System.out.println(linea);
                        respuesta = scanner.nextLine();
                        salida.println(respuesta);
                    } else if (opcion.trim().equals("4")) {
                        // Para añadir frase, se solicitan dos datos: personaje y frase
                        linea = entrada.readLine();
                        System.out.println(linea);
                        respuesta = scanner.nextLine();
                        salida.println(respuesta);

                        linea = entrada.readLine();
                        System.out.println(linea);
                        respuesta = scanner.nextLine();
                        salida.println(respuesta);
                    }
                }

                // Leer la respuesta del servidor hasta el marcador <FIN_RESPUESTA>
                while ((linea = entrada.readLine()) != null) {
                    if (linea.equals("<FIN_RESPUESTA>"))
                        break;
                    System.out.println(linea);
                }

                // Leer el siguiente menú hasta el marcador <FIN_MENU>
                while ((linea = entrada.readLine()) != null) {
                    if (linea.equals("<FIN_MENU>"))
                        break;
                    System.out.println(linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
