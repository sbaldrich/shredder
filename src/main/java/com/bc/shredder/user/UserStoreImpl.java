package com.bc.shredder.user;

import com.bc.shredder.store.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserStoreImpl implements UserStore {
    private static final Logger log = LoggerFactory.getLogger(UserStoreImpl.class);
    private static final String insertStatement = "INSERT INTO appuser(handle) VALUES (?) RETURNING userid";
    private static final String selectStatement = "SELECT userid, handle FROM appuser WHERE userid = ?";
    private final DataManager dataManager;

    public UserStoreImpl(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @Override
    public User save(User user) {
        try (Connection connection = dataManager.getConnection()){
            PreparedStatement statement = connection.prepareStatement(insertStatement);
            statement.setString(1, user.getHandle());
            ResultSet rs = statement.executeQuery();
            rs.next();
            return new User(rs.getLong(1), user.getHandle());
        } catch (SQLException ex) {
            log.warn("Couldn't store user", ex);
            return null;
        }
    }

    @Override
    public User fetch(long id) {
        try (Connection connection = dataManager.getConnection(id)){
            PreparedStatement statement = connection.prepareStatement(selectStatement);
            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();
            rs.next();
            return new User(rs.getLong(1), rs.getString(2));
        } catch (SQLException ex) {
            log.warn("Couldn't find user", ex);
            return null;
        }
    }
}
