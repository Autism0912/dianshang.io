package com.hxmall.shop.search.service.imp;



import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.hxmall.shop.common.to.SkuEsModel;
import com.hxmall.shop.common.utils.R;
import com.hxmall.shop.search.config.HxmallElasticSearchConfig;
import com.hxmall.shop.search.constant.EsConstant;
import com.hxmall.shop.search.feign.ProductFeignService;
import com.hxmall.shop.search.service.SearchService;
import com.hxmall.shop.search.vo.AttrRespVo;
import com.hxmall.shop.search.vo.BrandVo;
import com.hxmall.shop.search.vo.SearchParam;
import com.hxmall.shop.search.vo.SearchResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * author:黄龙强
 * time:{2024/1/4}
 * version:1.0
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {
    private final RestHighLevelClient restHighLevelClient;
    private final ProductFeignService productFeignService;

    public SearchServiceImpl(RestHighLevelClient restHighLevelClient, ProductFeignService productFeignService) {
        this.restHighLevelClient = restHighLevelClient;
        this.productFeignService = productFeignService;
    }

    @Override
    public SearchResult search(SearchParam searchParam) {
        //1.准备检索请求
       SearchRequest searchRequest = buildSearchRequest(searchParam);
       //2.执行检索请求
        SearchResponse searchResponse = null;
        SearchResult result = null;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, HxmallElasticSearchConfig.COMMON_OPTIONS);
            //3.分析响应数据
            result = buildSearchRestult(searchResponse,searchParam);
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }

    private SearchResult buildSearchRestult(SearchResponse searchResponse, SearchParam searchParam) {
        SearchResult searchResult = new SearchResult();
        //获取到我们查询的结果
        SearchHits hits = searchResponse.getHits();

        List<SkuEsModel> esModels = new ArrayList<>();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits) {
                String sourceAsString = hit.getSourceAsString();
                //es中检索获取到的对象
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                if (!StringUtils.isEmpty(searchParam.getKeyword())) {
                    //1.1获取标题的高亮属性
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String highlightTitle = skuTitle.getFragments()[0].string();
                    //1.2 设置文本高亮
                    esModel.setSkuTitle(highlightTitle);
                }
                esModels.add(esModel);
            }
        }

        searchResult.setProducts(esModels);

        //2.当前所有商品涉及到属性信息
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        ParsedNested attrAgg = searchResponse.getAggregations().get("attrAgg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo();
            //2.1获取属性id
            attrVo.setAttrId(bucket.getKeyAsNumber().longValue());
            //2.2获取属性的名字
            String attrName =
                    ((ParsedStringTerms) bucket.getAggregations().get("attrNameAgg")).getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            //2.3获取属性值
            List<String> attrValues =
                    ((ParsedStringTerms) bucket.getAggregations().get("attrValueAgg")).getBuckets().stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);
            attrVos.add(attrVo);

        }

        searchResult.setAttrs(attrVos);

        //3.当前所有商品涉及到的品牌信息
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        ParsedLongTerms brandAgg = searchResponse.getAggregations().get("brandAgg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();
            //3.1 获取品牌的id
            long brandId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);
            //3.2 获取到品牌的名称
            String brandName =
                    ((ParsedStringTerms) bucket.getAggregations().get("brandNameAgg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            //3.3 获取品牌的图片
            String brandImg =
                    ((ParsedStringTerms) bucket.getAggregations().get("brandImgAgg")).getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            brandVos.add(brandVo);
        }

        searchResult.setBrands(brandVos);

        //4.当前商品信息涉及的到分类信息
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalogAgg = searchResponse.getAggregations().get("catalogAgg");
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //4.1 获取分类id
            long catalogId = bucket.getKeyAsNumber().longValue();
            catalogVo.setCatalogId(catalogId);
            //4.2 获取分类名称
            String catalogName =
                    ((ParsedStringTerms) bucket.getAggregations().get("catalogNameAgg")).getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogVos.add(catalogVo);
        }

        searchResult.setCatalogs(catalogVos);

        //5.获取分页信息
        searchResult.setPageNum(searchParam.getPageNum());
        //总记录数
        long total = hits.getTotalHits().value;
        searchResult.setTotal(total);
        //总页码
        int totalPages = (int)(total/ EsConstant.PRODUCT_PAGE_SIZE + 0.999999999999999);
        searchResult.setTotalPages(totalPages);
        //设置导航页
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++) {
            pageNavs.add(i);
        }
        searchResult.setPageNavs(pageNavs);

        //6.构建面包屑的导航功能
        if (searchParam.getAttrs()!= null && searchParam.getAttrs().size() > 0){
            List<SearchResult.NavVo> navVos = searchParam.getAttrs().stream().map(attr -> {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                String[] s = attr.split("_");
                navVo.setNavValue(s[1]);
                //属性的信息需要去商品系统查询
                R r = productFeignService.info(Long.parseLong(s[0]));
                searchResult.getAttrsIds().add(Long.parseLong(s[0]));

                if (r.getCode() == 0) {
                    AttrRespVo data = r.getData(new TypeReference<AttrRespVo>(){});
                    navVo.setNavName(data.getAttrName());
                } else {
                    //失败了就设置为id
                    navVo.setNavName(s[0]);
                }

                String repalce = replaceQueryString(searchParam, attr, "attrs");
                navVo.setLink("http://search.hxmall.com/list.html?" + repalce);
                return navVo;
            }).collect(Collectors.toList());
            searchResult.setNavs(navVos);
        }

        //7.品牌和分类信息
        if (searchParam.getCatelog3Id()!= null && searchParam.getBrandId().size()>0) {
            List<SearchResult.NavVo> navs = searchResult.getNavs();
            SearchResult.NavVo navVo = new SearchResult.NavVo();
            navVo.setNavName("品牌");
            //远程调用商品服务查询品牌信息
            R r = productFeignService.brandInfo(searchParam.getBrandId());
            if (r.getCode() == 0) {
                List<BrandVo> data = r.getData("data", new TypeReference<List<BrandVo>>() {
                });
                StringBuffer buffer = new StringBuffer();
                String replace="";
                for (BrandVo brandVo : data) {
                    buffer.append(brandVo.getBrandName()).append(";");
                    replace = replaceQueryString(searchParam, brandVo.getBrandId().toString(), "brandId");
                }
                navVo.setNavValue(buffer.toString());
                navVo.setLink("http://search.hxmall.com/list.html?" + replace);
            }
        }

        return searchResult;
    }
    /**
     * 将浏览器的字符替换为指定的java字符
     * @param searchParam
     * @param value
     * @param key
     * @return
     */
    private String replaceQueryString(SearchParam searchParam, String value,String key) {
        String encode =null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            //浏览器的空格和java是不一样的
            encode = encode.replace("+","%20");
            encode = encode.replace("%28","(").replace("%29",")");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return searchParam.get_queryString().replace("&"+key+"="+encode,"");
    }

    /**
     * 构建检索请求的
     * @param searchParam
     * @return
     */
    private SearchRequest buildSearchRequest(SearchParam searchParam) {
        //构建dsl语句
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //1.模糊匹配，过滤
        BoolQueryBuilder boolQuery =  QueryBuilders.boolQuery();

        if (!StringUtils.isEmpty(searchParam.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",searchParam.getKeyword()));
        }

        if (searchParam.getCatelog3Id()!= null){
            boolQuery.filter(QueryBuilders.termQuery("catalogId",searchParam.getCatelog3Id()));
        }

        if (searchParam.getBrandId()!= null && searchParam.getBrandId().size() > 0){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",searchParam.getBrandId()));
        }

        //属性查询
        if (searchParam.getAttrs()!= null && searchParam.getAttrs().size() > 0){
            for (String attr : searchParam.getAttrs()) {
                BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
                String[] s = attr.split("_");
                //检索属性的id
                String attrId = s[0];
                String[] attrValue = s[1].split(":");
                boolQueryBuilder.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                boolQueryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue",attrValue));
                //构建一个嵌入式的Query，每一个都需要嵌入到nested查询
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", boolQueryBuilder, ScoreMode.None);
                boolQuery.filter(nestedQueryBuilder);
            }
        }

        //库存过滤
        if (searchParam.getHasStock()!= null){
            boolQuery.filter(QueryBuilders.termQuery("hasStock",searchParam.getHasStock() == 1));
        }

        //价格区间
        if (!StringUtils.isEmpty(searchParam.getSkuPrice())){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] s = searchParam.getSkuPrice().split("_");
            if (s.length == 2){
                rangeQuery.gte(s[0]).lte(s[1]);
            }else if (s.length == 1){
                if (searchParam.getSkuPrice().startsWith("_")){
                    rangeQuery.lte(s[0]);
                }
                if (searchParam.getSkuPrice().endsWith("_")){
                    rangeQuery.gte(s[0]);
                }
            }
            boolQuery.filter(rangeQuery);
        }

        //把之前所有的查询条件进行封装
        sourceBuilder.query(boolQuery);

        //排序
        if (!StringUtils.isEmpty(searchParam.getSort())){
            String sort = searchParam.getSort();
            String[] s = sort.split("_");
            SortOrder sortOrder = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(s[0],sortOrder);
        }

        //分页
        sourceBuilder.from((searchParam.getPageNum()-1)* EsConstant.PRODUCT_PAGE_SIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGE_SIZE);

        //高亮
        if (!StringUtils.isEmpty(searchParam.getKeyword())){
            sourceBuilder.highlighter(new HighlightBuilder().field("skuTitle").preTags("<b style='color:red'>").postTags("</b>"));
        }

        //聚合分析
        //1.1 品牌聚合
        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brandAgg");
        brandAgg.field("brandId").size(50);
        //1.1.1 品牌的子聚合
        brandAgg.subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brandImgAgg").field("brandImg").size(1));
        //将品牌聚合加入到sourceBuilder
        sourceBuilder.aggregation(brandAgg);

        //1.2 分类聚合
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalogAgg").field("catalogId").size(20);
        catalogAgg.subAggregation(AggregationBuilders.terms("catalogNameAgg").field("catalogName").size(1));
        //将分类的聚合加入到sourceBuilder
        sourceBuilder.aggregation(catalogAgg);

        //1.3 属性聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attrAgg", "attrs");
        //1.3.1 属性id聚合
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg").field("attrs.attrId");
        //1.3.2 聚合分析出attrId对应的属性名
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName").size(1));
        //1.3.3 聚合分析出attrId对应的属性值
        attrIdAgg.subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue").size(50));
        //将属性聚合加入到nested
        attrAgg.subAggregation(attrIdAgg);
        sourceBuilder.aggregation(attrAgg);
        log.info("\n构建语句：->\n"+sourceBuilder.toString());
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }
}
