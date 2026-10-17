package org.example.ui;

import org.example.model.ComplexTask;
import org.example.model.Employee;
import org.example.model.SimpleTask;
import org.example.model.Task;
import org.example.service.TaskManagement;
import org.example.service.Utility;
import org.example.storage.SerializationOperations;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static javax.swing.SwingUtilities.isRightMouseButton;

public class MainFrame extends JFrame {
    private JTree employeeTree;
    private JPanel mainPanel;
    private JButton btnAddEmployee;
    private JButton btnModifyStatus;
    private JButton btnAddTask;
    private JButton btnWorkingE;
    private JButton btnStats;
    private JTable table;

    private TaskManagement taskManagement;
    private Utility utility;

    public MainFrame() {
        this.taskManagement = new TaskManagement();
        this.utility = new Utility();

        SerializationOperations storage = new SerializationOperations();
        Map<Employee, List<Task>> data = storage.deserializeEmployeesAndTasks("company.txt");

        if (data != null) {
            taskManagement.setEmployees(data);
        }


        setTitle("Management Employees");
        setContentPane(mainPanel);

        refreshTree();

        setupPopupMenu();
        setupButtonListeners();

        //la iesire din aplicatie se salveaza datele
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                Map<Employee, List<Task>> data = taskManagement.getEmployees();

                storage.serializeEmployeesAndTasks(data, "company.txt");

                System.exit(0);
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void refreshTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Company Employees");
        for (Map.Entry<Employee, List<Task>> entry : taskManagement.getEmployees().entrySet()) {
            Employee employee = entry.getKey();

            String employeeLabel = employee.getName() + "(ID: " + employee.getIdEmployee() + ")";
            DefaultMutableTreeNode employeeNode = new DefaultMutableTreeNode(employeeLabel);
            employeeNode.setUserObject(employee);

            for (Task task : entry.getValue()) {
                employeeNode.add(createTaskNode(task));
            }
            root.add(employeeNode);
        }

        employeeTree.setModel(new DefaultTreeModel(root));

        for (int i = 0; i < employeeTree.getRowCount(); i++) {
            employeeTree.expandRow(i);
        }
    }

    public DefaultMutableTreeNode createTaskNode(Task task) {

        DefaultMutableTreeNode taskNode = new DefaultMutableTreeNode(task);

        if (task instanceof ComplexTask) {
            for (Task subtask : ((ComplexTask) task).getTasks()) {
                taskNode.add(createTaskNode(subtask));
            }
        }

        return taskNode;
    }

    private void setupPopupMenu() {
        // meniu pt task
        JPopupMenu taskMenu = new JPopupMenu();
        JMenuItem delete = new JMenuItem("Delete Task");

        taskMenu.add(delete);


        //meniu pt agajat
        JPopupMenu employeeMenu = new JPopupMenu();
        JMenuItem calculateWorkDuration = new JMenuItem("Calculate Total Hours Worked");
        employeeMenu.add(calculateWorkDuration);

        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) employeeTree.getLastSelectedPathComponent();

                if (selectedNode != null && selectedNode.getUserObject() instanceof Task) {
                    Task taskToRemove = (Task) selectedNode.getUserObject();
                    DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();


                    //1:daca parintele nodului e complex task(e subtask) -> stergem subtask-ul din complex task
                    if (parentNode.getUserObject() instanceof ComplexTask) {
                        int confirm = JOptionPane.showConfirmDialog(MainFrame.this, "Do you want to delete this task?");
                        if (confirm == JOptionPane.YES_OPTION) {
                            ComplexTask complexParent = (ComplexTask) parentNode.getUserObject();
                            complexParent.deleteTask(taskToRemove);
                        }
                    }
                    //2:daca parintele nodului e angajatul -> stergem task-ul din lista task-urilor sale
                    else if (parentNode.getUserObject() instanceof Employee) {
                        int confirm = JOptionPane.showConfirmDialog(MainFrame.this, "Do you want to delete this task?");
                        if (confirm == JOptionPane.YES_OPTION) {
                            Employee employeeParent = (Employee) parentNode.getUserObject();
                            List<Task> employeeTasks = taskManagement.getEmployees().get(employeeParent);
                            if (employeeTasks != null) {
                                employeeTasks.remove(taskToRemove);
                            }
                        }

                    }

                    refreshTree();

                }
            }
        });

        calculateWorkDuration.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) employeeTree.getLastSelectedPathComponent();

                if (selectedNode != null && selectedNode.getUserObject() instanceof Employee) {
                    Employee curentEmployee = (Employee) selectedNode.getUserObject();

                    int totalHours = taskManagement.calculateEmployeeWorkDuration(curentEmployee.getIdEmployee());

                    JOptionPane.showMessageDialog(MainFrame.this, "Total Hours Worked:" + totalHours, "HOURS", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        employeeTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isRightMouseButton(e)) {
                    int row = employeeTree.getRowForLocation(e.getX(), e.getY());

                    if (row != -1) {
                        employeeTree.setSelectionRow(row);

                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) employeeTree.getLastSelectedPathComponent();
                        if (selectedNode != null && selectedNode.getUserObject() instanceof Employee) {
                            employeeMenu.show(employeeTree, e.getX(), e.getY());
                        } else if (selectedNode != null && selectedNode.getUserObject() instanceof Task) {
                            taskMenu.show(employeeTree, e.getX(), e.getY());
                        }
                    }

                }
            }
        });

    }

    public void setupButtonListeners() {
        btnAddEmployee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(MainFrame.this, "Enter Name of Employee:");

                if (name != null && !name.trim().isEmpty()) {
                    Random random = new Random();
                    int generatedID;

                    do {
                        generatedID = random.nextInt(50) + 1;

                    } while (taskManagement.findByID(generatedID) != null);

                    Employee employee = new Employee(generatedID, name);
                    taskManagement.getEmployees().put(employee, new ArrayList<>());

                    JOptionPane.showMessageDialog(MainFrame.this, "Employee added successfully! generated ID:" + generatedID);

                    refreshTree();

                }
            }
        });


        btnAddTask.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) employeeTree.getLastSelectedPathComponent();

                if (selectedNode == null) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Please Select First");
                    return;
                }

                if (selectedNode.getUserObject() instanceof SimpleTask) {
                    JOptionPane.showMessageDialog(MainFrame.this, "ERROR: Cannot add a subtask to a simple task");
                    return;
                }

                //panou cu combo box pt tipurile de task-uri
                JPanel panel = new JPanel(new GridLayout());

                String[] taskTypes = {"Simple task", "Complex task"};
                JComboBox<String> comboType = new JComboBox<>(taskTypes);

                JTextField txtID = new JTextField();
                JTextField txtStart = new JTextField();
                JTextField txtEnd = new JTextField();

                JLabel lblStart = new JLabel("Start Hour:");
                JLabel lblEnd = new JLabel("End Hour:");

                panel.add(new JLabel("Task Type:"));
                panel.add(comboType);
                panel.add(new JLabel("ID:"));
                panel.add(txtID);
                panel.add(lblStart);
                panel.add(txtStart);
                panel.add(lblEnd);
                panel.add(txtEnd);

                comboType.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        boolean isSimple = comboType.getSelectedItem().equals("Simple task");
                        lblStart.setVisible(isSimple);
                        lblEnd.setVisible(isSimple);
                        txtStart.setVisible(isSimple);
                        txtEnd.setVisible(isSimple);
                    }
                });

                int result = JOptionPane.showConfirmDialog(MainFrame.this, panel, "Add New Task", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int idTask = Integer.parseInt(txtID.getText());
                        String selectedType = comboType.getSelectedItem().toString();

                        Task newTask = null;

                        if (selectedType.equals("Simple task")) {
                            int startHour = Integer.parseInt(txtStart.getText());
                            int endHour = Integer.parseInt(txtEnd.getText());
                            newTask = new SimpleTask(startHour, endHour, idTask, "UNCOMPLETED");

                        } else {
                            newTask = new ComplexTask(idTask, "UNCOMPLETED");
                        }


                        //daca obiectul selectat de utilizator e employee -> il adaugam in lista lui de task-uri
                        if (selectedNode.getUserObject() instanceof Employee) {
                            Employee employee = (Employee) selectedNode.getUserObject();
                            taskManagement.assignTaskToEmployee(employee.getIdEmployee(), newTask);
                        }
                        //daca obiectul selectat e complex task -> adaugam task-ul ca si subtask
                        else if (selectedNode.getUserObject() instanceof ComplexTask) {
                            ComplexTask complexTask = (ComplexTask) selectedNode.getUserObject();
                            complexTask.addTask(newTask);
                        }

                        refreshTree();

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(MainFrame.this, "Invalid arguments!");
                    }
                }


            }
        });


        btnModifyStatus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel panel = new JPanel(new GridLayout());

                JTextField txtEmployeeID = new JTextField();
                JTextField txtTaskID = new JTextField();

                panel.add(new JLabel("Employee ID:"));
                panel.add(txtEmployeeID);
                panel.add(new JLabel("Task ID:"));
                panel.add(txtTaskID);

                int result = JOptionPane.showConfirmDialog(MainFrame.this, panel, "Modify Status Task", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                    try {
                        int idEmployee = Integer.parseInt(txtEmployeeID.getText());
                        int idTask = Integer.parseInt(txtTaskID.getText());

                        taskManagement.modifyTaskStatus(idEmployee, idTask);

                        refreshTree();

                        JOptionPane.showMessageDialog(MainFrame.this, "Task modified successfully!");
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(MainFrame.this, "Invalid arguments!", "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnWorkingE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Employee> workers = utility.getWokingEmployees(taskManagement);

                String[] columns = {"Employee ID", "Name", "Hours Worked"};
                DefaultTableModel model = new DefaultTableModel(columns, 0);

                for (Employee worker : workers) {
                    int hours = taskManagement.calculateEmployeeWorkDuration(worker.getIdEmployee());
                    model.addRow(new Object[]{worker.getIdEmployee(), worker.getName(), hours});
                }

                table.setModel(model);
            }
        });

        btnStats.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Map<String, Map<String, Integer>> stats = utility.statusTasks(taskManagement);

                String[] columns = {"Employee Name", "Completed tasks", "Uncompleted tasks"};
                DefaultTableModel model = new DefaultTableModel(columns, 0);

                for (Map.Entry<String, Map<String, Integer>> entry : stats.entrySet()) {
                    String name = entry.getKey();
                    int completed = entry.getValue().get("COMPLETED");
                    int uncompleted = entry.getValue().get("UNCOMPLETED");

                    model.addRow(new Object[]{name, completed, uncompleted});
                }

                table.setModel(model);
            }
        });
    }


}
