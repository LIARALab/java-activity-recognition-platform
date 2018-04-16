package org.liara.api.data.entity;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.NonNull;

import com.fasterxml.jackson.annotation.JsonFormat;

@MappedSuperclass
public class ApplicationEntity
{
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "identifier", nullable = false, updatable = false, unique = true)
  private Long           _identifier;
  
  @Column(name = "created_at", nullable = false, updatable = false, unique = false)
  @ColumnDefault(value = "CURRENT_TIMESTAMP")
  @NonNull
  private ZonedDateTime _creationDate;

  @Column(name = "updated_at", nullable = false, updatable = true, unique = false)
  @ColumnDefault(value = "CURRENT_TIMESTAMP")
  @UpdateTimestamp
  @NonNull
  private ZonedDateTime _updateDate;

  @Column(name = "deleted_at", nullable = true, updatable = true, unique = false)
  @Nullable
  private ZonedDateTime _deletionDate;
  
  public long getIdentifier () {
    return _identifier.longValue();
  }
  
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS OOOO '['VV']'")
  public ZonedDateTime getCreationDate () {
    return _creationDate;
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS OOOO '['VV']'")
  public ZonedDateTime getDeletionDate () {
    return _deletionDate;
  }

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS OOOO '['VV']'")
  public ZonedDateTime getUpdateDate () {
    return _updateDate;
  }
  
  @PrePersist
  protected void onCreate() {
    _creationDate = ZonedDateTime.now();
  }
  
  @PreUpdate
  protected void onUpdate () {
    _updateDate = ZonedDateTime.now();
  }

  @Override
  public int hashCode () {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) getIdentifier();
    return result;
  }

  @Override
  public boolean equals (Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ApplicationEntity other = (ApplicationEntity) obj;
    if (getIdentifier() != other.getIdentifier()) return false;
    return true;
  }
}
