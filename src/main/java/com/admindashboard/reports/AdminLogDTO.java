package com.admindashboard.reports;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AdminLogDTO {

    private Long logId;

    @NotBlank(message = "Action cannot be blank")
    private String action;

    @NotBlank(message = "Admin name cannot be blank")
    private String adminName;

    private LocalDateTime timestamp;

}
