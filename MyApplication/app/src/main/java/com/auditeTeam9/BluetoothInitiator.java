package com.auditeTeam9;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import static android.content.ContentValues.TAG;

/**
 * Bluetooth connection initiator
 */
public class BluetoothInitiator extends Activity {

    private BluetoothThread bluetoothThread;

    private Handler activityHandler;
    private int state;
    private boolean stoppingConnection;


    // Constants to indicate message contents
    public static final int MSG_OK = 0;
    public static final int MSG_READ = 1;
    public static final int MSG_WRITE = 2;
    public static final int MSG_CANCEL = 3;
    public static final int MSG_CONNECTED = 4;

    // General purpose constants to be used inside activities as callback values
    public static final int MSG_1 = 10;
    public static final int MSG_2 = 11;
    public static final int MSG_3 = 12;

    // Constants that indicate the current connection state
    private static final int STATE_NONE = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    static final int BT_ENABLE = 1;

    protected BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();



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

        stoppingConnection = false;

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
        if(!stoppingConnection)
        {
            stoppingConnection = true;
            if(false)
                Log.i(TAG, "Stop");
            if(bluetoothThread != null)
            {
                bluetoothThread.cancel();
                bluetoothThread = null;
            }
            setState(STATE_NONE);
            sendMessage(MSG_CANCEL, "Connection ended");
        }
    }

    /**
     * Method that checks if a the phone has a
     * Bluetooth adapter and whether it's turned on
     *
     * @return State of Bluetooth
     */
    public Boolean checkBluetooth() {

        if(bluetoothAdapter.isEnabled()){
            return true;
        }
        else if(bluetoothAdapter == null){
            return null;
        }
        else{
            return false;
        }
    }

    public void enableBT(){
        startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), BT_ENABLE);

        bluetoothAdapter.enable();
    }

    /**
     * Constructor. Prepares a new Bluetooth session.
     */
    public BluetoothInitiator()
    {
        state = STATE_NONE;
        activityHandler = null;
    }

    private synchronized void setState(int newState)
    {
        if(false)
            Log.i(TAG, "Connection status: " + state + " -> " + newState);
        state = newState;
    }

    private synchronized void sendMessage(int type, Object value)
    {
        // It might happen that there's no activity handler, but here it doesn't prevent application work flow
        if(activityHandler != null)
        {
            activityHandler.obtainMessage(type, value).sendToTarget();
        }
    }

}

/**
 * This thread runs during a connection with a remote device. It handles the
 * initial connection and all incoming and outgoing transmissions.
 */
class BluetoothThread extends Thread {
    private final BluetoothSocket socket;


    public BluetoothThread(BluetoothDevice device) {
        BluetoothSocket tmp = null;
        try {
            // General purpose UUID
            tmp = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        socket = tmp;
    }

    public void cancel() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}