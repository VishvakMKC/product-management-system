package com.vish.pms.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(UUID id){
        super("User not found with the id : " + id);
    }

}
