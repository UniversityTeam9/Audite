package com.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends Activity {

    private MessageQueue messageQueue;

    /*
     * Bluetooth settings
     */
    private static final String BT_ADDRESS = "30:14:12:02:36:33";
    private static final String BT_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private BluetoothInitiator initiator =
            new BluetoothInitiator(BT_ADDRESS, UUID.fromString(BT_UUID));

    /*
     * Bluetooth indicator
     */
    private ImageButton bluetoothButton;

    /*
     * Direction buttons
     */
    private ImageButton buttonUp;
    private ImageButton buttonDown;
    private ImageButton buttonLeft;
    private ImageButton buttonRight;

    private ImageButton buttonFrontLight;
    private ImageButton buttonFrontProximity;
    private ImageButton buttonRearProximity;

    private boolean frontLightEnabled = false;
    private boolean frontProximityEnabled = false;
    private boolean rearProximityEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * Find views
         */
        bluetoothButton = (ImageButton) findViewById(R.id.bluetoothButton);

        buttonUp = (ImageButton) findViewById(R.id.buttonUp);
        buttonDown = (ImageButton) findViewById(R.id.buttonDown);
        buttonLeft = (ImageButton) findViewById(R.id.buttonLeft);
        buttonRight = (ImageButton) findViewById(R.id.buttonRight);

        buttonFrontLight = (ImageButton) findViewById(R.id.buttonFrontLight);
        buttonFrontProximity = (ImageButton) findViewById(R.id.buttonFrontProximity);
        buttonRearProximity = (ImageButton) findViewById(R.id.buttonRearProximity);

        /*
         * Disable control buttons
         */
        setControlEnabled(false);

        /*
         * Connect to bluetooth device
         */
        bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBluetoothEnabled(!bluetoothEnabled);
            }
        });

        /*
         * Connect actions
         */
        buttonUp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        messageQueue.outgoing.add(
                                new Command.Move(Command.Move.Direction.Forward).build());
                        break;

                    case MotionEvent.ACTION_UP:
                        messageQueue.outgoing.add(new Command.Stop().build());
                        break;
                }

                return false;
            }
        });

        buttonDown.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        messageQueue.outgoing.add(
                                new Command.Move(Command.Move.Direction.Backward).build());
                        break;

                    case MotionEvent.ACTION_UP:
                        messageQueue.outgoing.add(new Command.Stop().build());
                        break;
                }

                return false;
            }
        });

        buttonLeft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        messageQueue.outgoing.add(
                                new Command.Turn(
                                        Command.Turn.Direction.Left,
                                        Command.Turn.INDEFINITELY
                                ).build());
                        break;

                    case MotionEvent.ACTION_UP:
                        messageQueue.outgoing.add(new Command.Stop().build());
                        break;
                }

                return false;
            }
        });

    
