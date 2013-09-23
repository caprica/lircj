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
 * Specification for components that are interested in handling lircd events.
 */
public interface LircListener {

    /**
     * Notify an lirc event.
     * <p>
     * The button name and remote control name both come from the lircd configuration.
     *
     * @param buttonName name of the button that was pressed
     * @param remoteControlName name of the remote control
     * @param repeatCount number of signals received
     */
    void lircEvent(String buttonName, String remoteControlName, int repeatCount);
}
