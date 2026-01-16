package app.handler;

import app.db.CommentRepository;
import app.model.Comment;
import exception.ErrorCode;
import exception.ServiceException;
import http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import web.dispatch.argument.QueryParameters;
import web.filter.authentication.AuthenticationInfo;
import web.handler.DoubleArgHandler;
import web.response.HandlerResponse;
import web.response.RedirectResponse;

public class CreateCommentWithPost extends DoubleArgHandler<QueryParameters, AuthenticationInfo> {
    private static final Logger log = LoggerFactory.getLogger(CreateCommentWithPost.class);
    private final CommentRepository commentRepository;
    public CreateCommentWithPost(CommentRepository commentRepository) {
        super(HttpMethod.POST, "/comment/create");
        this.commentRepository = commentRepository;
    }

    @Override
    public HandlerResponse handle(QueryParameters params, AuthenticationInfo authInfo) {
        String content = params.getValidQueryValue("content");
        Long articleId = Long.parseLong(
                params.getValidQueryValue("articleId"));
        Long userId = authInfo.getUserId()
                .orElseThrow(
                        () -> new ServiceException(ErrorCode.UNAUTHORIZED));
        commentRepository.save(new Comment(articleId, userId, content));

        log.info("{}->{} - comment created", userId, articleId);

        return RedirectResponse.to("/?articleId=" + articleId);
    }
}
