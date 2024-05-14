package org.cartShopping;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.FileNotFoundException;


public class Client {
    public static void main(String args[]) {
        List<Product> carrito = new ArrayList<>(); // Lista para almacenar los productos seleccionados

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Escriba la dirección del servidor: ");
            String host = br.readLine();
            System.out.println("Escriba el puerto: ");
            int pto = Integer.parseInt(br.readLine());

            Socket cl = new Socket(host, pto);

            // Deserializar los productos
            ObjectInputStream des = new ObjectInputStream(cl.getInputStream());
            List<Product> productsDes = (List<Product>) des.readObject();

            JFrame frame = new JFrame("Catálogo de productos");
            frame.setLayout(new GridLayout(productsDes.size(), 1));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 800);

            for (Product product : productsDes) {
                JPanel panel = new JPanel();
                panel.setBorder(new EmptyBorder(10, 10, 10, 10));
                panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

                JLabel nameLabel = new JLabel("Nombre: " + product.getName());
                JLabel descriptionLabel = new JLabel("Descripción: " + product.getDescription());
                JLabel priceLabel = new JLabel("Precio: $" + product.getPrice());
                JLabel quantityLabel = new JLabel("Cantidad disponible: " + product.getQuantity()); // Muestra la cantidad disponible
                JLabel imageLabel = new JLabel();
                try {
                    BufferedImage img = ImageIO.read(new File(product.getImageRoute()));
                    ImageIcon icon = new ImageIcon(img);
                    imageLabel.setIcon(icon);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                panel.add(nameLabel);
                panel.add(descriptionLabel);
                panel.add(priceLabel);
                panel.add(quantityLabel);
                panel.add(imageLabel);

                // Agrega un listener de clic al panel para seleccionar el producto
                panel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Verifica si hay suficientes productos disponibles
                        if (product.getQuantity() > 0) {
                            // Agrega el producto seleccionado al carrito de compras
                            carrito.add(product);
                            // Resta la cantidad de productos seleccionados de la cantidad disponible
                            product.setQuantity(product.getQuantity() - 1);
                            // Actualiza la etiqueta de cantidad disponible
                            quantityLabel.setText("Cantidad disponible: " + product.getQuantity());
                            System.out.println("Producto añadido al carrito: " + product.getName());
                        } else {
                            System.out.println("¡No hay suficientes productos disponibles!");
                        }
                    }
                });
                frame.getContentPane().add(panel);
            }

            frame.setVisible(true);

            // Menú en terminal
            while (true) {
                System.out.println("\nMenú:");
                System.out.println("1. Ver carrito");
                System.out.println("2. Finalizar compra");
                System.out.println("3. Salir");
                System.out.print("Seleccione una opción: ");
                int opcion = Integer.parseInt(br.readLine());

                switch (opcion) {
                    case 1:
                        verCarrito(carrito, br);
                        break;
                    case 2:
                        generarResumenCompra(carrito);
                        break;
                    case 3:
                        enviarCatalogoActualizado(cl, productsDes);
                        System.out.println("Saliendo...");
                        frame.dispose();
                        return;
                    default:
                        System.out.println("Opción no válida.");
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void verCarrito(List<Product> carrito, BufferedReader br) throws IOException {
        System.out.println("Carrito de compras:");
        for (int i = 0; i < carrito.size(); i++) {
            Product product = carrito.get(i);
            System.out.println((i + 1) + ". " + product.getName() + " - $" + product.getPrice());
        }
        System.out.println("Seleccione el número de un producto para eliminarlo, o 0 para regresar al menú:");
        int seleccion = Integer.parseInt(br.readLine());
        if (seleccion > 0 && seleccion <= carrito.size()) {
            carrito.remove(seleccion - 1);
            System.out.println("Producto eliminado del carrito.");
        }
    }

    private static void generarResumenCompra(List<Product> carrito) {
        String dest = "resumenCompra.pdf";
        try {
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            document.add(new Paragraph("Resumen de la compra"));
            document.add(new Paragraph("Fecha y hora de la compra: " + new Date()));

            float[] columnWidths = {3, 1, 2, 2};
            Table table = new Table(columnWidths);

            table.addHeaderCell("Nombre");
            table.addHeaderCell("Cantidad");
            table.addHeaderCell("Precio unitario");
            table.addHeaderCell("Subtotal");

            double total = 0;
            for (Product product : carrito) {
                double subtotal = product.getPrice() * 1; // Se asume una cantidad de 1 para cada producto
                table.addCell(product.getName());
                table.addCell("1"); // Se asume una cantidad de 1 para cada producto
                table.addCell("$" + product.getPrice());
                table.addCell("$" + subtotal);
                total += subtotal;
            }

            document.add(table);
            document.add(new Paragraph("Total de la compra: $" + total));

            document.close();
            System.out.println("Resumen de la compra generado como PDF en: " + dest);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void enviarCatalogoActualizado(Socket cl, List<Product> products) throws IOException {
        // Serializar y enviar el catálogo actualizado al servidor
        ObjectOutputStream ser = new ObjectOutputStream(cl.getOutputStream());
        ser.writeObject(products);
        ser.close();
    }
}
