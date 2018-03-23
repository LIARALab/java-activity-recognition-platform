package org.domus.api.data.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.hibernate.annotations.Immutable;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

@Entity
@Table(name = "states")
@Inheritance(strategy = InheritanceType.JOINED)
@Immutable
public class State
{
  private int  identifier;
  private Date creationDate;
  private Date updateDate;
  private Date deletionDate;
  private Date date;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public int getIdentifier () {
    return this.identifier;
  }

  public void setIdentifier (int identifier) {
    this.identifier = identifier;
  }

  @Column(name = "created_at")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public Date getCreationDate () {
    return this.creationDate;
  }

  public void setCreationDate (@NonNull final Date creationDate) {
    this.creationDate = creationDate;
  }

  @Column(name = "updated_at")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public Date getUpdateDate () {
    return this.updateDate;
  }

  public void setUpdateDate (@NonNull final Date updateDate) {
    this.updateDate = updateDate;
  }

  @Column(name = "deleted_at")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public Date getDeletionDate () {
    return this.deletionDate;
  }

  public void setDeletionDate (@NonNull final Date deletionDate) {
    this.deletionDate = deletionDate;
  }

  @Column(name = "date")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
  public Date getDate () {
    return this.date;
  }

  public void setDate (@NonNull final Date date) {
    this.date = date;
  }
}
