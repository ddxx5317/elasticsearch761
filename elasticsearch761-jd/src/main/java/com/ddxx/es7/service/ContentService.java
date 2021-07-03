package com.ddxx.es7.service;

import com.alibaba.fastjson.JSON;
import com.ddxx.es7.bean.Content;
import com.ddxx.es7.utils.HtmlUtils;
import com.sun.scenario.effect.impl.prism.ps.PPSZeroSamplerPeer;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: DDxx
 * @Date: 2021/7/3
 */
@Component
public class ContentService {
    private final static String INDEX_NAME = "jd_goods";

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient elasticsearchClient;

    public Boolean parse(String keyWord) throws Exception {
        final List<Content> contents = new HtmlUtils().parse(keyWord);
        BulkRequest request = new BulkRequest();
        request.timeout(TimeValue.timeValueSeconds(3));

        for (int i = 0; i < contents.size(); i++) {
            request.add(new IndexRequest(INDEX_NAME)
                    .source(JSON.toJSONString(contents.get(i)), XContentType.JSON));
        }
        final BulkResponse bulkResponse = elasticsearchClient.bulk(request, RequestOptions.DEFAULT);
        return !bulkResponse.hasFailures();
    }

    public List<Map<String,Object>> searchPage(String keyword,int pageNo,int pageSize) throws Exception {
        if (pageNo <= 1){
            pageNo = 1;
        }
        final SearchRequest searchRequest = new SearchRequest(INDEX_NAME);

        final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(pageNo);
        searchSourceBuilder.size(pageSize);

        final TermQueryBuilder queryBuilder = QueryBuilders.termQuery("name", keyword);
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.timeout(TimeValue.timeValueSeconds(20));

        //高亮
        final HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        final SearchResponse searchResponse = elasticsearchClient.search(searchRequest, RequestOptions.DEFAULT);
        List<Map<String,Object>> result = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            final Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            final HighlightField name = highlightFields.get("name");
            final Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (name != null){
                final Text[] fragments = name.fragments();
                String newName = "";
                for (Text fragment : fragments) {
                    newName += fragment;
                }
                sourceAsMap.put("name",newName);
            }
            result.add(sourceAsMap);
        }
        return result;
    }
}
