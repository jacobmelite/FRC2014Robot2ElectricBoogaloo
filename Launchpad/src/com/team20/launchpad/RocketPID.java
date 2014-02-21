/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.team20.launchpad;

/**
 *
 * @author Driver
 */
public class RocketPID {
    private double P = 0, I = 0;
    private double error = 0;
    private double errorSum = 0;
    private double setpoint = 0;
    
    public RocketPID(double P, double I){
        this.P = P;
        this.I = I;
    }
    
    public void setSetpoint(double setpoint){
        this.setpoint = setpoint;
        error = 0;
        errorSum = 0;
    }
    
    public void setConstants(double P, double I){
        this.P = P;
        this.I = I;
    }
    
    public double calculate(double input){
        //Caclulating error
        error = input - setpoint;
        errorSum += error;
        
        return P*error + I*errorSum;
    }
}
