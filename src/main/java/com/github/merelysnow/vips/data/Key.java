package com.github.merelysnow.vips.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@AllArgsConstructor
@Data
public final class Key {

    private String id;
    private final String group;
    private Instant instant;

}
