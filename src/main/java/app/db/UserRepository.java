package app.db;

import app.model.User;
import database.ConnectionManager;
import database.CrudRepository;

public class UserRepository extends CrudRepository<User> {
    public UserRepository(ConnectionManager connectionManager) {
        super(connectionManager, User.class);
    }
}
