package com.venus.crud.config;

import com.mongodb.client.result.UpdateResult;
import com.venus.crud.document.ScanSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

@Component
public class ScanSessionVersionInitializer {

    private static final Logger log = LoggerFactory.getLogger(ScanSessionVersionInitializer.class);

    private static final String VERSION_FIELD = "version";
    private static final long FIRST_VERSION = 0L;

    private final MongoTemplate mongoTemplate;

    public ScanSessionVersionInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void fillMissingVersions() {
        try {
            UpdateResult result = mongoTemplate.updateMulti(
                    Query.query(Criteria.where(VERSION_FIELD).is(null)),
                    Update.update(VERSION_FIELD, FIRST_VERSION),
                    ScanSession.class);
            if (result.getModifiedCount() > 0) {
                log.info("{} sessoes de scan sem version receberam version {}", result.getModifiedCount(), FIRST_VERSION);
            }
        } catch (RuntimeException ex) {
            log.error("Nao foi possivel preencher o version das sessoes de scan antigas; aprovar ou recusar uma delas "
                    + "continua falhando ate a aplicacao subir de novo com o Mongo acessivel.", ex);
        }
    }
}
