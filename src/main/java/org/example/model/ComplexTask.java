package org.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ComplexTask extends Task implements Serializable {
    List<Task> tasks;

    public ComplexTask(int idTask, String statusTask) {
        super(idTask, statusTask);
        this.tasks = new ArrayList<Task>();
    }

    public void addTask(Task task){

        tasks.add(task);
    }

    public void deleteTask(Task task){
        tasks.remove(task);
    }

    @Override
    public int estimateDuration() {
        int duration = 0;
        for(Task task : tasks){
            duration+=task.estimateDuration();
        }

        return duration;
    }

    @Override
    public void setStatusTask(String status){
        super.setStatusTask(status);

        if(status.equals("COMPLETED")){
            for(Task subTask : tasks){
                subTask.setStatusTask(status);
            }
        }
    }

    public void autoUpdateStatus(){
        boolean allCompleted=true;

        for(Task subTask : tasks){
            if(Objects.equals(subTask.getStatusTask(),"UNCOMPLETED")){
                allCompleted=false;
                break;
            }
        }

        if(allCompleted){
            super.setStatusTask("COMPLETED");
        }else{
            super.setStatusTask("UNCOMPLETED");
        }
    }

    public List<Task> getTasks(){
        return tasks;
    }

    @Override
    public String toString(){
        return String.format("Complex Task ID:%d [%s] | Total Duration: %d h",this.getIdTask(),this.getStatusTask(),this.estimateDuration());
    }
}
