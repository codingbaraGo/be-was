package app.handler;

import app.db.ArticleRepository;
import app.db.UserRepository;
import app.model.Article;
import app.model.User;
import exception.ErrorException;
import http.HttpMethod;
import http.HttpStatus;
import http.request.HttpRequest;
import web.handler.SingleArgHandler;
import web.response.DynamicViewResponse;
import web.response.HandlerResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HomeHandler extends SingleArgHandler<HttpRequest> {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    public HomeHandler(ArticleRepository articleRepository, UserRepository userRepository) {
        super(HttpMethod.GET, "/");
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    @Override
    public HandlerResponse handle(HttpRequest request) {
        List<Article> articleList = articleRepository.findAll();
        DynamicViewResponse response = DynamicViewResponse.of(HttpStatus.OK, "/index.html");
        if(articleList.isEmpty()) {
            response.addModel("hasContents", false);
        } else {
            Article last = articleList.get(articleList.size() - 1);
            User writer = userRepository.findById(last.getWriterId()).orElseThrow(
                    ()-> new ErrorException("Writer not exists"));
            response.addModel("hasContents", true);
            response.addModel("article", last);
            response.addModel("writerNickname", writer.getNickname());
        }
        return response;
    }
}
