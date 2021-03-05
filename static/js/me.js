// 手机端菜单项的显示与隐藏
$('#menu-toggle').click(function() {
    $('.menu-item').toggleClass('mobile-hide');
});

// 头像的菜单打开与关闭
$('.item.login.after')
    .popup({
        popup: $('.custom.popup'),
        on: 'click'
    });

// 登录前登录链接的弹出层
$('.ui.modal').modal('attach events', ".item.login.before", 'show');

// 弹出层的切换
$('.tabular.menu .item').tab();


// 自定义表单验证规则
// $.fn.form.settings.rules.方法名 = function(value) {
//     方法体
// }

// 密码是否一致
$.fn.form.settings.rules.samePassword = function(value) {
    console.log($('#password2').val());
    console.log($('#password1').val());
    console.log($('#password2').val() == $('#password1').val());
    if ($('#password2').val() == $('#password1').val()) {
        return true;
    } else {
        return false;
    }
};

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
    onFailure: function() {
        console.log('表单验证不通关');
    },
    // onSuccess: function() {
    //     console.log($('.ui.form').form('get value', ['username', 'password']))
    // }
});

// 注册页面的表单验证
$('.ui.form.register').form({
    on: 'blur',
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
                type: 'samePassword',
                prompt: '密码不一致'
            }]
        }
    },
    onFailure: function() {
        console.log('表单验证不通关');
    },
    // onSuccess: function() {
    //     console.log($('.ui.form').form('get value', ['username', 'password']))
    // }
});

// 返回页面顶部
$('.rocket').click(function() {
    $(window).scrollTo(10, 500)
})

// rocket的显示与否
var waypoint = new Waypoint({
    element: document.getElementById('basic-waypoint'),
    handler: function(direction) {
        if (direction == 'down') {
            $('.tools.menu').show(200)
        } else {
            $('.tools.menu').hide(500)
        }
    },
    offset: '-20%'
})


/* 以上为共有 */
/* 以下为私有 */


// img-msg 关于页面的图片透明显示文字
$webfont.load(".about-img-msg", "125c5fea2db84179846130d8461ff1a9", "MHGHagoromoTHK-Light");
$webfont.draw();

/* 管理页 */

// 发布博客前的表单验证
$('.ui.form.blogInput').form({
    on: 'blur',
    inline: true,
    fields: {
        title: {
            identifier: 'title',
            rules: [{
                type: 'empty',
                prompt: '标题不可为空'
            }]
        },
        indexPicture: {
            identifier: 'indexPicture',
            rules: [{
                type: 'empty',
                prompt: '图片地址不可为空'
            }]
        }
    },
    onFailure: function() {
        console.log('表单验证不通关');
    },
    // onSuccess: function() {
    //     console.log($('.ui.form').form('get value', ['username', 'password']))
    // }
});

// 菜单侧边栏的触发
$('.ui.sidebar')
    .sidebar({
        context: $('.bottom.segment')
    })
    .sidebar('attach events', '.siderbar.menu.item');

// 侧边菜单显示对应内容 
$('.sidebar.menu .item').tab();

/* 发布页 */

//下拉框的弹出与收缩
$('.ui.selection.dropdown').dropdown();