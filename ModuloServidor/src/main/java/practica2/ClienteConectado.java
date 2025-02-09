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
                salida.println("\nBienvenido al servidor. Elija una opción:");
                salida.println("1. Listar archivos");
                salida.println("2. Leer archivo");
                salida.println("3. Buscar por personaje");
                salida.println("4. Añadir frase");
                salida.println("5. Salir");
                salida.println("<FIN_MENU>"); // uso marcadores para que sean detectados en los bucles de lectura

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
            salida.println("Archivos disponibles:");
            for (int i = 0; i < archivos.length; i++) {
                salida.println((i + 1) + ". " + archivos[i].getName());
            }
        } else {
            salida.println("No se encontraron archivos.");
        }
    }

    private void leerArchivo() throws IOException {
       File carpeta = new File(RUTA_ARCHIVOS);
       File[] archivos = carpeta.listFiles();
       if (archivos == null || archivos.length == 0) {
           salida.println("No se encontraron archivos.");
           return;
       }

       salida.println("\nArchivos disponibles:");
       for (int i = 0; i < archivos.length; i++) {
           salida.println((i + 1) + ". " + archivos[i].getName());
       }

       salida.println("<FIN_LISTA_ARCHIVOS>");
       String opcionArchivo = entrada.readLine();
       if (opcionArchivo == null) {
           salida.println("No se recibió una opción de archivo.");
           return;
       }

       int indiceArchivo;
       try {
           indiceArchivo = Integer.parseInt(opcionArchivo) - 1;
       } catch (NumberFormatException e) {
           salida.println("Opción no válida.");
           return;
       }

       if (indiceArchivo < 0 || indiceArchivo >= archivos.length) {
           salida.println("Opción fuera de rango.");
           return;
       }

       File archivo = archivos[indiceArchivo];
       try {
           semaforo.acquire();
           salida.println("\n" + archivo.getName() + ":");
           BufferedReader lector = new BufferedReader(new FileReader(archivo));
           String linea;
           while ((linea = lector.readLine()) != null) {
               salida.println(linea);
           }
           lector.close();
           salida.println("Fin del archivo. Espere por favor...");
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
            boolean imprimir = false;
            while ((linea = lector.readLine()) != null) {
                if (linea.equals(personaje)) {
                    imprimir = true;
                    continue;
                }
                if (imprimir) {
                    if (linea.matches("^[A-Za-z ]+$")) {
                        imprimir = false;
                    } else {
                        salida.println(linea);
                    }
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
                if (linea.equals(personaje)) {
                    existePersonaje = true;
                    break;
                }
            }
            lector.close();

            if (existePersonaje) {
                PrintWriter escritor = new PrintWriter(new FileWriter(archivo, true));
                escritor.println(personaje);
                escritor.println(frase);
                escritor.close();
                salida.println("Frase añadida correctamente. Espere por favor...");
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
