package com.paymentic.infra.events;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.common.protocol.types.Field.Str;
import org.jboss.logging.Logger;

@ApplicationScoped
public class EventRepository  implements PanacheRepository<Event> {
  private static final Logger LOGGER = Logger.getLogger(EventRepository.class);
  private static final String ERROR = "Event %s already handled!!!";

  public boolean shouldHandle(Event event){
    try {
      persist(event);
      return true;
    }catch (Exception exception){
      LOGGER.error(String.format(ERROR,event.getId().toString()));
      return false;
    }
  }

}
