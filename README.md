# Employee Task Management System 

A Java desktop application designed to manage employees and their assigned tasks. Built with a clean architecture, this project demonstrates advanced Object-Oriented Programming (OOP) concepts, graphical user interface (GUI) design using Java Swing, and data persistence through object serialization.

## Core Features
* **Employee & Task Tracking:** Assign, update, and manage tasks for different employees.
* **Complex Task Handling:** Supports sub-tasks and task hierarchies (Simple vs. Complex tasks) with automatic status updates.
* **Data Persistence:** Automatically saves and loads the application state using binary file serialization, ensuring no data is lost between sessions.
* **Graphical Interface:** Interactive and user-friendly desktop GUI built with Java Swing.

## Architecture & OOP Principles
The project is structured into logical packages (`model`, `service`, `storage`, `ui`) and applies standard software design principles:
* **Inheritance & Polymorphism:** A base `Task` class extended by `SimpleTask` and `ComplexTask` to handle different behaviors dynamically.
* **Collections:** Extensive use of Java Collections (e.g., `Map<Employee, List<Task>>`) to map employees to their specific workloads.
* **Serialization:** Custom file I/O operations (`SerializationOperations`) for packing and unpacking Java objects to binary files.
* **Dependency Management:** Managed via **Maven** (`pom.xml`) for clean builds and standardized project structure.

## Technologies Used
* **Language:** Java
* **Framework:** Java Swing (GUI)
* **Build Tool:** Maven
* **Core Concepts:** OOP, Data Serialization, Collections
