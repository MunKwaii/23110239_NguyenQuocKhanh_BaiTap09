package vn.iostar.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.ConstraintViolationException;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

@Component
public class GlobalExceptionHandler extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {

        if (ex instanceof MethodArgumentNotValidException e) {
            String message = e.getBindingResult()
                    .getAllErrors()
                    .get(0)
                    .getDefaultMessage();
            return GraphqlErrorBuilder.newError(env)
                    .message(message)
                    .build();
        }

        if (ex instanceof ConstraintViolationException e) {
            String message = e.getConstraintViolations()
                    .iterator()
                    .next()
                    .getMessage();
            return GraphqlErrorBuilder.newError(env)
                    .message(message)
                    .build();
        }

        return null;
    }
}
