package com.xinyu.webapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

@Entity
@Table(name = "ProductTB")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AppProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    private String description;
    private String sku;
    private String manufacturer;

    @Min(value = 0, message = "Quantity should not be less than 0")
    @Max(value = 100, message = "Quantity should not be greater than 100")
    private Integer quantity;

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @JsonProperty("date_added")
    private Date date_added = new Date();

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @JsonProperty("date_last_updated")
    private Date date_last_updated = new Date();

    private Integer owner_user_id;
}
