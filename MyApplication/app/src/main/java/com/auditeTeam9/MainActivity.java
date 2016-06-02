package com.auditeTeam9;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import com.myapplication.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MainActivity extends Activity {

    private MessageQueue messageQueue;
    protected boolean preventCancel;

    private BluetoothInitiator btIni = new BluetoothInitiator();
    /*
     * Bluetooth settings
     */
    private static final String BT_ADDRESS = "30:14:12:02:36:33";
    private static final String BT_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    // TODO: Allow the user to add/edit commands
    private BluetoothInitiator initiator =
            new BluetoothInitiator(BT_ADDRESS, UUID.fromString(BT_UUID));

    /*
     * Bluetooth indicator
     */
    private ImageView bluetoothButton;

    /*
     * Direction buttons
     */
    private ImageView buttonUp;
    private ImageView buttonDown;
    private ImageView buttonLeft;
    private ImageView buttonRight;
    private ImageView voiceButton;
    private ImageView stopButton;


    private ImageView buttonFrontLight;
    private ImageView buttonFrontProximity;
    private ImageView buttonRearProximity;

    SeekBar seekBar;
    int barProgress = 0;

    private boolean frontLightEnabled = false;
    private boolean frontProximityEnabled = false;
    private boolean rearProximityEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        voiceButton = (ImageView) findViewById(R.id.voiceButton);

        /*
         *  Disable button if no recognition service is present
         */
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if(activities.size() == 0)
        {
            voiceButton.setEnabled(false);
            //voiceButton.setText("Speech recognizer not present");
        }


        /*
         * Find views
         */
        bluetoothButton = (ImageView) findViewById(R.id.bluetoothButton);
        stopButton = (ImageView) findViewById(R.id.stopButton);

        buttonUp = (ImageView) findViewById(R.id.buttonUp);
        buttonDown = (ImageView) findViewById(R.id.buttonDown);
        buttonLeft = (ImageView) findViewById(R.id.buttonLeft);
        buttonRight = (ImageView) findViewById(R.id.buttonRight);

        buttonFrontLight = (ImageView) findViewById(R.id.buttonFrontLight);
        buttonFrontProximity = (ImageView) findViewById(R.id.buttonFrontProximity);
        buttonRearProximity = (ImageView) findViewById(R.id.buttonRearProximity);

        /*
         * Slider
         */
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        //set initial value to 0 (furthest left)
        //seekBar.setProgress(0);
        //increment the initial progress by the value of 1
        //seekBar.incrementProgressBy(1);
        //maximum the slider can reach is 2
        //seekBar.setMax(2);

        seekBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {

                        switch (progressValue) {
                            case 0:
                                barProgress = progressValue;
                                messageQueue.outgoing.add(
                                        new Command.Velocity(Command.Velocity.Speed.Slow).build());
                                break;
                            case 1:
                                barProgress = progressValue;
                                messageQueue.outgoing.add(
                                        new Command.Velocity(Command.Velocity.Speed.Mid).build());
                                break;
                            case 2:
                                barProgress = progressValue;
                                messageQueue.outgoing.add(
                                        new Command.Velocity(Command.Velocity.Speed.Fast).build());
                                break;
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // What to do when the user starts touching the bar
                        // Save the progress if an initial value is needed.
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // When the user stops interacting with the bar
                        // Do something
                    }
                });



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
                if (btIni.checkBluetooth()) {
                    setBluetoothEnabled(!bluetoothEnabled);
                } else {
                    startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), btIni.BT_ENABLE);
                }
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
        stopButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        messageQueue.outgoing.add(
                                new Command.Stop().build());
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


        checkBlueToothState();
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

    /**
     *  Voice command section
     */

    // List of available commands
    private static final String[] commands = {"forward", "back", "left", "right", "stop", "front on", "front disabled","back on", "back disabled","lights off", "lights on",
            "turn on bluetooth",};
    // TODO: Allow the user to add/edit commands
    private int speed;
    boolean foundCommand;

    @Override
    protected void onResume()
    {
        preventCancel = false;
        speed = 20;
        super.onResume();
    }

    /**
     * Triggered by the voiceButton, it's bound inside the layout XML file (activity_main.xml)
     *
     * @param v
     */
    public void speakButtonClicked(View v)
    {
        startVoiceRecognitionActivity();
    }

    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        preventCancel = true;
        startActivityForResult(intent, BluetoothInitiator.MSG_1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == BluetoothInitiator.MSG_1 && resultCode == RESULT_OK)
        {
            // ArrayList contains the words interpreted by the voice recognition
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            foundCommand = false;
            for(String command : commands)
            {
                // If the array contains at least one word that's in the commands array
                if(matches.contains(command))
                {
                    foundCommand = true;
                    if(command == "forward")
                    {
                        messageQueue.outgoing.add(
                                new Command.Move(Command.Move.Direction.Forward).build());
                    }
                    else if(command == "left")
                    {
                        messageQueue.outgoing.add(
                                new Command.Turn(
                                        Command.Turn.Direction.Left,
                                        Command.Turn.INDEFINITELY
                                ).build());
                    }
                    else if(command == "right")
                    {
                        messageQueue.outgoing.add(
                                new Command.Turn(
                                        Command.Turn.Direction.Right,
                                        Command.Turn.INDEFINITELY
                                ).build());
                    }
                    else if(command == "back")
                    {
                        messageQueue.outgoing.add(
                                new Command.Move(Command.Move.Direction.Backward).build());
                    }
                    else if(command == "stop")
                    {
                        messageQueue.outgoing.add(new Command.Stop().build());

                    }

                    else if(command == "front on")
                    {
                                messageQueue.outgoing.add(new Command.Toggle(
                                        Command.Toggle.Feature.FrontProximity,
                                        Command.Toggle.Status.ON


                                ).build());
                        buttonFrontProximity.setColorFilter(getToggleColor(true));

                    }
                    else if(command == "front disabled") {
                        messageQueue.outgoing.add(new Command.Toggle(
                                Command.Toggle.Feature.FrontProximity,
                                Command.Toggle.Status.OFF


                        ).build());
                        buttonFrontProximity.setColorFilter(getToggleColor(false));

                    }

                    else if(command == "back on")
                    {
                        messageQueue.outgoing.add(new Command.Toggle(
                                Command.Toggle.Feature.RearProximity,
                                Command.Toggle.Status.ON


                        ).build());
                        buttonRearProximity.setColorFilter(getToggleColor(true));

                    }
                    else if(command == "back disabled") {
                        messageQueue.outgoing.add(new Command.Toggle(
                                Command.Toggle.Feature.RearProximity,
                                Command.Toggle.Status.OFF


                        ).build());
                        buttonRearProximity.setColorFilter(getToggleColor(false));

                    }
                    else if(command == "lights on") {
                        messageQueue.outgoing.add(new Command.Toggle(
                                Command.Toggle.Feature.FrontLight,
                                Command.Toggle.Status.ON


                        ).build());
                        buttonFrontLight.setColorFilter(getToggleColor(true));


                    }
                    else if(command == "lights off") {
                        messageQueue.outgoing.add(new Command.Toggle(
                                Command.Toggle.Feature.FrontLight,
                                Command.Toggle.Status.OFF


                        ).build());
                        buttonFrontLight.setColorFilter(getToggleColor(false));


                    }
                    else if(command == "turn on bluetooth") {

                        setBluetoothEnabled(true);
                        bluetoothButton.setColorFilter(getToggleColor(true));


                    }

                    break;
                }
            }

            // If the command is not in the list of commands, show a message saying invalid command
            // The message can be found in strings.xml
            if(!foundCommand)
            {
                Toast.makeText(getApplicationContext(), getString(R.string.invalid_command), Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Check if device has BT adapter and wheather it is enabled
     *
     * @return State of Bluetooth
     */
    private void checkBlueToothState()
    {
        // Inform user that the phone does not have Bluetooth
        if(btIni.checkBluetooth() == null)
        {
            Toast.makeText(getApplicationContext(), "Bluetooth not available.", Toast.LENGTH_SHORT).show();
            finish();
            System.exit(0);
        }
        else if(!btIni.checkBluetooth())
        {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), btIni.BT_ENABLE);
        }
    }
}


    
