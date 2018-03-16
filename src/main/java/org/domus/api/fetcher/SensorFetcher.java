package org.domus.api.fetcher;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import org.domus.api.data.Sensor;
import org.domus.api.database.Database;

import org.hibernate.Session;
import org.hibernate.Query;

public class SensorFetcher implements DataFetcher<Sensor> {
  public Sensor get(DataFetchingEnvironment environment) {
    try  {
      Session session = Database.sessions().openSession();

      Query<Sensor> query = session.createQuery(
        String.join(
          "",
          "SELECT sensor FROM ", Sensor.class.getName(), " sensor ",
          "WHERE sensor.identifier = :identifier"
        ), Sensor.class
      );

      query.setParameter("identifier", environment.getArgument("identifier"));

      System.out.println(query.getFetchSize());

      return query.getSingleResult();
    } catch (Exception e) {
      throw new Error(e);
    }
  }
}
