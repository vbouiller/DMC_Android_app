package fr.emse.com.environmentcontrol.Model;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Valentin on 20/12/2017.
 */

public abstract class RoomContextRule implements Parcelable, Serializable {

    String name;
    String description;

    public void apply(RoomContextState contextState){
        if(condition(contextState))
            action();
    }



    public abstract boolean condition(RoomContextState roomContextState);
    public abstract void action();

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



}

