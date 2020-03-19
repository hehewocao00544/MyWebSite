package cn.boom.mywebsite.content.service.impl;

import cn.boom.mywebsite.content.service.AdminService;
import cn.boom.mywebsite.mapper.TbAdminMapper;
import cn.boom.mywebsite.pojo.MyWebSiteResult;
import cn.boom.mywebsite.pojo.PageResult;
import cn.boom.mywebsite.pojo.TbAdmin;
import cn.boom.mywebsite.pojo.TbAdminExample;
import cn.boom.mywebsite.utils.BCryptUtil;
import cn.boom.mywebsite.utils.DateUtil;
import cn.boom.mywebsite.utils.MailUtils;
import cn.boom.mywebsite.utils.TokenUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.List;

@Service(timeout = 8000)
public class AdminServiceImpl implements AdminService {

    @Value("${TOKEN_REGISTER_URL}")
    private String TOKEN_REGISTER_URL;
    @Value("${TOKEN_REGISTER_TITLE}")
    private String TOKEN_REGISTER_TITLE;
    @Value("${TOKEN_REGISTER_CONTENT}")
    private String TOKEN_REGISTER_CONTENT;
    @Value("${TOKEN_PASSWORD_TITLE}")
    private String TOKEN_PASSWORD_TITLE;
    @Value("${TOKEN_PASSWORD_CONTENT}")
    private String TOKEN_PASSWORD_CONTENT;
    @Value("${TOKEN_REEMAIL_TITLE}")
    private String TOKEN_REEMAIL_TITLE;
    @Value("${TOKEN_REEMAIL_CONTENT}")
    private String TOKEN_REEMAIL_CONTENT;
    @Value("${DEFAULT_ADMIN_IMAGE}")
    private String DEFAULT_ADMIN_IMAGE;

    @Autowired
    private TbAdminMapper tbAdminMapper;

    /**
     * 根据用户名查询用户
     * @param admin
     * @return
     */
    @Override
    public TbAdmin findOneByAdmin(String admin) {

        TbAdminExample example = new TbAdminExample();

        TbAdminExample.Criteria criteria = example.createCriteria();

        criteria.andAdminEqualTo(admin);

        List<TbAdmin> adminList = tbAdminMapper.selectByExample(example);

        if (adminList != null && adminList.size() > 0) {

            return adminList.get(0);
        }
        return null;
    }

    @Override
    public TbAdmin findOneById(Long id) {
        return tbAdminMapper.selectByPrimaryKey(id);
    }

    @Override
    public TbAdmin findOneByEmail(String email) {

        TbAdminExample example = new TbAdminExample();

        TbAdminExample.Criteria criteria = example.createCriteria();

        criteria.andEmailEqualTo(email.toLowerCase());

        List<TbAdmin> adminList = tbAdminMapper.selectByExample(example);

        if (adminList != null && adminList.size() > 0) {
            return adminList.get(0);
        }

        return null;
    }

    /**
     * 添加用户
     * @param admin
     */
    @Override
    public MyWebSiteResult add(TbAdmin admin) {

        if (admin.getAdmin() == null || admin.getAdmin().length() == 0 || admin.getPassword() == null ||
                admin.getPassword().length() == 0 || admin.getEmail() == null || admin.getEmail().length() == 0) {
            throw new IllegalArgumentException(" Add Error ! This Admin is illegal argument. ");
        }

        if (findOneByAdmin(admin.getAdmin()) != null) {
            return MyWebSiteResult.build(400, "该用户名已存在!");
        }

        if (findOneByEmail(admin.getEmail()) != null) {
            return MyWebSiteResult.build(400, "该邮箱已绑定过账号!");
        }

        admin.setCreated(new Date());
        admin.setPassword(BCryptUtil.encode(admin.getPassword()));//加密
        admin.setToken(TokenUtils.getToken());
        admin.setStatus("-1");
        admin.setEmail(admin.getEmail().toLowerCase());//转小写
        admin.setPicUrl(DEFAULT_ADMIN_IMAGE);

        tbAdminMapper.insert(admin);

        sendTokenMail(admin);

        return MyWebSiteResult.ok();
    }

    /**
     * 激活用户
     * @param id
     * @param token
     * @return
     */
    @Override
    public MyWebSiteResult active(Long id, String token) {

        TbAdmin admin = findOneById(id);

        if (admin == null) {
            return MyWebSiteResult.build(400, "未查询到此用户！");
        }

        if (!admin.getToken().equals(token)) {
            return MyWebSiteResult.build(400, "激活码错误！");
        }else {
            admin.setStatus("0");
            admin.setToken("");
            tbAdminMapper.updateByPrimaryKey(admin);
            return MyWebSiteResult.ok();
        }
    }

    /**
     * 条件分页查询
     * @param admin
     * @param pageSize
     * @param pageNum
     * @return
     */
    @Override
    public MyWebSiteResult search(TbAdmin admin, int pageSize, int pageNum) {

        if (pageSize == 0) {
            pageSize = 5;
        }

        if (pageNum == 0) {
            pageNum = 1;
        }

        PageHelper.startPage(pageNum, pageSize);

        TbAdminExample example = new TbAdminExample();

        TbAdminExample.Criteria criteria = example.createCriteria();

        if (admin.getId() != null) {
            criteria.andIdEqualTo(admin.getId());
        }

        if (admin.getAdmin() != null && !"".equals(admin.getAdmin())) {
            criteria.andAdminLike("%" + admin.getAdmin() + "%");
        }

        if (admin.getName() != null && !"".equals(admin.getName())) {
            criteria.andNameLike("%" + admin.getName() + "%");
        }

        if (admin.getStatus() != null && !"".equals(admin.getStatus())) {
            criteria.andStatusEqualTo(admin.getStatus());
        }

        if (admin.getEmail() != null && !"".equals(admin.getEmail())) {
            criteria.andEmailEqualTo(admin.getEmail().toLowerCase());
        }

        if (admin.getPhone() != null && !"".equals(admin.getPhone())) {
            criteria.andPhoneEqualTo(admin.getPhone());
        }

        Page page = (Page) tbAdminMapper.selectByExample(example);

        List<TbAdmin> adminList = page.getResult();

        for (TbAdmin tbAdmin : adminList) {
            tbAdmin.setPassword(null); //隐藏密码
            tbAdmin.setToken(null);
        }

        PageResult result = new PageResult(page.getTotal(), adminList);

        return MyWebSiteResult.ok(result);
    }

    /***
     * 修改用户
     * @param admin
     * @return
     */
    @Override
    public MyWebSiteResult update(TbAdmin admin) {

        if (admin.getId() == null || admin.getId() <= 0) {
            return MyWebSiteResult.build(400, "参数不合法");
        }

        admin.setPassword( tbAdminMapper.selectByPrimaryKey(admin.getId()).getPassword());

        tbAdminMapper.updateByPrimaryKey(admin);
        return MyWebSiteResult.ok();
    }

    /**
     * 删除用户
     * @param ids
     * @return
     */
    @Override
    public MyWebSiteResult delete(Long[] ids) {

        if (ids == null || ids.length == 0) {
            return MyWebSiteResult.build(400, "参数不合法");
        }

        for (int i = 0; i < ids.length; i++) {

            if (findOneById(ids[i]) != null) {
                tbAdminMapper.deleteByPrimaryKey(ids[i]);
            }
        }
        
        return MyWebSiteResult.ok();
    }

    /**
     * 审核用户
     * @param ids
     * @param status
     * @return
     */
    @Override
    public MyWebSiteResult updateStatusIds(Long[] ids, String status) {

        if (status == null || !status.equals("-1") && !status.equals("0") && !status.equals("1") && !status.equals("2")
            || ids == null || ids.length == 0) {
            return MyWebSiteResult.build(400, "参数不合法");
        }

        for (int i = 0; i < ids.length; i++) {

            TbAdmin admin;
            if ((admin = findOneById(ids[i])) != null && !admin.getStatus().equals("-1")) {
                admin.setStatus(status);
                tbAdminMapper.updateByPrimaryKey(admin);
            }
        }

        return MyWebSiteResult.ok();
    }

    /**
     * 修改用户密码
     * @param admin
     * @return
     */
    @Override
    public MyWebSiteResult updatePassword(TbAdmin admin) {

        if (admin == null || admin.getId() == null || admin.getStatus() == null
                || admin.getEmail() == null || admin.getAdmin() == null
                || admin.getPassword() == null || admin.getToken() == null) {
            return MyWebSiteResult.build(400, "参数不合法");
        }

        TbAdmin oldAdmin = tbAdminMapper.selectByPrimaryKey(admin.getId());

        if (oldAdmin == null) {
            return  MyWebSiteResult.build(400, "参数不合法");
        }

        if (!oldAdmin.getToken().equals(admin.getToken())) {
            return MyWebSiteResult.build(400, "验证码有误");
        }

        admin.setPassword(BCryptUtil.encode(admin.getPassword()));//加密
        admin.setToken(null);
        tbAdminMapper.updateByPrimaryKey(admin);

        return MyWebSiteResult.ok();
    }

    /**
     * 发送密码验证邮箱
     * @param id
     * @return
     */
    @Override
    public MyWebSiteResult sendPasswordEmail(Long id) {

        TbAdmin tbAdmin = tbAdminMapper.selectByPrimaryKey(id);

        if (tbAdmin == null) {
            return MyWebSiteResult.build(400, "验证码有误");
        }

        String token = TokenUtils.getToken();
        tbAdmin.setToken(token);
        tbAdminMapper.updateByPrimaryKey(tbAdmin);
        //发送邮箱
        sendPasswordEmail(tbAdmin);

        return MyWebSiteResult.ok();
    }

    /**
     * 修改用户邮箱
     * @param admin
     * @return
     */
    @Override
    public MyWebSiteResult updateEmail(TbAdmin admin) {

        if (admin == null || admin.getId() == null
                || admin.getEmail() == null || admin.getToken() == null) {
            return MyWebSiteResult.build(400, "参数不合法");
        }

        TbAdmin oldAdmin = tbAdminMapper.selectByPrimaryKey(admin.getId());

        if (oldAdmin == null) {
            return  MyWebSiteResult.build(400, "参数不合法");
        }

        if (!oldAdmin.getToken().equals(admin.getToken())) {
            return MyWebSiteResult.build(400, "验证码有误");
        }

        oldAdmin.setToken(null);
        oldAdmin.setEmail(admin.getEmail());

        tbAdminMapper.updateByPrimaryKey(oldAdmin);

        return MyWebSiteResult.ok();
    }

    /**
     * 发送邮箱绑定验证邮箱
     * @param admin
     * @return
     */
    @Override
    public MyWebSiteResult sendReEmail(TbAdmin admin) {

        if (admin == null || admin.getId() == null|| admin.getEmail() == null) {
            return MyWebSiteResult.build(400, "参数不合法");
        }

        String token = TokenUtils.getToken();

        TbAdmin oldAdmin = tbAdminMapper.selectByPrimaryKey(admin.getId());

        if (oldAdmin == null) {
            return MyWebSiteResult.build(400, "参数不合法");
        }

        oldAdmin.setToken(token);
        tbAdminMapper.updateByPrimaryKey(oldAdmin);

        //发送邮箱
        sendReEmailTo(oldAdmin);

        return MyWebSiteResult.ok();
    }

    /**
     * 发送邮箱绑定验证邮箱
     * @param admin
     * @return
     */
    private void sendReEmailTo(TbAdmin admin){

        String time =  "</br>" + DateUtil.getNowTime();
        String content = admin.getAdmin() + "您好:</br>" + TOKEN_PASSWORD_CONTENT + "</br>您的验证码是：" + admin.getToken() + time;
        MailUtils.sendMail(admin.getEmail(), content, TOKEN_PASSWORD_TITLE);
    }


    /**
     * 发送密码验证邮箱
     * @param admin
     */
    private void sendPasswordEmail(TbAdmin admin){

        String time =  "</br>" + DateUtil.getNowTime();
        String content = admin.getAdmin() + "您好:</br>" + TOKEN_PASSWORD_CONTENT + "</br>您的验证码是：" + admin.getToken() + time;
        MailUtils.sendMail(admin.getEmail(), content, TOKEN_PASSWORD_TITLE);
    }

    /**
     * 发送激活邮箱
     * @param admin
     */
    private void sendTokenMail(TbAdmin admin){

        String url = TOKEN_REGISTER_URL + "active.do?id=" + admin.getId() + "&token=" + admin.getToken();
        String msg = "<a href='" + url + "'>点击此链接或复制链接到地址栏，访问该地址以激活账号</a>";
        String time =  "</br>" + DateUtil.getNowTime();
        MailUtils.sendMail(admin.getEmail(),admin.getAdmin() + "您好:</br>"  + TOKEN_REGISTER_CONTENT + msg + time,TOKEN_REGISTER_TITLE);
    }
}