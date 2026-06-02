package com.nexus.cxm.exception;

import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.ErrorClassification;
import graphql.kickstart.execution.error.GraphQLErrorHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GraphQLExceptionHandler implements GraphQLErrorHandler {

    public enum CustomErrorType implements ErrorClassification {
        NOT_FOUND,
        BAD_REQUEST,
        FORBIDDEN,
        INTERNAL_ERROR
    }

    @Override
    public List<GraphQLError> processErrors(List<GraphQLError> errors) {
        return errors.stream().map(this::getOrMapError).collect(Collectors.toList());
    }

    private GraphQLError getOrMapError(GraphQLError error) {
        if (error instanceof ExceptionWhileDataFetching) {
            ExceptionWhileDataFetching dataFetchingError = (ExceptionWhileDataFetching) error;
            Throwable ex = dataFetchingError.getException();

            if (ex instanceof ResourceNotFoundException) {
                return GraphqlErrorBuilder.newError()
                        .message(ex.getMessage())
                        .errorType(CustomErrorType.NOT_FOUND)
                        .locations(dataFetchingError.getLocations())
                        .path(dataFetchingError.getPath())
                        .build();
            } else if (ex instanceof BusinessValidationException) {
                return GraphqlErrorBuilder.newError()
                        .message(ex.getMessage())
                        .errorType(CustomErrorType.BAD_REQUEST)
                        .locations(dataFetchingError.getLocations())
                        .path(dataFetchingError.getPath())
                        .build();
            } else if (ex instanceof AccessDeniedException) {
                return GraphqlErrorBuilder.newError()
                        .message(ex.getMessage())
                        .errorType(CustomErrorType.FORBIDDEN)
                        .locations(dataFetchingError.getLocations())
                        .path(dataFetchingError.getPath())
                        .build();
            }
            return GraphqlErrorBuilder.newError()
                    .message("An internal server error occurred")
                    .errorType(CustomErrorType.INTERNAL_ERROR)
                    .locations(dataFetchingError.getLocations())
                    .path(dataFetchingError.getPath())
                    .build();
        }
        return error;
    }
}
