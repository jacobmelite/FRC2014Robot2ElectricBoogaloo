/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team20.launchpad;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Talon;

/**
 *
 * @author Driver
 */
public class Collector extends Subsystem {
    Talon collector = new Talon(9);
    
    DoubleSolenoid piston = new DoubleSolenoid(2, 7, 8);
    
    private int state;
    private final int kDrive = 1, kOff = 0, kBackdrive = -1;
    
    public void update(){
        if(state == kDrive){
            collector.set(1);
        }else if (state == kBackdrive){
            collector.set(-1);
        }else{
            collector.set(0);
        }
    }
    
    public void drive(){
        state = kDrive;
    }
    
    public void stop(){
        state = kOff;
    }
    
    public void backdrive(){
        state = kBackdrive;
    }
    
    public void bloom(){
        piston.set(DoubleSolenoid.Value.kForward);
    }
    
    public void wilt(){
        piston.set(DoubleSolenoid.Value.kReverse);
    }
    
    /**
     * @return true if the speed is greater than 0 
     */
    public boolean isDrivingForward() {
        return state==kDrive;
    }

    /**
     * @return  true if the motor speed equals 0
     */
    public boolean isStopped() {
        return state == kOff;
    }

    /**
     * @return true if the motor speed is below 0
     */
    public boolean isDrivingBackwards() {
        return state==kBackdrive;
    }

    /**
     * @return true if both doublesolenoids have a value of kReverse
     */
    public boolean isBloomed() {
        return piston.get().equals(DoubleSolenoid.Value.kForward);
    }

    /**
     * @return true if both doublesolenoids have a value of kForward
     */
    public boolean isWilted() {
        return piston.get().equals(DoubleSolenoid.Value.kReverse);
    }
}
