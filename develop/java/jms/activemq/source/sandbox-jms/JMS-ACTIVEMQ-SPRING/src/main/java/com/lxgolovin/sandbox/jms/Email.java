package com.lxgolovin.sandbox.jms;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Email {

    private Date date;

    private String name;

    private String to;
}
