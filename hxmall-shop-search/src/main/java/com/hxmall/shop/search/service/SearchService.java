package com.hxmall.shop.search.service;

import com.hxmall.shop.search.vo.SearchParam;
import com.hxmall.shop.search.vo.SearchResult;

/**
 * author:黄龙强
 * time:{2024/1/4}
 * version:1.0
 */
public interface SearchService {
    SearchResult search(SearchParam searchParam);
}
