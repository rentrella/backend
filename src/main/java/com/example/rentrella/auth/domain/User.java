package com.example.rentrella.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "student_number", length = 20)
    private String studentNumber;

    @Column(name = "is_banned", nullable = false)
    private boolean banned;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UserRole role;

    @Column(name = "data_gsm_id", length = 100)
    private String dataGsmId;

    @Column(name = "has_borrowed_umbrella", nullable = false)
    private boolean hasBorrowedUmbrella;

    @Builder
    private User(String email, String password, String name, String studentNumber, String dataGsmId, UserRole role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.studentNumber = studentNumber;
        this.dataGsmId = dataGsmId;
        this.role = role == null ? UserRole.USER : role;
        this.banned = false;
        this.hasBorrowedUmbrella = false;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}
