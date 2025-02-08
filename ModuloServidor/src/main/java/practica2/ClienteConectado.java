package practica2;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class ClienteConectado implements Runnable {
    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
    private static final String RUTA_ARCHIVOS = "archivos/"; // Carpeta donde están los archivos
    private Semaphore semaforo;

    public ClienteConectado(Socket socket, Semaphore semaforo) {
        this.socket = socket;
        this.semaforo = semaforo;
    }

    @Override
    public void run() {
        try {
            salida = new PrintWriter(socket.getOutputStream(), true);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String opcion;
            while (true) {
                // Enviar el menú
                salida.println("\nBienvenido al servidor. Elija una opción:");
                salida.println("1. Listar archivos");
                salida.println("2. Leer archivo");
                salida.println("3. Buscar por personaje");
                salida.println("4. Añadir frase");
                salida.println("5. Salir");
                // Marcador para indicar fin de menú
                salida.println("<FIN_MENU>");

                // Leer la opción del cliente
                opcion = entrada.readLine();
                if (opcion == null) break;
                opcion = opcion.trim();
                if (opcion.equals("5")) {
                    salida.println("Desconectando...");
                    break;
                }
                switch (opcion) {
                    case "1":
                        listarArchivos();
                        break;
                    case "2":
                        leerArchivo();
                        break;
                    case "3":
                        buscarFrases();
                        break;
                    case "4":
                        añadirFrase();
                        break;
                    default:
                        salida.println("Opción no válida.");
                        break;
                }
                // Indicar el fin de la respuesta de la operación actual
                salida.println("<FIN_RESPUESTA>");
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void listarArchivos() {
        File carpeta = new File(RUTA_ARCHIVOS);
        File[] archivos = carpeta.listFiles();
        if (archivos != null && archivos.length > 0) {
            for (File archivo : archivos) {
                salida.println( archivo.getName());
            }
        } else {
            salida.println("No se encontraron archivos.");
        }
    }

    private void leerArchivo() throws IOException {
        salida.println("Ingrese el nombre del archivo:");
        String nombreArchivo = entrada.readLine();
        if (nombreArchivo == null) {
            salida.println("No se recibió nombre de archivo.");
            return;
        }
        File archivo = new File(RUTA_ARCHIVOS + nombreArchivo);
        if (!archivo.exists()) {
            salida.println("El archivo no existe.");
            return;
        }
        try {
            semaforo.acquire(); // Acceso exclusivo
            BufferedReader lector = new BufferedReader(new FileReader(archivo));
            String linea;
            while ((linea = lector.readLine()) != null) {
                salida.println(linea);
            }
            lector.close();
            Thread.sleep(3000); // Retardo artificial
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforo.release();
        }
    }

    private void buscarFrases() throws IOException {
        salida.println("Ingrese el nombre del archivo:");
        String nombreArchivo = entrada.readLine();
        salida.println("Ingrese el personaje:");
        String personaje = entrada.readLine();

        File archivo = new File(RUTA_ARCHIVOS + nombreArchivo);
        if (!archivo.exists()) {
            salida.println("El archivo no existe.");
            return;
        }
        try {
            semaforo.acquire();
            BufferedReader lector = new BufferedReader(new FileReader(archivo));
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.startsWith(personaje)) {
                    salida.println(linea);
                }
            }
            lector.close();
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforo.release();
        }
    }

    private void añadirFrase() throws IOException {
        salida.println("Ingrese el nombre del archivo:");
        String nombreArchivo = entrada.readLine();
        salida.println("Ingrese el personaje:");
        String personaje = entrada.readLine();
        salida.println("Ingrese la frase:");
        String frase = entrada.readLine();

        File archivo = new File(RUTA_ARCHIVOS + nombreArchivo);
        if (!archivo.exists()) {
            salida.println("El archivo no existe.");
            return;
        }
        try {
            semaforo.acquire();
            BufferedReader lector = new BufferedReader(new FileReader(archivo));
            boolean existePersonaje = false;
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.startsWith(personaje)) {
                    existePersonaje = true;
                    break;
                }
            }
            lector.close();

            if (existePersonaje) {
                PrintWriter escritor = new PrintWriter(new FileWriter(archivo, true));
                escritor.println(personaje + ": " + frase);
                escritor.close();
                salida.println("Frase añadida correctamente.");
            } else {
                salida.println("El personaje no existe en el archivo.");
            }
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaforo.release();
        }
    }
}
