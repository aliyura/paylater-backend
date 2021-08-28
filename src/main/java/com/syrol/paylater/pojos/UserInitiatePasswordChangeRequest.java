package com.syrol.paylater.pojos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInitiatePasswordChangeRequest<T> {
    private String username;
}
