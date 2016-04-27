package com.myapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Bluetooth connection initiator
 */
public class BluetoothInitiator {

    /**
     * Result from search attempt
     */
    public enum SearchResult {
        ADAPTER_NOT_FOUND("Bluetooth adapter not found"),
        ADAPTER_NOT_ENABLED("Bluetooth adapter not enabled"),

        TARGET_FOUND("Bluetooth target device found"),
        TARGET_NOT_FOUND("Bluetooth target device not found");

        private String message;

        /**
         * Initialize {@code SearchResult}
         * @param message Message
         */
        SearchResult(String message) {
            this.message = message;
        }

        /**
         * Get message
         * @return Message
         */
        public String getMessage() {
            return message;
        }
    }

    /**
     * Result from connection attempt
     */
    public enum ConnectionResult {
        CONNECTION_ERROR("Bluetooth connection error"),
        CONNECTION_ESTABLISHED("Bluetooth connection established");

        private String message;

        /**
         * Initialize {@code ConnectionResult}
         * @param message Message
         */
        ConnectionResult(String message) {
            this.message = message;
        }

        /**
         * Get message
         * @return Message
         */
        public String getMessage() {
            return message;
        }
    }
