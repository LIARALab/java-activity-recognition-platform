package org.domus.api.executor;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import org.domus.api.collection.EntityCollection;
import org.domus.api.collection.EntityCollectionView;

import org.domus.api.request.APIRequest;

/**
* Restrict an entity collection by using an api request.
*/
public class EntityCollectionRestrictionExecutor<T>
       implements Executor
{
  @NonNull private final static String LIMIT_PARAMETER_NAME = "first";
  @NonNull private final static Pattern LIMIT_PARAMETER_TEST = Pattern.compile("^\\s*\\d+\\s*$");
  @NonNull private final static String OFFSET_PARAMETER_NAME = "after";
  @NonNull private final static Pattern OFFSET_PARAMETER_TEST = Pattern.compile("^\\s*\\d+\\s*$");
  @NonNull private final static String ALL_PARAMETER_NAME = "all";
  @NonNull private final static Pattern ALL_PARAMETER_TEST = Pattern.compile("^\\s*(true|false|)\\s*$");
  private final static int DEFAULT_LIMIT = 10;

  @Nullable private EntityCollection<T> _collection;
  @Nullable private EntityCollectionView<T> _result;

  public EntityCollectionRestrictionExecutor () {
    this._collection = null;
    this._result = null;
  }

  public void setCollection (@Nullable final EntityCollection<T> collection) {
    this._collection = collection;
    this._result = null;
  }

  public EntityCollection<T> getCollection () {
    return this._collection;
  }

  public EntityCollectionView<T> getResult () {
    return this._result;
  }

  /**
  * @see Executor#execute
  */
  public void execute (@NonNull final APIRequest request) {
    int offset = 0;
    
    if (request.contains(OFFSET_PARAMETER_NAME)) {
      offset = Integer.parseInt(request.get(OFFSET_PARAMETER_NAME, 0).trim());
    }
    
    if (request.contains(LIMIT_PARAMETER_NAME)) {
      int limit = Integer.parseInt(request.get(LIMIT_PARAMETER_NAME, 0).trim());
      this._result = this._collection.getView(offset, limit);
    } else if (request.contains(ALL_PARAMETER_NAME)) {
       final String value = request.get(ALL_PARAMETER_NAME, 0).trim();
       
       if (value.equals("") || value.equals("true")) {
         this._result = this._collection.getView(offset, this._collection.getSize() - offset);
       } else {
         this._result = this._collection.getView(offset, DEFAULT_LIMIT);
       } 
    } else {
      this._result = this._collection.getView(offset, DEFAULT_LIMIT);
    }
  }

  /**
  * @see Executor#validate
  */
  public List<RequestError> validate (@NonNull final APIRequest request) {
    final List<RequestError> errors = new ArrayList<>(7);

    this.assertIsValidRequest(errors, request);
    this.assertHasValidAllParameter(errors, request);
    this.assertHasValidOffsetParameter(errors, request);
    this.assertHasValidLimitParameter(errors, request);

    return errors;
  }

  /**
  * Check if the limitation parameter of the given request is valid.
  *
  * @param errors A collection of errors to feed.
  * @param request Request to check.
  */
  private void assertHasValidLimitParameter (
    @NonNull final List<RequestError> errors,
    @NonNull final APIRequest request
  ) {
    if (request.contains(LIMIT_PARAMETER_NAME)) {
      if (request.size(LIMIT_PARAMETER_NAME) > 1) {
        errors.add(this.createLimitParameterCountError(request));
      }

      if (
        !LIMIT_PARAMETER_TEST
          .matcher(request.get(LIMIT_PARAMETER_NAME, 0))
          .find()
      ) {
        errors.add(this.createLimitParameterContentError(request));
      }
    }
  }

  private RequestError createLimitParameterCountError (
    @NonNull final APIRequest request
  ) {
    return new RequestParameterError(
      request,
      LIMIT_PARAMETER_NAME,
      String.join(
        "",
        "Only one \"", LIMIT_PARAMETER_NAME,
        "\" parameter is allowed by request. This parameter was setted ",
        String.valueOf(request.size(LIMIT_PARAMETER_NAME)),
        " times."
      )
    );
  }

  private RequestError createLimitParameterContentError (
    @NonNull final APIRequest request
  ) {
    return new RequestParameterValueError(
      request,
      LIMIT_PARAMETER_NAME,
      0,
      String.join(
        "",
        "\"", LIMIT_PARAMETER_NAME,
        "\" parameter must be a non-negative number, \"",
        request.get(LIMIT_PARAMETER_NAME, 0),
        "\" given."
      )
    );
  }

  /**
  * Check if the offset parameter of the given request is valid.
  *
  * @param errors A collection of errors to feed.
  * @param request Request to check.
  */
  private void assertHasValidOffsetParameter (
    @NonNull final List<RequestError> errors,
    @NonNull final APIRequest request
  ) {
    if (request.contains(OFFSET_PARAMETER_NAME)) {
      if (request.size(OFFSET_PARAMETER_NAME) > 1) {
        errors.add(this.createOffsetParameterCountError(request));
      }

      if (
        !OFFSET_PARAMETER_TEST
          .matcher(request.get(OFFSET_PARAMETER_NAME, 0))
          .find()
      ) {
        errors.add(this.createOffsetParameterContentError(request));
      }
    }
  }

  private RequestError createOffsetParameterCountError (
    @NonNull final APIRequest request
  ) {
    return new RequestParameterError(
      request,
      OFFSET_PARAMETER_NAME,
      String.join(
        "",
        "Only one \"", OFFSET_PARAMETER_NAME,
        "\" parameter is allowed by request. This parameter was setted ",
        String.valueOf(request.size(OFFSET_PARAMETER_NAME)),
        " times."
      )
    );
  }

  private RequestError createOffsetParameterContentError (
    @NonNull final APIRequest request
  ) {
    return new RequestParameterValueError(
      request,
      OFFSET_PARAMETER_NAME,
      0,
      String.join(
        "",
        "\"", OFFSET_PARAMETER_NAME,
        "\" parameter must be a non-negative number, \"",
        request.get(OFFSET_PARAMETER_NAME, 0),
        "\" given."
      )
    );
  }

  /**
  * Check if the unlimitation parameter of the given request is valid.
  *
  * @param errors A collection of error to feed.
  * @param request Request to check.
  */
  private void assertHasValidAllParameter (
    @NonNull final List<RequestError> errors,
    @NonNull final APIRequest request
  ) {
    if (request.contains(ALL_PARAMETER_NAME)) {
      if (request.size(ALL_PARAMETER_NAME) > 1) {
        errors.add(this.createAllParameterCountException(request));
      }

      if (
        !ALL_PARAMETER_TEST
          .matcher(request.get(ALL_PARAMETER_NAME, 0))
          .find()
      ){
        errors.add(this.createAllParameterContentException(request));
      }
    }
  }

  private RequestError createAllParameterCountException (
    @NonNull final APIRequest request
  ) {
    return new RequestParameterError(
      request,
      ALL_PARAMETER_NAME,
      String.join(
        "",
        "Only one \"", ALL_PARAMETER_NAME,
        "\" parameter is allowed by request. This parameter was setted ",
        String.valueOf(request.size(ALL_PARAMETER_NAME)),
        " times."
      )
    );
  }

  private RequestError createAllParameterContentException (
    @NonNull final APIRequest request
  ) {
    return new RequestParameterValueError(
      request,
      ALL_PARAMETER_NAME,
      0,
      String.join(
        "",
        "\"", ALL_PARAMETER_NAME,
        "\" filter must be empty, false or true, \"",
        request.get(ALL_PARAMETER_NAME, 0),
        "\" given."
      )
    );
  }


  /**
  * Check if not both of "all" and "first" parameters are present at the
  * same time in a request.
  *
  * @param errors A collection of error to feed.
  * @param request Request to check.
  */
  private void assertIsValidRequest (
    @NonNull final List<RequestError> errors,
    @NonNull final APIRequest request
  ) {
    if (
      request.contains(ALL_PARAMETER_NAME) &&
      request.contains(LIMIT_PARAMETER_NAME) &&
      !request.get(ALL_PARAMETER_NAME, 0).trim().equals("false")
    ) {
      errors.add(this.createAllAndFirstError(request));
    }
  }

  private RequestError createAllAndFirstError (
    @NonNull final APIRequest request
  ) {
    return new RequestError(
      request,
      String.join(
        "",
        "\"", ALL_PARAMETER_NAME,
        "\" and \"", LIMIT_PARAMETER_NAME,
        "\" parameters can't be both present on the same request. You have ",
        "to choose one of them in accordance with the result that you ",
        "expect to get. Refer to the documentation for more information."
      )
    );
  }
}
