package Classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AccountManager {

    private final Connection connection;
    private final Scanner sc;

    AccountManager(Connection connect, Scanner scanner) {
        this.connection = connect;
        this.sc = scanner;
    }


    public void credit_money(Long number) {
        Accounts accounts = new Accounts(connection, sc);
        System.out.println("Enter Amount : ");
        int amount = sc.nextInt();
        System.out.println("Enter Security Pin : ");
        int sec = sc.nextInt();
        try {
            String query = "SELECT *FROM accounts WHERE account_number = ? AND security_pin = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, number);
            statement.setInt(2, sec);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                PreparedStatement statement1 = connection.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE account_number = ?");
                statement1.setInt(1, amount);
                statement1.setLong(2, number);
                statement1.execute();
                System.out.println(amount + " $ Credited Successfully");
                accounts.selectAction(number);

            } else {
                System.out.println("Invalid Security Pin!");
                System.out.println("Transaction Failed!");
                accounts.selectAction(number);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void debit_money(Long number) {
        Accounts accounts = new Accounts(connection, sc);
        sc.nextLine();
        System.out.println("Enter Amount: ");
        double amount = sc.nextDouble();
        System.out.println("Enter Security Pin : ");
        int sec = sc.nextInt();


        try {
            String query = "SELECT *FROM accounts WHERE account_number = ? AND security_pin = ? ";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, number);
            statement.setInt(2, sec);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                double current_balance = rs.getDouble("balance");

                if (amount <= current_balance) {
                    String debit_query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ? ";
                    PreparedStatement preparedStatement = connection.prepareStatement(debit_query);
                    preparedStatement.setDouble(1, amount);
                    preparedStatement.setLong(2, number);
                    preparedStatement.executeUpdate();
                    System.out.println(amount + " $ Debited Successfully");
                    accounts.selectAction(number);

                } else {
                    System.out.println("Insufficient Balance!");
                    System.out.println("Transaction Failed!");
                    accounts.selectAction(number);
                }
            } else {
                System.out.println("Invalid Pin!");
                debit_money(number);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void transfer_money(Long senderAccountNumber) {
        Accounts accounts = new Accounts(connection,sc);
        System.out.print("Enter Receiver Account Number: ");
        long receiverAccountNumber = sc.nextLong();
        System.out.print("Enter Amount: ");
        double amount = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Security Pin: ");
        String securityPin = sc.nextLine();


        if (senderAccountNumber.equals(receiverAccountNumber)) {
            System.out.println("You shouldn't send money to the same account.");
            try {
                accounts.selectAction(senderAccountNumber);
            } catch (SQLException e) {
                System.out.println("Failed to send money "+ e.getMessage());
            }
            ;
        }

        try {
            connection.setAutoCommit(false);


            if (!validateSender(senderAccountNumber, securityPin)) {
                System.out.println("Invalid Security Pin or Account Details!");
                connection.rollback();
                accounts.selectAction(senderAccountNumber);
            }

            if (!performTransaction(senderAccountNumber, receiverAccountNumber, amount)) {
                connection.rollback();
                accounts.selectAction(senderAccountNumber);
            }

            connection.commit();
            System.out.println("Transaction Successful! " + amount + " $ Transferred Successfully.");
            accounts.selectAction(senderAccountNumber);
        } catch (SQLException e) {
            System.err.println("Transaction Failed: " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Failed to rollback: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Failed to reset auto-commit: " + e.getMessage());
            }
        }
    }


    private boolean validateSender(Long senderAccountNumber, String securityPin) throws SQLException {
        String query = "SELECT * FROM accounts WHERE account_number = ? AND security_pin = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setLong(1, senderAccountNumber);
            statement.setString(2, securityPin);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    double balance = rs.getDouble("balance");
                    if (balance < 0) {
                        System.out.println("Insufficient Balance!");
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }


    private boolean performTransaction(Long senderAccountNumber, Long receiverAccountNumber, double amount) throws SQLException {
        String debitQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

        try (
                PreparedStatement debitStatement = connection.prepareStatement(debitQuery);
                PreparedStatement creditStatement = connection.prepareStatement(creditQuery)
        ) {
            debitStatement.setDouble(1, amount);
            debitStatement.setLong(2, senderAccountNumber);
            creditStatement.setDouble(1, amount);
            creditStatement.setLong(2, receiverAccountNumber);

            int debitResult = debitStatement.executeUpdate();
            int creditResult = creditStatement.executeUpdate();

            if (debitResult > 0 && creditResult > 0) {
                return true;
            } else {
                System.out.println("Invalid account number(s). Transaction Failed!");
                return false;
            }
        }
    }

    public void check_balance (Long number)throws SQLException {
            Accounts accounts = new Accounts(connection, sc);
            sc.nextLine();
            System.out.println("Enter security Pin");
            String sec = sc.nextLine();
            String query = "SELECT *FROM accounts WHERE account_number = ? AND security_pin = ? ";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, number);
            statement.setString(2, sec);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                System.out.println("Balance : " + balance);
                accounts.selectAction(number);
            } else {
                System.out.println("Invalid Pin!");
                check_balance(number);
            }


        }

    }

