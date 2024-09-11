package com.restapi.point.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="point_history")
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="USER_ID")
    private long userId;
    private long point;
    private String type;


    public PointHistory(long userId , long point , String type) {
        this.userId = userId;
        this.point = point;
        this.type = type;
    }

}
