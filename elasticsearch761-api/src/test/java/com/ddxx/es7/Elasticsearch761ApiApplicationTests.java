package com.ddxx.es7;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        final User user = new User("孙悟空", "花果山", 500,new Date());
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
        final User user = new User("齐天大圣", "花果山888号", 500,new Date());
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
        final List<User> users = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        users.add(new User("孙悟空","花果山",500, toDate(now.plusDays(1))));
        users.add(new User("红孩儿","火焰山",200, toDate(now.plusDays(2))));
        users.add(new User("嫦娥","广寒宫",100, toDate(now.plusDays(4))));
        users.add(new User("猪八戒","高老庄",300, toDate(now.plusDays(5))));
        users.add(new User("敖丙","东海龙宫",400, toDate(now.plusDays(6))));
        users.add(new User("孙悟空","花果山",800, toDate(now.plusDays(8))));
        users.add(new User("孙悟空","水帘洞",600, toDate(now.plusDays(3))));

        System.out.println(JSON.toJSONString(users));
        for (int i = 0; i < users.size(); i++) {
            request.add(new IndexRequest("es761_users")
                    .id(""+(i+1))
                    .source(JSON.toJSONString(users.get(i)),XContentType.JSON));
        }
        final BulkResponse bulkResponse = elasticsearchClient.bulk(request, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(!bulkResponse.hasFailures()));
        System.out.println(JSON.toJSONString(bulkResponse.buildFailureMessage()));
    }

    @Test
    void searchQuery() throws Exception {
        final SearchRequest searchRequest = new SearchRequest("es761_users");

        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        List<Integer> ages = new ArrayList<>();
        ages.add(500);
        ages.add(600);

        //等值条件
        final BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.termQuery("name", "孙悟空"));

        //IN条件
        BoolQueryBuilder agesQueryBuilder = QueryBuilders.boolQuery();
        ages.forEach(age ->{
            agesQueryBuilder.should(QueryBuilders.termQuery("age", age));
        });
        queryBuilder.must(agesQueryBuilder);

        //范围条件
        String start = "2021-09-10 00:00:00";
        String end ="2021-09-10 23:23:59";
        String format = "yyyy-MM-dd HH:mm:ss";

        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("birthDay")
                //.format(format)
                .from(start).to(end);
        queryBuilder.must(rangeQueryBuilder);


        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(2));

        //过滤字段
        String[] includeFields = new String[] {"name", "age"};
        String[] excludeFields = new String[] {"_type"};
        searchSourceBuilder.fetchSource(includeFields, excludeFields);

        searchRequest.source(searchSourceBuilder);
        final SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse.getHits()));

        final String jsonString = JSON.toJSONString(searchResponse.getHits().getHits());
        System.out.println(jsonString);

        System.out.println("------------------------");
        List<User> users = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            //final Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            final User user = JSONObject.parseObject(hit.getSourceAsString(), User.class);
            users.add(user);
        }
        System.out.println(JSON.toJSONString(users));

    }

    public static Date toDate(LocalDateTime localDateTime) {
        if(null == localDateTime) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }
}
