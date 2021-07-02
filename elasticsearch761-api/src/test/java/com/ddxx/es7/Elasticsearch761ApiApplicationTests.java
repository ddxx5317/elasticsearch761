package com.ddxx.es7;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
class Elasticsearch761ApiApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient elasticsearchClient;

    @Test
    void createIndex() throws Exception {
        CreateIndexRequest request = new CreateIndexRequest("es761");
        final CreateIndexResponse createIndexResponse = elasticsearchClient.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(createIndexResponse));

    }

    @Test
    void deleteIndex() throws Exception {
        DeleteIndexRequest request = new DeleteIndexRequest("es761");
        final AcknowledgedResponse acknowledgedResponse = elasticsearchClient.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(acknowledgedResponse));
    }

    @Test
    void addDoc() throws Exception {
        final User user = new User("孙悟空", "花果山", 500);
        IndexRequest request = new IndexRequest("es761");
        request.id("1");
        request.timeout(TimeValue.timeValueDays(1));
        request.source(JSON.toJSONString(user),XContentType.JSON);
        final IndexResponse indexResponse = elasticsearchClient.index(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(indexResponse));
        System.out.println(JSON.toJSONString(indexResponse.status()));
    }

    @Test
    void getDoc() throws Exception {
        GetRequest request = new GetRequest("es761","1");
        //request.fetchSourceContext(new FetchSourceContext(false));
        //request.storedFields("_none_");
        final boolean exists = elasticsearchClient.exists(request, RequestOptions.DEFAULT);
        if (exists){
            final GetResponse getResponse = elasticsearchClient.get(request, RequestOptions.DEFAULT);
            System.out.println(getResponse.getSourceAsString());
            System.out.println(JSON.toJSONString(getResponse));
        }
    }
    @Test
    void updateDoc() throws Exception {
        UpdateRequest request = new UpdateRequest("es761","1");
        request.timeout(TimeValue.timeValueSeconds(1));
        final User user = new User("齐天大圣", "花果山888号", 500);
        request.doc(JSON.toJSONString(user),XContentType.JSON);
        final UpdateResponse updateResponse = elasticsearchClient.update(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(updateResponse));
    }

    @Test
    void deleteDoc() throws Exception {
        DeleteRequest request = new DeleteRequest("es761","1");
        request.timeout(TimeValue.timeValueSeconds(1));
        final DeleteResponse deleteResponse = elasticsearchClient.delete(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(deleteResponse));
    }

    @Test
    void bulkAddDoc() throws Exception {
        BulkRequest request = new BulkRequest();
        request.timeout(TimeValue.timeValueSeconds(3));
        final ArrayList<User> users = new ArrayList<>();
        users.add(new User("孙悟空","花果山",500));
        users.add(new User("红孩儿","火焰山",200));
        users.add(new User("嫦娥","广寒宫",400));

        for (int i = 0; i < users.size(); i++) {
            request.add(new IndexRequest("es761-bulk")
                    .id(""+(i+1))
                    .source(JSON.toJSONString(users.get(i)),XContentType.JSON));
        }
        final BulkResponse bulkResponse = elasticsearchClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(bulkResponse));
    }
}
