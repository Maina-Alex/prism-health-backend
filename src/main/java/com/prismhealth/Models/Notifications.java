package com.prismhealth.Models;

import lombok.Data;

import java.util.List;

@Data
public class Notifications {
    private int counter;
    private List<Notice> notices;
}
