package com.venus.crud.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mongodb.client.result.UpdateResult;
import com.venus.crud.document.ScanSession;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.UpdateDefinition;

@ExtendWith(MockitoExtension.class)
class ScanSessionVersionInitializerTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Test
    void scansWithoutVersionReceiveVersionZero() {
        when(mongoTemplate.updateMulti(any(Query.class), any(UpdateDefinition.class), eq(ScanSession.class)))
                .thenReturn(UpdateResult.acknowledged(2, 2L, null));
        ArgumentCaptor<Query> query = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<UpdateDefinition> update = ArgumentCaptor.forClass(UpdateDefinition.class);

        new ScanSessionVersionInitializer(mongoTemplate).fillMissingVersions();

        verify(mongoTemplate).updateMulti(query.capture(), update.capture(), eq(ScanSession.class));
        assertThat(query.getValue().getQueryObject()).isEqualTo(new Document("version", null));
        assertThat(update.getValue().getUpdateObject()).isEqualTo(new Document("$set", new Document("version", 0L)));
    }

    @Test
    void mongoOutOfReachDoesNotStopTheApplication() {
        when(mongoTemplate.updateMulti(any(Query.class), any(UpdateDefinition.class), eq(ScanSession.class)))
                .thenThrow(new DataAccessResourceFailureException("Mongo fora do ar"));

        assertThatCode(() -> new ScanSessionVersionInitializer(mongoTemplate).fillMissingVersions())
                .doesNotThrowAnyException();
    }
}
