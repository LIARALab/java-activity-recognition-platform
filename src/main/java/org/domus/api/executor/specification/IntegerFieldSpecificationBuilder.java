package org.domus.api.executor.specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.metamodel.SingularAttribute;

import org.domus.api.collection.specification.ConjunctionSpecification;
import org.domus.api.collection.specification.DisjunctionSpecification;
import org.domus.api.collection.specification.FieldBetweenSpecification;
import org.domus.api.collection.specification.FieldEqualToSpecification;
import org.domus.api.collection.specification.FieldGreatherThanOrEqualToSpecification;
import org.domus.api.collection.specification.FieldGreatherThanSpecification;
import org.domus.api.collection.specification.FieldLessThanOrEqualToSpecification;
import org.domus.api.collection.specification.FieldLessThanSpecification;
import org.domus.api.collection.specification.NegationSpecification;
import org.domus.api.collection.specification.Specification;
import org.domus.api.executor.RequestError;
import org.domus.api.executor.RequestParameterValueError;
import org.domus.api.request.APIRequest;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class IntegerFieldSpecificationBuilder<Entity> implements SpecificationBuilder<Entity>
{
  @NonNull
  private static final Pattern                     NUMBER_PATTERN              = Pattern.compile("((\\+|\\-)?\\d+)");

  @NonNull
  private static final Pattern                     PREDICATE_PATTERN           = Pattern.compile(
    String.join(
      "|",
      "(?<greaterThan>gt:" + NUMBER_PATTERN.pattern() + ")",
      "(?<greaterThanOrEqualTo>gte:" + NUMBER_PATTERN.pattern() + ")",
      "(?<lessThan>lt:" + NUMBER_PATTERN.pattern() + ")",
      "(?<lessThanOrEqualTo>lte:" + NUMBER_PATTERN.pattern() + ")",
      "(?<between>" + NUMBER_PATTERN.pattern() + "\\:" + NUMBER_PATTERN.pattern() + ")",
      "(?<notBetween>\\!" + NUMBER_PATTERN.pattern() + "\\:" + NUMBER_PATTERN.pattern() + ")",
      "(?<equalTo>" + NUMBER_PATTERN.pattern() + ")",
      "(?<notEqualTo>\\!" + NUMBER_PATTERN.pattern() + ")"
    )
  );

  @NonNull
  private static final Pattern                     UNGROUPED_PREDICATE_PATTERN = Pattern
    .compile(PREDICATE_PATTERN.pattern().replaceAll("\\?\\<[a-zA-Z]*?\\>", ""));

  @NonNull
  private static final Pattern                     CONJUNCTION_PATTERN         = Pattern
    .compile(
      String
        .join("", "(", UNGROUPED_PREDICATE_PATTERN.pattern(), ")", "(,(", UNGROUPED_PREDICATE_PATTERN.pattern(), "))*")
    );

  @NonNull
  private static final Pattern                     FILTER_PATTERN              = Pattern.compile(
    String.join("", "^", "(", CONJUNCTION_PATTERN.pattern(), ")", "(;(", CONJUNCTION_PATTERN.pattern(), "))*", "$")
  );

  @NonNull
  private final String                             _parameter;
  @NonNull
  private final SingularAttribute<Entity, Integer> _field;
  @Nullable
  private Specification<Entity>                    _result;

  public IntegerFieldSpecificationBuilder(
    @NonNull final String parameter,
    @NonNull final SingularAttribute<Entity, Integer> field
  )
  {
    this._parameter = parameter;
    this._field = field;
  }

  @Override
  public void execute (@NonNull final APIRequest request) {
    final List<Specification<Entity>> specs = new ArrayList<>();

    if (request.contains(this._parameter)) {
      final int valueCount = request.size(_parameter);
      for (int index = 0; index < valueCount; ++index) {
        specs.add(this.parseFilter(request.get(_parameter, index)));
      }
    }

    this._result = new DisjunctionSpecification<>(specs);
  }

  private Specification<Entity> parseFilter (@NonNull final String value) {
    @SuppressWarnings("unchecked")
    Specification<Entity>[] specs = Arrays.stream(value.split(";")).map(token -> parseConjunction(token.trim()))
      .toArray(size -> (Specification<Entity>[]) new Specification[size]);

    return new DisjunctionSpecification<>(specs);
  }

  private Specification<Entity> parseConjunction (@NonNull final String value) {
    @SuppressWarnings("unchecked")
    Specification<Entity>[] specs = Arrays.stream(value.split(",")).map(token -> parsePredicate(token.trim()))
      .toArray(size -> (Specification<Entity>[]) new Specification[size]);

    return new ConjunctionSpecification<>(specs);
  }

  private Specification<Entity> parsePredicate (@NonNull final String value) {
    final Matcher matcher = PREDICATE_PATTERN.matcher(value);
    matcher.matches();
    matcher.groupCount();

    String result = null;

    if ((result = matcher.group("greaterThan")) != null) {
      return this.parseGreaterThan(result);
    } else if ((result = matcher.group("greaterThanOrEqualTo")) != null) {
      return this.parseGreaterThanOrEqualTo(result);
    } else if ((result = matcher.group("lessThan")) != null) {
      return this.parseLessThan(result);
    } else if ((result = matcher.group("lessThanOrEqualTo")) != null) {
      return this.parseLessThanOrEqualTo(result);
    } else if ((result = matcher.group("between")) != null) {
      return this.parseBetweenThan(result);
    } else if ((result = matcher.group("notBetween")) != null) {
      return this.parseNotBetweenThan(result);
    } else if ((result = matcher.group("equalTo")) != null) {
      return this.parseEqualTo(result);
    } else {
      return this.parseNotEqualTo(matcher.group("notEqualTo"));
    }
  }

  private Specification<Entity> parseNotEqualTo (@NonNull final String result) {
    return new NegationSpecification<Entity>(this.parseEqualTo(result.substring(1)));
  }

  private Specification<Entity> parseEqualTo (@NonNull final String result) {
    return new FieldEqualToSpecification<Entity, Integer>(this._field, Integer.parseInt(result));
  }

  private Specification<Entity> parseNotBetweenThan (@NonNull final String result) {
    return new NegationSpecification<Entity>(this.parseBetweenThan(result.substring(1)));
  }

  private Specification<Entity> parseBetweenThan (@NonNull final String result) {
    final String[] tokens = result.split(":");

    return new FieldBetweenSpecification<Entity, Integer>(
      this._field, Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1])
    );
  }

  private Specification<Entity> parseLessThanOrEqualTo (@NonNull final String result) {
    return new FieldLessThanOrEqualToSpecification<Entity, Integer>(
      this._field, Integer.parseInt(result.split(":")[1])
    );
  }

  private Specification<Entity> parseLessThan (@NonNull final String result) {
    return new FieldLessThanSpecification<Entity, Integer>(this._field, Integer.parseInt(result.split(":")[1]));
  }

  private Specification<Entity> parseGreaterThanOrEqualTo (@NonNull final String result) {
    return new FieldGreatherThanOrEqualToSpecification<Entity, Integer>(
      this._field, Integer.parseInt(result.split(":")[1])
    );
  }

  private Specification<Entity> parseGreaterThan (@NonNull final String result) {
    return new FieldGreatherThanSpecification<Entity, Integer>(this._field, Integer.parseInt(result.split(":")[1]));
  }

  @Override
  public List<RequestError> validate (@NonNull final APIRequest request) {
    final List<RequestError> errors = new ArrayList<>();

    if (request.contains(this._parameter)) {
      final int valueCount = request.size(_parameter);
      for (int index = 0; index < valueCount; ++index) {
        this.assertIsValidParameter(errors, index, request);
      }
    }

    return errors;
  }

  private void assertIsValidParameter (
    @NonNull final List<RequestError> errors,
    final int index,
    @NonNull final APIRequest request
  )
  {
    final String value = request.get(_parameter, index);

    if (!FILTER_PATTERN.matcher(value).find()) {
      errors.add(
        new RequestParameterValueError(
          request, _parameter, index,
          String.join(
            "\\r\\n",
            "The value : \"" + value + "\" does not match the integer filter structure :",
            "",
            "integer-filter: disjunction",
            "",
            "disjunction: conjunction(;conjunction)*",
            "",
            "conjunction: predicate(,predicate)*",
            "",
            "predicate: gt:number # Greather than",
            "         | gte:number # Greather than or equal to",
            "         | lt:number # Less than",
            "         | lte:number # Less than or equal to",
            "         | number:number # Between",
            "         | !number:number # Not between",
            "         | number # Equal",
            "         | !number # Not equal",
            "",
            "number: (\\+|\\-)?\\d+"
          )
        )
      );
    }
  }

  @Override
  public Specification<Entity> getResult () {
    return this._result;
  }
}
