package com.mauricio.gastos.exceptions;

public class UserExceptions extends Exception{

    public UserExceptions(){
        super();
    }

    public UserExceptions(String message){
        super(message);
    }

    public static class UsernameExistException extends UserExceptions {
        public UsernameExistException(String message) {
            super(message);
        }
    }

    public static class EmailExistException extends UserExceptions {
        public EmailExistException(String message) {
            super(message);
        }
    }

    public static class UserNotFoundException extends UserExceptions {
        public UserNotFoundException(String message) {
            super(message);
        }
    }

    public static class TokenNotValidException extends Throwable {
        public TokenNotValidException(String message) {
            super(message);
        }
    }
}
