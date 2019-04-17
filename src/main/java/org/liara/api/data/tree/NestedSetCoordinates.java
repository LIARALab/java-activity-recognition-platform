package org.liara.api.data.tree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

@Embeddable
@JsonPropertyOrder({"start", "end", "depth"})
public class NestedSetCoordinates
{
  @Nullable
  private Integer _start;

  @Nullable
  private Integer _end;

  @Nullable
  private Integer _depth;

  public NestedSetCoordinates () {
    _start = null;
    _end = null;
    _depth = null;
  }

  public NestedSetCoordinates (
    @NonNull final NestedSetCoordinates toCopy
  )
  {
    _start = toCopy.getStart();
    _end = toCopy.getEnd();
    _depth = toCopy.getDepth();
  }

  public NestedSetCoordinates (
    @Nullable final Integer start, @Nullable final Integer end, @Nullable final Integer depth
  )
  {
    _start = start;
    _end = end;
    _depth = depth;
  }

  @Column(name = "set_start", nullable = false, updatable = true, unique = false)
  public @Nullable Integer getStart () {
    return _start;
  }

  public void setStart (@Nullable final Integer start) {
    _start = start;
  }

  @Column(name = "set_end", nullable = false, updatable = true, unique = false)
  public @Nullable Integer getEnd () {
    return _end;
  }

  public void setEnd (@Nullable final Integer end) {
    _end = end;
  }

  @Column(name = "depth", nullable = false, updatable = true, unique = false)
  public @Nullable Integer getDepth () {
    return _depth;
  }

  public void setDepth (@Nullable final Integer depth) {
    _depth = depth;
  }

  public void set (
    @Nullable final Integer start, @Nullable final Integer end, @Nullable final Integer depth
  )
  {
    _start = start;
    _end = end;
    _depth = depth;
  }

  public void set (@Nullable final NestedSetCoordinates coordinates) {
    _start = coordinates == null ? null : coordinates.getStart();
    _end = coordinates == null ? null : coordinates.getEnd();
    _depth = coordinates == null ? null : coordinates.getDepth();
  }

  @Transient
  @JsonIgnore
  public boolean isDefined () {
    return _start != null && _end != null && _depth != null;
  }

  @JsonIgnore
  @Transient
  public @Nullable Integer getSize () {
    return (_end == null || _start == null) ? null : (_end - _start - 1);
  }

  @JsonIgnore
  @Transient
  public @Nullable Boolean isLeaf () {
    return (_end == null || _start == null) ? null : _start + 1 == _end;
  }

  public @Nullable Boolean hasChildren () {
    return (_end == null || _start == null) ? null : _start + 1 != _end;
  }

  public @Nullable Boolean isChildSetOf (@NonNull final NestedSetCoordinates set) {
    if (isDefined() && set.isDefined()) {
      return _start < set.getStart() && _end > set.getEnd() && set.getDepth() > _depth;
    } else {
      return null;
    }
  }

  public @Nullable Boolean isDirectChildSetOf (@NonNull final NestedSetCoordinates set) {
    return (isChildSetOf(set) == null) ? null : _depth + 1 == set.getDepth();
  }

  public @Nullable Boolean isParentSetOf (@NonNull final NestedSetCoordinates set) {
    if (isDefined() && set.isDefined()) {
      return _start < set.getStart() && _end > set.getEnd();
    } else {
      return null;
    }
  }

  public @Nullable Boolean isDirectParentSet (@NonNull final NestedSetCoordinates set) {
    return (isParentSetOf(set) == null) ? null : set.getDepth() + 1 == _depth;
  }

  @Override
  public int hashCode () {
    return Objects.hashCode(_start, _end, _depth);
  }

  @Override
  public boolean equals (@Nullable final Object other) {
    if (this == other) return true;
    if (other == null) return false;

    if (other instanceof NestedSetCoordinates) {
      @NonNull final NestedSetCoordinates otherCoordinates = (NestedSetCoordinates) other;

      return Objects.equal(_depth, otherCoordinates.getDepth()) && Objects.equal(_end, otherCoordinates.getEnd()) &&
             Objects.equal(_start, otherCoordinates.getStart());
    }

    return false;
  }

  @Override
  public String toString () {
    return super.toString() + "{" + "_start=" + _start + ", _end=" + _end + ", _depth=" + _depth + '}';
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

  @Override
  protected @NonNull NestedSetCoordinates clone ()
  {
    return new NestedSetCoordinates(this);
  }
}
