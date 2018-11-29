package bgu.spl.mics.Messages;

public class TickBroadcast implements Broadcast{
    private int currentTick;
    public TickBroadcast(int currentTick){
        this.currentTick = currentTick;
    }

    public int getCurrentTick(){
        return this.currentTick;
    }
}
