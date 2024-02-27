package com.hxmall.shop.product.product.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hxmall.shop.common.utils.PageUtils;
import com.hxmall.shop.common.utils.Query;
import com.hxmall.shop.product.product.dao.CategoryDao;
import com.hxmall.shop.product.product.entity.CategoryEntity;
import com.hxmall.shop.product.product.service.CategoryBrandRelationService;
import com.hxmall.shop.product.product.service.CategoryService;
import com.hxmall.shop.product.product.vo.Catalog3Vo;
import com.hxmall.shop.product.product.vo.Catelog2Vo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    /**
     * 我们在注入的过程中要注意依赖的循环注入
     * 1，field注入
     * 2，setter注入
     * 3，构造器注入（Spring没有办法处理构造器注入，所以我们要手动处理）
     * <p>
     * 循环依赖在Spring中最终的解决方案：在三级缓存中进行曝光
     */

    private final CategoryBrandRelationService categoryBrandRelationService;
    private final StringRedisTemplate strRedisTemplate;

    public CategoryServiceImpl(CategoryBrandRelationService categoryBrandRelationService, StringRedisTemplate strRedisTemplate) {
        this.categoryBrandRelationService = categoryBrandRelationService;
        this.strRedisTemplate = strRedisTemplate;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        //1.查询出来所有的分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //2,组装成父子结构的树形结构
        //找到所有的一级菜单
        List<CategoryEntity> treeList =
                categoryEntities.stream().filter(categoryEntity -> categoryEntity.getParentCid() == 0)
                        .map((categoryEntity) -> {
                            //找到一级下所有的子菜单
                            categoryEntity.setChildrenList(getChildren(categoryEntity, categoryEntities));
                            return categoryEntity;
                        }).sorted((o1, o2) -> {
                            return (o1.getSort() == null ? 0 : o1.getSort()) - (o2.getSort() == null ? 0 : o2.getSort());
                        }).collect(Collectors.toList());
        return treeList;
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        paths = findParentPath(catelogId, paths);
        //我们收集的时候顺序的，前端需要逆序
        Collections.reverse(paths);
        return paths.toArray(new Long[paths.size()]);
    }

    /**
     * 更新分类以及关联表分类信息
     *
     * @param category
     */
    @Override
    public void updateCategoryBrandRelation(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isEmpty(category.getName())) {
            categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
        }
    }

    /**
     * 页面初始化加载一级分类
     *
     * @return
     */
    @Override
    public List<CategoryEntity> getLevelCategorys() {
        return baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("cat_level", 1));
    }

    @Cacheable(value = {"catalogJson"}, key = "#root.methodName")
    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJson() {
        //1.查出来所有的分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //查询所有的一级分类
        List<CategoryEntity> level1Categorys = getCategroyEnties(categoryEntities, 0L);
        System.out.println("调用getCatalogJson");
        Map<String, List<Catelog2Vo>> collect =
                level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                    //每拿到一个一级分类,需要查询它的二级分类
                    List<CategoryEntity> level2Categorys = getCategroyEnties(categoryEntities, v.getCatId());
                    List<Catelog2Vo> catelog2Vos = null;
                    if (level2Categorys != null) {
                        catelog2Vos = level2Categorys.stream().map(l2 -> {
                            Catelog2Vo catelog2Vo = new Catelog2Vo(l2.getCatId().toString(), l2.getName(),
                                    v.getCatId().toString(), null);
                            List<CategoryEntity> level3Categorys = getCategroyEnties(categoryEntities, l2.getCatId());
                            if (level3Categorys != null) {
                                List<Catalog3Vo> catalog3Vos =
                                        level2Categorys.stream().map(l3 -> new Catalog3Vo(l3.getCatId().toString(),
                                                l3.getName(), l2.getCatId().toString())).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(catalog3Vos);
                            }
                            return catelog2Vo;
                        }).collect(Collectors.toList());

                    }
                    return catelog2Vos;
                }));
        return collect;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromResdis() {
        //缓存中获取
        String catalogJson = strRedisTemplate.opsForValue().get("catalogJson");
        if (!StringUtils.isEmpty(catalogJson)) {
            return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catelog2Vo>>>() {
            });
        }
        //1.查出来所有的分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //查询所有的一级分类
        List<CategoryEntity> level1Categorys = getCategroyEnties(categoryEntities, 0L);
        Map<String, List<Catelog2Vo>> collect =
                level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                    //每拿到一个一级分类,需要查询它的二级分类
                    List<CategoryEntity> level2Categorys = getCategroyEnties(categoryEntities, v.getCatId());
                    List<Catelog2Vo> catelog2Vos = null;
                    if (level2Categorys != null) {
                        catelog2Vos = level2Categorys.stream().map(l2 -> {
                            Catelog2Vo catelog2Vo = new Catelog2Vo(l2.getCatId().toString(), l2.getName(),
                                    v.getCatId().toString(), null);
                            List<CategoryEntity> level3Categorys = getCategroyEnties(categoryEntities, l2.getCatId());
                            if (level3Categorys != null) {
                                List<Catalog3Vo> catalog3Vos =
                                        level2Categorys.stream().map(l3 -> new Catalog3Vo(l3.getCatId().toString(),
                                                l3.getName(), l2.getCatId().toString())).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(catalog3Vos);
                            }
                            return catelog2Vo;
                        }).collect(Collectors.toList());

                    }
                    return catelog2Vos;
                }));
        //缓存中没有的情况我们从数据库查询后放入缓存
        strRedisTemplate.opsForValue().set("catalogJson", JSON.toJSONString(collect), 1, TimeUnit.DAYS);
        return collect;
    }

    @Override
    public Map<String, List<Catelog2Vo>> getCatalogJsonFromDB() {
        //1.查出来所有的分类
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        //查询所有的一级分类
        List<CategoryEntity> level1Categorys = getCategroyEnties(categoryEntities, 0L);
        Map<String, List<Catelog2Vo>> collect =
                level1Categorys.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
                    //每拿到一个一级分类,需要查询它的二级分类
                    List<CategoryEntity> level2Categorys = getCategroyEnties(categoryEntities, v.getCatId());
                    List<Catelog2Vo> catelog2Vos = null;
                    if (level2Categorys != null) {
                        catelog2Vos = level2Categorys.stream().map(l2 -> {
                            Catelog2Vo catelog2Vo = new Catelog2Vo(l2.getCatId().toString(), l2.getName(),
                                    v.getCatId().toString(), null);
                            List<CategoryEntity> level3Categorys = getCategroyEnties(categoryEntities, l2.getCatId());
                            if (level3Categorys != null) {
                                List<Catalog3Vo> catalog3Vos =
                                        level2Categorys.stream().map(l3 -> new Catalog3Vo(l3.getCatId().toString(),
                                                l3.getName(), l2.getCatId().toString())).collect(Collectors.toList());
                                catelog2Vo.setCatalog3List(catalog3Vos);
                            }
                            return catelog2Vo;
                        }).collect(Collectors.toList());

                    }
                    return catelog2Vos;
                }));
        return collect;
    }

    private List<CategoryEntity> getCategroyEnties(List<CategoryEntity> categoryEntities, long parentCid) {
        return categoryEntities.stream().filter(categoryEntity -> categoryEntity.getParentCid() == parentCid).collect(Collectors.toList());
    }

    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        paths.add(catelogId);
        CategoryEntity categoryEntity = this.getById(catelogId);
        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 递归查找所有的分类的子菜单
     *
     * @param root             菜单的上一级节点
     * @param categoryEntities 所有的菜单数据
     * @return
     */
    private List<CategoryEntity> getChildren(CategoryEntity root, List<CategoryEntity> categoryEntities) {

        List<CategoryEntity> childrenList = categoryEntities.stream().filter(categoryEntity -> {
            //过滤条件
            //当前分类的父id等于要查询的分类id
            //说明当前分类的是查询的子菜单
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //继续查找当前分类的子菜单
            categoryEntity.setChildrenList(getChildren(categoryEntity, categoryEntities));
            return categoryEntity;
        }).sorted((o1, o2) -> {
            return (o1.getSort() == null ? 0 : o1.getSort()) - (o2.getSort() == null ? 0 : o2.getSort());
        }).collect(Collectors.toList());

        return childrenList;
    }

}