package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 员工注册
     * @param employeeDTO
     */
    void saveEmployee(EmployeeDTO employeeDTO);

    /**
     * 员工分页模糊查询
     * @param employeePageQueryDTO
     * @return
     */
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 修改员工状态
     * @param newStatus
     * @param id
     */
    void modifyStatus(Integer newStatus, Long id);

    /**
     * 根据用户id获得用户对象
     * @param id
     * @return employee 对象
     */
    Employee getById(long id);

    /**
     * 修改员工信息
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);

    /**
     * 员工修改密码
     * @param passwordEditDTO
     * @return
     */
    Boolean modifyPassword(PasswordEditDTO passwordEditDTO);

}
