package com.roommate.roommate.application.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApplicationList {
    private List<ApplicationListDto> applications;
}
