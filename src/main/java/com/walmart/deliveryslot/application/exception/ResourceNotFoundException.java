package com.walmart.deliveryslot.application.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException commune(String id) {
        return new ResourceNotFoundException("Comuna no encontrada: " + id);
    }

    public static ResourceNotFoundException zone(String id) {
        return new ResourceNotFoundException("Zona no encontrada: " + id);
    }

    public static ResourceNotFoundException order(String id) {
        return new ResourceNotFoundException("Orden no encontrada: " + id);
    }

    public static ResourceNotFoundException reservation(String id) {
        return new ResourceNotFoundException("Reserva no encontrada: " + id);
    }

    public static ResourceNotFoundException windowZoneCapacity(String id) {
        return new ResourceNotFoundException("Ventana/zona no encontrada: " + id);
    }
}
