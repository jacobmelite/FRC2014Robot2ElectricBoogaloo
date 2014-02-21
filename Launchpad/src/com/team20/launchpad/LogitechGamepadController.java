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
public class LogitechGamepadController extends Joystick {

    private JoystickButton[] buttons;

    public LogitechGamepadController(int channel) {
        super(channel);
        buttons = new JoystickButton[8];
        for (int i = 0; i < 8; ++i) {
            buttons[i] = new JoystickButton(this, i + 1);
        }
    }

    public JoystickButton getAButton() {
        return buttons[0];
    }

    public JoystickButton getBButton() {
        return buttons[1];
    }

    public JoystickButton getXButton() {
        return buttons[2];
    }

    public JoystickButton getYButton() {
        return buttons[3];
    }

    public JoystickButton getLeftBumper() {
        return buttons[4];
    }

    public JoystickButton getRightBumper() {
        return buttons[5];
    }

    public JoystickButton getBackButton() {
        return buttons[6];
    }

    public JoystickButton getStartButton() {
        return buttons[7];
    }

    public double getLeftX() {
        return super.getRawAxis(1);
    }

    public double getLeftY() {
        return super.getRawAxis(2);
    }

    public double getRightX() {
        return super.getRawAxis(4);
    }
    
    public double getRightY() {
        return super.getRawAxis(5);
    }
    
    public double getAnalogTriggers(){
        return super.getRawAxis(3);
    }
}