package bsu.tp.financial.dao.impl;

import bsu.tp.financial.connection.ConnectionDB;
import bsu.tp.financial.connectionPool.Connector;
import bsu.tp.financial.dao.BankAccountDAO;
import bsu.tp.financial.dao.DAOFactory;
import bsu.tp.financial.dao.UserDAO;
import bsu.tp.financial.entity.User;
import bsu.tp.financial.exception.DAOException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLUserDAO implements UserDAO {

    //DAOFactory daoFactory = DAOFactory.getInstance();
    //BankAccountDAO bankAccountDAO = new SQLBankAccountDAO();



    private static final String FIND_USER_BY_ID = "SELECT * FROM user WHERE id = ?";
    private static final String FIND_USER_BY_EMAIL = "SELECT * FROM user WHERE email = ?";
    private static final String CREATE_USER = "INSERT INTO user (name, surname, email, password) VALUES(?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE user SET name = ?, surname = ?, email = ?, password = ?  WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM user WHERE id = ?";
    private static final String FIND_ALL_USER = "SELECT * FROM user ORDER BY surname";


    @Override
    public User findUserById(int id) throws DAOException {
        //Connection connection = ConnectionDB.getConnection();
        Connection connection = Connector.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_ID);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return setUser(resultSet);
            }
            return null;
        } catch (SQLException exception) {
            throw new DAOException("Failed findUserById", exception);
        } finally {
            Connector.releaseConnection(connection);
        }
    }

    @Override
    public User findUserByEmail(String email) throws DAOException {
        //Connection connection = ConnectionDB.getConnection();
        Connection connection = Connector.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_USER_BY_EMAIL);
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return setUser(resultSet);
            }
            return null;
        } catch (SQLException exception) {
            throw new DAOException("Failed findUserByEmail", exception);
        } finally {
            Connector.releaseConnection(connection);
        }
    }

    @Override
    public List<User> findAllUsers() throws DAOException {
        List<User> users = new ArrayList<>();
        //Connection connection = ConnectionDB.getConnection();
        Connection connection = Connector.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_USER);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                users.add(setUser(resultSet));
            }
        } catch (SQLException exception) {
            throw new DAOException("Failed findAllUsers", exception);
        } finally {
            Connector.releaseConnection(connection);
        }
        return users;
    }

    @Override
    public User createUser(User user) throws DAOException {
        //Connection connection = ConnectionDB.getConnection();
        Connection connection = Connector.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getSurname());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, String.valueOf(user.getPassword()));
            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
                return user;
            }
            return null;
        } catch (SQLException exception) {
            throw new DAOException("Failed createUser", exception);
        } finally {
            Connector.releaseConnection(connection);
        }
    }

    @Override
    public void updateUser(User user) throws DAOException {
        //Connection connection = ConnectionDB.getConnection();
        Connection connection = Connector.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getSurname());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, String.valueOf(user.getPassword()));
            preparedStatement.setInt(5, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DAOException("Failed updateUser", exception);
        } finally {
            Connector.releaseConnection(connection);
        }
    }

    @Override
    public void deleteUser(User user) throws DAOException {
        //Connection connection = ConnectionDB.getConnection();
        Connection connection = Connector.getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            throw new DAOException("Failed deleteUser", exception);
        } finally {
            Connector.releaseConnection(connection);
        }
    }


    private User setUser(ResultSet resultSet) throws DAOException {
        DAOFactory daoFactory = DAOFactory.getInstance();
        BankAccountDAO bankAccountDAO = daoFactory.getBankAccountDAO();
        User user;
        try {
            user = new User();
            user.setId(resultSet.getInt("id"));
            user.setName(resultSet.getString("name"));
            user.setSurname(resultSet.getString("surname"));
            user.setEmail(resultSet.getString("email"));
            user.setPassword(resultSet.getString("password").toCharArray());
            user.setBankAccountList(bankAccountDAO.findBankAccountsByUserId(user.getId()));
        } catch (SQLException exception) {
            throw new DAOException("Failed setUser", exception);
        }
        return user;
    }
}
