package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.ParamMissException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;

import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
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

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对

        //String secretPwd02=org.springframework.util.Digestutils.md5DigestAsHex(password.getBytes());
        String secretPwd = DigestUtils.md5Hex(password);

        if (!secretPwd.equals(employee.getPassword())) {
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

    /*新增员工*/
    @Override
    public int insertEmployee(EmployeeDTO employeeDTO) {

        if(StringUtils.isBlank(employeeDTO.getUsername()) || StringUtils.isBlank(employeeDTO.getPhone ())){
            throw new ParamMissException("传入参数缺失!");
        }
        //2.将employeeDTO交给employee对象
        Employee targetEmp = new Employee ();
        BeanUtils.copyProperties(employeeDTO,targetEmp); //将同名同类型属性做赋值

        //3.
        String hexPwd = DigestUtils.md5Hex(PasswordConstant.DEFAULT_PASSWORD);
        targetEmp.setPassword(hexPwd);

         targetEmp.setStatus(1);
         targetEmp.setCreateTime(LocalDateTime.now());
         targetEmp.setUpdateTime(LocalDateTime.now());
         targetEmp.setCreateUser(1L);
         targetEmp.setUpdateUser(1L);

        int insertEmps = employeeMapper.insertEmps(targetEmp);
        return insertEmps;
    }


    @Override
    public PageResult findPage(EmployeePageQueryDTO PageQuery) {
        PageHelper.startPage(PageQuery.getPage(),PageQuery.getPageSize());
        if(StringUtils.isNotBlank(PageQuery.getName ())){
            PageQuery.setName ("%"+PageQuery.getName () +"%");
        }
        // SL执行 返回结果
        Page<Employee> employeePage = employeeMapper.findByName (PageQuery);

        long total = employeePage.getTotal();
        List<Employee> result = employeePage.getResult();
        return new PageResult(total,result);

    }
}
