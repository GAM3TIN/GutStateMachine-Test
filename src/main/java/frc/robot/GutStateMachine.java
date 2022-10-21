
package frc.robot;
import edu.wpi.first.wpilibj.Timer;
import java.lang.Math;

public class GutStateMachine extends ColorSensor {

    private boolean requestIntake = false;
    private boolean requestShoot = false;
    private boolean acceptAll = false;
    //Color sensors will be in a seperate file
    //Spit is auto infered if accept all = false

    private GutStates systemState = GutStates.IDLE_NO_CARGO;

    public void requestShoot(boolean shoot){ requestShoot = shoot; }
    public void requestReset(boolean reset){ systemState = GutStates.IDLE_NO_CARGO;}



public enum GutStates {
    IDLE_NO_CARGO,
    IDLE_ONE_CARGO,
    IDLE_TWO_CARGO,
    INTAKING_NO_CARGO,
    INTAKING_ONE_CARGO,
    INTAKING_TWO_CARGO,
    SPIT_ONE_CARGO,
    SHOOT_CARGO
}

public void periodic() {
GutStates nextSystemState = systemState; // this is different in C++? I would like to make system state change to next system state

if(systemState == GutStates.IDLE_NO_CARGO){

//State Outputs
gut.set(0.0);
gutVecteryBois.set(0.0); // fill in once defined later



//State Transitions

//IDLE based states

if(requestIntake){
//If we want to intake and we are given these inital conditions then change states to -->
if(getColorFar() == correctColor() && getColorClose() == CloseColorSensorStates.NONE){

    nextSystemState = GutStates.INTAKING_ONE_CARGO;
}

if(getColorFar() == correctColor() && getColorClose() == correctColor()){

    nextSystemState = GutStates.INTAKING_TWO_CARGO;
}

if(getColorFar() == correctColor() && getColorClose() == wrongColor()){

    nextSystemState = GutStates.SPIT_ONE_CARGO;
}

} else {
//When not requesting intake and we think we have no cargo double check that all is true
//I belive this is useful in autos and such

if(getColorFar() == correctColor() && getColorClose() == CloseColorSensorStates.NONE){

    nextSystemState = GutStates.IDLE_ONE_CARGO;
}

if(getColorFar() == correctColor() && getColorClose() == correctColor()){

    nextSystemState = GutStates.IDLE_TWO_CARGO;
}

if(getColorFar() == correctColor() && getColorClose() == wrongColor()){

    nextSystemState = GutStates.SPIT_ONE_CARGO;
}

}

}

//If we have one correct ball
if(systemState == GutStates.IDLE_ONE_CARGO){

    //State Outputs
    gut.set(0.0);
    gutVecteryBois.set(0.0); // fill in once defined later
    
    
    
    //State Transitions
    
    if(requestIntake && !requestShoot){
        nextSystemState = GutStates.INTAKING_ONE_CARGO;
    }

    if(requestShoot){
        nextSystemState = GutStates.SHOOT_CARGO;
    }
    
}

//If we have two correct balls
if(systemState == GutStates.IDLE_TWO_CARGO){

    //State Outputs
    gut.set(0.0);
    gutVecteryBois.set(0.0); // fill in once defined later
    
    
    
    //State Transitions
    
    if(requestIntake && !requestShoot){
        nextSystemState = GutStates.INTAKING_TWO_CARGO;
    }

    if(requestShoot){
        nextSystemState = GutStates.SHOOT_CARGO;
    }
    
}

//Intaking based states

//If we have no balls
if(systemState == GutStates.INTAKING_NO_CARGO){

    //State Outputs
    gut.set(0.5);
    gutVecteryBois.set(0.5); // fill in once defined later
    
    //Make sure shooter is idleing for the barf acttion
    
    //State Transitions
    
    if(!requestIntake){
        nextSystemState = GutStates.IDLE_ONE_CARGO;
    }

    if(getColorFar() == correctColor()){
        nextSystemState = GutStates.INTAKING_ONE_CARGO;
    }
    
}

//If we have one ball and intaking
if(systemState == GutStates.INTAKING_ONE_CARGO){

    //State Outputs
    gut.set(0.5); // only close to intake (might be none)
    gutVecteryBois.set(0.5); // fill in once defined later
    
    //Make sure the backmost roller is not running so that we dont spit
    
    //State Transitions
    
    if(!requestIntake){
        nextSystemState = GutStates.IDLE_ONE_CARGO;
    }

    if(getColorClose() == correctColor()){
        nextSystemState = GutStates.INTAKING_TWO_CARGO;
    }

    if(requestShoot){
        nextSystemState = GutStates.SHOOT_CARGO;
    }

    if(getColorClose() == wrongColor()){
        nextSystemState = GutStates.SPIT_ONE_CARGO;
    }
    
}


if(systemState == GutStates.INTAKING_TWO_CARGO){

    //State Outputs
    gut.set(0.0);
    gutVecteryBois.set(0.0); // fill in once defined later
    
    //Make sure shooter is idleing for the shoot
    
    //State Transitions
    
    if(!requestIntake){
        nextSystemState = GutStates.IDLE_TWO_CARGO;
    }


    if(requestShoot){
        nextSystemState = GutStates.SHOOT_CARGO;
    }
    
}

//Special Cases, Shooting, 


if(systemState == GutStates.SHOOT_CARGO){

    //State Outputs

    //shooter.setRPM(fender)

    //shooter subsystem should watch for this as a flag then spin up flywheel
    if(shooter.getState() == shooterState.AT_SETPOINT){
        
    if(getColorFar() != wrongColor() && getColorClose() != wrongColor()){
        gut.set(0.5);
        gutVecteryBois.set(0.5); // fill in once defined later
    }
    //Make sure shooter is idleing for the barf acttion

    if(getColorFar() != wrongColor() && getColorClose() == wrongColor()){
        gut.set(0.5); // only shoot one ball
    }


    //

    }
    //State Transitions
    
    if(!requestShoot){
        nextSystemState = GutStates.IDLE_NO_CARGO; 
    } // I can do this because of the logic in IDLE_NO_CARGO
    //it will auto sence what state it is acctually in


}

//Spit out of the intake
if(systemState == GutStates.SPIT_ONE_CARGO){

    //State Outputs
    gut.set(0.0);
    gutVecteryBois.set(-0.5); // fill in once defined later
    
    //Make sure shooter is idleing for the shoot
    
    //State Transitions
    
    if(requestShoot){
        nextSystemState = GutStates.SHOOT_CARGO;
    }

    final double now = Timer.getMatchTime();

    //poor formatting, wait for 0.7 seconds
    if((now-Timer.getMatchTime()) < -0.7){
        if(requestIntake){ nextSystemState = GutStates.INTAKING_ONE_CARGO; }
        else { nextSystemState = GutStates.IDLE_ONE_CARGO; }
    }
        
    
    
}





}

}