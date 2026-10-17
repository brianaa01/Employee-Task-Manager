package org.example.service;

import org.example.model.ComplexTask;
import org.example.model.Employee;
import org.example.model.Task;

import java.util.*;


public class Utility {

    public List<Employee> getWokingEmployees(TaskManagement management) {
        List<Employee> workingEmployees = new ArrayList<>();

        for(Employee e : management.getEmployees().keySet()){
            if(management.calculateEmployeeWorkDuration(e.getIdEmployee())>40){
                workingEmployees.add(e);
            }
        }

        workingEmployees.sort(new Comparator<Employee>() {
            @Override
            public int compare(Employee e1, Employee e2) {
                int hours1=management.calculateEmployeeWorkDuration(e1.getIdEmployee());
                int hours2=management.calculateEmployeeWorkDuration(e2.getIdEmployee());
                return Integer.compare(hours1,hours2);
            }
        });

        return workingEmployees;
    }

    public Map<String, Map<String, Integer>> statusTasks(TaskManagement taskManagement) {
        Map<String, Map<String, Integer>> map = new HashMap<>();

        for (Map.Entry<Employee, List<Task>> entry : taskManagement.getEmployees().entrySet()) {
            String name = entry.getKey().getName();
            List<Task> tasks = entry.getValue();

            int completed = 0;
            int uncompleted = 0;

            for (Task task : tasks) {
                if (task.getStatusTask().equalsIgnoreCase("completed")) {
                    completed++;
                } else if(task.getStatusTask().equalsIgnoreCase("uncompleted")){
                    uncompleted++;
                }

            }

            Map<String, Integer> map1 = new HashMap<>();
            map1.put("COMPLETED", completed);
            map1.put("UNCOMPLETED", uncompleted);

            map.put(name, map1);
        }
        return map;
    }

}