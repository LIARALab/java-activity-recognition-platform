package org.liara.api.data.entity.tree;

import javax.persistence.Embeddable;

import org.springframework.lang.NonNull;

@Embeddable
public class ImmutableNestedSetCoordinates extends NestedSetCoordinates
{
  public ImmutableNestedSetCoordinates () {
    super();
  }
  
  public ImmutableNestedSetCoordinates (@NonNull final NestedSetCoordinates toCopy) {
    super(toCopy);
  }
  
  public ImmutableNestedSetCoordinates (
    final int start,
    final int end,
    final int depth
  ) {
    super(start, end, depth);
  }
  
  @Override
  public ImmutableNestedSetCoordinates setStart (final int start) {
    return new ImmutableNestedSetCoordinates(start, getEnd(), getDepth());
  }

  @Override
  public ImmutableNestedSetCoordinates setEnd (final int end) {
    return new ImmutableNestedSetCoordinates(getStart(), end, getDepth());
  }

  @Override
  public ImmutableNestedSetCoordinates setDepth (final int depth) {
    return new ImmutableNestedSetCoordinates(getStart(), getEnd(), depth);
  }

  @Override
  public ImmutableNestedSetCoordinates setDefault () {
    return new ImmutableNestedSetCoordinates();
  }

  @Override
  public NestedSetCoordinates moveRight (final int move) {
    return new ImmutableNestedSetCoordinates(
      getStart() + move,
      getEnd() + move, 
      getDepth()
    );
  }

  @Override
  public NestedSetCoordinates moveLeft (final int move) {
    return new ImmutableNestedSetCoordinates(
      getStart() - move,
      getEnd() - move, 
      getDepth()
    );
  }
}
