package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.time.LocalDateTime;
import com.sky.dto.PasswordEditDTO;
import com.sky.constant.StatusConstant;

@Service
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();
        //将明文密码password 通过md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }


    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    public void  saveEmployee(EmployeeDTO employeeDTO) {

        //employee 类 是与数据库中的表对应的
        Employee employee = new Employee();

        //使用对象属性拷贝(源，目标),注意属性名一定要一致
        BeanUtils.copyProperties(employeeDTO,employee);

        //补充employee对象的属性

        //设置账号的状态，默认正常状态 1 ： 正常    0： 锁定
        employee.setStatus(StatusConstant.ENABLE);

        //设置默认密码
        String password = PasswordConstant.DEFAULT_PASSWORD;
        //将明文密码password 通过md5加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        employee.setPassword(password);

        //设置记录的创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //设置记录的创建人和修改人
        // 后期需要改为当前登录用户的id   通过  线程本地变量   来确定  修改人id  和 创建人id
        Long operationId = BaseContext.getCurrentId();

        employee.setUpdateUser(operationId);
        employee.setCreateUser(operationId);
        employeeMapper.insertEmployee(employee);

    }


    /**
     * 员工分页模糊查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //select * from employee limit 0 ,10

        //使用pagehelper 包  基于mybaits 中的拦截器 实现

        //开始分页,动态拦截器  , 把sql语句进行拼接  动态计算  limit值
        //pagehelpler.startPage()方法将 page,pagesize 封装到一个page对象
        // 并将这个page对象set进ThreadLocal(本地线程变量)的中,并计算好limit(,)中的两个值
        //mybatis进行分页查询的时候   会在ThreadLocal中取出 pagehelper通过page对象算到的 limit(,)中的值
        // 并在mybatis的的配置文件中的sql语句中  自动  添加上 limit(,)部分,
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());

        //要实现根据姓名的模糊查询  需要把 employeePageQueryDTO 中的name传入mapper

        //返回值 要遵循  pagehleper 的规则    下面的page对象  是 pagehelper包中的    不要引用错
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);

        log.info("持久层 返回的分页查询结果:{}",page);

        long total = page.getTotal();
        List<Employee> records = page.getResult();

//        将持久层传回的page对象,封装到PageResult对象中
        PageResult pageResult = new PageResult();
        pageResult.setTotal(total);
        pageResult.setRecords(records);

        return pageResult;
    }


    /**
     * 修改员工状态
     * @param newStatus
     * @param id
     */

    @Override
    public void modifyStatus(Integer newStatus, Long id) {
        // update employee set status = ? where id = ?

//        构建一个employee对象

//        Employee employee = new Employee();
//        employee.setStatus(newStatus);
//        employee.setId(id);

        //从进程中取当前登录用户的id
        Long operationId = BaseContext.getCurrentId();
        //使用builder方法创建 employee兑现
        Employee employee = Employee.builder()
                .status(newStatus)
                .id(id)
                .updateTime(LocalDateTime.now())
                .updateUser(operationId)
                .build();

        //调用持久层的 update方法
        employeeMapper.update(employee);
    }


    /**
     * 根据用户id获得用户对象
     * @param id
     * @return
     */
    @Override
    public Employee getById(long id) {
        Employee employee = employeeMapper.getById(id);
        //        掩饰员工的  MD5加密后的密码
        employee.setPassword("*************");
        return employee;
    }

    /**
     * 修改员工信息
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {

        Employee employee = new Employee();

        //        使用对象属性拷贝(源，目标),注意属性名一定要一致
        BeanUtils.copyProperties(employeeDTO,employee);

        //添加修改时间 和修改人
        Long operationId = BaseContext.getCurrentId();
        employee.setUpdateUser(operationId);

        employee.setUpdateTime(LocalDateTime.now());


        employeeMapper.update(employee);
    }

    @Override
    public Boolean modifyPassword(PasswordEditDTO passwordEditDTO) {

        //从进程中取当前登录用户的id
        Long employeeId = BaseContext.getCurrentId();

        //给dto对象赋当前登录的id
        passwordEditDTO.setEmpId(employeeId);

//        将dto对象中的 旧密码 ，新密码分别进行MD5加密
        String oldPassword = passwordEditDTO.getOldPassword();
        String newPassword = passwordEditDTO.getNewPassword();
        oldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        newPassword = DigestUtils.md5DigestAsHex(newPassword.getBytes());

//        根据员工id查询员工对象
        Employee employee = employeeMapper.getById(passwordEditDTO.getEmpId());
        log.info("修改密码时查询的员工对象：{}",employee);

        //原密码进行比对 ，若比对失败则        抛出异常OLD_PASSWORD_ERROR“原密码错误”
        if (!oldPassword.equals(employee.getPassword())) {
            //原密码错误
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR);
        }

        employee.setPassword(newPassword);
        employeeMapper.update(employee);


        return true;
    }

}
