package org.bitkernel.cache.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "account_info")
public class AccountInfo implements Serializable {
    private static final long serialVersionUID = 3826906852679556530L;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "email", length = 30)
    private String email;

    @Column(name = "password", length = 32)
    private String password;

    @Column(name = "valid_date")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime validDate;

    @Column(name = "status", columnDefinition = "int default 0")
    private int status;

    @Column(name = "create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime create_time;

    @Column(name = "change_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime change_time;

    @Column(name = "text", length = 2048)
    private String text;
}
