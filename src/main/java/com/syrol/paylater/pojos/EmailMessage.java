package com.syrol.paylater.pojos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class EmailMessage implements Serializable {

    private static final long serialVersionUID = -295422703255886286L;
    protected String body;
    protected String subject;
    protected String recipient;
    protected String recipientName;
}