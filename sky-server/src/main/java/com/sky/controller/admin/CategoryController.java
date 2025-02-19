package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Slf4j

//对类进行说明
@Api(tags = "分类套餐 接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增  分类套餐
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增分类套餐")
    public Result save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类{}",categoryDTO);

        //name(数据库中 值具有唯一性) 通过添加的时候  数据库  提示的异常信息 来创立异常处理器  去禁止注册同username的用户,在异常处理器中处理的
        categoryService.saveCategory(categoryDTO);
        return Result.success();
    }

    /**
     * 分类套餐  分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分类套餐 分页查询模糊查询")
//    不需要自己 使用@PathVariable  注解 从路径中取变量值
//    使用 CategoryPageQueryDTO 对象 ,里面的属性名与路径中的变量名一致 ,
//    springMVC会自己 把路径中的变量  封装 到 CategoryPageQueryDTO 对象中的属性
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分类套餐 分页查询模糊查询{}",categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改 分类套餐 状态
     * @param newStatus
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "修改 分类套餐 状态")
    public Result modifyStatus (@PathVariable("status") Integer newStatus,@RequestParam("id") Long id){
        log.info("修改分类套餐的信息：  更改到状态{}，要修改分类套餐的id：{}");
        categoryService.modifyStatus(newStatus,id);
        return null;
    }

    @PutMapping
    @ApiOperation(value = "修改 分类套餐 信息")
    public Result update(@RequestBody CategoryDTO categoryDTO){
        log.info("要修改 分类套餐 的信息：{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation(value = "删除 分类套餐 ")
    public Result<String> deleteById(@RequestParam("id") long id){
        log.info("要删除的分类套餐的id：{}",id);
        categoryService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation(value = "根据 分类套餐  类型查询  分类套餐 ")
    public Result<List<Category>> list(@RequestParam("type") Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }

}
