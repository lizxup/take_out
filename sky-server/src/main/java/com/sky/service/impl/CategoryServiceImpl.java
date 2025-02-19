package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.EmployeeDTO;
import com.sky.entity.Category;
import com.sky.entity.Employee;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;


    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;


    /**
     * 新增分类套餐
     * @param categoryDTO
     */
    @Override
    public void saveCategory(CategoryDTO categoryDTO) {

        Category category = new Category();
        //        使用对象属性拷贝(源，目标),注意属性名一定要一致
        BeanUtils.copyProperties(categoryDTO,category);

        //补充category对象的属性

//        设置分类的默认状态
        category.setStatus(StatusConstant.DISABLE);

        //根据系统时间   设置记录的创建时间和修改时间
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());

        //从线程本地变量中获得 操作者id
        Long operationId = BaseContext.getCurrentId();

        category.setUpdateUser(operationId);
        category.setCreateUser(operationId);
        log.info("即将添加的分类对象：{}",category);

        categoryMapper.insertCategory(category);
    }

    /**
     * 分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {

        //使用pagehelper 包
        PageHelper.startPage(categoryPageQueryDTO.getPage(),categoryPageQueryDTO.getPageSize());

        //要实现根据name的模糊查询   根据 type的值查询    需要把 categoryPageQueryDTO 中的name，type传入mapper

        //返回值 要遵循  pagehleper 的规则    下面的page对象  是 pagehelper包中的    不要引用错
        Page<Category> page = categoryMapper.pageQuery(categoryPageQueryDTO);

        log.info("持久层 返回的分页查询结果:{}",page);

        long total = page.getTotal();
        List<Category> records = page.getResult();

//        将持久层传回的page对象,封装到PageResult对象中
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setRecords(records);

        return pageResult;
    }


    /**
     * 修改 分类状态修改
     * @param newStatus
     * @param id
     */
    @Override
    public void modifyStatus(Integer newStatus, Long id) {
        //从进程中取当前登录用户的id
        Long operationId = BaseContext.getCurrentId();
        //使用builder方法创建 employee兑现
        Category category  = Category.builder()
                .status(newStatus)
                .id(id)
                .updateTime(LocalDateTime.now())
                .updateUser(operationId)
                .build();

        //调用持久层的 update方法

        categoryMapper.update(category);
    }

    /**
     * 更新分类信息
     * @param categoryDTO
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO,category);
        //添加修改时间 和修改人
        Long operationId = BaseContext.getCurrentId();
        category.setUpdateUser(operationId);

        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.update(category);
    }


    /**
     * 根据id删除分类
     * @param id
     */
    @Override
    public void deleteById(long id) {
        //查询当前分类是否关联了菜品，如果关联了 菜品 抛出业务 异常
        Integer count = dishMapper.countByCategoryId(id);
        if (count > 0 ){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        //查询当前分类是否关联了套餐，如果关联了 套餐 抛出业务 异常
        count = setmealMapper.countByCategory(id);
        if (count > 0){
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }

        //删除  分类 套餐
        categoryMapper.deleteById(id);
    }


    /**
     * 根据分类的类型（type）查询分类  如果没有规定type的值 则查询全部分类
     * @param type
     * @return
     */
    @Override
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }
}
