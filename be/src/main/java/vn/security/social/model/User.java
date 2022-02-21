package vn.security.social.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(generator = "idUser")
    @GenericGenerator(
            name = "idUser",
            parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "U"),
            strategy = "vn.security.social.util.IdGenerator"
    )
    private String id;

    private String fullName;

    private String email;

    private String image;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @OneToOne(cascade = CascadeType.ALL)
    private Account account;
}