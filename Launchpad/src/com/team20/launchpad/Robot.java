/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package com.team20.launchpad;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    //Driver joystick and driver buttons
    LogitechGamepadController driverGamepad;
    JoystickButton yButton, xButton, bButton, aButton, rightTrigger, leftTrigger;

    //Operator joystick and operator buttons
    LogitechDualActionController operatorController;
    JoystickButton button1, button2, button3, button4, button5, button6, button7, button8, button9, button10;
    boolean previousDPadUp = false, previousDPadLeft = false, previousDPadRight = false, previousDPadDown = false;
    boolean allPanelsBlooming = false;
    boolean launcherPulledBackLEDFinished = false, firstBallShot = false;
    long launcherPulledBackLEDStartTime = 0, autoStartTime;
    final long LAUNCHER_PULLED_BACK_LED_TIME_LIMIT = 2000;//2 second time limit
    final boolean STARTING_ON_RIGHT_HALF_OF_FIELD = true;//"right" relative to facing the goal we score in

    Drivetrain drivetrain;
    Collector collector;
    Compressor compressor;
    //Relay relay;
    Catapult catapult;
    CatcherPanel rightPanel, leftPanel, backPanel;

    Vision vision;
    LEDs leds;

    //Auto
    int autonomousMode = 1;
    final int kCloseOneBall = 1, kFarOneBall = 2, kFarOneBallDriveBack = 3, kTwoBall = 4;

    int counter = 0;

    public void robotInit() {
        //Initializing and starting compressor
        compressor = new Compressor(1, 1);
        compressor.start();

        //Initializing and starting up subsystems
        drivetrain = new Drivetrain();
        collector = new Collector();
        catapult = new Catapult();

        //Initializing and setting up catcher panels
        rightPanel = new CatcherPanel(new DoubleSolenoid(2, 1, 2));
        backPanel = new CatcherPanel(new DoubleSolenoid(2, 3, 4));
        leftPanel = new CatcherPanel(new DoubleSolenoid(2, 5, 6));

        //Initializing driver joystick
        driverGamepad = new LogitechGamepadController(1);
        yButton = driverGamepad.getYButton();
        xButton = driverGamepad.getXButton();
        bButton = driverGamepad.getBButton();
        aButton = driverGamepad.getAButton();
        rightTrigger = driverGamepad.getRightBumper();
        leftTrigger = driverGamepad.getLeftBumper();

        //Initializing operator joystick
        operatorController = new LogitechDualActionController(2);
        button1 = operatorController.getButton(1);
        button2 = operatorController.getButton(2);
        button3 = operatorController.getButton(3);
        button4 = operatorController.getButton(4);
        button5 = operatorController.getButton(5);
        button6 = operatorController.getButton(6);
        button7 = operatorController.getButton(7);
        button8 = operatorController.getButton(8);
        button9 = operatorController.getButton(9);
        button10 = operatorController.getButton(10);

        //Initializing comms for Beaglebone
        //vision = new Vision();
        vision = new Vision();
        vision.startThread();
        leds = new LEDs(0, 7, 6, 5);
    }

    public void init() {
        collector.bloom();
        drivetrain.resetEncoders();
    }

    public void periodic() {
        //Update subsystems
        collector.update();
        if (catapult.isRetracting()) {
            compressor.stop();
        } else {
            compressor.start();
        }

        updateLEDs();
        //System.out.println("Vision:\t"+vision.getHorizontalDetected());
    }

    private void updateLEDs() {
        //if we're in state 6 and the time for the state is over the limit, end the state
        if (leds.getMode() == 6 && System.currentTimeMillis() - launcherPulledBackLEDStartTime > LAUNCHER_PULLED_BACK_LED_TIME_LIMIT) {
            launcherPulledBackLEDFinished = true;
        }
        //set led mode here
        if (!DriverStation.getInstance().isTest() && !DriverStation.getInstance().isDisabled()) {//if it's not in test or disable mode, continue

            //if there is a horizontal goal that has been found and it is up to date
            if (vision.isHorizontalDetected() && vision.isHorizontalInfoUpdated()) {
                leds.setMode(3);
            } else if (DriverStation.getInstance().isAutonomous()) {//if we are in auto mode
                leds.setMode(2);
            } else if (catapult.isDown() && !launcherPulledBackLEDFinished) {
                if (leds.getMode() != 6) {//if you are setting it to 6 from another number, get the current time
                    launcherPulledBackLEDStartTime = System.currentTimeMillis();
                }
                leds.setMode(6);
            } else if (catapult.isRetracting()) {
                leds.setMode(7);
                launcherPulledBackLEDFinished = false;//allows the LEDs to go to state 6 again
            } else if (drivetrain.isInHighGear()) {
                leds.setMode(4);
            } else {//low gear
                leds.setMode(5);
            }
        }
    }

    public void autonomousInit() {
        init();
        vision.resetData();
        counter = 0;
        if (DriverStation.getInstance().getDigitalIn(1)) {
            autonomousMode = 1;
        } else if (DriverStation.getInstance().getDigitalIn(2)) {
            autonomousMode = 2;
        } else if (DriverStation.getInstance().getDigitalIn(3)) {
            autonomousMode = 3;
        } else if (DriverStation.getInstance().getDigitalIn(4)) {
            autonomousMode = 4;
        } else if (DriverStation.getInstance().getDigitalIn(5)) {
            autonomousMode = 5;
        } else if (DriverStation.getInstance().getDigitalIn(6)) {
            autonomousMode = 6;
        } else if (DriverStation.getInstance().getDigitalIn(7)) {
            autonomousMode = 7;
        } else if (DriverStation.getInstance().getDigitalIn(8)) {
            autonomousMode = 8;
        }

        autoStartTime = System.currentTimeMillis();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
        counter++;
        periodic();
        switch (autonomousMode) {
            case kCloseOneBall:
                closeOneBallPeriodic();
                break;
            case kFarOneBall:
                farOneBallPeriodic();
                break;
            case kFarOneBallDriveBack:
                farOneBallDriveBackPeriodic();
                break;
            case kTwoBall:
                twoBallPeriodic();
                break;
            default:
                break;
        }
    }

    public void closeOneBallPeriodic() {
        catapult.update();
        drivetrain.lowGear();
        if (counter < 20) {
            //Waiting for panels and collector to come out

        } else if (counter < 170) {
            if (drivetrain.getLeftEncoderCount() < 1100) {
                drivetrain.arcadeDrive(-1, 0);
            } else {
                drivetrain.arcadeDrive(0, 0);
            }

        } else if (counter < 180) {
            catapult.shoot();
            drivetrain.arcadeDrive(0, 0);
        } else {
            //Catapult retracts automatically
            drivetrain.arcadeDrive(0, 0);
        }
    }

    public void farOneBallPeriodic() {
        catapult.update(30);
        drivetrain.lowGear();
        if (counter < 40) {
            //Wait for stuff to open
            backPanel.bloom();
        } else if (counter < 50) {
            catapult.shoot();
            drivetrain.arcadeDrive(0, 0);
        } else if (counter < 230) {
            if (drivetrain.getLeftEncoderCount() < 1100) {
                drivetrain.arcadeDrive(-1, 0);
                collector.drive();
            } else {
                drivetrain.arcadeDrive(0, 0);
            }
        } else {
            drivetrain.arcadeDrive(0, 0);
            collector.stop();
        }
    }

    public void farOneBallDriveBackPeriodic() {
        catapult.update(30);
        drivetrain.lowGear();
        if (counter < 40) {
            //Wait for stuff to open
            backPanel.bloom();
        } else if (counter < 50) {
            catapult.shoot();
            drivetrain.arcadeDrive(0, 0);
        } else if (counter < 230) {
            if (drivetrain.getLeftEncoderCount() < 1100) {
                drivetrain.arcadeDrive(-1, 0);
                collector.drive();
            } else {
                drivetrain.arcadeDrive(0, 0);
            }
        } else {
            if (drivetrain.getLeftEncoderCount() > 0) {
                drivetrain.arcadeDrive(1, 0);
            } else {
                drivetrain.arcadeDrive(0, 0);
                //backPanel.wilt();
                collector.stop();
                //collector.wilt();
            }
        }
    }

    public void twoBallPeriodic() {
        catapult.update(20);
        drivetrain.lowGear();
        if (counter < 40) {
            //Wait for stuff to open
            backPanel.bloom();
        } else if (counter < 50) {
            catapult.shoot();
            drivetrain.arcadeDrive(0, 0);
        } else if (counter < 250) {
            //catapult.engageMotors();
            if (drivetrain.getLeftEncoderCount() < (1300 * 1.25)) {
                drivetrain.arcadeDrive(-1, 0);
                leftPanel.bloom();
                rightPanel.bloom();
                backPanel.bloom();
                collector.drive();
            } else {
                System.out.println("Counter:" + counter);
                drivetrain.arcadeDrive(0, 0);
                leftPanel.wilt();
                rightPanel.wilt();
            }
        } else if (counter < 260) {
            //catapult.engageMotors();
            catapult.shoot();
            drivetrain.arcadeDrive(0, 0);
            leftPanel.wilt();
            rightPanel.wilt();
        } else {
            //Ratchet back
            drivetrain.arcadeDrive(0, 0);
            collector.stop();
        }
    }

    
    /**
     * waits for stuff to open, waits for horizontal info to update, if there is
     * no goal detected within the first 5 seconds of auto, then you shoot the
     * ball into the now-hot goal
     */
    public void hotOneBallPeriodic() {
        if (!vision.isHorizontalInfoUpdated()) {
            vision.lookForHorizontalInfo();
        }
        if (counter < 40) {
            //Wait for stuff to open
            backPanel.bloom();
        } else if (vision.isHorizontalInfoUpdatedTwice()) {
            if (vision.isHorizontalDetectedFirstUpdate()||vision.isHorizontalDetectedSecondUpdate()) {
                catapult.shoot();
                drivetrain.arcadeDrive(0, 0);
            } else if (System.currentTimeMillis() - autoStartTime > 5000) {

                catapult.shoot();
                drivetrain.arcadeDrive(0, 0);
            }

        }

    }
    
    final int kStop = -1, kBlooming = 0, kProcessing = 1, kShooting = 2,
            kIdling = 3, kGettingBall = 4, kTurning = 5, kShooting2 = 6,
            kIdling2 = 7, kTurning2 = 8;
    int state = kBlooming;    
    public void hotTwoBallPeriodic() {
        switch (state) {
            case kStop://stops the robot
                drivetrain.arcadeDrive(0, 0);
                break;
            case kBlooming://blooms everything and starts collector
                vision.lookForHorizontalInfo();//sets the vision to look for the horizontal info
                
                if (counter < 40) {
                    //Wait for stuff to open
                    leftPanel.bloom();
                    rightPanel.bloom();
                    backPanel.bloom();
                    collector.bloom();
                    collector.drive();
                } else {
                    state = kProcessing;//wait for image processing
                }
                break;
            case kProcessing://wait for image processing
                if (vision.isHorizontalInfoUpdatedTwice()) {//wait for camera to send data
                    if (vision.isHorizontalDetectedFirstUpdate()||vision.isHorizontalInfoUpdatedTwice()) {//hot goal found
                        state = kShooting;//hot goal found, shoot into it
                    } else {
                        state = kTurning;//hot goal not found, turn to opposite goal
                    }
                }
                break;
            case kShooting://shoots the ball
                if (!firstBallShot) {
                    state = kIdling;//idles after the ball is shot
                } else {
                    state = kStop;//stops the robot after autonomous has ended
                }
                catapult.shoot();
                firstBallShot = true;
                drivetrain.arcadeDrive(0, 0);
                counter = 0;
                break;
            case kIdling://waits for the shooter to finish
                if (counter > 200) {
                    state = kGettingBall;//drives to the ball in front of us
                    drivetrain.resetEncoders();
                }
                break;
            case kGettingBall://drives to the ball in front of us
                if (drivetrain.getLeftEncoderCount() >-1100) { 
                    drivetrain.arcadeDrive(-1, 0);//drive backwards
                } else if(vision.isHorizontalDetectedFirstUpdate()||vision.isHorizontalInfoUpdatedTwice()){
                    drivetrain.arcadeDrive(0, 0);
                    state = kShooting;//shoot the ball we collected
                } else{
                    drivetrain.resetEncoders();
                    state = kTurning2;//turn if we haven't shot into the opposite goal yet
                }
                break;
            case kTurning://turns to either the opposite goal or the goal in front of us
                if (!firstBallShot) {
                    if (turnToGoal(false)) {//turns to the opposite (hot) goal
                        state = kShooting2;//shoots the ball
                    }
                } else {
                    if (turnToGoal(true)) {//turns to the goal we were initially facing
                        state = kGettingBall;//gets the ball in front of us
                    }
                }
                break;
            case kShooting2://shoots the ball
                catapult.shoot();
                firstBallShot = true;
                drivetrain.arcadeDrive(0, 0);
                counter = 0;
                state = kIdling2;
                break;
            case kIdling2://waits for the ball to shoot
                if (counter > 200) {
                    state = kTurning;//turns back to initial goal
                }
                break;
            case kTurning2:
                if(turnToGoal(false)){
                    state=kShooting;
                }
                break;
        }
    }

    /**
     *
     * @return false if the turn has not been completed
     */
    private boolean turnToGoal(boolean turnToStartPos) {
        if (!turnToStartPos && STARTING_ON_RIGHT_HALF_OF_FIELD && drivetrain.getLeftEncoderCount() > (-500 * 1.25)) {//TODO:test the value needed to turn to the correct angle
            //turning
            drivetrain.arcadeDrive(0, -1);//turn left if on the right half
            return false;
        } else if (!turnToStartPos && !STARTING_ON_RIGHT_HALF_OF_FIELD && drivetrain.getLeftEncoderCount() < (500 * 1.25)) {
            drivetrain.arcadeDrive(0, 1);//turn right if on the left half
            return false;
        } else if (STARTING_ON_RIGHT_HALF_OF_FIELD && drivetrain.getLeftEncoderCount() < (0)) {//turn to starting goal
            drivetrain.arcadeDrive(0, 1);//turns right if on right half
            return false;
        } else if (!STARTING_ON_RIGHT_HALF_OF_FIELD && drivetrain.getLeftEncoderCount() > (0)) {//turns to starting goal
            drivetrain.arcadeDrive(0, -1);//turns left if on left half
            return false;
        }
        drivetrain.arcadeDrive(0, 0);
        return true;
    }

    public void teleopInit() {
        init();
        leftPanel.bloom();
        backPanel.bloom();
        rightPanel.bloom();
        drivetrain.highGear();
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        periodic();
        catapult.update();

        //catapult.drive(operatorController.getLeftY());
        //Drive the robot
        drivetrain.arcadeDrive(driverGamepad.getLeftY(), driverGamepad.getAnalogTriggers());

        if (rightTrigger.get()) {
            drivetrain.highGear();
        } else if (leftTrigger.get()) {
            drivetrain.lowGear();
        }

        //Set catapult state to shoot
        if (button5.get() && button7.get()) {
            if (collector.isBloomed()) {
                catapult.shoot();
            } else {
                catapult.engageMotors();
                catapult.shoot();
                collector.backdrive();
                //collector.bloom();
            }
        }
        //catapult.disengageRatchet();

        if (button9.get()) {
            catapult.engage();
        } else if (button10.get()) {
            catapult.engageMotors();
        }

        //Setting collector position
        if (button6.get()) {
            collector.wilt();
        } else if (button8.get()) {
            collector.bloom();
        }

        //Setting collector state
        if (button4.get()) {
            collector.drive();
        } else if (button2.get()) {
            collector.backdrive();
        } else if (button3.get() || button1.get()) {
            collector.stop();
        }

        //Catcher panels
        if (operatorController.dPadUp() && !previousDPadUp) {
            if (allPanelsBlooming) {
                leftPanel.bloom();
                backPanel.bloom();
                rightPanel.bloom();
                allPanelsBlooming = false;
            } else {
                leftPanel.wilt();
                backPanel.wilt();
                rightPanel.wilt();
                allPanelsBlooming = true;
            }
        } else if (operatorController.dPadDown() && !previousDPadDown) {
            if (backPanel.isBloomed()) {
                backPanel.wilt();
            } else {
                backPanel.bloom();
            }
        } else if (operatorController.dPadLeft() && !previousDPadLeft) {
            if (leftPanel.isBloomed()) {
                leftPanel.wilt();
            } else {
                leftPanel.bloom();
            }
        } else if (operatorController.dPadRight() && !previousDPadRight) {
            if (rightPanel.isBloomed()) {
                rightPanel.wilt();
            } else {
                rightPanel.bloom();
            }
        }

        //Updating previous DPad states for toggle
        previousDPadUp = operatorController.dPadUp();
        previousDPadDown = operatorController.dPadDown();
        previousDPadLeft = operatorController.dPadLeft();
        previousDPadRight = operatorController.dPadRight();
    }

    public void testInit() {
        leds.setMode(0);
    }

    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        compressor.start();

        collector.wilt();
        leftPanel.wilt();
        rightPanel.wilt();
        backPanel.wilt();

        collector.stop();
        catapult.update();

        drivetrain.lowGear();
    }

    public void disabledInit() {
        leds.setMode(1);
    }

    public void disabledPeriodic() {
        //System.out.println("Vision:\t"+vision.getHorizontalDetected());
    }
}
