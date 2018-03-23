package org.domus.api.request;

import java.lang.Iterable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.lang.NonNull;

/**
 * An api request.
 */
public class APIRequest implements Iterable<Map.Entry<String, String[]>>
{
  /**
   * Request parameter.
   */
  @NonNull
  private final Map<String, List<String>> _parameters;

  public static APIRequest from (@NonNull final Map<String, String[]> map) {
    final APIRequest result = new APIRequest();

    for (Map.Entry<String, String[]> entry : map.entrySet()) {
      result.add(entry.getKey(), entry.getValue());
    }

    return result;
  }

  public static APIRequest from (@NonNull final HttpServletRequest request) {
    return APIRequest.from(request.getParameterMap());
  }

  /**
   * Create a new empty request.
   */
  public APIRequest() {
    this._parameters = new HashMap<>();
  }

  /**
   * Check if a parameter is registered in this request.
   *
   * @param name The name of the parameter to find.
   *
   * @return True if the given parameter exists in this request, false otherwise.
   */
  public boolean contains (@NonNull final String name) {
    return this._parameters.containsKey(name);
  }

  /**
   * Add a value to a parameter of this request.
   *
   * @param name Name of the parameter to mutate.
   * @param value The value to add to the parameter.
   */
  public void add (@NonNull final String name, @NonNull final String value) {
    if (!this._parameters.containsKey(name)) {
      this._parameters.put(name, new ArrayList<>());
    }

    this._parameters.get(name).add(value);
  }

  /**
   * Add some values to a parameter of this request.
   *
   * @param name Name of the parameter to mutate.
   * @param values All values to add to the parameter.
   */
  public void add (@NonNull final String name, @NonNull final Iterable<String> values) {
    if (!this._parameters.containsKey(name)) {
      this._parameters.put(name, new ArrayList<>());
    }

    final List<String> parameterValues = this._parameters.get(name);

    for (final String value : values) {
      parameterValues.add(value);
    }
  }

  /**
   * Add some values to a parameter of this request.
   *
   * @param name Name of the parameter to mutate.
   * @param values All values to add to the parameter.
   */
  public void add (@NonNull final String name, @NonNull final String[] values) {
    if (!this._parameters.containsKey(name)) {
      this._parameters.put(name, new ArrayList<>(values.length));
    }

    final List<String> parameterValues = this._parameters.get(name);

    for (final String value : values) {
      parameterValues.add(value);
    }
  }

  /**
   * Add some values to a parameter of this request.
   *
   * @param name Name of the parameter to mutate.
   * @param values All values to add to the parameter.
   */
  public void add (@NonNull final String name, @NonNull final Iterator<String> values) {
    if (!this._parameters.containsKey(name)) {
      this._parameters.put(name, new ArrayList<>());
    }

    final List<String> parameterValues = this._parameters.get(name);

    while (values.hasNext()) {
      parameterValues.add(values.next());
    }
  }

  /**
   * Remove a parameter from the request.
   *
   * @param name Name of the parameter to remove.
   */
  public void remove (@NonNull final String name) {
    this._parameters.remove(name);
  }

  /**
   * Remove a value of a parameter of this request.
   *
   * @param name Name of the parameter to mutate.
   * @param index Index of the value to remove.
   */
  public void remove (@NonNull final String name, final int index) {
    final List<String> parameterValues = this._parameters.get(name);
    parameterValues.remove(index);

    if (parameterValues.size() <= 0) {
      this._parameters.remove(name);
    }
  }

  /**
   * Return the number of parameter registered in this request.
   *
   * @return The number of parameter registered in this request.
   */
  public int size () {
    return this._parameters.size();
  }

  /**
   * Return the number of values registered for a given parameter.
   *
   * @param name The name of the parameter to check.
   *
   * @return The number of values registered for the given parameter.
   */
  public int size (@NonNull final String name) {
    return this._parameters.get(name).size();
  }

  /**
   * Return all values assigned to a parameter of this request.
   *
   * @param name The name of the parameter to find.
   *
   * @return All values assigned to the given parameter.
   */
  public String[] get (@NonNull final String name) {
    final List<String> parameterValues = this._parameters.get(name);
    return parameterValues.toArray(new String[parameterValues.size()]);
  }

  /**
   * Return a value assigned to a parameter of this request.
   *
   * @param name The name of the parameter to find.
   * @param index The index of the value to return.
   *
   * @return The requested value of the given parameter.
   */
  public String get (@NonNull final String name, final int index) {
    return this._parameters.get(name).get(index);
  }

  /**
   * Return all parameters of this request.
   *
   * @return All parameters of this request.
   */
  public Set<String> parameters () {
    return this._parameters.keySet();
  }

  /**
   * @see Iterable#iterator
   */
  public Iterator<Map.Entry<String, String[]>> iterator () {
    return new APIRequestIterator(this);
  }
}
