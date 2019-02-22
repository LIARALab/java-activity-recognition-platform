package org.liara.api.filter.parser


import org.liara.api.filter.ast.FilterNodes
import spock.lang.Specification

class DoubleFilterParserSpecification
  extends Specification
{
  def "it can parse a greater than predicate and return a valid filtering AST" () {
    given: "an double filter parser"
      final DoubleFilterParser parser = new DoubleFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"    
      parser.parse("gt:55.365").equals(FilterNodes.greaterThan(55.365d)) == true
      
      parser.parse("gt:-25066.0000").equals(
        FilterNodes.greaterThan(-25066.0d)
      ) == true
      
      parser.parse("gt:-33489.036").equals(
        FilterNodes.greaterThan(-33489.036d)
      ) == true
      
      parser.parse("gt:+3000.22").equals(
        FilterNodes.greaterThan(3000.22d)
      ) == true
      
      parser.parse("gt:+0000.00").equals(
        FilterNodes.greaterThan(0.0d)
      ) == true
  }
  
  def "it can parse a greater than or equal to predicate and return a valid filtering AST" () {
    given: "an double filter parser"
      final DoubleFilterParser parser = new DoubleFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("gte:55.365").equals(
        FilterNodes.greaterThanOrEqualTo(55.365d)
      ) == true
      
      parser.parse("gte:-25066.6").equals(
        FilterNodes.greaterThanOrEqualTo(-25066.6d)
      ) == true
      
      parser.parse("gte:-33489.24").equals(
        FilterNodes.greaterThanOrEqualTo(-33489.24d)
      ) == true
      
      parser.parse("gte:+3000").equals(
        FilterNodes.greaterThanOrEqualTo(3000.0d)
      ) == true
      
      parser.parse("gte:+0000.000").equals(
        FilterNodes.greaterThanOrEqualTo(0.0d)
      ) == true
  }

  def "it can parse a less than predicate and return a valid filtering AST" () {
    given: "an double filter parser"
      final DoubleFilterParser parser = new DoubleFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("lt:55.365").equals(FilterNodes.lessThan(55.365d)) == true
      
      parser.parse("lt:-25066.6").equals(
        FilterNodes.lessThan(-25066.6d)
      ) == true
      
      parser.parse("lt:-33489.6").equals(
        FilterNodes.lessThan(-33489.6d)
      ) == true
      
      parser.parse("lt:+3000").equals(
        FilterNodes.lessThan(3000.0d)
      ) == true
      
      parser.parse("lt:+0000.0000").equals(
        FilterNodes.lessThan(0.0000d)
      ) == true
  }
  
  def "it can parse a less than or equal to predicate and return a valid filtering AST" () {
    given: "an double filter parser"
      final DoubleFilterParser parser = new DoubleFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("lte:55.365").equals(
        FilterNodes.lessThanOrEqualTo(55.365d)
      ) == true
      
      parser.parse("lte:-25066.6").equals(
        FilterNodes.lessThanOrEqualTo(-25066.6d)
      ) == true
      
      parser.parse("lte:+33489.0").equals(
        FilterNodes.lessThanOrEqualTo(33489.0d)
      ) == true
      
      parser.parse("lte:-3000.06").equals(
        FilterNodes.lessThanOrEqualTo(-3000.06d)
      ) == true
      
      parser.parse("lte:+0000.000").equals(
        FilterNodes.lessThanOrEqualTo(0.0d)
      ) == true
  }
  
  def "it can parse an equal to predicate and return a valid filtering AST" () {
    given: "an double filter parser"
      final DoubleFilterParser parser = new DoubleFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("55.365").equals(
        FilterNodes.equalTo(55.365d)
      ) == true
      
      parser.parse("-25066.6").equals(
        FilterNodes.equalTo(-25066.6d)
      ) == true
      
      parser.parse("+33489.49").equals(
        FilterNodes.equalTo(33489.49d)
      ) == true
      
      parser.parse("-3000").equals(
        FilterNodes.equalTo(-3000.0d)
      ) == true
      
      parser.parse("+0000.000").equals(
        FilterNodes.equalTo(0.000d)
      ) == true
  }
  
  def "it can parse a not equal to predicate and return a valid filtering AST" () {
    given: "an double filter parser"
      final DoubleFilterParser parser = new DoubleFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
    parser.parse("not:55.365").equals(
        FilterNodes.not(FilterNodes.equalTo(55.365d))
      ) == true

    parser.parse("not:-25066.6").equals(
        FilterNodes.not(FilterNodes.equalTo(-25066.6d))
      ) == true

    parser.parse("not:-33489.29").equals(
        FilterNodes.not(FilterNodes.equalTo(-33489.29d))
      ) == true

    parser.parse("not:+3000").equals(
        FilterNodes.not(FilterNodes.equalTo(3000.0d))
      ) == true

    parser.parse("not:+0000.000").equals(
        FilterNodes.not(FilterNodes.equalTo(0.0d))
      ) == true
  }
  
  def "it can parse a between predicate and return a valid filtering AST" () {
    given: "an double filter parser"
      final DoubleFilterParser parser = new DoubleFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("5.54:456.89").equals(
        FilterNodes.between(5.54d, 456.89d)
      ) == true
      
      parser.parse("456:5").equals(
        FilterNodes.between(5.0d, 456.0d)
      ) == true
      
      parser.parse("+50.63:-250.12").equals(
        FilterNodes.between(-250.12d, 50.63d)
      ) == true
      
      parser.parse("+0000.56:-0000.98").equals(
        FilterNodes.between(-0.98d, 0.56d)
      ) == true
      
      parser.parse("-369745.33:+59.29").equals(
        FilterNodes.between(-369745.33d, 59.29d)
      ) == true
  }
  
  def "it can parse a not between predicate and return a valid filtering AST" () {
    given: "an double filter parser"
      final DoubleFilterParser parser = new DoubleFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
    parser.parse("not:5:456").equals(
        FilterNodes.not(FilterNodes.between(5.0d, 456.0d))
      ) == true

    parser.parse("not:456.22:5.33").equals(
        FilterNodes.not(FilterNodes.between(5.33d, 456.22d))
      ) == true

    parser.parse("not:+50.16:-250").equals(
        FilterNodes.not(FilterNodes.between(-250d, 50.16d))
      ) == true

    parser.parse("not:+0000.98:-0000.1589").equals(
        FilterNodes.not(FilterNodes.between(-0.1589d, 0.98d))
      ) == true

    parser.parse("not:-369745.22:+59").equals(
        FilterNodes.not(FilterNodes.between(-369745.22d, 59.0d))
      ) == true
  }
  
  def "it can parse conjunction of predicates and return a valid filtering AST" () {
    given: "an double filter parser"
      final DoubleFilterParser parser = new DoubleFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("gt:8.5,lte:69").equals(
        FilterNodes.and(
          FilterNodes.greaterThan(8.5d),
          FilterNodes.lessThanOrEqualTo(69.0d)
        )
      ) == true

    parser.parse("not:2,not:6.3,not:9,not:10.8,not:-11,not:-56.4").equals(
        FilterNodes.and(
          FilterNodes.not(FilterNodes.equalTo(2.0d)),
          FilterNodes.not(FilterNodes.equalTo(6.3d)),
          FilterNodes.not(FilterNodes.equalTo(9.0d)),
          FilterNodes.not(FilterNodes.equalTo(10.8d)),
          FilterNodes.not(FilterNodes.equalTo(-11.0d)),
          FilterNodes.not(FilterNodes.equalTo(-56.4d)),
        )
      ) == true
      
      parser.parse("50.55:-100,gte:-30.78").equals(
        FilterNodes.and(
          FilterNodes.between(-100.0d, 50.55d),
          FilterNodes.greaterThanOrEqualTo(-30.78d)
        )
      ) == true

    parser.parse("not:50.2:-100,gte:-30.33").equals(
        FilterNodes.and(
          FilterNodes.not(FilterNodes.between(-100.0d, 50.2d)),
          FilterNodes.greaterThanOrEqualTo(-30.33d)
        )
      ) == true
  }
  
  def "it can parse disjunction of predicates and return a valid filtering AST" () {
    given: "an double filter parser"
      final DoubleFilterParser parser = new DoubleFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("gt:8.3;lte:69").equals(
        FilterNodes.or(
          FilterNodes.greaterThan(8.3d),
          FilterNodes.lessThanOrEqualTo(69.0d)
        )
      ) == true

    parser.parse("not:2,not:6.4;not:9,not:10.2;not:-11.5,not:-56").equals(
        FilterNodes.or(
          FilterNodes.and(
            FilterNodes.not(FilterNodes.equalTo(2.0d)),
            FilterNodes.not(FilterNodes.equalTo(6.4d))
          ),
          FilterNodes.and(
            FilterNodes.not(FilterNodes.equalTo(9.0d)),
            FilterNodes.not(FilterNodes.equalTo(10.2d))
          ),
          FilterNodes.and(
            FilterNodes.not(FilterNodes.equalTo(-11.5d)),
            FilterNodes.not(FilterNodes.equalTo(-56.0d))
          )
        )
      ) == true
      
      parser.parse("50:-100.3;gte:-30.0").equals(
        FilterNodes.or(
          FilterNodes.between(-100.3d, 50.0d),
          FilterNodes.greaterThanOrEqualTo(-30.0d)
        )
      ) == true

    parser.parse("not:50.25:-100;gte:-30.3,lte:10.2").equals(
        FilterNodes.or(
          FilterNodes.not(FilterNodes.between(-100.0d, 50.25d)),
          FilterNodes.and(
            FilterNodes.greaterThanOrEqualTo(-30.3d),
            FilterNodes.lessThanOrEqualTo(10.2d)
          )
        )
      ) == true
  }
}
