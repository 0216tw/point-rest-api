package com.restapi.point.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor //JPA는 기본생성자를 사용한다
@Entity
@Table(name="users")
public class User {

    @Id
    @Column(name="USER_ID")
    private long userId;
    private long point;


}

