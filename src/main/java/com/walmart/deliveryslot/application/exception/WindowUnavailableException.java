package com.walmart.deliveryslot.application.exception;

public class WindowUnavailableException extends RuntimeException {
    public WindowUnavailableException(String message) {
        super(message);
    }

    public static WindowUnavailableException noSlots(String windowId, String zoneId) {
        return new WindowUnavailableException(
            "Ventana agotada para la zona indicada. windowId=%s, zoneId=%s"
                .formatted(windowId, zoneId)
        );
    }
}
