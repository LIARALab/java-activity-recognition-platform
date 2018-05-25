package org.liara.api.filter.ast;

import java.util.Collection;

import org.springframework.lang.NonNull;

public class FilterNodes
{
  public static <T extends Comparable<? super T>> PredicateFilterNode lessThan (
    @NonNull final T value
  ) {
    return new LessThanFilterNode<T>(ValueFilterNode.fromGeneric(value));
  }
  
  public static <T extends Comparable<? super T>> PredicateFilterNode lessThanOrEqualTo (
    @NonNull final T value
  ) {
    return new LessThanOrEqualToFilterNode<T>(ValueFilterNode.fromGeneric(value));
  }
  
  public static <T extends Comparable<? super T>> PredicateFilterNode greaterThan (
    @NonNull final T value
  ) {
    return new GreaterThanFilterNode<T>(ValueFilterNode.fromGeneric(value));
  }
  
  public static <T extends Comparable<? super T>> PredicateFilterNode greaterThanOrEqualTo (
    @NonNull final T value
  ) {
    return new GreaterThanOrEqualToFilterNode<T>(ValueFilterNode.fromGeneric(value));
  }
  
  public static <T extends Comparable<? super T>> PredicateFilterNode equalTo (
    @NonNull final T value
  ) {
    return new EqualToFilterNode<T>(ValueFilterNode.fromGeneric(value));
  }
  
  public static <T extends Comparable<? super T>> PredicateFilterNode between (
    @NonNull final T a, @NonNull final T b
  ) {
    return new BetweenFilterNode<T>(ValueFilterNode.fromGeneric(a), ValueFilterNode.fromGeneric(b));
  }
  
  public static PredicateFilterNode not (
    @NonNull final PredicateFilterNode value
  ) {
    return new NotFilterNode(value);
  }
  
  public static PredicateFilterNode and (
    @NonNull final PredicateFilterNode ...values
  ) {
    return new ConjunctionFilterNode(values);
  }
  
  public static PredicateFilterNode and (
    @NonNull final Collection<PredicateFilterNode> values
  ) {
    return new ConjunctionFilterNode(values);
  }
  
  public static PredicateFilterNode or (
    @NonNull final PredicateFilterNode ...values
  ) {
    return new DisjunctionFilterNode(values);
  }
  
  public static PredicateFilterNode or (
    @NonNull final Collection<PredicateFilterNode> values
  ) {
    return new DisjunctionFilterNode(values);
  }
}
