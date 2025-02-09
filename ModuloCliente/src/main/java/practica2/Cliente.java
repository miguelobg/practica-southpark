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
        System.out.println("Conectando al servidor...");
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

                if (opcion.trim().equals("5")) {
                    linea = entrada.readLine();
                    System.out.println(linea);
                    break;
                }

                if (opcion.trim().equals("2") || opcion.trim().equals("3") || opcion.trim().equals("4")) {
                    while ((linea = entrada.readLine()) != null) {
                        if (linea.equals("<FIN_LISTA_ARCHIVOS>"))
                            break;
                        System.out.println(linea);
                    }

                    System.out.print("Ingrese el número del archivo que desea leer:");
                    String respuesta = scanner.nextLine();
                    salida.println(respuesta);

                    if (opcion.trim().equals("3")) {
                        linea = entrada.readLine();
                        System.out.println(linea);
                        respuesta = scanner.nextLine();
                        salida.println(respuesta);
                    } else if (opcion.trim().equals("4")) {
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

                while ((linea = entrada.readLine()) != null) {
                    if (linea.equals("<FIN_RESPUESTA>"))
                        break;
                    System.out.println(linea);
                }

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