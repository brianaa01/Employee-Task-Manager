package org.example.storage;

import org.example.model.*;

import java.io.*;
import java.util.List;
import java.util.Map;

public class SerializationOperations {
    // serialize pt angajati si lista lor de task-uri
    public void serializeEmployeesAndTasks(Map<Employee, List<Task>> data, String fileName) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(data);
            objectOutputStream.flush();
            objectOutputStream.close();

            System.out.println("Serialized data has been written to " + fileName);
        } catch (Exception e) {
            System.out.println("Error while saving data!");
        }
    }

    // deserialize pt angajati si lista lor de task-uri
    public Map<Employee, List<Task>> deserializeEmployeesAndTasks(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            Map<Employee, List<Task>> restoredData = (Map<Employee, List<Task>>) objectInputStream.readObject();
            objectInputStream.close();

            System.out.println("Data was read from " + fileName);
            return restoredData;

        } catch (Exception e) {
            System.out.println("Error while deserializing data!");
        }

        return null;
    }

}
