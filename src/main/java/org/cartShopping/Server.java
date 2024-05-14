import org.cartShopping.Product;

import java.io.*;
import java.net.*;
import java.util.*;


public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket s = new ServerSocket(6050);
            System.out.println("Esperando cliente...");

            //Creación de productos
            List<Product> Products = new ArrayList<>();
            Products.add(new Product("Apple iPhone 13 Pro (1 TB) - Azul Sierra", "Pantalla Super Retina XDR de 6.1 pulgadas con ProMotion que brinda una respuesta más rápida y fluida. Modo Cine con baja profundidad de campo y cambios de enfoque automáticos en tus videos.", 25899.30, 2, "./img/iphone.jpg"));
            Products.add(new Product("Apple iPhone 15 (128 GB) - Azul", "El iPhone 15 tiene un robusto vidrio con infusión de color en un diseño de aluminio. Es resistente a las salpicaduras, al agua y al polvo, y viene con frente de Ceramic Shield, más duro que cualquier vidrio de smartphone. Además, la pantalla Super Retina XDR de 6.1 pulgadas es hasta dos veces más brillante bajo el sol en comparación con el iPhone 14.", 17909.0, 4, "./img/iphone15.jpeg"));
            Products.add(new Product("Pulsar Gaming Gears", "Peso ligero inalámbrico, 55 g, sin retraso 2.4 GHz inalámbrico o con cable", 1615, 3, "./img/pulsar.jpeg"));
            Products.add(new Product("Apple 2020 Laptop MacBook Air", "Batería para todo el día – Hasta 18 horas de batería según el uso, para hacer mucho más.", 11999, 1, "./img/mac.jpeg"));
            Products.add(new Product("Razer Viper Ultimate", "Fabricado para profesionales de los esports y jugadores de competición, el Razer Viper es el favorito por su rendimiento sin complicaciones y su diseño simétrico y ligero.", 1933.18, 4, "./img/razer.jpeg"));

            for (; ; ) {
                Socket cl = s.accept();
                System.out.println("Conexión establecida desde " + cl.getInetAddress() + ":" + cl.getPort());

                // Archivo donde se guardarán los productos
                String catalogo = "productos.txt";

                // Guardar el producto en el archivo
                ObjectOutputStream ser = new ObjectOutputStream(new FileOutputStream(catalogo));
                ser.writeObject(Products);
                ser.close();

                // Envío del archivo al servidor
                FileInputStream fileIn = new FileInputStream(catalogo);
                OutputStream dos = cl.getOutputStream();
                byte[] b = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileIn.read(b)) != -1) {
                    dos.write(b, 0, bytesRead);
                }
                System.out.println("\nCatálogo enviado");

                // Recibir el catálogo actualizado del cliente
                ObjectInputStream des = new ObjectInputStream(cl.getInputStream());
                List<Product> updatedCatalog = (List<Product>) des.readObject();
                des.close();

                // Actualizar el catálogo del servidor con los datos recibidos
                Products.clear();
                Products.addAll(updatedCatalog);
                System.out.println("Catálogo actualizado\n");

                fileIn.close();
                dos.close();
                cl.close();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}