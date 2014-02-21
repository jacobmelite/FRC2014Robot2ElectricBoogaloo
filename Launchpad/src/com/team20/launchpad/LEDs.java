/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team20.launchpad;

import edu.wpi.first.wpilibj.DigitalOutput;

/**
 *
 * @author Driver
 */
public class LEDs {

    DigitalOutput ones;
    DigitalOutput twos;
    DigitalOutput fours;
    int state;

    public LEDs(int mode, int foursChannel, int twosChannel, int onesChannel) {
        setMode(mode);
        ones = new DigitalOutput(onesChannel);
        twos = new DigitalOutput(twosChannel);
        fours = new DigitalOutput(foursChannel);
    }

    public void setMode(int mode) {
        if (mode > 7 || mode < 0) {
            return;
        }
        state = mode;
        switch (mode) {
            case 0:
                setOutputs(false, false, false);
                break;
            case 1:
                setOutputs(false, false, true);
                break;
            case 2:
                setOutputs(false, true, false);
                break;
            case 3:
                setOutputs(false, true, true);
                break;
            case 4:
                setOutputs(true, false, false);
                break;
            case 5:
                setOutputs(true, false, true);
                break;
            case 6:
                setOutputs(true, true, false);
                break;
            case 7:
                setOutputs(true, true, true);
                break;
        }
    }

    public void setOutputs(boolean four, boolean two, boolean one) {
        ones.set(one);
        twos.set(two);
        fours.set(four);
    }

    public int getMode() {
        return state;
    }
}
