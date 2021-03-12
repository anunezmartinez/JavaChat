import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;

public class ChatServidor {

    //HashSet para evitar duplicados en los nombres.
    private static final Set<String> listaNombres = new HashSet<>();

    //Printwriters para cada cliente para que pueda ser usado para broadcast.
    private static final Set<PrintWriter> printWriterHashSet = new HashSet<>();

    public static void main(String[] args) throws Exception {

        System.out.println("Servidor corriendo.");
        var pool = Executors.newFixedThreadPool(500);
        try (var listener = new ServerSocket(59011)) {
            while (true) pool.execute(new Handler(listener.accept()));
        }
    }

    private static class Handler implements Runnable {
        private String nombreUsuario;
        private final Socket socket;
        private Scanner in;
        private PrintWriter out;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new Scanner(socket.getInputStream());
                out = new PrintWriter(socket.getOutputStream(), true);

                //Solicitar repetidamente el nombre del usuario.
                while (true) {
                    out.println("ENVIAR_NOMBRE");
                    nombreUsuario = in.nextLine();
                    if (nombreUsuario == null) {
                        return;
                    }
                    //Comprobamos que el nombre no esta repetido y lo anhadimos a hashset.
                    synchronized (listaNombres) {
                        if (!nombreUsuario.isBlank() && !listaNombres.contains(nombreUsuario)) {
                            listaNombres.add(nombreUsuario);
                            break;
                        }
                    }
                }

                //Una vez tenemos el nombre del cliente aceptado, tenemos que darle su propio
                //PrintWriter pero antes hay que notificar a todos los usuarios de que alguien
                //se ha unido.
                out.println("NOMBRE_ACEPTADO" + nombreUsuario);
                printWriterHashSet.forEach(writer -> writer.println("MENSAJE" + nombreUsuario + " se ha unido a la sala."));
                printWriterHashSet.add(out);

                //Aceptar mensajes del cliente y hace el broadcast del mensaje.
                while (true) {
                    String input = in.nextLine();
                    //Si el mensaje empieza con /leave se desconecta al cliente.
                    if (input.toLowerCase().startsWith("/leave")) return;
                    printWriterHashSet.forEach(writer -> writer.println("MENSAJE" + nombreUsuario + ">> " + input));
                }
            } catch (Exception e) {
                System.out.println();
            } finally {

                if (out != null) {
                    printWriterHashSet.remove(out);
                }
                if (nombreUsuario != null) {
                    System.out.println(nombreUsuario + " ha marchado de la sala.");
                    listaNombres.remove(nombreUsuario);
                    printWriterHashSet.forEach(writer -> writer.println("MENSAJE" + nombreUsuario + " ha marchado."));
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("El servidor no se ha podido cerrar correctamente.");
                }
            }
        }
    }
}
