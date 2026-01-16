package app.db;

import app.model.Comment;
import database.ConnectionManager;
import database.CrudRepository;

public class CommentRepository extends CrudRepository<Comment> {
    public CommentRepository(ConnectionManager connectionManager) {
        super(connectionManager, Comment.class);
    }
}
