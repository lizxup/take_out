package com.sky.service;

import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

import java.time.LocalDateTime;
import java.util.List;

public interface CategoryService {
    /**
     * 新增  分类套餐
     * @param categoryDTO
     */
    void saveCategory(CategoryDTO categoryDTO);


    /**
     * 分类套餐 分页模糊查询
     * @param categoryPageQueryDTO
     * @return
     */
    PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);


    void modifyStatus(Integer newStatus, Long id);


    void update(CategoryDTO categoryDTO);

    void deleteById(long id);


    /**
     * 根据分类的类型（type）查询分类  如果没有规定type的值 则查询全部分类
     * @param type
     * @return
     */
    List<Category> list(Integer type);
}
