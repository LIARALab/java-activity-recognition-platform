package org.liara.api.filter.parser


import org.liara.api.filter.ast.FilterNodes
import spock.lang.Specification

class DurationFilterParserSpecification
  extends Specification
{
  static final long DAY = 24L * 3600L * 1000L
  static final long WEEK = 7L * 24L * 3600L * 1000L
  static final long HOUR = 3600L * 1000L
  static final long MINUTE = 60L * 1000L
  static final long SECOND = 1000L
  
  def "it can parse a greater than predicate and return a valid filtering AST" () {
    given: "an duration filter parser"
      final DurationFilterParser parser = new DurationFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"    
      parser.parse("gt:5d").equals(
        FilterNodes.greaterThan(5 * DAY)
      ) == true
      
      parser.parse("gt:5day2h3min").equals(
        FilterNodes.greaterThan(5 * DAY + 2 * HOUR + 3 * MINUTE)
      ) == true
      
      parser.parse("gt:-5days+2hours+1hour-5m").equals(
        FilterNodes.greaterThan(-5 * DAY + 2 * HOUR + 1 * HOUR - 5 * MINUTE)
      ) == true
      
      parser.parse("gt:-59787468697").equals(
        FilterNodes.greaterThan(-59787468697L * SECOND)
      ) == true
      
      parser.parse("gt:4000ms").equals(
        FilterNodes.greaterThan(4000L)
      ) == true
      
      parser.parse("gt:000000").equals(
        FilterNodes.greaterThan(0L)
      ) == true
  }
  
  def "it can parse a greater than or equal to predicate and return a valid filtering AST" () {
    given: "an duration filter parser"
      final DurationFilterParser parser = new DurationFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("gte:5d").equals(
        FilterNodes.greaterThanOrEqualTo(5 * DAY)
      ) == true
      
      parser.parse("gte:5day2h3min").equals(
        FilterNodes.greaterThanOrEqualTo(5 * DAY + 2 * HOUR + 3 * MINUTE)
      ) == true
      
      parser.parse("gte:-5days+2hours+1hour-5m").equals(
        FilterNodes.greaterThanOrEqualTo(-5 * DAY + 2 * HOUR + 1 * HOUR - 5 * MINUTE)
      ) == true
      
      parser.parse("gte:-59787468697").equals(
        FilterNodes.greaterThanOrEqualTo(-59787468697L * SECOND)
      ) == true
      
      parser.parse("gte:4000ms").equals(
        FilterNodes.greaterThanOrEqualTo(4000L)
      ) == true
      
      parser.parse("gte:000000").equals(
        FilterNodes.greaterThanOrEqualTo(0L)
      ) == true
  }

  def "it can parse a less than predicate and return a valid filtering AST" () {
    given: "an duration filter parser"
      final DurationFilterParser parser = new DurationFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("lt:5d").equals(
        FilterNodes.lessThan(5 * DAY)
      ) == true
      
      parser.parse("lt:5day2h3min").equals(
        FilterNodes.lessThan(5 * DAY + 2 * HOUR + 3 * MINUTE)
      ) == true
      
      parser.parse("lt:-5days+2hours+1hour-5m").equals(
        FilterNodes.lessThan(-5 * DAY + 2 * HOUR + 1 * HOUR - 5 * MINUTE)
      ) == true
      
      parser.parse("lt:-59787468697").equals(
        FilterNodes.lessThan(-59787468697L * SECOND)
      ) == true
      
      parser.parse("lt:4000ms").equals(
        FilterNodes.lessThan(4000L)
      ) == true
      
      parser.parse("lt:000000").equals(
        FilterNodes.lessThan(0L)
      ) == true
  }
  
  def "it can parse a less than or equal to predicate and return a valid filtering AST" () {
    given: "an duration filter parser"
      final DurationFilterParser parser = new DurationFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("lte:5d").equals(
        FilterNodes.lessThanOrEqualTo(5 * DAY)
      ) == true
      
      parser.parse("lte:5day2h3min").equals(
        FilterNodes.lessThanOrEqualTo(5 * DAY + 2 * HOUR + 3 * MINUTE)
      ) == true
      
      parser.parse("lte:-5days+2hours+1hour-5m").equals(
        FilterNodes.lessThanOrEqualTo(-5 * DAY + 2 * HOUR + 1 * HOUR - 5 * MINUTE)
      ) == true
      
      parser.parse("lte:-59787468697").equals(
        FilterNodes.lessThanOrEqualTo(-59787468697L * SECOND)
      ) == true
      
      parser.parse("lte:4000ms").equals(
        FilterNodes.lessThanOrEqualTo(4000L)
      ) == true
      
      parser.parse("lte:000000").equals(
        FilterNodes.lessThanOrEqualTo(0L)
      ) == true
  }
  
  def "it can parse an equal to predicate and return a valid filtering AST" () {
    given: "an duration filter parser"
      final DurationFilterParser parser = new DurationFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("5d").equals(
        FilterNodes.equalTo(5 * DAY)
      ) == true
      
      parser.parse("5day2h3min").equals(
        FilterNodes.equalTo(5 * DAY + 2 * HOUR + 3 * MINUTE)
      ) == true
      
      parser.parse("-5days+2hours+1hour-5m").equals(
        FilterNodes.equalTo(-5 * DAY + 2 * HOUR + 1 * HOUR - 5 * MINUTE)
      ) == true
      
      parser.parse("-59787468697").equals(
        FilterNodes.equalTo(-59787468697L * SECOND)
      ) == true
      
      parser.parse("4000ms").equals(
        FilterNodes.equalTo(4000L)
      ) == true
      
      parser.parse("000000").equals(
        FilterNodes.equalTo(0L)
      ) == true
  }
  
  def "it can parse a not equal to predicate and return a valid filtering AST" () {
    given: "an duration filter parser"
      final DurationFilterParser parser = new DurationFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
    parser.parse("not:5d").equals(
        FilterNodes.not(FilterNodes.equalTo(5 * DAY))
      ) == true

    parser.parse("not:5day2h3min").equals(
        FilterNodes.not(
          FilterNodes.equalTo(5 * DAY + 2 * HOUR + 3 * MINUTE)
        )
      ) == true

    parser.parse("not:-5days+2hours+1hour-5m").equals(
        FilterNodes.not(
          FilterNodes.equalTo(-5 * DAY + 2 * HOUR + 1 * HOUR - 5 * MINUTE)
        )
      ) == true

    parser.parse("not:-59787468697").equals(
        FilterNodes.not(FilterNodes.equalTo(-59787468697L * SECOND))
      ) == true

    parser.parse("not:4000ms").equals(
        FilterNodes.not(FilterNodes.equalTo(4000L))
      ) == true

    parser.parse("not:000000").equals(
        FilterNodes.not(FilterNodes.equalTo(0L))
      ) == true
  }
  
  def "it can parse a between predicate and return a valid filtering AST" () {
    given: "an duration filter parser"
      final DurationFilterParser parser = new DurationFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("5d:3h").equals(
        FilterNodes.between(3 * HOUR, 5 * DAY)
      ) == true
      
      parser.parse("5weeks:14684645689674").equals(
        FilterNodes.between(14684645689674 * SECOND, 5 * WEEK)
      ) == true
      
      parser.parse("1h+5h6h3m:1d2d-5hour+2min").equals(
        FilterNodes.between((1 + 5 + 6) * HOUR + 3 * MINUTE, 3 * DAY - 5 * HOUR + 2 * MINUTE)
      ) == true
      
      parser.parse("0day:5h").equals(
        FilterNodes.between(0L, 5 * HOUR)
      ) == true
      
      parser.parse("-589hour:32day").equals(
        FilterNodes.between(-589 * HOUR, 32 * DAY)
      ) == true
  }
  
  def "it can parse a not between predicate and return a valid filtering AST" () {
    given: "an duration filter parser"
      final DurationFilterParser parser = new DurationFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
    parser.parse("not:5d:3h").equals(
        FilterNodes.not(FilterNodes.between(3 * HOUR, 5 * DAY))
      ) == true

    parser.parse("not:5weeks:14684645689674").equals(
        FilterNodes.not(FilterNodes.between(14684645689674 * SECOND, 5 * WEEK))
      ) == true

    parser.parse("not:1h+5h6h3m:1d2d-5hour+2min").equals(
        FilterNodes.not(FilterNodes.between((1 + 5 + 6) * HOUR + 3 * MINUTE, 3 * DAY - 5 * HOUR + 2 * MINUTE))
      ) == true

    parser.parse("not:0day:5h").equals(
        FilterNodes.not(FilterNodes.between(0L, 5 * HOUR))
      ) == true

    parser.parse("not:-589hour:32day").equals(
        FilterNodes.not(FilterNodes.between(-589 * HOUR, 32 * DAY))
      ) == true
  }
  
  def "it can parse conjunction of predicates and return a valid filtering AST" () {
    given: "an duration filter parser"
      final DurationFilterParser parser = new DurationFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("gt:5day,lte:6day5hour").equals(
        FilterNodes.and(
          FilterNodes.greaterThan(5 * DAY),
          FilterNodes.lessThanOrEqualTo(6 * DAY + 5 * HOUR)
        )
      ) == true

    parser.parse("not:2day,not:6day,not:9h,not:10ms,not:-11min,not:-56s").equals(
        FilterNodes.and(
          FilterNodes.not(FilterNodes.equalTo(2 * DAY)),
          FilterNodes.not(FilterNodes.equalTo(6 * DAY)),
          FilterNodes.not(FilterNodes.equalTo(9 * HOUR)),
          FilterNodes.not(FilterNodes.equalTo(10L)),
          FilterNodes.not(FilterNodes.equalTo(-11 * MINUTE)),
          FilterNodes.not(FilterNodes.equalTo(-56 * SECOND)),
        )
      ) == true
      
      parser.parse("50:-100day,gte:-30h").equals(
        FilterNodes.and(
          FilterNodes.between(-100 * DAY, 50 * SECOND),
          FilterNodes.greaterThanOrEqualTo(-30 * HOUR)
        )
      ) == true

    parser.parse("not:50day:-100day,gte:-30").equals(
        FilterNodes.and(
          FilterNodes.not(FilterNodes.between(-100 * DAY, 50 * DAY)),
          FilterNodes.greaterThanOrEqualTo(-30 * SECOND)
        )
      ) == true
  }
  
  def "it can parse disjunction of predicates and return a valid filtering AST" () {
    given: "an duration filter parser"
      final DurationFilterParser parser = new DurationFilterParser()
     
    expect: "it to transform valid predicates into valid filtering AST"
      parser.parse("gt:8day;lte:69hours").equals(
        FilterNodes.or(
          FilterNodes.greaterThan(8 * DAY),
          FilterNodes.lessThanOrEqualTo(69 * HOUR)
        )
      ) == true

    parser.parse("not:2day,not:6day;not:9hours,not:10h;not:-11min,not:-56ms").equals(
        FilterNodes.or(
          FilterNodes.and(
            FilterNodes.not(FilterNodes.equalTo(2 * DAY)),
            FilterNodes.not(FilterNodes.equalTo(6 * DAY))
          ),
          FilterNodes.and(
            FilterNodes.not(FilterNodes.equalTo(9 * HOUR)),
            FilterNodes.not(FilterNodes.equalTo(10 * HOUR))
          ),
          FilterNodes.and(
            FilterNodes.not(FilterNodes.equalTo(-11 * MINUTE)),
            FilterNodes.not(FilterNodes.equalTo(-56L))
          )
        )
      ) == true
      
      parser.parse("50ms:-100h;gte:-30").equals(
        FilterNodes.or(
          FilterNodes.between(-100 * HOUR, 50L),
          FilterNodes.greaterThanOrEqualTo(-30 * SECOND)
        )
      ) == true

    parser.parse("not:50:-100day;gte:-30h,lte:10").equals(
        FilterNodes.or(
          FilterNodes.not(FilterNodes.between(-100 * DAY, 50 * SECOND)),
          FilterNodes.and(
            FilterNodes.greaterThanOrEqualTo(-30 * HOUR),
            FilterNodes.lessThanOrEqualTo(10 * SECOND)
          )
        )
      ) == true
  }
}
