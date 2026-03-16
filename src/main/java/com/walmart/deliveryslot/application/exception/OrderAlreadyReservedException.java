package com.walmart.deliveryslot.application.exception;

public class OrderAlreadyReservedException extends RuntimeException {
    public OrderAlreadyReservedException(String orderId) {
        super("La orden ya tiene una reserva activa: " + orderId);
    }
}
