package com.prismhealth.exception;

import com.prismhealth.util.EntityType;
import com.prismhealth.util.ExceptionType;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Optional;

@Component
public class PrismHealthExceptions {

    public static class UserNotFoundException extends RuntimeException{
        public UserNotFoundException(String message, Throwable cause){
            super(message,cause);
        }
    }
    public static class SignUpFailedException extends RuntimeException{
        public SignUpFailedException(String message, Throwable cause){
            super(message,cause);
        }
    }

    public PrismHealthExceptions() {
    }

        public static RuntimeException throwException(String messageTemplate, String... args) {
            return new RuntimeException(format(messageTemplate, args));
        }

        public static RuntimeException throwException(EntityType entityType, ExceptionType exceptionType, String... args) {
            String messageTemplate = getMessageTemplate(entityType, exceptionType);
            return throwException(exceptionType, messageTemplate, args);
        }

        public static RuntimeException throwExceptionWithId(EntityType entityType, ExceptionType exceptionType, String id, String... args) {
            String messageTemplate = getMessageTemplate(entityType, exceptionType).concat(".").concat(id);
            return throwException(exceptionType, messageTemplate, args);
        }

        public static RuntimeException throwExceptionWithTemplate(EntityType entityType, ExceptionType exceptionType, String messageTemplate, String... args) {
            return throwException(exceptionType, messageTemplate, args);
        }

        public static class EntityNotFoundException extends RuntimeException {
            public EntityNotFoundException(String message) {
                super(message);
            }
        }

        public static class DuplicateEntityException extends RuntimeException {
            public DuplicateEntityException(String message) {
                super(message);
            }
        }

        private static RuntimeException throwException(ExceptionType exceptionType, String messageTemplate, String... args) {
            if (ExceptionType.ENTITY_NOT_FOUND.equals(exceptionType)) {
                return new EntityNotFoundException(format(messageTemplate, args));
            } else if (ExceptionType.DUPLICATE_ENTITY.equals(exceptionType)) {
                return new DuplicateEntityException(format(messageTemplate, args));
            }
            return new RuntimeException(format(messageTemplate, args));
        }

        private static String getMessageTemplate(EntityType entityType, ExceptionType exceptionType) {
            return entityType.name().concat(".").concat(exceptionType.getValue()).toLowerCase();
        }

        private static String format(String template, String... args) {
            Optional<String> templateContent = Optional.ofNullable("error");
            if (templateContent.isPresent()) {
                return MessageFormat.format(templateContent.get(), args);
            }
            return String.format(template, args);
        }


}
