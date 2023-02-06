package com.github.merelysnow.vips.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Reward {

    private final String group;
    private List<String> commands;
}
