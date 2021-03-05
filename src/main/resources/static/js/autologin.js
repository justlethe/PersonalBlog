//判断是否需要自动登录
if (sessionStorage.getItem("verify") == null) {
    $.ajax({
        type: 'get', // 提交方式 get/post
        url: 'http://47.119.131.193/blog/autoLogin', // 需要提交的 url

        success: function(data) {
            if (data.code == 200) {

                sessionStorage.setItem("verify", "true");

                // 将登录状态设置为已登录
                sessionStorage.setItem("code", data.code);
                // 保存当前用户的权限
                sessionStorage.setItem("permission", data.data.user.permission);

                // 将用户信息转化为string再存进sessionStorage
                sessionStorage.setItem("user", JSON.stringify(data.data.user));
            }
        }
    });
}