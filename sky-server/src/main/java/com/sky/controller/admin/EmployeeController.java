package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口" )
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation(value = "员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),//jwtProperties.getAdminSecretKey()：JWT 的密钥（用于签名和验证令牌）
                jwtProperties.getAdminTtl(), //jwtProperties.getAdminTtl()：JWT 的有效时间（Time To Live，例如 24 小时）。
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @ApiOperation(value = "员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }




    /**
     * 新增员工功能,  默认密码:123456  @RequestBody 将前端传过来的 json 转换为 注解后面的对象（字段名相同就匹配）
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增员工")
    public Result save(@RequestBody EmployeeDTO employeeDTO){

        log.info("新增员工{}",employeeDTO);
        //username(数据库中 值具有唯一性) 通过添加的时候  数据库  提示的异常信息 来创立异常处理器  去禁止注册同username的用户,在异常处理器中处理的
        employeeService.saveEmployee(employeeDTO);
        return Result.success();
    }


    /**
     * 员工分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "员工分页查询模糊查询")
//    不需要自己 使用@PathVariable  注解 从路径中取变量值
//    使用 EmployeePageQueryDTO 对象 ,里面的属性名与路径中的变量名一致 ,
//    springMVC会自己 把路径中的变量  封装 到 EmployeePageQueryDTO 对象中的属性
    public Result<PageResult> pageQuery(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("员工分页模糊查询{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 修改员工状态
     * @param newStatus
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation(value = "修改员工状态")
    /**
     * 在 HTTP 请求中，? 前后分别代表
     *      1.路径（Path）：? 前面的部分是请求的路径（Path），它表示服务器上的资源位置
     *      2.查询参数（Query Parameters）：? 后面的部分是查询参数，用于向服务器传递额外的信息或数据 是键值对
     */
    /**@PathVariable("param1") :路径中只有值没有名称 ,后端在路径中添加一个参数{param1}用于标记 该值,
     *         controller层中 方法的形参中 加入 @PathVariable("param1") 表示接收 用param1标记的路径中的值 . 后面 接 接收该参数的变量
     *@RequestParam("param2")  接收请求中 名为 param2 的参数.后面 接 接收该参数的变量
     */
    public Result modifyStatus (@PathVariable("status") Integer newStatus,@RequestParam("id") Long id){
        log.info("启用禁用员工账号:状态 {},员工id:{}",newStatus,id);
        employeeService.modifyStatus(newStatus,id);
        return Result.success();
    }



    /**
     * 根据用户id获得用户对象
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询用户")
    public Result<Employee> getById (@PathVariable("id") long id ){
        Employee employee = employeeService.getById(id);
        return Result.success(employee);
    }

    /**
     * 修改员工信息
     * @param employeeDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改员工信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("修改员工信息:{}",employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }


    /**
     * 修改员工密码
     *
     * @return
     */
    @PutMapping("/editPassword")
    @ApiOperation("员工修改密码")
    public Result editPassword(@RequestBody PasswordEditDTO passwordEditDTO){
        log.info("修改密码员工的信息：{}",passwordEditDTO);
        Boolean b = employeeService.modifyPassword(passwordEditDTO);
        if (b) {
            return Result.success();
        }
        return Result.error("修改密码失败！");
    }

}
