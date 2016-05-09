package com.auditeTeam9;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Message queue for communicating with target device
 */
public class MessageQueue extends Thread {

    private static final char DELIMITER = '\n';

    private boolean running;

    private InputStream input;
    private OutputStream output;

    public BlockingQueue<String> incoming = new LinkedBlockingQueue<String>();
    public BlockingQueue<String> outgoing = new LinkedBlockingQueue<String>();

    /**
     * Initialize {@code MessageQueue}
     * @param input Input stream
     * @param output Output stream
     */
    public MessageQueue(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public void run() {
        running = true;

        try {
            while (running) {
                /*
                 * Write outgoing message to output stream
                 */
                if (outgoing.peek() != null) {
                    String message = outgoing.take() + DELIMITER;
                    output.write(message.getBytes());
                }

                /*
                 * Read incoming message from input stream
                 */
                if (input.available() > 0) {
                    int next;
                    StringWriter message = new StringWriter();

                    do {
                        next = input.read();
                        message.write(next);
                    } while (next != DELIMITER);

                    incoming.add(message.toString());
                }
            }
        } catch (InterruptedException exception) {
            exception.printStackTrace();
            this.running = false;
        } catch (IOException exception) {
            exception.printStackTrace();
            this.running = false;
        }
    }

    /**
     * Finish communication thread
     */
    public void finish() {
        this.running = false;
    }
}
