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

        buttonRight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        messageQueue.outgoing.add(
                                new Command.Turn(
                                        Command.Turn.Direction.Right,
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

        buttonFrontLight.setColorFilter(getToggleColor(false));
        buttonFrontLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frontLightEnabled = !frontLightEnabled;
                buttonFrontLight.setColorFilter(getToggleColor(frontLightEnabled));

                messageQueue.outgoing.add(new Command.Toggle(
                        Command.Toggle.Feature.FrontLight,
                        frontLightEnabled ? Command.Toggle.Status.ON : Command.Toggle.Status.OFF
                ).build());
            }
        });

        buttonFrontProximity.setColorFilter(getToggleColor(false));
        buttonFrontProximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frontProximityEnabled = !frontProximityEnabled;
                buttonFrontProximity.setColorFilter(getToggleColor(frontProximityEnabled));

                messageQueue.outgoing.add(new Command.Toggle(
                        Command.Toggle.Feature.FrontProximity,
                        frontProximityEnabled ? Command.Toggle.Status.ON : Command.Toggle.Status.OFF
                ).build());
            }
        });

        buttonRearProximity.setColorFilter(getToggleColor(false));
        buttonRearProximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rearProximityEnabled = !rearProximityEnabled;
                buttonRearProximity.setColorFilter(getToggleColor(rearProximityEnabled));

                messageQueue.outgoing.add(new Command.Toggle(
                        Command.Toggle.Feature.RearProximity,
                        rearProximityEnabled ? Command.Toggle.Status.ON : Command.Toggle.Status.OFF
                ).build());
            }
        });
    }

    private void setControlEnabled(boolean enabled) {
        buttonUp.setEnabled(enabled);
        buttonDown.setEnabled(enabled);
        buttonLeft.setEnabled(enabled);
        buttonRight.setEnabled(enabled);

        buttonFrontLight.setEnabled(enabled);
        buttonFrontProximity.setEnabled(enabled);
        buttonRearProximity.setEnabled(enabled);
    }

    private int getToggleColor(boolean value) {
        if (value) {
            return Color.argb(255, 30, 144, 255);
        } else {
            return Color.argb(255, 0, 0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        setBluetoothEnabled(false);
    }

    private boolean bluetoothEnabled = false;

    private void setBluetoothEnabled(boolean enabled) {
        this.bluetoothEnabled = enabled;
        bluetoothButton.setColorFilter(getToggleColor(this.bluetoothEnabled));

        setControlEnabled(false);

        if (bluetoothEnabled) {
            BluetoothInitiator.SearchResult searchResult = initiator.search();
            if (searchResult == BluetoothInitiator.SearchResult.TARGET_FOUND) {
                (new AsyncTask<BluetoothInitiator, Void, BluetoothInitiator.ConnectionResult>() {
                    @Override
                    protected BluetoothInitiator.ConnectionResult doInBackground(
                            BluetoothInitiator... params) {
                        return params[0].connect();
                    }

                    @Override
                    protected void onPostExecute(BluetoothInitiator.ConnectionResult result) {
                        super.onPostExecute(result);

                        /*
                         * Start messageQueue thread
                         */
                        if (result == BluetoothInitiator.ConnectionResult.CONNECTION_ESTABLISHED) {
                            try {
                                messageQueue = new MessageQueue(
                                        initiator.getSocket().getInputStream(),
                                        initiator.getSocket().getOutputStream()
                                );
                                messageQueue.start();

                                /*
                                 * Enable control buttons
                                 */
                                setControlEnabled(true);

                                /*
                                 * Turn all features off
                                 */
                                messageQueue.outgoing.add(new Command.Toggle(
                                        Command.Toggle.Feature.FrontLight,
                                        Command.Toggle.Status.OFF
                                ).build());

                                messageQueue.outgoing.add(new Command.Toggle(
                                        Command.Toggle.Feature.FrontProximity,
                                        Command.Toggle.Status.OFF
                                ).build());

                                messageQueue.outgoing.add(new Command.Toggle(
                                        Command.Toggle.Feature.RearProximity,
                                        Command.Toggle.Status.OFF
                                ).build());

                            } catch (IOException exception) {
                                exception.printStackTrace();
                            }
                        }

                        Toast.makeText(
                                getApplicationContext(),
                                result.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }).execute(initiator);
            }

            Toast.makeText(
                    getApplicationContext(),
                    searchResult.getMessage(),
                    Toast.LENGTH_LONG
            ).show();
        } else {
            /*
             * Disconnect from bluetooth device
             */
            if (initiator != null) {
                (new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        initiator.disconnect();
                        return null;
                    }
                }).execute();
            }

            /*
             * Stop messageQueue thread
             */
            if (messageQueue != null) {
                messageQueue.finish();
            }
        }
    }
}


    
