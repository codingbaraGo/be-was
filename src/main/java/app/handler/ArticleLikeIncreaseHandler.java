package app.handler;

import app.db.ArticleRepository;
import app.model.Article;
import exception.ErrorCode;
import exception.ServiceException;
import http.HttpMethod;
import web.dispatch.argument.QueryParameters;
import web.handler.SingleArgHandler;
import web.response.HandlerResponse;
import web.response.RedirectResponse;

public class ArticleLikeIncreaseHandler extends SingleArgHandler<QueryParameters> {
    private final ArticleRepository articleRepository;
    public ArticleLikeIncreaseHandler(ArticleRepository articleRepository) {
        super(HttpMethod.POST, "/like");
        this.articleRepository = articleRepository;
    }

    @Override
    public HandlerResponse handle(QueryParameters params) {
        Article article = articleRepository.findById(
                Long.parseLong(params.getValidQueryValue("articleId"))).orElseThrow(
                        () -> new ServiceException(ErrorCode.NO_SUCH_RESOURCE));
        article.increaseLikeCount();
        articleRepository.update(article);
        return RedirectResponse.to("/?articleId=" + article.getId());
    }
}
