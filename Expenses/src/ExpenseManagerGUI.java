import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.awt.FontFormatException;

public class ExpenseManagerGUI {
    private JFrame frame;
    private JTextField expenseField;
    private ExpenseManager manager;
    private String month;
    private JButton selectedButton;
    private JTextField expenseNameField;
    private JTable table;
    private DefaultTableModel tableModel;

    public ExpenseManagerGUI() {
        manager = new ExpenseManager();

        // Load the custom font
        Font customFont = null;
        try (InputStream fontStream = getClass().getResourceAsStream("/custom_font.ttf")) {
            customFont = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(12f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            customFont = new Font("Arial", Font.PLAIN, 12); // Fallback font
        }

        Font customBoldFont = customFont.deriveFont(Font.BOLD, 12f); // Bold version of the custom font

        // Create the frame
        frame = new JFrame("Expense Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 300);
        frame.setLayout(new BorderLayout(10, 10)); // Add padding between components
        frame.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding between window and contents

        // Create the input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.PAGE_AXIS));

        // Add components to the input panel
        JPanel monthPanel = new JPanel(new GridLayout(1, 12));
        String[] monthNames = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        String[] monthFullNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        
        for (int i = 0; i < monthNames.length; i++) {
            String fullName = monthFullNames[i];
            JButton monthButton = new JButton(monthNames[i]);
            monthButton.setFont(customBoldFont); // Set custom bold font
            monthButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (selectedButton != null) {
                        selectedButton.setBackground(null); // Reset color of previously selected button
                    }
                    monthButton.setBackground(Color.YELLOW); // Set color of currently selected button
                    selectedButton = monthButton; 
                    month = fullName;
                    System.out.println("Selected month: " + month); // Print selected month to console
                    listExpenses(); // List expenses for the selected month
                }
            });
            monthPanel.add(monthButton);
        }

        inputPanel.add(monthPanel);
        inputPanel.add(Box.createVerticalStrut(7)); // Add space between monthPanel and expensePanel

        JPanel expensePanel = new JPanel(new GridLayout(1, 2)); // 2 columns for name and value
        JLabel expenseLabel = new JLabel("Expense:");
        expenseLabel.setFont(customFont); // Set custom font
        expensePanel.add(expenseLabel);
        expenseField = new JTextField();
        expenseField.setFont(customFont); // Set custom font
        expensePanel.add(expenseField);

        JLabel expenseNameLabel = new JLabel("Expense Name:");
        expenseNameLabel.setFont(customFont); // Set custom font
        expensePanel.add(expenseNameLabel);
        expenseNameField = new JTextField(10); // 10 is the number of columns in the text field
        expenseNameField.setFont(customFont); // Set custom font
        expensePanel.add(expenseNameField);

        inputPanel.add(expensePanel);
        inputPanel.add(Box.createVerticalStrut(7)); // Add space between expensePanel and operationPanel1

        // Initialize table and table model
        tableModel = new DefaultTableModel(new Object[]{"Expense Name", "Value"}, 0);
        table = new JTable(tableModel);
        table.setFont(customFont); // Set custom font
        table.getTableHeader().setFont(customBoldFont); // Set custom bold font for header
        JScrollPane tableScrollPane = new JScrollPane(table); // Create a scroll pane for the table

        JPanel operationPanel1 = new JPanel(new GridLayout(1, 3)); // 3 operations now
        JButton addButton = new JButton("Add Expense");
        addButton.setFont(customBoldFont); // Set custom bold font
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addExpense();
            }
        });
        operationPanel1.add(addButton);

        JButton totalButton = new JButton("Calculate Total");
        totalButton.setFont(customBoldFont); // Set custom bold font
        totalButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateTotal();
            }
        });
        operationPanel1.add(totalButton);

        JButton deleteButton = new JButton("Delete Expenses");
        deleteButton.setFont(customBoldFont); // Set custom bold font
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteExpenses();
            }
        });
        operationPanel1.add(deleteButton);

        inputPanel.add(operationPanel1);
        inputPanel.add(Box.createVerticalStrut(7)); // Add space between operationPanel1 and operationPanel2

        JPanel operationPanel2 = new JPanel(new GridLayout(1, 2)); // 2 operations in the new row
        JButton totalAllButton = new JButton("Calculate Total All Months");
        totalAllButton.setFont(customBoldFont); // Set custom bold font
        totalAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                calculateTotalAllMonths();
            }
        });
        operationPanel2.add(totalAllButton);

        JButton deleteAllButton = new JButton("Delete All Expenses");
        deleteAllButton.setFont(customBoldFont); // Set custom bold font
        deleteAllButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteAllExpenses();
            }
        });
        operationPanel2.add(deleteAllButton);

        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(tableScrollPane, BorderLayout.CENTER); // Add table scroll pane instead of text area scroll pane
        frame.add(operationPanel2, BorderLayout.SOUTH); // Add the new row of buttons at the bottom
        frame.setVisible(true);
    }

    private void addExpense() {
        System.out.println("addExpense() called");
        String month = this.month;
        String expenseName = expenseNameField.getText();
        double amount;
        try {
            System.out.println("Expense field text: " + expenseField.getText());
            amount = Double.parseDouble(expenseField.getText());
            System.out.println("Parsed amount: " + amount);
            manager.addExpense(month, expenseName, amount); // Pass expenseName to manager.addExpense
            System.out.println("Expense added in manager");
            // Automatically display the updated list
            listExpenses();
            // Clear the input text fields
            expenseField.setText("");
            expenseNameField.setText("");
        } catch (NumberFormatException e) {
            tableModel.addRow(new Object[]{"Error", "Invalid amount. Please enter a number."});
        } catch (IOException e) {
            tableModel.addRow(new Object[]{"Error", e.getMessage()});
        }
    }

    private void listExpenses() {
        String month = this.month;
        tableModel.setRowCount(0); // Clear previous table rows
        try {
            List<String> expenses = manager.listExpenses(month);
            for (String expense : expenses) {
                String[] parts = expense.split(": ");
                tableModel.addRow(new Object[]{parts[0], parts[1]});
            }
        } catch (IOException e) {
            tableModel.addRow(new Object[]{"Error", e.getMessage()});
        }
    }

    private void calculateTotal() {
        String month = this.month;
        try {
            double total = manager.calculateTotal(month);
            tableModel.addRow(new Object[]{"Total expenses for " + month, total});
        } catch (IOException e) {
            tableModel.addRow(new Object[]{"Error", e.getMessage()});
        }
    }

    private void calculateTotalAllMonths() {
        tableModel.setRowCount(0); // Clear previous table rows
        try {
            double totalAllMonths = 0.0;
            for (String monthName : new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"}) {
                List<String> expenses = manager.listExpenses(monthName);
                tableModel.addRow(new Object[]{monthName, ""});
                for (String expense : expenses) {
                    String[] parts = expense.split(": ");
                    tableModel.addRow(new Object[]{parts[0], parts[1]});
                }
                double total = manager.calculateTotal(monthName);
                totalAllMonths += total;
                tableModel.addRow(new Object[]{"Total expenses for " + monthName, total});
            }
            tableModel.addRow(new Object[]{"Total expenses for all months", totalAllMonths});
        } catch (IOException e) {
            tableModel.addRow(new Object[]{"Error", e.getMessage()});
        }
    }

    private void deleteExpenses() {
        String month = this.month;
        try {
            manager.deleteExpenses(month);
            tableModel.setRowCount(0); // Clear table after deleting
            tableModel.addRow(new Object[]{"Expenses deleted for " + month, ""});
        } catch (IOException e) {
            tableModel.addRow(new Object[]{"Error", e.getMessage()});
        }
    }

    private void deleteAllExpenses() {
        try {
            manager.deleteAllExpenses();
            tableModel.setRowCount(0); // Clear table after deleting
            tableModel.addRow(new Object[]{"All expenses deleted.", ""});
        } catch (IOException e) {
            tableModel.addRow(new Object[]{"Error", e.getMessage()});
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ExpenseManagerGUI();
            }
        });
    }
}
