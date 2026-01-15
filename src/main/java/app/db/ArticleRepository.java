package app.db;

import app.model.Article;
import database.ConnectionManager;
import database.CrudRepository;

public class ArticleRepository extends CrudRepository<Article> {
    public ArticleRepository(ConnectionManager connectionManager) {
        super(connectionManager, Article.class);
    }
}
