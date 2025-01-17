package xyz.lirui123.mywebsite.manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import xyz.lirui123.mywebsite.pojo.TbAdmin;
import xyz.lirui123.mywebsite.response.ResponseResult;
import xyz.lirui123.mywebsite.manager.service.AdminService;

@Controller
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 注册
     * @param admin
     * @return
     */
    @RequestMapping("/admin/add")
    @ResponseBody
    public ResponseResult add(@RequestBody TbAdmin admin) {
        return adminService.add(admin);
    }

    /**
     * 激活
     * @param id
     * @param token
     * @return
     */
    @RequestMapping("/active")
    public String active(Long id, String token) {

        ResponseResult result = adminService.active(id, token);
        if (result.getStatus() == 200) {
            return "redirect:/active.html";
        }
        return "redirect:/error.html";
    }

    /**
     * 获取登录信息
     * @return
     */
    @RequestMapping("/admin/getLoginInfo")
    @ResponseBody
    public ResponseResult getLoginInfo(){
        String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
        TbAdmin admin = adminService.findOneByAdmin(loginName);
        admin.setToken(null);
        return ResponseResult.ok(admin);
    }

    /**
     * 分页条件搜索
     * @param admin
     * @param rows
     * @param page
     * @return
     */
    @RequestMapping("/admin/search")
    @ResponseBody
    public ResponseResult search(@RequestBody TbAdmin admin, int rows, int page) {
        return adminService.search(admin, rows, page);
    }

    /**
     * 根据Email查询用户
     * @param email
     * @return
     */
    @RequestMapping("/admin/findOneByEmail")
    @ResponseBody
    public ResponseResult findOneByEmail(String email) {

        if (email == null) {
            return ResponseResult.build(400, "参数不合法");
        }

        TbAdmin admin = adminService.findOneByEmail(email);
        if (admin == null) {
            return ResponseResult.build(400, "未查询到此用户");
        }
        admin.setToken(null);
        return ResponseResult.ok(admin);
    }


    /**
     * 修改用户
     * @param admin
     * @return
     */
    @RequestMapping("/admin/update")
    @ResponseBody
    public ResponseResult update(@RequestBody TbAdmin admin) {
        return adminService.update(admin);
    }

    /**
     * 删除用户
     * @param ids
     * @return
     */
    @RequestMapping("/admin/deleteIds")
    @ResponseBody
    public ResponseResult deleteIds(Long[] ids){
        return adminService.delete(ids);
    }

    /**
     * 审核用户
     * @param ids
     * @return
     */
    @RequestMapping("/admin/updateStatusIds")
    @ResponseBody
    public ResponseResult updateStatusIds(Long[] ids,String status){
        return adminService.updateStatusIds(ids,status);
    }


    /**
     * 修改用户密码
     * @param admin
     * @return
     */
    @RequestMapping("/admin/updatePassword")
    @ResponseBody
    public ResponseResult updatePassword(@RequestBody TbAdmin admin) {
        return adminService.updatePassword(admin);
    }

    /**
     * 发送密码验证邮箱
     * @param id
     * @return
     */
    @RequestMapping("/admin/sendPasswordEmail")
    @ResponseBody
    public ResponseResult sendPasswordEmail(Long id){
        return adminService.sendPasswordEmail(id);
    }


    /**
     * 修改用户邮箱地址
     * @param admin
     * @return
     */
    @RequestMapping("/admin/updateEmail")
    @ResponseBody
    public ResponseResult updateEmail(@RequestBody TbAdmin admin) {
        return adminService.updateEmail(admin);
    }

    /**
     * 发送邮箱绑定验证邮箱
     * @param admin
     * @return
     */
    @RequestMapping("/admin/sendReEmail")
    @ResponseBody
    public ResponseResult sendReEmail(@RequestBody TbAdmin admin){
        return adminService.sendReEmail(admin);
    }

}
