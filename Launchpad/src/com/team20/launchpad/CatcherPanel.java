/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team20.launchpad;

import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 *
 * @author Driver
 */
public class CatcherPanel {
    DoubleSolenoid piston;

    public CatcherPanel(DoubleSolenoid piston) {
        this.piston = piston;
    }
    
    public void bloom(){
        piston.set(DoubleSolenoid.Value.kForward);
    }
    
    public void wilt(){
        piston.set(DoubleSolenoid.Value.kReverse);
    }
    
    /**
     * @return true if the doublesolenoid is kOff
     */
    public boolean isBloomed() {
        return piston.get().equals(DoubleSolenoid.Value.kForward);
    }
    /**
     * @return true if the doublesolenoid is kForward
     */
    public boolean isWilted(){
        return piston.get().equals(DoubleSolenoid.Value.kReverse);
    }
}
