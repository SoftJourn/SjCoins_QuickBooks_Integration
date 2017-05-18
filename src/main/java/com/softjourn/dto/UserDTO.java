package com.softjourn.dto;

import lombok.Data;


@Data
public class UserDTO {
    private final String ldapId;
    private final String fullName;
    private final String email;
}
