package controllers;

import models.Cliente;

public class ClienteValidator {

    public static String validar(Cliente c) {
        if (c.getNombre() == null || c.getNombre().trim().isEmpty()) {
            return "El nombre no puede estar vacío.";
        }
        if (c.getCorreo() == null || c.getCorreo().trim().isEmpty() || !c.getCorreo().contains("@")) {
            return "Correo electrónico inválido.";
        }
        return "";
    }
}