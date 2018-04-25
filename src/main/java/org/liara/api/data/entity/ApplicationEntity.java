/*******************************************************************************
 * Copyright (C) 2018 Cedric DEMONGIVERT <cedric.demongivert@gmail.com>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package org.liara.api.data.entity;

import java.time.ZonedDateTime;

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
  
  public Long getIdentifier () {
    return _identifier;
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
  
  /**
   * Mark this entity as deleted. (Soft-deletion support)
   */
  public void delete () {
    if (_deletionDate == null) _deletionDate = ZonedDateTime.now();
  }
  
  /**
   * Restore this entity. (Soft-deletion support)
   */
  public void restore () {
    if (_deletionDate != null) _deletionDate = null;
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
    result = prime * result + ((getIdentifier() == null) ? 0 : getIdentifier().intValue());
    return result;
  }

  @Override
  public boolean equals (Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ApplicationEntity other = (ApplicationEntity) obj;
    if (getIdentifier() == null || !getIdentifier().equals(other.getIdentifier())) return false;
    return true;
  }
}
