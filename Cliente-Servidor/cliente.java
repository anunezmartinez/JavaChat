import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class cliente {

    public static void main(String[] args) {
        try {
            
            Scanner sc = new Scanner(System.in);
            
            System.out.println("Creando socket cliente.");
            Socket clienteSocket = new Socket();
            System.out.println("Estableciendo la conexion.");

            InetSocketAddress addr = new InetSocketAddress("localhost", 6666);
            clienteSocket.connect(addr);

            InputStream is = clienteSocket.getInputStream();
            OutputStream os = clienteSocket.getOutputStream();
            
            byte[] contestacion1=new byte[25];
            byte[] contestacion2=new byte[25];
            byte[] contestacion3=new byte[25];

            System.out.println("Enviando mensaje.");
            System.out.println("Introduce mensaje 1.");
            String mensaje = sc.nextLine();
            os.write(mensaje.getBytes());
          
            is.read(contestacion1);
            System.out.println("Contestacion1 recibida: "+new String(contestacion1));
            
            System.out.println("Introduce mensaje 2.");
            String mensaje2 = sc.nextLine();
            os.write(mensaje2.getBytes());
            
            is.read(contestacion2);
            System.out.println("Contestacion 2 recibida: "+new String(contestacion2));
            
            System.out.println("Introduce mensaje 1.");
            String mensaje3 = sc.nextLine();
            os.write(mensaje3.getBytes());
            
            is.read(contestacion3);
            System.out.println("Contestacion3 recibida: "+new String(contestacion3));

            System.out.println("Mensaje enviado.");

            System.out.println("Cerrando el socket cliente.");

            clienteSocket.close();

            System.out.println("Terminado.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}