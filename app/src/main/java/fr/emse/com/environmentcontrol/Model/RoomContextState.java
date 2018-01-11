package fr.emse.com.environmentcontrol.Model;

/**
 * Created by Valentin on 11/10/2017.
 */

public class RoomContextState {
    private String room;
    private Status lightStatus;
    private Status ringerStatus;
    private int light;
    private float ringer;

    public RoomContextState(String room, Status lightStatus, Status ringerStatus, int light, float ringer) {
        super();
        this.room = room;
        this.lightStatus = lightStatus;
        this.ringerStatus = ringerStatus;
        this.light = light;
        this.ringer = ringer;
    }

    public String getRoom() {
        return this.room;
    }

    public Status getLightStatus() {
        return lightStatus;
    }

    public Status getRingerStatus() {
        return ringerStatus;
    }

    public int getLight() {
        return this.light;
    }

    public float getRinger() {
        return this.ringer;
    }

    @Override
    public String toString() {
        String res ="";
        res+="|======>RoodID: "+ room;
        res+=" |—| Light Status: "+String.valueOf(lightStatus);
        res+=" |—| Light LEVEL: "+String.valueOf(light);
        res+=" |—| Ringer Status: "+String.valueOf(ringerStatus);
        res+=" |—| Ringer LEVEL: "+String.valueOf(ringer);

        return res;

    }
}
