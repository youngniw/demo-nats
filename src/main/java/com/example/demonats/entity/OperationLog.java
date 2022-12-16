package com.example.demonats.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Table
@Entity
@EntityListeners(AuditingEntityListener.class)
public class OperationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long logId;

    // 아래의 주석은 cascade 를 통해 Car 먼저 저장 후에 OperationLog 가 저장되게 함
    // @ManyToOne(cascade = CascadeType.PERSIST)
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "car_id")
    private Car car;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime logTime;

    @Column
    private Double longitude;

    @Column
    private Double latitude;

    @Column
    private Integer gear;
}
