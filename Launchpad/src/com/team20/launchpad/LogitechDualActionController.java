/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team20.launchpad;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 *
 * @author Driver
 */
public class LogitechDualActionController extends Joystick {

    private JoystickButton[] buttons;

    public LogitechDualActionController(int channel) {
        super(channel);
        buttons = new JoystickButton[10];
        for (int i = 0; i < 10; ++i) {
            buttons[i] = new JoystickButton(this, i + 1);
        }
    }

    public JoystickButton getButton(int buttonNumber) {
        return buttons[buttonNumber - 1];
    }

    public boolean dPadLeft() {
        return super.getRawAxis(5) < -.5;
    }

    public boolean dPadRight() {
        return super.getRawAxis(5) > .5;
    }

    public boolean dPadUp() {
        return super.getRawAxis(6) < -.5;
    }

    public boolean dPadDown() {
        return super.getRawAxis(6) > .5;
    }

    public double getLeftX() {
        return super.getRawAxis(1);
    }

    public double getLeftY() {
        return super.getRawAxis(2);
    }

    public double getRightX() {
        return super.getRawAxis(3);
    }

    public double getRightY() {
        return super.getRawAxis(4);
    }
}