package com.starking.hubsport.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateContactRequest {
    private String firstname;
    private String lastname;
    private String email;
}
