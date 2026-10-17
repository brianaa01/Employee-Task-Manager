package org.example.service;

import org.example.model.ComplexTask;
import org.example.model.Employee;
import org.example.model.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TaskManagement {
    private Map<Employee, List<Task>>  employees;

    public TaskManagement(){
        employees = new HashMap<>();
    }

    public Employee findByID(int id){
        for(Employee e:employees.keySet()){
            if(e.getIdEmployee()==id){
                return e;
            }
        }

        return null;
    }

    public void assignTaskToEmployee(int id, Task task){
        Employee employee = findByID(id);
        if(employee!=null){
            employees.get(employee).add(task);
        }
        else{
            System.out.println("Employee with id:"+id+" not found");
        }
    }

    public int calculateEmployeeWorkDuration(int idEmployee) {
        Employee employee = findByID(idEmployee);
        int duration=0;

        if(employee!=null){
            List<Task> tasks = employees.get(employee);

            for(Task task:tasks){
                if(task.getStatusTask().equalsIgnoreCase("completed")){
                    duration+=task.estimateDuration();
                }
                else if(task instanceof ComplexTask){
                    ComplexTask complexTask=(ComplexTask)task;
                    for(Task subTask:complexTask.getTasks()){
                        if(subTask.getStatusTask().equalsIgnoreCase("completed")){
                            duration+=subTask.estimateDuration();
                        }
                    }
                }
            }
        }

        return duration;
    }

    public void modifyTaskStatus(int idEmployee, int idTask){
        Employee employee = findByID(idEmployee);
        if(employee!=null){
            List<Task> tasks = employees.get(employee);

            for(Task task:tasks){
                if(task.getIdTask()==idTask){
                    if(Objects.equals(task.getStatusTask(), "UNCOMPLETED")){
                        task.setStatusTask("COMPLETED");
                    }else{
                        task.setStatusTask("UNCOMPLETED");
                    }
                    break;
                }

                if(task instanceof ComplexTask){
                    ComplexTask complexTask = (ComplexTask) task;

                    for(Task subTask:complexTask.getTasks()){
                        if(subTask.getIdTask()==idTask){
                            if(Objects.equals(subTask.getStatusTask(), "COMPLETED")){
                                subTask.setStatusTask("UNCOMPLETED");
                            }else{
                                subTask.setStatusTask("COMPLETED");
                            }
                        }
                    }

                    complexTask.autoUpdateStatus();
                }
            }


        }else{
            System.out.println("Employee with id:"+idEmployee+" not found");
        }
    }

    public Map<Employee, List<Task>> getEmployees() {
        return employees;
    }

    public void setEmployees(Map<Employee, List<Task>> employees) {
        this.employees = employees;
    }
}
