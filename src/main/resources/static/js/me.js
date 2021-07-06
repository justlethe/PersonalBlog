// 手机端菜单项的显示与隐藏
$('#menu-toggle').click(function () {
    $('.menu-item').toggleClass('mobile-hide');
});

// 头像的菜单打开与关闭
$('.item.login.after')
    .popup({
        inline: true,
        hoverable: true,
        lastResort: true,
    });

$(function () {

    //判断是否已经登录用户，200为已登录
    if (sessionStorage.getItem("code") == 200) {
        // 将登录按钮hide，头像show
        $(".login.before").addClass("hide");
        $(".login.after").removeClass("hide");
        $('.login img').attr('src',JSON.parse(sessionStorage.getItem("user")).avator)
        if (sessionStorage.getItem("permission") == "true") {
            $("a.admin.button").parent().removeClass("hide")
        }
    } else {
        // 将登录按钮show，头像hide
        $(".login.after").addClass("hide");
        $(".login.before").removeClass("hide");
    }
})

/* 头像菜单的功能实现 */
// 退出登录
function logOut() {

    sessionStorage.removeItem("code");
    sessionStorage.removeItem("permission");
    sessionStorage.removeItem("user");
    sessionStorage.removeItem("verify");

    $.ajax({
        type: 'get', // 提交方式 get/post
        url: 'http://meiko2021.net.cn/blog/logout', // 需要提交的 url
    });

    location.reload();

}

// 登录前登录链接的弹出层
$('.ui.modal').modal('attach events', ".item.login.before", 'show');

// 弹出层的切换
$('.tabular.menu .item').tab();


// 登录页面的表单验证
$('.ui.form.login').form({
    inline: true,
    fields: {
        username: {
            identifier: 'username',
            rules: [{
                type: 'empty',
                prompt: '用户名不可为空'
            }]
        },
        password: {
            identifier: 'password',
            rules: [{
                type: 'empty',
                prompt: '密码不可为空'
            }]
        }
    },
    // onFailure: function() {
    //     console.log('表单验证不通关');
    // },
    onSuccess: function (event) {
        event.preventDefault();

        $.ajax({
            type: 'post', // 提交方式 get/post
            url: 'http://meiko2021.net.cn/blog/login', // 需要提交的 url
            data: {
                'username': $('.login.form').form('get value', 'username'),
                'password': $('.login.form').form('get value', 'password'),
                'auto': $('.login.form').form('get value', 'auto')
            },
            success: function (data) { // data 保存提交后返回的数据，一般为 json 数据
                // 此处可对 data 作相关处理
                console.log(data.code)
                console.log(data.data.user)
                // code==200代表用户登录成功，否则登录失败
                if (data.code == 200) {

                    // 将登录状态设置为已登录
                    sessionStorage.setItem("code", data.code);
                    // 保存当前用户的权限
                    sessionStorage.setItem("permission", data.data.user.permission);

                    // 将用户信息转化为string再存进sessionStorage
                    sessionStorage.setItem("user", JSON.stringify(data.data.user));
                    // console.log(JSON.parse(sessionStorage.getItem("user")));

                    location.reload();

                } else {
                    //清空输入框
                    $(".form.login input").val("");

                    // 将错误信息显示于界面上
                    cocoMessage.config({
                        duration: 10000,
                    })
                    cocoMessage.error("登录失败，请检查用户名密码是否正确", 3000);
                }
            },
        });
    }
});


// 注册页面的表单验证
$('.ui.form.register').form({
    inline: true,
    fields: {
        username: {
            identifier: 'username',
            rules: [{
                type: 'empty',
                prompt: '用户名不可为空'
            }, {
                type: 'regExp[/^[-_a-zA-Z0-9]{4,16}$/]',
                prompt: '用户名需要4-16个由字母、数字、下划线或减号组成'
            }]
        },
        password1: {
            identifier: 'password-1',
            rules: [{
                type: 'empty',
                prompt: '密码不可为空'
            }, {
                type: 'regExp[/(?=.*([a-zA-Z].*))(?=.*[0-9].*)[a-zA-Z0-9-*/+.~!@#$%^&*()]{8,16}$/]',
                prompt: '至少8-16个字符，至少1个字母和1个数字'
            }]
        },
        password2: {
            identifier: 'password-2',
            rules: [{
                type: 'empty',
                prompt: '密码不可为空'
            }, {
                type: 'match[password-1]',
                prompt: '密码不一致'
            }]
        }
    },
    onSuccess: function (event) {
        event.preventDefault();
        $.ajax({
            type: 'post', // 提交方式 get/post
            url: 'http://meiko2021.net.cn/blog/register', // 需要提交的 url
            data: {
                'username': $('.register.form').form('get value', 'username'),
                'password1': $('.register.form').form('get value', 'password-1'),
                'password2': $('.register.form').form('get value', 'password-2')
            },
            success: function (data) { // data 保存提交后返回的数据，一般为 json 数据
                // 此处可对 data 作相关处理
                console.log(data.code)
                console.log(data.data.user)
                // code==200代表用户注册成功，否则注册失败
                if (data.code == 200) {

                    // 将登录状态设置为已登录
                    sessionStorage.setItem("code", data.code);
                    // 保存当前用户的权限
                    sessionStorage.setItem("permission", data.data.user.permission);

                    // 将用户信息转化为string再存进sessionStorage
                    sessionStorage.setItem("user", JSON.stringify(data.data.user));
                    // console.log(JSON.parse(sessionStorage.getItem("user")));

                    location.reload();

                } else {
                    //清空输入框
                    $(".form.register input").val("");

                    // 将错误信息显示于界面上
                    cocoMessage.config({
                        duration: 10000,
                    })
                    cocoMessage.error(data.data.errorMsg, 3000);
                }
            },
        });
    }
});


// 返回页面顶部
$('.button.goTop').click(function () {
    $(window).scrollTo(10, 500)
})

// rocket的显示与否
new Waypoint({
    element: document.getElementById('basic-waypoint'),
    handler: function (direction) {
        if (direction == 'down') {
            $('.tools.menu').show(200)
        } else {
            $('.tools.menu').hide(500)
        }
    },
    offset: '-20%'
})

// 运行时间统计
function secondToDate(second) {
    if (!second) {
        return 0;
    }
    var time = new Array(0, 0, 0, 0, 0);
    if (second >= 365 * 24 * 3600) {
        time[0] = parseInt((second / (365 * 24 * 3600))-51);
        second %= 365 * 24 * 3600;
    }
    if (second >= 24 * 3600) {
        time[1] = parseInt((second / (24 * 3600))-72);
        second %= 24 * 3600;
    }
    if (second >= 3600) {
        time[2] = parseInt(second / 3600);
        second %= 3600;
    }
    if (second >= 60) {
        time[3] = parseInt(second / 60);
        second %= 60;
    }
    if (second > 0) {
        time[4] = second;
    }
    return time;
}
function setTime() {
    /*此处为网站的创建时间*/
    let timestamp = Math.round((new Date().getTime()+ 8 * 60 * 60 * 1000) / 1000);
    let currentTime = secondToDate(timestamp);
    let currentTimeHtml = currentTime[0] + '年' + currentTime[1] + '天'
        + currentTime[2] + '时' + currentTime[3] + '分' + currentTime[4]
        + '秒';
    $("#run_time").text("该网站已运行:"+currentTimeHtml);
}
setInterval(setTime, 1000);
