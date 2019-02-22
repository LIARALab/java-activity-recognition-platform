package org.liara.api.filter.parser


import org.liara.api.filter.ast.FilterNodes
import spock.lang.Specification

class IntegerFilterParserSpecification
  extends Specification
{
  def "it can parse a greater than predicate and return a valid filtering AST" () {
    given: "an integer filter parser"
      final IntegerFilterParser parser = new IntegerFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("gt:55").equals(FilterNodes.greaterThan(55)) == true
      
      parser.parse("gt:-25066").equals(
        FilterNodes.greaterThan(-25066)
      ) == true
      
      parser.parse("gt:-33489").equals(
        FilterNodes.greaterThan(-33489)
      ) == true
      
      parser.parse("gt:+3000").equals(
        FilterNodes.greaterThan(3000)
      ) == true
      
      parser.parse("gt:+0000").equals(
        FilterNodes.greaterThan(0)
      ) == true
  }
  
  def "it can parse a greater than or equal to predicate and return a valid filtering AST" () {
    given: "an integer filter parser"
      final IntegerFilterParser parser = new IntegerFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("gte:55").equals(
        FilterNodes.greaterThanOrEqualTo(55)
      ) == true
      
      parser.parse("gte:-25066").equals(
        FilterNodes.greaterThanOrEqualTo(-25066)
      ) == true
      
      parser.parse("gte:-33489").equals(
        FilterNodes.greaterThanOrEqualTo(-33489)
      ) == true
      
      parser.parse("gte:+3000").equals(
        FilterNodes.greaterThanOrEqualTo(3000)
      ) == true
      
      parser.parse("gte:+0000").equals(
        FilterNodes.greaterThanOrEqualTo(0)
      ) == true
  }

  def "it can parse a less than predicate and return a valid filtering AST" () {
    given: "an integer filter parser"
      final IntegerFilterParser parser = new IntegerFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("lt:55").equals(FilterNodes.lessThan(55)) == true
      
      parser.parse("lt:-25066").equals(
        FilterNodes.lessThan(-25066)
      ) == true
      
      parser.parse("lt:-33489").equals(
        FilterNodes.lessThan(-33489)
      ) == true
      
      parser.parse("lt:+3000").equals(
        FilterNodes.lessThan(3000)
      ) == true
      
      parser.parse("lt:+0000").equals(
        FilterNodes.lessThan(0)
      ) == true
  }
  
  def "it can parse a less than or equal to predicate and return a valid filtering AST" () {
    given: "an integer filter parser"
      final IntegerFilterParser parser = new IntegerFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("lte:55").equals(
        FilterNodes.lessThanOrEqualTo(55)
      ) == true
      
      parser.parse("lte:-25066").equals(
        FilterNodes.lessThanOrEqualTo(-25066)
      ) == true
      
      parser.parse("lte:+33489").equals(
        FilterNodes.lessThanOrEqualTo(33489)
      ) == true
      
      parser.parse("lte:-3000").equals(
        FilterNodes.lessThanOrEqualTo(-3000)
      ) == true
      
      parser.parse("lte:+0000").equals(
        FilterNodes.lessThanOrEqualTo(0)
      ) == true
  }
  
  def "it can parse an equal to predicate and return a valid filtering AST" () {
    given: "an integer filter parser"
      final IntegerFilterParser parser = new IntegerFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("55").equals(
        FilterNodes.equalTo(55)
      ) == true
      
      parser.parse("-25066").equals(
        FilterNodes.equalTo(-25066)
      ) == true
      
      parser.parse("+33489").equals(
        FilterNodes.equalTo(33489)
      ) == true
      
      parser.parse("-3000").equals(
        FilterNodes.equalTo(-3000)
      ) == true
      
      parser.parse("+0000").equals(
        FilterNodes.equalTo(0)
      ) == true
  }
  
  def "it can parse a not equal to predicate and return a valid filtering AST" () {
    given: "an integer filter parser"
      final IntegerFilterParser parser = new IntegerFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
    parser.parse("not:55").equals(
        FilterNodes.not(FilterNodes.equalTo(55))
      ) == true

    parser.parse("not:-25066").equals(
        FilterNodes.not(FilterNodes.equalTo(-25066))
      ) == true

    parser.parse("not:-33489").equals(
        FilterNodes.not(FilterNodes.equalTo(-33489))
      ) == true

    parser.parse("not:+3000").equals(
        FilterNodes.not(FilterNodes.equalTo(3000))
      ) == true

    parser.parse("not:+0000").equals(
        FilterNodes.not(FilterNodes.equalTo(0))
      ) == true
  }
  
  def "it can parse a between predicate and return a valid filtering AST" () {
    given: "an integer filter parser"
      final IntegerFilterParser parser = new IntegerFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("5:456").equals(
        FilterNodes.between(5, 456)
      ) == true
      
      parser.parse("456:5").equals(
        FilterNodes.between(5, 456)
      ) == true
      
      parser.parse("+50:-250").equals(
        FilterNodes.between(-250, 50)
      ) == true
      
      parser.parse("+0000:-0000").equals(
        FilterNodes.between(0, 0)
      ) == true
      
      parser.parse("-369745:+59").equals(
        FilterNodes.between(-369745, 59)
      ) == true
  }
  
  def "it can parse a not between predicate and return a valid filtering AST" () {
    given: "an integer filter parser"
      final IntegerFilterParser parser = new IntegerFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
    parser.parse("not:5:456").equals(
        FilterNodes.not(FilterNodes.between(5, 456))
      ) == true

    parser.parse("not:456:5").equals(
        FilterNodes.not(FilterNodes.between(5, 456))
      ) == true

    parser.parse("not:+50:-250").equals(
        FilterNodes.not(FilterNodes.between(-250, 50))
      ) == true

    parser.parse("not:+0000:-0000").equals(
        FilterNodes.not(FilterNodes.between(0, 0))
      ) == true

    parser.parse("not:-369745:+59").equals(
        FilterNodes.not(FilterNodes.between(-369745, 59))
      ) == true
  }
  
  def "it can parse conjunction of predicates and return a valid filtering AST" () {
    given: "an integer filter parser"
      final IntegerFilterParser parser = new IntegerFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("gt:8,lte:69").equals(
        FilterNodes.and(
          FilterNodes.greaterThan(8),
          FilterNodes.lessThanOrEqualTo(69)
        )
      ) == true

    parser.parse("not:2,not:6,not:9,not:10,not:-11,not:-56").equals(
        FilterNodes.and(
          FilterNodes.not(FilterNodes.equalTo(2)),
          FilterNodes.not(FilterNodes.equalTo(6)),
          FilterNodes.not(FilterNodes.equalTo(9)),
          FilterNodes.not(FilterNodes.equalTo(10)),
          FilterNodes.not(FilterNodes.equalTo(-11)),
          FilterNodes.not(FilterNodes.equalTo(-56)),
        )
      ) == true
      
      parser.parse("50:-100,gte:-30").equals(
        FilterNodes.and(
          FilterNodes.between(-100, 50),
          FilterNodes.greaterThanOrEqualTo(-30)
        )
      ) == true

    parser.parse("not:50:-100,gte:-30").equals(
        FilterNodes.and(
          FilterNodes.not(FilterNodes.between(-100, 50)),
          FilterNodes.greaterThanOrEqualTo(-30)
        )
      ) == true
  }
  
  def "it can parse disjunction of predicates and return a valid filtering AST" () {
    given: "an integer filter parser"
      final IntegerFilterParser parser = new IntegerFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("gt:8;lte:69").equals(
        FilterNodes.or(
          FilterNodes.greaterThan(8),
          FilterNodes.lessThanOrEqualTo(69)
        )
      ) == true

    parser.parse("not:2,not:6;not:9,not:10;not:-11,not:-56").equals(
        FilterNodes.or(
          FilterNodes.and(
            FilterNodes.not(FilterNodes.equalTo(2)),
            FilterNodes.not(FilterNodes.equalTo(6))
          ),
          FilterNodes.and(
            FilterNodes.not(FilterNodes.equalTo(9)),
            FilterNodes.not(FilterNodes.equalTo(10))
          ),
          FilterNodes.and(
            FilterNodes.not(FilterNodes.equalTo(-11)),
            FilterNodes.not(FilterNodes.equalTo(-56))
          )
        )
      ) == true
      
      parser.parse("50:-100;gte:-30").equals(
        FilterNodes.or(
          FilterNodes.between(-100, 50),
          FilterNodes.greaterThanOrEqualTo(-30)
        )
      ) == true

    parser.parse("not:50:-100;gte:-30,lte:10").equals(
        FilterNodes.or(
          FilterNodes.not(FilterNodes.between(-100, 50)),
          FilterNodes.and(
            FilterNodes.greaterThanOrEqualTo(-30),
            FilterNodes.lessThanOrEqualTo(10)
          )
        )
      ) == true
  }
}
