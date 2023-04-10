package edu.cornell.gdiac.bubblebound;

public class Timer {

    private int maxTime;

    private int currentTime;

    boolean hasFinished;

    boolean pause;

    public Timer(int newMax){
        maxTime = newMax;
        currentTime = maxTime;
        pause = false;
        hasFinished = false;
    }

    public Timer(int newMax, boolean isPause){
        maxTime = newMax;
        currentTime = maxTime;
        pause = isPause;
        hasFinished = false;
    }

    public int getTime(){
        return currentTime;
    }

    public void setTime(int value){
        currentTime = value;
    }

    public int getMaxTime(){
        return maxTime;
    }

    public void setMaxTime(int value){
        maxTime = value;
    }

    public void update(){
        if(!(hasFinished || pause)) {
            currentTime--;
            if(currentTime == 0){
                hasFinished = true;
            }
        }
    }

    public void reset(){
        currentTime = maxTime;
        hasFinished = false;
    }

    //CHANGING SOMETHING RANDOM SO THAT A PULL REQUEST POPS UP BECAUSE GITHUB IS BEING A DUMMY!

    public boolean hasFinished(){
        return hasFinished;
    }
}
