package Classes;

import java.sql.*;
import java.util.Scanner;

public class Accounts {


    private final Connection connection;
    private final Scanner sc;

    Accounts(Connection connect, Scanner scanner) {
        this.connection = connect;
        this.sc = scanner;
    }

    public Long open_accounts(String email)  {
        if (!emailExists(email)) {
            sc.nextLine();
            System.out.print("Enter Full Name : ");
            String name = sc.nextLine();
            System.out.print("Enter Initial Amount : ");
            int initial = sc.nextInt();
            System.out.print("Enter Security Pin : ");
            int sec = sc.nextInt();

            long accountNumber = generateAccountNumber();
            String query = "INSERT INTO accounts (account_number,full_name,email,balance,security_pin) VALUES(?,?,?,?,?)";

            try (
                    PreparedStatement statement = connection.prepareStatement(query);
                    ){
                statement.setLong(1, accountNumber);
                statement.setString(2, name);
                statement.setString(3, email);
                statement.setInt(4, initial);
                statement.setInt(5, sec);

                statement.execute();
                System.out.println("Account Created Successfully");
                System.out.println("Your Account Number is " + accountNumber);

                return accountNumber;
            }catch (SQLException e){
                System.out.println(e.getMessage());
            }}

        System.out.println("You have already an account");
        System.out.println();
        usingExistsAccount();

        return 0L;

    }

    private long generateAccountNumber() {
        try (
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery("SELECT account_number from accounts ORDER BY account_number DESC LIMIT 1");
        ) {
            if (rs.next()) {
                long last_account_number = rs.getLong("account_number");
                return last_account_number + 1;
            } else {
                return 100010;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    private boolean emailExists(String email)  {
        String query = "SELECT *FROM accounts WHERE email = ?";

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query);
            ){
            preparedStatement.setString(1, email);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
         return false;
    }


    public void usingExistsAccount() {
        sc.nextLine();
        System.out.println("Enter your Account Number : ");
        long number = sc.nextLong();
        System.out.println("Enter your Security Pin : ");
        int pin = sc.nextInt();

        String query = "SELECT *FROM accounts WHERE account_number = ? AND security_pin= ?";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, number);
            statement.setInt(2, pin);

            ResultSet re = statement.executeQuery();
            if (re.next()) {
                selectAction(number);
            } else {
                System.out.println("Incorrect Account or Password!");
                System.out.println();
                usingExistsAccount();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectAction(Long accountNumber) throws SQLException {
        AccountManager account = new AccountManager(connection, sc);


        System.out.println();
        System.out.println("1. Debit Money ");
        System.out.println("2. Credit Money");
        System.out.println("3. Transfer Money");
        System.out.println("4. Check Balance");
        System.out.println("5. Log Out");
        System.out.println();
        System.out.println("Enter your choice : ");
        int choice = sc.nextInt();


        switch (choice) {
            case 1:
                account.debit_money(accountNumber);
                break;
            case 2:
                account.credit_money(accountNumber);
                break;
            case 3:
                account.transfer_money(accountNumber);
                break;
            case 4:
                account.check_balance(accountNumber);
                break;
            case 5:
                return;
            default:
                System.out.println("Enter Valid Choice");
                break;
        }
    }


    public void deleteAccount(String email) {
        System.out.println("Are you sure to delete your account (y/n) ");
        char choice = sc.next().charAt(0);
        if (choice == 'Y' || choice == 'y'){
            String deleteUser = "DELETE FROM user WHERE email= ?";
            String deleteAccount = "DELETE FROM accounts WHERE email = ? ";

            try(
                    PreparedStatement userStatement = connection.prepareStatement(deleteUser);
                    PreparedStatement accountStatement = connection.prepareStatement(deleteAccount);

            ){
                userStatement.setString(1, email);
                accountStatement.setString(1, email);
                userStatement.executeUpdate();
                int userRowsAffected = userStatement.executeUpdate();
                int accountRowsAffected = accountStatement.executeUpdate();
                if (userRowsAffected > 0 || accountRowsAffected > 0) {
                    System.out.println("Your account has been deleted.");
                    System.out.println();
                    System.out.println("Thank you for using our app!");
                } else {
                    System.out.println("No account found with the provided email.");
                }
            }catch (SQLException e) {
                System.err.println("An error occurred while deleting the account: " + e.getMessage());
            }
        }else {
            System.out.println("Account deletion canceled.");

        }

    }
}