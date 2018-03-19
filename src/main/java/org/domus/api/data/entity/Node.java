package org.domus.api.data.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;

import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

@Entity
@Table(name="nodes")
public class Node {
  private int identifier;
  private Date creationDate;
  private Date updateDate;
  private Date deletionDate;
  private String name;
  private int start;
  private int end;

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  public int getIdentifier () {
    return this.identifier;
  }

  public void setIdentifier (final int identifier) {
    this.identifier = identifier;
  }

  @Column(name = "created_at")
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
  public Date getCreationDate () {
    return this.creationDate;
  }

  public void setCreationDate (@NonNull final Date creationDate) {
    this.creationDate = creationDate;
  }

  @Column(name = "updated_at")
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
  public Date getUpdateDate () {
    return this.updateDate;
  }

  public void setUpdateDate (@NonNull final Date updateDate) {
    this.updateDate = updateDate;
  }

  @Column(name = "deleted_at")
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
  public Date getDeletionDate () {
    return this.deletionDate;
  }

  public void setDeletionDate (@NonNull final Date deletionDate) {
    this.deletionDate = deletionDate;
  }

  @Column(name = "name")
  public String getName () {
    return this.name;
  }

  public void setName (@NonNull final String name) {
    this.name = name;
  }

  @Column(name = "start")
  public int getStart () {
    return this.start;
  }

  public void setStart (final int start) {
    this.start = start;
  }

  @Column(name = "end")
  public int getEnd () {
    return this.end;
  }

  public void setEnd (final int end) {
    this.end = end;
  }
}
