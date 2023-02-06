package com.xinyu.webapp.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "UserTB")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AppUser {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String first_name;
  private String last_name;
  private String password;

  private String username;

  @Column(nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  @JsonProperty("account_created")
  private Date account_created = new Date();

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  @LastModifiedDate
  @JsonProperty("account_update")
  private Date account_update = new Date();

  @JsonIgnore
  public String getPassword() {
    return password;
  }

  @JsonProperty
  public void setPassword(String password) {
    this.password = password;
  }
}
