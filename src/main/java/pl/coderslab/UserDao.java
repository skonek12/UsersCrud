package pl.coderslab;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Arrays;


public class UserDao {

    public User user;
    private static final String CREATE_USER_QUERY =
            "INSERT INTO users(user_email, user_name, password) VALUES (?, ?, ?)";

    private static final String UPDATE_All_USER_DATA =
            "UPDATE users SET user_email = ?, user_name = ? ,password = ? WHERE id = ?";

    private static final String GET_ALL_USER_DATA =
            "SELECT * FROM users WHERE id = ?";

    private static final String GET_ALL_DB_DATA =
            "SELECT * FROM users";
    private static final String GET_ALL_IDS =
            "SELECT id FROM users";


    // arguments: id
    private static final String DELETE_QUERY =
            "DELETE FROM users where id = ?";


    public static User create(User user) {

        try (Connection conn = DbUtil.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUserName());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.executeUpdate();

            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static User read(int userId) {
        User user1 = new User();
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(GET_ALL_USER_DATA);
            statement.setString(1, String.valueOf(userId));
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user1.setId(resultSet.getInt(1));
                user1.setEmail(resultSet.getString(2));
                user1.setUserName(resultSet.getString(3));
                user1.setPassword(resultSet.getString(4));
            }
            return user1;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void update(User user) {

        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(UPDATE_All_USER_DATA);
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUserName());
            statement.setString(3, hashPassword(user.getPassword()));
            statement.setString(4, String.valueOf(user.getId()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        }

    }

    public static void delete(int userId) {

        try (Connection conn = DbUtil.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement(DELETE_QUERY)) {
                statement.setInt(1, userId);
                statement.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private static User[] addToArray(User u, User[] users) {
        User[] tmpUsers = Arrays.copyOf(users, users.length + 1); // Tworzymy kopię tablicy powiększoną o 1.
        tmpUsers[users.length] = u; // Dodajemy obiekt na ostatniej pozycji.
        return tmpUsers; // Zwracamy nową tablicę.
    }

    public static User[] findAll() {
        User[] users = new User[0];
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(GET_ALL_DB_DATA);

            ResultSet countOfSet = statement.executeQuery();
            int counter = 0;
            while (countOfSet.next()) {
                counter++;
            }
            int[] numberOfIDs = new int[counter];
            PreparedStatement getThoseIDs = conn.prepareStatement(GET_ALL_IDS);
            ResultSet countOfIDs = getThoseIDs.executeQuery();
//            while (countOfIDs.next()) {
            for (int i = 0; i < counter; i++) {
                numberOfIDs[i] = countOfIDs.getInt(1);
            }
//            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                for (int i = 0; i < counter; i++) {
                    int id = 0;

                    users[i] = read(numberOfIDs[i]);


                    addToArray(users[i], users);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return users;
    }


    private static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


    private static int countOfIDs() {
        try (Connection conn = DbUtil.getConnection()) {
            PreparedStatement statement = conn.prepareStatement(GET_ALL_IDS);
            ResultSet countOfSet = statement.executeQuery();
            int counter = 0;
            while (countOfSet.next()) {
                counter++;
            }
            return counter;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static int[] arrayOfIDs(){ //// tbd
        int[] arr = new int[0];
        return arr;
    }

}
