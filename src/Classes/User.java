package Classes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class User {


    private final Scanner sc ;
    private final Connection connection;

    User(Connection connect , Scanner scanner) {
        this.connection = connect;
        this.sc = scanner;
    }
    public void register() throws SQLException {
        sc.nextLine();
        System.out.print("Full Name: ");
        String name = sc.nextLine();
        System.out.print("Email: ");
        String email = sc.nextLine();
        System.out.print("Password:");
        String password = sc.nextLine();
        if (user_exist(email)){
            System.out.println("Classes.User Already Exists for this Email Address!");
            return;
        }
        String query = "INSERT INTO user (full_name,email,password) values (?,?,?)";
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(query);
            ){
            preparedStatement.setString(1,name);
            preparedStatement.setString(2,email);
            preparedStatement.setString(3,password);

            preparedStatement.execute();

        } catch (SQLException e ) {
            System.out.println(e.getMessage());
        }

    }

    public String login() throws SQLException{

        sc.nextLine();
        System.out.print("Email : ");
        String email = sc.nextLine();
        System.out.print("Password : ");
        String password = sc.nextLine();

        String query = "select *from user where email = ? and password = ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1,email);
        statement.setString(2,password);
        ResultSet rs = statement.executeQuery();
        if (rs.next()){
            return email;
        }else {
           return null;
        }

    }

    public boolean user_exist(String email) throws SQLException{

        String query = "Select *from user where email =?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1,email);
        ResultSet rs = preparedStatement.executeQuery();
        return rs.next();
    }



}
