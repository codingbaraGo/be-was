package app.handler;

import app.db.ArticleRepository;
import app.db.CommentRepository;
import app.db.UserRepository;
import app.model.Article;
import app.model.Comment;
import app.model.User;
import exception.ErrorCode;
import exception.ErrorException;
import exception.ServiceException;
import http.HttpMethod;
import http.HttpStatus;
import web.dispatch.argument.QueryParameters;
import web.handler.SingleArgHandler;
import web.response.DynamicViewResponse;
import web.response.HandlerResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeHandler extends SingleArgHandler<QueryParameters> {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public HomeHandler(ArticleRepository articleRepository, UserRepository userRepository, CommentRepository commentRepository) {
        super(HttpMethod.GET, "/");
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public HandlerResponse handle(QueryParameters params) {
        Article article;
        List<Article> articleList = articleRepository.findAll();
        DynamicViewResponse response =
                DynamicViewResponse.of(HttpStatus.OK, "/index.html");
        if (params.getQueryValue("articleId").isPresent()) {
            article = articleRepository.findById(
                    Long.parseLong(params.getQueryValue("articleId").get()))
                    .orElseThrow(
                            () -> new ServiceException(ErrorCode.NO_SUCH_RESOURCE));
        } else {
            if (articleList.isEmpty()) {
                response.addModel("article", null);
                return response;
            }
            article = articleList.get(articleList.size() - 1);
        }
        User writer = userRepository.findById(article.getWriterId())
                .orElseThrow(
                        ()-> new ErrorException("Writer not exists"));

        List<Comment> commentList = commentRepository.findByColumn("articleId", article.getId());
        response.addModel("commentCount", commentList.size());
        if (params.getQueryValue("fullComments").isEmpty()
                || params.getQueryValue("fullComments").get().equals("false")) {
            if(commentList.size()>3) {
                response.addModel("hasMoreComments", true);
                response.addModel("additionalComments", commentList.size()-3);
                commentList = commentList.subList(0, 3);
            } else{
                response.addModel("hasMoreComments", false);
            }
        }

        response.addModel("article", article);
        response.addModel("writerNickname", writer.getNickname());
        response.addModel("commentList", toCommentDtoList(commentList));
        response.addModel("next", article.getId() < articleList.size()
                            ? article.getId() + 1 : false);
        response.addModel("prev", article.getId() > 1
                            ? article.getId()-1 : false);

        return response;
    }

    private List<Map<String, Object>> toCommentDtoList(List<Comment> commentList){
        List<Map<String, Object>> commentDtoList = new ArrayList<>();
        for (Comment comment : commentList) {
            Map<String, Object> commentDto = new HashMap<>();
            commentDto.put("writerId", comment.getWriterId());
            commentDto.put("content", comment.getContent());
            String nickname = userRepository.findById(comment.getWriterId()).orElseThrow(
                            () -> new ErrorException("Comment Writer Id Not Exists"))
                    .getNickname();
            commentDto.put("nickname", nickname);
            commentDtoList.add(commentDto);
        }
        return commentDtoList;
    }
}
