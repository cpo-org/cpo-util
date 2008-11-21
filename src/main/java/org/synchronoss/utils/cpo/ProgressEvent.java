/*
 *  Copyright (C) 2008
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at
 *  http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.utils.cpo;

import java.util.EventObject;

/**
 * Events to indicate progress on a long-running job.
 */
public class ProgressEvent extends EventObject {

    private int max = -1;
    private int value = -1;

    public ProgressEvent(Object source, int max, int value) {
        super(source);
        this.max = max;
        this.value = value;
    }

    public String toString() {
        return "max=" + max + "; value=" + value;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int m) {
        max = m;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int m) {
        value = m;
    }

    public boolean isMaxEvent() {
        return (max >= 0);
    }

    public boolean isProgressEvent() {
        return (value >= 0);
    }
}
