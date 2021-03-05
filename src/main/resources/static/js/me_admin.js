// 初始化提示信息插件
cocoMessage.config({
    duration: 10000,
});

// 初始化tab选项卡
layui.use('element', function () {
    let element = layui.element;
});

// user_table的设置
layui.use('table', function () {
    let table = layui.table;

    table.render({
        elem: '#user_table',
        url: 'http://47.119.131.193/blog/admin/getUsers',
        cellMinWidth: 70, //全局定义常规单元格的最小宽度，layui 2.2.1 新增

        toolbar: '#toolbarDemo',

        // 设置表头
        cols: [[
            {type: 'checkbox', width: 40, fixed: 'left'},
            {field: 'userId', title: 'ID', width: 50, type: 'numbers'},
            {field: 'username', title: '用户名', width: 110, sort: true},
            {field: 'nickname', title: '昵称', width: 110, sort: true},
            {field: 'permission', width: 100, title: '权限'},
            {field: 'email', title: '邮件', width: 220},
            {field: 'status', title: '状态', width: 80, templet: '#statusTpl'},
            {
                field: 'createDate',
                title: '创建日期',
                width: 200,
                sort: true,
                templet: '<div>{{ layui.util.toDateString(d.createDate,"yyyy-MM-dd &nbsp;&nbsp; HH:mm:ss") }}</div>'
            },
            {fixed: 'right', title: '操作', width: 150, toolbar: '#barDemo'}
        ]],
        done: function () {
            // 用户权限
            $("[data-field='permission']").children().each(function () {
                if ($(this).text() == 'true') {
                    $(this).text("管理员");
                } else if ($(this).text() == 'false') {
                    $(this).text("普通用户");
                }
            })
        },

        // 后端数据显示
        response: {
            statusCode: 200,
        },
        parseData: function (data) {
            // console.log(data.code);
            // console.log(data.data.users.list);
            return {
                "code": data.code, //解析接口状态
                "msg": data.msg, //解析提示文本
                "count": data.data.users.total, //解析数据长度
                "data": data.data.users.list //解析数据列表
            };
        },
        // 分页开启
        page: {
            limit: 10,
            layout: ['count', 'prev', 'page', 'next', 'skip'] //自定义分页布局
        }
    });

    //user_table头工具栏事件
    table.on('toolbar(user_table)', function (obj) {
        let checkStatus = table.checkStatus(obj.config.id);
        switch (obj.event) {
            case 'addNewUser':
                // 新增用户功能modal的弹出
                $('.ui.create_user.modal')
                    .modal('setting', 'closable', false)
                    .modal('show');
                break;
            case 'deleteAllUser':
                if (checkStatus.data.length == 0) {
                    cocoMessage.warning('未选中任何用户!', 3000);
                    break;
                }
                let username = [];
                for (let user of checkStatus.data) {
                    username.push(user.username)
                }
                layer.confirm('确认删除用户' + username + '吗?', function (index) {
                    table.reload('user_table', {
                        url: 'http://47.119.131.193/blog/admin/delUser',
                        method: 'post',
                        where: {
                            username: JSON.stringify(username)
                        },
                    });
                    layer.close(index);
                })
                break;
        }
        ;
    });

    //user_table行工具事件
    table.on('tool(user_table)', function (obj) {
        let data = obj.data;
        //console.log(obj)
        if (obj.event === 'del') {
            layer.confirm('真的删除用户名为' + data.username + '的用户吗', function (index) {
                obj.del();
                table.reload('user_table', {
                    url: 'http://47.119.131.193/blog/admin/delUser',
                    method: 'post',
                    where: {
                        username: data.username
                    }
                });
                layer.close(index);
            });
        } else if (obj.event === 'edit') {

            // 修改详情模态框的值
            $('span.username').text(data.username);
            $(".edit_input[name='nickname']").val(data.nickname);
            $(".edit_input[name='email']").val(data.email);
            console.log(data.permission);
            if (!data.permission) {
                $("input[name='permission'][value='0']").prop('checked', true)
            }
            $('span.create_time').text(layui.util.toDateString(data.createDate, "yyyy-MM-dd  HH:mm:ss"));
            $('span.update_time').text(layui.util.toDateString(data.updateDate, "yyyy-MM-dd  HH:mm:ss"));

            // 用户详情的弹出
            $('.standard.test.modal')
                .modal({
                    closable: false,
                    onApprove: function () {
                        table.reload('user_table', {
                            url: 'http://47.119.131.193/blog/admin/updateUser',
                            method: 'post',
                            where: {
                                username: $('span.username').text(),
                                nickname: $(".edit_input[name='nickname']").val(),
                                email: $(".edit_input[name='email']").val(),
                                permission: $("input[name='permission']:checked").val()
                            }
                        });
                        return true;
                    }
                })
                .modal('show');
        }
    });
});

// 切换显示或隐藏已注销的用户
layui.use('form', function () {
    let form = layui.form;

    form.on('switch(showLogoutUsers)', function (data) {
        // console.log(data.elem.checked)
        sessionStorage.setItem("showLogoutUser", data.elem.checked);
        let table = layui.table;
        table.reload('user_table', {
            url: 'http://47.119.131.193/blog/admin/getUsers',
            method: 'post',
            where: {
                showFlag: data.elem.checked
            },
            page: {
                curr: 1
            }
        });
    })
});

// 输入用户名时同步输入用户昵称
$("input[name='username']").on('keyup', function () {
    $("input[name='nickname']").val($(this).val());
});


// 新增用户的表单验证
$('.ui.form.create_user').form({
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
            }, {
                type: 'regExp[/(?=.*([a-zA-Z].*))(?=.*[0-9].*)[a-zA-Z0-9-*/+.~!@#$%^&*()]{8,16}$/]',
                prompt: '至少8-16个字符，至少1个字母和1个数字'
            }]
        },
        email: {
            identifier: 'email',
            rules: [{
                type: 'empty',
                prompt: '邮箱不可为空'
            }, {
                type: 'regExp[/^[A-Za-z0-9]+([_\\.][A-Za-z0-9]+)*@([A-Za-z0-9\\-]+\\.)+[A-Za-z]{2,6}$/]',
                prompt: '只允许英文字母、数字、下划线、英文句号组成'
            }]
        }
    },
    // onFailure: function() {
    //     console.log('表单验证不通关');
    // },
    onSuccess: function (event) {
        event.preventDefault();

        console.log($(this).form('get values'))

        $.ajax({
            type: 'post', // 提交方式 get/post
            url: 'http://47.119.131.193/blog/admin/addUsers', // 需要提交的 url
            data: {
                user: JSON.stringify($(this).form('get values'))
            },
            success: function (data) { // data 保存提交后返回的数据，一般为 json 数据
                // 此处可对 data 作相关处理
                console.log(data.code)

                // code==200
                if (data.code == 200) {
                    cocoMessage.config({
                        duration: 10000,
                    })
                    cocoMessage.success("创建成功", 3000);
                    $('.ui.create_user.modal').modal('hide');
                    let table = layui.table;
                    table.reload('usertable', {
                        url: 'http://47.119.131.193/blog/admin/getUsers',
                        method: 'post',
                        where: {
                            showFlag: sessionStorage.getItem("showLogoutUser")
                        }
                    });
                } else {
                    cocoMessage.config({
                        duration: 10000,
                    })
                    // 将错误信息显示于界面上
                    cocoMessage.error(data.data.errorMsg, 3000);
                }
            },
        });
    }
});


// 用户详情modal内的input框编辑设置
$('.edit.icon').click(function () {
    // console.log("点击了");
    // console.log($(this));
    $(this).prev().removeClass('edit_input').removeAttr('readonly').focus();
})

$('.edit_input').blur(function () {
    // console.log($(this));
    $(this).attr("readonly", "readonly").addClass('edit_input');
})


var BlogList;
// category_table的设置
layui.use('table', function () {
    let table = layui.table;

    table.render({
        elem: '#category_table',
        url: 'http://47.119.131.193/blog/admin/getCategories',
        cellMinWidth: 70, //全局定义常规单元格的最小宽度，layui 2.2.1 新增

        toolbar: '#category_toolbar',

        // 设置表头
        cols: [[
            {type: 'checkbox', width: 50, fixed: 'left'},
            {field: 'categoryId', title: 'ID', width: 50, type: 'numbers'},
            {field: 'categoryName', title: '类别名', width: 220, sort: true},
            {
                field: 'title',
                title: '相关博客',
                width: 320,
                templet: function (d) {
                    var bloglist = "";
                    let i = 0;
                    if (d.blogList.length != 0) {
                        for (; i < d.blogList.length; i++) {
                            bloglist += d.blogList[i].title + '，';
                        }
                    }
                    return bloglist.slice(0, -1);
                }
            },
            {fixed: 'right', title: '操作', minwidth: 150, toolbar: '#barDemo'}
        ]],

        // 后端数据显示
        response: {
            statusCode: 200,
        },
        parseData: function (data) {
            // console.log(data.code);
            // console.log(data.data.users.list);
            BlogList = data.data.blogList;
            return {
                "code": data.code, //解析接口状态
                "msg": data.msg, //解析提示文本
                "count": data.data.total, //解析数据长度
                "data": data.data.categories //解析数据列表
            };
        },
        page: {
            layout: ['count'] //自定义分页布局
        }
    });

    //category_table头工具栏事件
    table.on('toolbar(category_table)', function (obj) {
        let checkStatus = table.checkStatus(obj.config.id);
        switch (obj.event) {
            case 'addNewCategory':
                // 新增分类功能modal的弹出
                $('.ui.create_category.modal')
                    .modal({
                        closable: false,
                        onApprove: function () {
                            let name = $(this).find("input[name='category']").val();
                            if (name == "") {
                                cocoMessage.warning("类别名不可为空", 0)
                            } else {
                                $.ajax({
                                    url: "http://47.119.131.193/blog/admin/addCategory",
                                    type: 'post',
                                    data: {
                                        name: name
                                    },
                                    success: function (res) {
                                        if (res.code == 200) {
                                            table.reload('category_table');
                                            cocoMessage.success("添加成功", 3000);
                                            $('.ui.create_category.modal').modal('hide');
                                        } else {
                                            cocoMessage.error(res.data.errorMsg, 3000);
                                        }
                                    }
                                })
                            }
                            return false;
                        }
                    })
                    .modal('show');
                break;
            case 'deleteAllCategory':
                if (checkStatus.data.length == 0) {
                    cocoMessage.warning('未选中任何分类!', 3000);
                    break;
                }
                let categoryName = [];
                for (let category of checkStatus.data) {
                    categoryName.push(category.categoryName)
                }
                layer.confirm('确认删除分类 ' + categoryName + ' 吗?', function (index) {
                    $.ajax({
                        url: 'http://47.119.131.193/blog/admin/delCategory',
                        type: 'post',
                        data: {
                            category: JSON.stringify(categoryName)
                        },
                        success: function (res) {
                            if (res.code == 200) {
                                table.reload('category_table');
                                cocoMessage.success(res.data.successMsg, 3000);
                            } else {
                                cocoMessage.error(res.data.errorMsg, 3000);
                            }
                        }
                    })
                    layer.close(index);
                })
                break;
        }
        ;
    });

    //category_table行工具事件
    table.on('tool(category_table)', function (obj) {

        let data = obj.data;
        let changeCategory = data.categoryName;

        if (obj.event === 'del') {
            layer.confirm('真的删除类别名为' + data.categoryName + '的分类吗', function (index) {
                obj.del();
                $.ajax({
                    url: 'http://47.119.131.193/blog/admin/delCategory',
                    type: 'post',
                    data: {
                        category: data.categoryName
                    },
                    success: function (res) {
                        if (res.code == 200) {
                            cocoMessage.success(res.data.successMsg, 3000);
                        } else {
                            cocoMessage.error(res.data.errorMsg, 3000);
                        }
                    }
                })
                layer.close(index);
            });
        } else if (obj.event === 'edit') {

            let blogIdList = [];
            if (data.blogList.length != 0) {
                for (let i = 0; i < data.blogList.length; i++) {
                    blogIdList.push(data.blogList[i].id);
                }
            }

            // 渲染分类详情框
            layui.use('transfer', function () {
                var transfer = layui.transfer;
                //渲染
                transfer.render({
                    elem: '#category_detail', //绑定元素
                    title: ['所有博客', '该类别博客'],
                    parseData: function (res) {
                        return {
                            "value": res.id, //数据值
                            "title": res.title, //数据标题
                            "disabled": res.disabled, //是否禁用
                            "checked": res.checked //是否选中
                        }
                    },
                    value: blogIdList,
                    data: BlogList,
                    id: 'demo1', //定义索引

                });
            });


            // 显示分类详情modal
            $('.category.detail.modal').modal('show').modal({
                closable: false,
                onApprove: function () {
                    let addCategoryBlogIdList = [];
                    var transfer = layui.transfer;
                    transfer.getData('demo1').forEach(element => {
                        if (blogIdList.indexOf(element.value) < 0) {
                            addCategoryBlogIdList.push(element.value)
                        } else {
                            delete blogIdList[blogIdList.indexOf(element.value)];
                        }
                    });
                    ;

                    blogIdList = blogIdList.filter(function (e) {
                        return e
                    });

                    $.ajax({
                        url: "http://47.119.131.193/blog/admin/updateBlogsCategory",
                        type: 'post',
                        data: {
                            changeCategory: changeCategory,
                            addCategoryList: JSON.stringify(addCategoryBlogIdList),
                            remCategoryList: JSON.stringify(blogIdList)
                        },
                        success: function (res) {
                            if (res.code == 200) {
                                table.reload("category_table")
                                cocoMessage.success("修改成功", 3000)
                            } else {
                                cocoMessage.error("修改失败", 3000)
                            }
                        }
                    })
                    return true;
                }
            })
        }
    });
});

// blog_table的设置
layui.use('table', function () {
    let table = layui.table;

    table.render({
        elem: '#blog_table',
        url: 'http://47.119.131.193/blog/admin/getBlogs',
        toolbar: '#blog_toolbar',
        cols: [[
            {field: 'blogid', width: 50, title: '序号', type: 'numbers'},
            {field: 'title', width: 220, title: '标题'},
            {field: 'type', width: 100, title: '版权声明'},
            {field: 'category', width: 180, title: '类别'},
            {
                field: 'recommend', width: 60, title: '推荐', templet: function (d) {
                    if (d.recommend) {
                        return '<i class="layui-icon layui-icon-ok" style="color: green;font-weight: bold;"></i> '
                    } else {
                        return '<i class="layui-icon layui-icon-close" style="color: red;font-weight: bold;"></i> '
                    }
                }
            },
            {
                field: 'published', width: 100, title: '发布情况', templet: function (d) {
                    if (d.published) {
                        return '<span style="color: green;font-weight: 400;">已发布</span> '
                    } else {
                        return '<span style="color: red;font-weight: 400;">未发布</i> '
                    }
                }
            },
            {
                field: 'createDate',
                title: '创建日期',
                templet: '<div>{{ layui.util.toDateString(d.createDate,"yyyy-MM-dd &nbsp;&nbsp; HH:mm:ss") }}</div>'
            },
            {fixed: 'right', title: '操作', width: 150, toolbar: '#barDemo'}
        ]],
        // 后端数据显示
        response: {
            statusCode: 200,
        },
        parseData: function (res) {
            return {
                "code": res.code, //解析接口状态
                "msg": res.msg, //解析提示文本
                "count": res.data.blogs.total, //解析数据长度
                "data": res.data.blogs.list //解析数据列表
            };
        },
        page: {
            limit: 10,
            limits: [5, 10, 20, 40],
            layout: ['limit', 'count', 'prev', 'page', 'next', 'skip']
        },
    });

    //blog_table头工具栏事件
    table.on('toolbar(blog_table)', function (obj) {
        switch (obj.event) {
            case 'addNewBlog':
                sessionStorage.setItem("editBlogFlag", "")
                window.location.href = "http://47.119.131.193/blog/input"
                break;
        }
        ;
    });

    //blog_table行工具栏事件
    table.on('tool(blog_table)', function (obj) {

        let data = obj.data;

        if (obj.event === 'edit') {
            sessionStorage.setItem("editBlogFlag", "true");
            sessionStorage.setItem("editBlogTitle", data.title);
            setTimeout(function () {
                window.location.href = "http://47.119.131.193/blog/input"
            }, 500)
        }
    })
});

// comment_table的设置
layui.use(['jquery', 'table', 'code', 'tableFilter'], function () {

    var tableData;
    layui.table.render({
        elem: '#comment_table',
        url: 'http://47.119.131.193/blog/admin/getAllComments',
        cols: [[
            {field: 'commentid', width: 50, title: '序号', type: 'numbers'},
            {field: 'blogTitle', width: 210, title: '相关标题'},
            {field: 'userName', width: 80, minwidth: 80, title: '发言用户'},
            {field: 'content', width: 210, title: '发言内容'},
            {field: 'replyUser', width: 80, minwidth: 80, title: '回复给',},
            {field: 'status', width: 110, minwidth: 80, title: '审核情况', templet: '#commentsSwitchTpl'},
            {
                field: 'createDate',
                title: '创建日期',
                width: 200,
                templet: '<div>{{ layui.util.toDateString(d.createDate,"yyyy-MM-dd &nbsp;&nbsp; HH:mm:ss") }}</div>'
            },
            {fixed: 'right', width: 100, title: '操作', toolbar: '#commentBar'}
        ]],
        // 后端数据显示
        response: {
            statusCode: 200,
        },
        parseData: function (res) {
            return {
                "code": res.code, //解析接口状态
                "msg": res.msg, //解析提示文本
                "count": res.data.total, //解析数据长度
                "data": res.data.comments //解析数据列表
            };
        }
        ,done:function () {
            tableData = layui.table.cache.comment_table;
        }
    });

    // 表头过滤插件
    layui.tableFilter.render({
        'elem': '#comment_table',
        'mode': 'local',
        'filters': [
            {field: 'blogTitle', type:'checkbox'},
            {field: 'status', type:'radio'},
            {field: 'createDate',type: 'date'}
        ],
        'done': function (filters) {}
    })

    //监听审核状态操作
    layui.form.on('switch(statusChange)', function(obj){
        let index = obj.othis.parents('tr').attr("data-index");
        console.log(index)
        console.log(tableData[index])
        $.post('http://47.119.131.193/blog/admin/changeCommentStatus?status='+obj.elem.checked
            +"&id="+tableData[index].id,function (res) {

        })
    });

    //comment_table行工具栏事件
    layui.table.on('tool(comment_table)', function (obj) {

        let data = obj.data;

        if (obj.event === 'del') {
            layer.confirm('是否确认删除',function (index){
                $.post('http://47.119.131.193/blog/admin/delComment?id='+data.id,function (res) {
                    if (res.code==200){
                        obj.del();
                        cocoMessage.success("删除成功!",2000)
                    }else {
                        cocoMessage.error(res.data.errorMsg,3000)
                    }
                })
                layer.close(index);
            })
        }
    })
})
