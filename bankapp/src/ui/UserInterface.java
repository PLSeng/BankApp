package ui;

import model.User;
import database.DatabaseConnection;

import java.util.Scanner;

public class UserInterface {
    private Scanner scanner;
    private DatabaseConnection dbConnection;

    public UserInterface() {
        scanner = new Scanner(System.in);
        dbConnection = new DatabaseConnection();
    }

    public void start() {
        DatabaseConnection.getConnection();
        while (true) {
            System.out.println("\nWelcome to Simple Bank App");
            System.out.println("1. User Login");
            System.out.println("2. User Signup");
            System.out.println("3. Admin Login");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    handleUserLogin();
                    break;
                case 2:
                    System.out.print("Enter new username: ");
                    String newUsername = scanner.nextLine();
                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine();
                    if (dbConnection.addUser(newUsername, newPassword)) {
                        System.out.println("User added successfully.");
                    } else {
                        System.out.println("Failed to add user.");
                    }
                    break;
                case 3:
                    handleAdminLogin();
                    break;
                case 4:
                    System.out.println("Exiting application.");
                    DatabaseConnection.closeConnection();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleAdminLogin() {
        System.out.print("Enter admin password: ");
        String password = scanner.nextLine();

        if ("admin".equals(password)) {
            performAdminActions();
        } else {
            System.out.println("Incorrect password.");
        }
    }

    private void performAdminActions() {
        while (true) {
            System.out.println("\nAdmin Menu:");
            System.out.println("1. View Users");
            System.out.println("2. Add User");
            System.out.println("3. Delete User");
            System.out.println("4. Update User");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    dbConnection.viewAllUsers();
                    break;
                case 2:
                    System.out.print("Enter new username: ");
                    String newUsername = scanner.nextLine();
                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine();
                    if (dbConnection.addUser(newUsername, newPassword)) {
                        System.out.println("User added successfully.");
                    } else {
                        System.out.println("Failed to add user.");
                    }
                    break;
                case 3:
                    System.out.print("Enter username of user to delete: ");
                    String usernameToDelete = scanner.nextLine();
                    if (dbConnection.deleteUser(usernameToDelete)) {
                        System.out.println("User deleted successfully.");
                    } else {
                        System.out.println("Failed to delete user.");
                    }
                    break;
                case 4:
                    System.out.print("Enter username of user to update: ");
                    String usernameToUpdate = scanner.nextLine();
                    System.out.print("Enter new password: ");
                    String updatedPassword = scanner.nextLine();
                    if (dbConnection.updateUserPassword(usernameToUpdate, updatedPassword)) {
                        System.out.println("User password updated successfully.");
                    } else {
                        System.out.println("Failed to update user password.");
                    }
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleUserLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = dbConnection.getUser(username, password);

        if (user != null) {
            performUserActions(user);
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private void performUserActions(User user) {
        System.out.println("\nWelcome, " + user.getUsername() + "!");
        while (true) {
            System.out.println("\nUser Menu:");
            System.out.println("1. Check Balance");
            System.out.println("2. Withdraw Money");
            System.out.println("3. Transfer Money");
            System.out.println("4. Deposit Money");
            System.out.println("5. Change Password");
            System.out.println("6. Delete Account");
            System.out.println("7. Logout");
            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("Current balance: " + user.getBalance());
                    break;
                case 2:
                    System.out.print("Enter withdrawal amount: ");
                    double withdrawalAmount = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter password: ");
                    String password = scanner.nextLine();
                    if(user.getPassword().equals(password)) {
                        if (user.withdraw(withdrawalAmount)) {
                            System.out.println("Withdrawn successfully. Remaining Balance: " + user.getBalance());
                        } else {
                            System.out.println("Insufficient balance.");
                        }
                    } else {
                        System.out.println("Incorrect password.");
                    }
                    break;
                case 3:
                    System.out.print("Enter receiver's username: ");
                    String receiverUsername = scanner.nextLine();
                    System.out.print("Enter transfer amount: ");
                    double transferAmount = scanner.nextDouble();
                    scanner.nextLine(); // Consume newline
                    System.out.print("Enter password: ");
                    password = scanner.nextLine();
                    if (user.getPassword().equals(password)) {
                        User receiver = dbConnection.getUser(receiverUsername);
                        if (receiver != null) {
                            if (user.transfer(receiver.getUsername(), transferAmount)) {
                                System.out.println("Transferred successfully. Remaining Balance: " + user.getBalance());
                            } else {
                                System.out.println("Insufficient balance.");
                            }
                        } else {
                            System.out.println("Receiver does not exist.");
                        }
                    } else {
                        System.out.println("Incorrect password.");
                    }
                    break;
                case 4:
                    System.out.print("Enter deposit amount: ");
                    double depositAmount = scanner.nextDouble();
                    user.deposit(depositAmount);
                    System.out.println("Deposited successfully. New Balance: " + user.getBalance());
                    break;
                case 5:
                    System.out.print("Enter new password: ");
                    String newPassword = scanner.nextLine();
                    user.setPassword(newPassword);
                    System.out.println("Password changed successfully.");
                    break;
                case 6:
                    System.out.print("Enter password: ");
                    password = scanner.nextLine();
                    if (user.getPassword().equals(password)) {
                        if (dbConnection.deleteUser(user.getUsername())) {
                            System.out.println("Account deleted successfully.");
                            return;
                        } else {
                            System.out.println("Failed to delete account.");
                        }
                    } else {
                        System.out.println("Incorrect password.");
                    }
                    break;
                case 7:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}

