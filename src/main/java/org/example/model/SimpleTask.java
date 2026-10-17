package org.example.model;

import java.io.Serializable;

public final class SimpleTask extends Task implements Serializable {
    private int startHour;
    private int endHour;

    public SimpleTask(int startHour, int endHour,int idTask, String statusTask){
        super(idTask,statusTask);
        this.startHour = startHour;
        this.endHour = endHour;
    }

    @Override
    public int estimateDuration(){
        if(endHour>=startHour){
            return endHour-startHour;
        }
        else return (24-startHour)+endHour;
    }

    public int getStartHour(){
        return startHour;
    }

    public int getEndHour(){
        return endHour;
    }

    public void setStartHour(int startHour){
        this.startHour = startHour;
    }

    public void setEndHour(int endHour){
        this.endHour = endHour;
    }

    @Override
    public String toString() {
        return String.format("Simple Task ID:%d [%s] | %d:00-%d:00 (%d h)",this.getIdTask(),this.getStatusTask(),this.getStartHour(),this.getEndHour(),this.estimateDuration());
    }
}
