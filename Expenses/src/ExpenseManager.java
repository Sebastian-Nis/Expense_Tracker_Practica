import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ExpenseManager {
    private static final String DIRECTORY = "expenses/";

    public ExpenseManager() {
        // Create directory if it doesn't exist
        File dir = new File(DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public List<String> listExpenses(String month) throws IOException {
        File file = new File(DIRECTORY + "/" + month + ".txt");
        if (!file.exists()) {
            throw new IOException("No expenses for " + month);
        }

        List<String> expenses = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                expenses.add(line);
            }
        }

        return expenses;
    }

    public void deleteExpenses(String month) throws IOException {
        File file = new File(DIRECTORY + "/" + month + ".txt");
        if (!file.exists()) {
            throw new IOException("No expenses for " + month);
        }

        if (!file.delete()) {
            throw new IOException("Unable to delete expenses for " + month);
        }
    }

    public void addExpense(String month, String expenseName, double amount) throws IOException {
        String filename = DIRECTORY + month + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(expenseName + ": " + amount + "\n"); // Include expenseName in the file
        }
    }

    public double calculateTotal(String month) throws IOException {
        String filename = DIRECTORY + month + ".txt";
        if (!Files.exists(Paths.get(filename))) {
            return 0.0;
        }

        double total = 0.0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(": "); // Split the line into expenseName and amount
                total += Double.parseDouble(parts[1]); // Parse the amount part
            }
        }
        return total;
    }

    public double calculateTotalAllMonths() throws IOException {
        double total = 0.0;
        File dir = new File(DIRECTORY);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(": "); // Split the line into expenseName and amount
                        total += Double.parseDouble(parts[1]); // Parse the amount part
                    }
                }
            }
        }
        return total;
    }

    public void deleteAllExpenses() throws IOException {
        File dir = new File(DIRECTORY);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    throw new IOException("Unable to delete file: " + file.getName());
                }
            }
        }
    }
}
