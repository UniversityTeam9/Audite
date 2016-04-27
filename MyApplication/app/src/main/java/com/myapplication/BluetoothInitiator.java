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

    private String address;
    private UUID uuid;

    private BluetoothAdapter adapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;

    /**
     * Initialize {@code BluetoothInitiator}
     * @param address Target device address
     * @param uuid Application UUID
     */
    public BluetoothInitiator(String address, UUID uuid) {
        this.address = address;
        this.uuid = uuid;
    }

    /**
     * Get current socket
     * @return Socket
     */
    public BluetoothSocket getSocket() {
        return socket;
    }

    /**
     * Find target device
     * @return Result from search attempt
     */
    public SearchResult search() {
        adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            return SearchResult.ADAPTER_NOT_FOUND;
        }

        if (!adapter.isEnabled()) {
            return SearchResult.ADAPTER_NOT_ENABLED;
        }

        for (BluetoothDevice device : adapter.getBondedDevices()) {
            if (device.getAddress().equals(address)) {
                this.device = device;
                return SearchResult.TARGET_FOUND;
            }
        }

        return SearchResult.TARGET_NOT_FOUND;
    }

    /**
     * Establish connection with target device
     * @return Result from connection attempt
     */
    public ConnectionResult connect() {
        try {
            socket = device.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException createException) {
            createException.printStackTrace();
            return ConnectionResult.CONNECTION_ERROR;
        }

        adapter.cancelDiscovery();

        try {
            socket.connect();
            return ConnectionResult.CONNECTION_ESTABLISHED;
        } catch (IOException connectException) {
            connectException.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException closeException) {
            closeException.printStackTrace();
        }

        return ConnectionResult.CONNECTION_ERROR;
    }

    /**
     * Disconnect from target device
     */
    public void disconnect() {
        try {
            socket.close();
        } catch (IOException closeException) {
            closeException.printStackTrace();
        }
    }
}