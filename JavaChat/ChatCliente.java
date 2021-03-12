import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


public class ChatCliente {

    String direccionServidor;
    Scanner in;
    PrintWriter out;

    JFrame frame = new JFrame("Telegram 2");
    JTextField textField = new JTextField(30);
    JTextArea messageArea = new JTextArea(40, 30);


    public ChatCliente(String direccionServidor) {
        this.direccionServidor = direccionServidor;
        //El textfield por defecto no es editable, solo se vuelve editable una vez ha sido conectado con el servidor.
        textField.setEditable(false);
        messageArea.setEditable(false);

        frame.getContentPane().add(textField, BorderLayout.SOUTH);
        frame.getContentPane().add(new JScrollPane(messageArea), BorderLayout.CENTER);
        frame.pack();

        //Limpia el holder de texto escrito para el siguiente mensaje.
        textField.addActionListener(e -> {
            out.println(textField.getText());
            textField.setText("");
        });
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Introduce la IP del servidor.");
        Scanner sc = new Scanner(System.in);
        String IP = sc.nextLine();
        var client = new ChatCliente(IP);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }

    private String setNombre() {
        return JOptionPane.showInputDialog(frame, "Elige un nombre:", "Seleccion de nombre", JOptionPane.PLAIN_MESSAGE);
    }

    private void run() throws IOException {
        try {
            var socket = new Socket(direccionServidor, 59011);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            while (in.hasNextLine()) {
                var linea = in.nextLine();
                if (linea.startsWith("ENVIAR_NOMBRE")) {
                    out.println(setNombre());
                } else if (linea.startsWith("NOMBRE_ACEPTADO")) {
                    //Aqui hay que hacer el substring para cortar el mensaje del servidor. Si no saldría MENSAJE"nombreusuario".
                    this.frame.setTitle(linea.substring(15));
                    textField.setEditable(true);
                } else if (linea.startsWith("MENSAJE")) {
                    //Aqui hay que hacer el substring para cortar el mensaje del servidor. Si no saldría MENSAJE"nombreusuario".
                    messageArea.append(linea.substring(7) + "\n");
                }
            }
        } finally {
            frame.setVisible(false);



            frame.dispose();
        }
    }
}
