package com.taskflow_api.user;

import com.taskflow_api.shared.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SQLRestriction("deleted = false")
@Table(name = "users")
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private boolean isVerified = false;

    @Column(nullable = false)
    private boolean isActive = true;

    public static User create(String email, String password,
                              String firstName, String lastName) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.firstName = firstName;
        user.lastName = lastName;
        return user;
    }
}
