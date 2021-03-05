// 初始化提示信息插件
cocoMessage.config({
    duration: 10000,
});

// 渲染表单
layui.use('form', function () {
    let form = layui.form;

    let select = xmSelect.render({
        el: '#category_list',
        language: 'zn',
        name: 'category',
        size: 'medium',
        prop: {
            name: 'categoryName',
            value: 'categoryName'
        },
        data: []
    })

    $.ajax({
        type: "get",
        url: "http://47.119.131.193/blog/admin/getCategory",
        success: function (data) {
            let list = data.data.category;
            select.update({
                data:list,
            })
        }
    });

    if (sessionStorage.getItem("editBlogFlag") == "true") {
        $.ajax({
            url: 'http://47.119.131.193/blog/admin/getBlog',
            type: 'post',
            data: {
                title: sessionStorage.getItem("editBlogTitle")
            },
            success: function (res) {
                console.log(res)
                layui.form.val('blog', {
                    "title": res.data.blog.title // "name": "value"
                    , "type": res.data.blog.type
                    , "recommend": res.data.blog.recommend ? 1:0
                    , "description": res.data.blog.description
                });
                let categoryList = res.data.blog.category.trim().split(',');
                // console.log(categoryList);
                select.setValue(categoryList)
                $("textarea[name='content']").val(res.data.blog.mdcontent)
            },
        })
    }

    //监听提交
    form.on('submit(save)', function (data) {
        uploadBlog(0, data.field);
        return false;
    });
    form.on('submit(publish)', function (data) {
        console.log("按下了发布")
        uploadBlog(1, data.field);
        return false;
    });
});

// 定义博客发表的ajax函数
function uploadBlog(published, blogData) {
    console.log(blogData);
    console.log(sessionStorage.getItem("editBlogFlag"))
    $.ajax({
        type: "post",
        url: "http://47.119.131.193/blog/admin/saveBlog",
        data: {
            blogData: JSON.stringify(blogData),
            published: published,
            updateFlag: sessionStorage.getItem("editBlogFlag")
        },
        success: function (data) {
            sessionStorage.setItem("editBlogFlag","");
            if (data.code == 200) {
                cocoMessage.success("发布成功,3秒后将返回管理页面", 3000);
                setTimeout(function () {
                    window.location.href = "http://47.119.131.193/blog/admin"
                }, 3000)
            } else {
                cocoMessage.error(data.data.errorMsg, 3000)
            }
        }
    });
}

function updateImg(element) {
    $('#blogImg').attr("src",$(element).val()+"/505/295")
}