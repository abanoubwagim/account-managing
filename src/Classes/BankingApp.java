package Classes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class BankingApp {

    private static String url = "jdbc:mysql://localhost:3306/banking_system";
    private static String driverName = "com.mysql.cj.jdbc.Driver";
    private static String name = "root";
    private static String password = "";
    public static void main(String[] args)  {


        try(
                Connection connection = DriverManager.getConnection(url,name,password);
                Scanner sc = new Scanner(System.in);
           )
        {
            Class.forName(driverName);
            User user = new User(connection,sc);
            Accounts accounts = new Accounts(connection,sc);

            while(true){
                System.out.println("*** WELCOME TO BANKING SYSTEM ***");
                System.out.println();
                System.out.println("1. Register");
                System.out.println("2. Login");
                System.out.println("3. Exit");
                System.out.println("Enter your choice: ");
                int choice = sc.nextInt();
                switch(choice){
                    case 1:
                        user.register();
                        break;
                    case 2 :
                        String email = user.login();
                        Long accountNumber = 0L;
                        if (email!= null){

                            System.out.println();
                            System.out.println("Classes.User Logged In!");
                            System.out.println();
                            System.out.println("1. Open a new bank account ");
                            System.out.println("2. Using an existing account ");
                            System.out.println("3. Delete My account");
                            System.out.println("4. Exit ");
                            int in = sc.nextInt();
                            switch (in){
                                case 1 :
                                     accountNumber = accounts.open_accounts(email);
                                    accounts.selectAction(accountNumber);
                                    break;
                                case 2:
                                    accounts.usingExistsAccount();
                                    break;
                                case 3:
                                    accounts.deleteAccount(email);
                                    break;
                                case 4:
                                    return;
                                default:
                                    System.out.println("your choice is uncorrected ");
                                    break;
                            }
                        }else {
                            System.out.println("Incorrect Email or Password!");
                        }
                        break;
                    case 3 :
                        System.out.println("THANK YOU FOR USING BANKING SYSTEM");
                        System.out.println("Exiting System");
                        return;
                    default:
                        System.out.println("Enter Valid Choice");
                        break;}

            }
        } catch (ClassNotFoundException | SQLException e  ) {
            System.out.println(e.getMessage());
        }


    }
}