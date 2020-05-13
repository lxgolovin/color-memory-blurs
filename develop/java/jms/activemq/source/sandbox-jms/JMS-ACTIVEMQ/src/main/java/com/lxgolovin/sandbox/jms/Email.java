package com.lxgolovin.sandbox.jms;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Email {
    private final LocalDate localDate;
    private final String name;
    private final String to;
}
