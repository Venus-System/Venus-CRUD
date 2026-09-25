package com.venus.crud.config;

import com.venus.crud.document.ScanSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.stereotype.Component;

@Component
public class MongoIndexInitializer {

    private static final Logger log = LoggerFactory.getLogger(MongoIndexInitializer.class);

    private static final String SCAN_ID_INDEX = "ux_scan_sessions_scan_id";
    private static final String ADMIN_QUEUE_INDEX = "idx_scan_sessions_status_created_at";

    private final MongoTemplate mongoTemplate;

    public MongoIndexInitializer(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ensureIndexes() {
        IndexOperations scanSessionIndexes = mongoTemplate.indexOps(ScanSession.class);
        ensure(scanSessionIndexes, SCAN_ID_INDEX, new Index()
                .on("scan_id", Sort.Direction.ASC)
                .unique()
                .named(SCAN_ID_INDEX));
        ensure(scanSessionIndexes, ADMIN_QUEUE_INDEX, new Index()
                .on("status", Sort.Direction.ASC)
                .on("created_at", Sort.Direction.DESC)
                .named(ADMIN_QUEUE_INDEX));
    }

    private void ensure(IndexOperations indexOperations, String name, Index index) {
        try {
            indexOperations.ensureIndex(index);
        } catch (RuntimeException ex) {
            log.error("Nao foi possivel criar o indice {} em scan_sessions. Se ainda houver documentos antigos sem scan_id, "
                    + "apague-os e reinicie a aplicacao; ate la, dois reenvios simultaneos do mesmo scan podem duplicar.", name, ex);
        }
    }
}
