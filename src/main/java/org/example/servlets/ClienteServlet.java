package org.example.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.example.Credentials;

public class ClienteServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Cargar el controlador JDBC de MySQL
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            response.getWriter().println("Error al cargar el controlador JDBC: " + e.getMessage());
            return;
        }

        // Leer parámetros del formulario
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));
        String nombre = request.getParameter("nombre");
        String apellido = request.getParameter("apellido");
        String contrasena = request.getParameter("contrasena");
        String fechaNacimiento = request.getParameter("fechaNacimiento");
        String correo = request.getParameter("correo");
        long telefono = Long.parseLong(request.getParameter("telefono"));
        String fechaAfiliacion = request.getParameter("fechaAfiliacion");

        // Conectar a la base de datos y registrar cliente
        try (Connection connection = DriverManager.getConnection(Credentials.JDBC_URL, Credentials.USERNAME, Credentials.PASSWORD)) {
            // Insertar cliente
            String query = "INSERT INTO cliente (IDCliente, nombre, apellido, contrasena, fechaNacimiento, correo, telefono, fechaAfiliacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, idCliente);
                pstmt.setString(2, nombre);
                pstmt.setString(3, apellido);
                pstmt.setString(4, contrasena);
                pstmt.setDate(5, java.sql.Date.valueOf(fechaNacimiento));
                pstmt.setString(6, correo);
                pstmt.setLong(7, telefono);
                pstmt.setDate(8, java.sql.Date.valueOf(fechaAfiliacion));
                pstmt.executeUpdate();
            }

            // Obtener todos los clientes
            String selectQuery = "SELECT * FROM cliente";
            try (PreparedStatement pstmt = connection.prepareStatement(selectQuery);
                 ResultSet rs = pstmt.executeQuery()) {

                // Generar tabla HTML
                response.setContentType("text/html");
                response.getWriter().println("<h1>Cliente registrado exitosamente</h1>");
                response.getWriter().println("<h2>Lista de Clientes</h2>");
                response.getWriter().println("<table border='1' style='width:100%;text-align:left;'>");
                response.getWriter().println("<tr><th>ID</th><th>Nombre</th><th>Apellido</th><th>Correo</th><th>Teléfono</th></tr>");
                while (rs.next()) {
                    response.getWriter().println("<tr>");
                    response.getWriter().println("<td>" + rs.getInt("IDCliente") + "</td>");
                    response.getWriter().println("<td>" + rs.getString("nombre") + "</td>");
                    response.getWriter().println("<td>" + rs.getString("apellido") + "</td>");
                    response.getWriter().println("<td>" + rs.getString("correo") + "</td>");
                    response.getWriter().println("<td>" + rs.getLong("telefono") + "</td>");
                    response.getWriter().println("</tr>");
                }
                response.getWriter().println("</table>");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Error al registrar cliente: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Formulario de registro
        response.setContentType("text/html");
        response.getWriter().println("<h1>Formulario de Registro de Cliente</h1>");
        response.getWriter().println("<form method='POST'>");
        response.getWriter().println("ID Cliente: <input type='text' name='idCliente' /><br>");
        response.getWriter().println("Nombre: <input type='text' name='nombre' /><br>");
        response.getWriter().println("Apellido: <input type='text' name='apellido' /><br>");
        response.getWriter().println("Contraseña: <input type='password' name='contrasena' /><br>");
        response.getWriter().println("Fecha de Nacimiento: <input type='date' name='fechaNacimiento' /><br>");
        response.getWriter().println("Correo: <input type='email' name='correo' /><br>");
        response.getWriter().println("Teléfono: <input type='text' name='telefono' /><br>");
        response.getWriter().println("Fecha de Afiliación: <input type='date' name='fechaAfiliacion' /><br>");
        response.getWriter().println("<input type='submit' value='Registrar Cliente' />");
        response.getWriter().println("</form>");
    }
}
