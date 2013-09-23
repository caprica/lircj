/*
 * This file is part of LIRCJ.
 *
 * LIRCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * LIRCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with LIRCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2010, 2011, 2012, 2013 Caprica Software Limited.
 */

package uk.co.caprica.lircj;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.etsy.net.UnixDomainSocketClient;

/**
 * A bridge between LIRC and Java.
 * <p>
 * This implementation opens a Unix Domain Socket and reads the broadcast messages from the lircd
 * daemon.
 * <p>
 * Events are <strong>not</strong> delivered on the Swing Event Dispatch Thread.
 * <p>
 * Usage is trivial:
 *
 * <pre>
 *   LircBridge bridge = new LircBridge("/var/run/lirc/lircd");
 *   bridge.addLircListener(...add your listener here...);
 *   bridge.start();
 *   ...
 *   // do some interesting things in your application until you exit
 *   ...
 *   bridge.release();
 * </pre>
 */
public class LircBridge {

    /**
     * Special case message sent from the lircd daemon e.g. after the lircd configuration file has
     * been re-read.
     */
    private static final String SIGHUP_LINE = "SIGHUP";

    /**
     * Collection of registered event listeners.
     */
    private final List<LircListener> listeners = new ArrayList<LircListener>();

    /**
     * The native socket file name, ordinarily something like <code>/var/run/lirc/lircd</code>.
     */
    private final String nativeSocketFile;

    /**
     * Only notify events where the repeat count is greater than or equal to this.
     */
    private final int repeatThreshold;

    /**
     * Signal to control whether or not the event thread should exit.
     */
    private volatile boolean shouldRun = true;

    /**
     * Event thread.
     */
    private Thread eventThread;

    /**
     * Create a new bridge to LIRC.
     *
     * @param nativeSocketFile native socket file name
     */
    public LircBridge(String nativeSocketFile) {
        this(nativeSocketFile, -1);
    }

    /**
     * Create a new bridge to LIRC.
     *
     * @param nativeSocketFile native socket file name
     * @param repeatThreshold only notify events where the repeat count is greater than or equal to
     *            this
     */
    public LircBridge(String nativeSocketFile, int repeatThreshold) {
        this.nativeSocketFile = nativeSocketFile;
        this.repeatThreshold = repeatThreshold;
    }

    /**
     * Add a component to be notified of IRC events.
     *
     * @param listener component to start notifying
     */
    public void addLircListener(LircListener listener) {
        listeners.add(listener);
    }

    /**
     * Stop a component from being notified of IRC events.
     *
     * @param listener component to stop notifying
     */
    public void removeLircListener(LircListener listener) {
        listeners.remove(listener);
    }

    /**
     * Start delivering events from the lircd daemon.
     */
    public void start() {
        eventThread = new LircEventThread();
        eventThread.setDaemon(true);
        eventThread.start();
    }

    /**
     * Stop delivering events from the lircd daemon and shut down this component.
     */
    public void release() {
        if(eventThread != null) {
            shouldRun = false;
            eventThread.interrupt();
            eventThread = null;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        release();
    }

    /**
     * Thread implementation to listen to lircd broadcast events and deliver them to all registered
     * event listeners.
     */
    private final class LircEventThread extends Thread {

        private static final int REPEAT_COUNT_COMPONENT = 1;

        private static final int BUTTON_NAME_COMPONENT = 2;

        private static final int REMOTE_CONTROL_NAME_COMPONENT = 3;

        /**
         * Native socket.
         */
        private UnixDomainSocketClient socketClient;

        /**
         * Line-based socket stream reader.
         */
        private BufferedReader socketReader;

        /**
         * Create a new thread.
         */
        private LircEventThread() {
            try {
                // 1 => stream socket type
                this.socketClient = new UnixDomainSocketClient(nativeSocketFile, 1);
                this.socketReader = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
            }
            catch(IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            String line;
            String[] components;
            while (shouldRun) {
                try {
                    System.out.println("READ<");
                    line = socketReader.readLine();
                    System.out.println("LINE>" + line);
                    if (!listeners.isEmpty()) {
                        if (!line.equals(SIGHUP_LINE)) {
                            components = line.split(" ");
                            int repeatCount = Integer.parseInt(components[REPEAT_COUNT_COMPONENT], 16);
                            if (repeatCount >= repeatThreshold) {
                                for (int i = listeners.size() - 1; i >= 0; i -- ) {
                                    listeners.get(i).lircEvent(components[BUTTON_NAME_COMPONENT], components[REMOTE_CONTROL_NAME_COMPONENT], repeatCount);
                                }
                            }
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                socketReader.close();
            }
            catch (IOException e) {
            }
            socketClient.close();
        }
    }
}
