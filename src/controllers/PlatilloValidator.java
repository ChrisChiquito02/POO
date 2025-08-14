package controllers;

import models.Platillo;

public class PlatilloValidator {

    public static String validar(Platillo p) {
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()) {
            return "El nombre no puede estar vacío.";
        }
        if (p.getPrecio() <= 0) {
            return "El precio debe ser mayor a cero.";
        }
        return "";
    }
}