package org.liara.api.data.entity.tree;

import java.security.InvalidParameterException;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Objects;

// @Formula("(SELECT (COUNT(*) - 1) FROM nodes AS parent WHERE set_start BETWEEN parent.set_start AND parent.set_end)")
@Embeddable
@JsonPropertyOrder({ "start", "end", "depth" })
public class NestedSetCoordinates
{
  @Column(name = "set_start", nullable = false, updatable = true, unique = false)
  private int _start;

  @Column(name = "set_end", nullable = false, updatable = true, unique = false)
  private int _end;

  @Column(name = "set_depth", nullable = false, updatable = true, unique = false)
  private int _depth;
  
  public NestedSetCoordinates () {
    _start = 1;
    _end = 2;
    _depth = 1;
  }
  
  public NestedSetCoordinates (@NonNull final NestedSetCoordinates toCopy) {
    _start = toCopy.getStart();
    _end = toCopy.getEnd();
    _depth = toCopy.getDepth();
  }
  
  public NestedSetCoordinates (
    final int start,
    final int end,
    final int depth
  ) {
    //assertThatAreValidNestedSetCoordinates(start, end);
    _start = start;
    _end = end;
    _depth = depth;
  }
  
  private void assertThatAreValidNestedSetCoordinates (
    final int start, 
    final int end
  ) {
    if (start >= end) {
      throw new InvalidParameterException(
        String.join(
          "", 
          "The given nested set coordinates are invalid because the set starting coordinate (",
          String.valueOf(start), ") is greater or equal to the set ending coordinate (",
          String.valueOf(end), ") a nested set end coordinate must be greater than the ",
          "given starting coordinate."
        )
      );
    }
    
    if ((end - start - 1) % 2 == 1) {
      throw new InvalidParameterException(
        String.join(
          "", 
          "The given nested set coordinates are invalid because the given ending coordinate ",
          "induce an invalid set size : (end - start - 1), (", 
          String.valueOf(end - start - 1), ") is not divisible by 2."
        )
      );
    }
  }

  @JsonIgnore
  public int getSize () {
    return (_end - _start - 1) >> 1;
  }

  @Column(name = "set_start", nullable = false, updatable = true, unique = false)
  public int getStart () {
    return _start;
  }
  
  public NestedSetCoordinates setStart (final int start) {
    //assertThatAreValidNestedSetCoordinates(start, _end);
    _start = start;
    return this;
  }

  @Column(name = "set_end", nullable = false, updatable = true, unique = false)
  public int getEnd () {
    return _end;
  }
  
  public NestedSetCoordinates setEnd (final int end) {
    //assertThatAreValidNestedSetCoordinates(_start, end);
    _end = end;
    return this;
  }
  
  public NestedSetCoordinates set (final int start, final int end, final int depth) {
    //assertThatAreValidNestedSetCoordinates(start, end);
    _start = start;
    _end = end;
    _depth = depth;
    return this;
  }
  
  public NestedSetCoordinates set (@NonNull final NestedSetCoordinates coordinates) {
    _start = coordinates.getStart();
    _end = coordinates.getEnd();
    _depth = coordinates.getDepth();
    return this;
  }

  @Column(name = "depth", nullable = false, updatable = true, unique = false)
  public int getDepth () {
    return _depth;
  }
  
  public NestedSetCoordinates setDepth (final int depth) {
    _depth = depth;
    return this;
  }
  
  @JsonIgnore
  public boolean isLeaf () {
    return _start + 1 == _end;
  }
  
  public boolean hasChildren () {
    return _start + 1 != _end; 
  }
  
  public boolean isChildSet (@NonNull final NestedSetCoordinates set) {
    return _start < set.getStart() && _end > set.getEnd() && set.getDepth() > _depth;
  }
  
  public boolean isDirectChildSet (@NonNull final NestedSetCoordinates set) {
    return isChildSet(set) && _depth + 1 == set.getDepth();
  }
  
  public boolean isParentSet (@NonNull final NestedSetCoordinates set) {
    return _start > set.getStart() && _end < set.getEnd() && set.getDepth() < _depth;
  }
  
  public boolean isDirectParentSet (@NonNull final NestedSetCoordinates set) {
    return isParentSet(set) && set.getDepth() + 1 == _depth;
  }
  
  @Override
  public int hashCode () {
    return Objects.hashCode(_start, _end, _depth);
  }

  @Override
  public boolean equals (@Nullable final Object obj) {
    if (this == obj) return true;
    if (obj == null || !(obj instanceof NestedSetCoordinates)) return false;
    
    final NestedSetCoordinates other = (NestedSetCoordinates) obj;
    return _depth == other.getDepth() && 
           _end == other.getEnd() &&
           _start == other.getStart();
  }

  public NestedSetCoordinates setDefault () {
    _start = 1;
    _end = 2;
    _depth = 1;
    return this;
  }

  public NestedSetCoordinates moveRight (final int move) {
    _start += move;
    _end += move;
    return this;
  }
  
  public NestedSetCoordinates moveLeft (final int move) {
    _start -= move;
    _end -= move;
    return this;
  }
}
