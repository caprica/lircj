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

/**
 * Simple test.
 */
public class SocketTest {

    private static final String SOCKET_FILE = "/var/run/lirc/lircd";

    public static void main(String[] args) throws Exception {
        new SocketTest().start();
        Thread.currentThread().join();
    }

    public SocketTest() {
    }

    private void start() {
        LircBridge lircBridge = new LircBridge(SOCKET_FILE, 1);
        lircBridge.addLircListener(new LircListener() {
            @Override
            public void lircEvent(String buttonName, String remoteControlName, int repeatCount) {
                System.out.println(buttonName + " -> " + remoteControlName + " -> " + repeatCount);
            }
        });
        lircBridge.start();
    }
}
