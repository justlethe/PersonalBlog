$(function () {
    initialBlog(sessionStorage.getItem("curCategory"));
    initialCategory();
    initialHotBlog();
})

// 初始化首页博客列表函数
function initialBlog(category) {
    layui.use('flow', function () {
        let $ = layui.jquery; //不用额外加载jQuery，flow模块本身是有依赖jQuery的，直接用即可。
        let flow = layui.flow;
        flow.load({
            elem: '#blogList' //指定列表容器
            , isAuto: true
            , done: function (page, next) { //到达临界点（默认滚动触发），触发下一页
                let lis = [], search = "";
                let url = 'http://47.119.131.193/blog/admin/getBlogs?page=' + page + '&limit=5';
                if (category != null) {
                    url += "&category=" + category;
                }
                if (sessionStorage.getItem("search") != null) {
                    search = sessionStorage.getItem("search");
                }
                //以jQuery的Ajax请求为例，请求下一页数据（注意：page是从2开始返回）
                $.ajax({
                    url: url,
                    type: 'post',
                    data: {
                        search: search,
                    },
                    success: function (res) {
                        //假设你的列表返回在data集合中
                        // console.log(res.data.blogs.list)
                        let list = []
                        layui.each(res.data.blogs.list, function (index, blog) {
                            if (blog.recommend) {
                                let categoryArray = blog.category.split(",")
                                let li = "                            <li class=\"ui content-padding segment stackable grid animate__animated animate__bounceInLeft\"\n" +
                                    "                                style=\"padding: 5px !important;\">\n" +
                                    "                                <a class=\"ui left corner red label\">\n" +
                                    "                                    <i class=\"heart icon\"></i>\n" +
                                    "                                </a>\n" +
                                    "                                <a class=\"sixteen wide column ui header\" target=\"_blank\" onclick=\"goBlog(this)\">" + blog.title + "</a>\n" +
                                    "                                <a class=\"six wide column\"  target=\"_blank\" onclick=\"goBlog(this)\">\n" +
                                    "                                    <img src=\"https://picsum.photos/id/1/300/200\" alt=\"\" class=\"ui rounded image\">\n" +
                                    "                                </a>\n" +
                                    "                                <p class=\"ten wide column text-spacing\">" + blog.description + "</p>\n" +
                                    "                                <div class=\"ui mobile stackable sixteen wide column\">\n" +
                                    "                                    <div>\n" +
                                    "                                        <i class=\"calendar icon\"></i> " + blog.createDate.substring(0, 19).replace("T", "  ") + "\n" +
                                    "                                        <i class=\"eye icon\" style=\"margin-left: 1em\"></i> " + blog.views + "\n" +
                                    "                                        <div style=\"float:right;\">\n"
                                for (let i = 0; i < categoryArray.length; i++) {
                                    li += "                                            <a class=\"type\" onclick=\"reloadBlog(this)\" style=\"margin-left: 1em\">\n" +
                                        "                                                <i class=\"tags icon\"></i>" + categoryArray[i] + "\n" +
                                        "                                            </a>\n"
                                }
                                li += "                                            </a>\n" +
                                    "                                        </div>\n" +
                                    "                                    </div>\n" +
                                    "                                </div>\n" +
                                    "                            </li>\n";
                                lis.push(li);
                            } else {
                                let categoryArray = blog.category.split(",")
                                let li = "                            <li class=\"ui content-padding segment stackable grid animate__animated animate__bounceInLeft\"\n" +
                                    "                                style=\"padding: 5px !important;\">\n" +
                                    "                                <a class=\"sixteen wide column ui header\" target=\"_blank\" onclick=\"goBlog(this)\">" + blog.title + "</a>\n" +
                                    "                                <a class=\"six wide column\"  target=\"_blank\" onclick=\"goBlog(this)\">\n" +
                                    "                                    <img src=\"https://picsum.photos/id/1/300/200\" alt=\"\" class=\"ui rounded image\">\n" +
                                    "                                </a>\n" +
                                    "                                <p class=\"ten wide column text-spacing\">" + blog.description + "</p>\n" +
                                    "                                <div class=\"ui mobile stackable sixteen wide column\">\n" +
                                    "                                    <div>\n" +
                                    "                                        <i class=\"calendar icon\"></i> " + blog.createDate.substring(0, 19).replace("T", "  ") + "\n" +
                                    "                                        <i class=\"eye icon\" style=\"margin-left: 1em\"></i> " + blog.views + "\n" +
                                    "                                        <div style=\"float:right;\">\n"
                                for (let i = 0; i < categoryArray.length; i++) {
                                    li += "                                            <a class=\"type\" onclick=\"reloadBlog(this)\" style=\"margin-left: 1em\">\n" +
                                        "                                                <i class=\"tags icon\"></i>" + categoryArray[i] + "\n" +
                                        "                                            </a>\n"
                                }
                                li += "                                            </a>\n" +
                                    "                                        </div>\n" +
                                    "                                    </div>\n" +
                                    "                                </div>\n" +
                                    "                            </li>\n";
                                list.push(li);
                            }
                        });
                        for (let i = 0; i < list.length; i++) {
                            lis.push(list[i]);
                        }

                        $('#allBlog').text(res.data.total);

                        //执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
                        //pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
                        next(lis.join(''), page < res.data.blogs.pages);
                    }
                });
            }
        });
    });
}

// 初始化首页分类列表函数
function initialCategory() {
    $.get('http://47.119.131.193/blog/admin/getCategories', function (res) {
        let category = res.data.categories;
        // console.log(category)
        for (let i = 0; i < category.length; i++) {
            $("#categoryList").append("<a onclick=\"reloadBlog(this)\" class=\"item teal\">\n" +
                "<span class=\"category\">" + category[i].categoryName + "</span>\n" +
                "<div class=\"ui teal basic label\">" + category[i].blogList.length + "</div>\n" + "</a>\n")
        }
        if (sessionStorage.getItem("curCategory") != null) {
            let curCategory = sessionStorage.getItem("curCategory");
            $('#categoryList').children('a:first-child').removeClass("active");
            $('#categoryList').children('a:contains(' + curCategory + ')').addClass("active")
        }
    })
}

// 初始化热门文章列表
function initialHotBlog() {
    $.get('http://47.119.131.193/blog/admin/getHotBlogs', function (res) {
        let data = res.data.blogs;
        console.log(data);
        for (let i = 0; i < data.length; i++) {
            $('.ordered.selection.list').append(
                "<a onclick=\"hotToBlog(this)\" class=\"item\">\n" +
                "" + data[i].title + "\n" + "</a>")
        }
    })
}

// 跳转到博客详情页
function goBlog(element) {
    // 获取标题
    console.log($(element).parent().find("a.ui.header").text())
    sessionStorage.removeItem("search")
    sessionStorage.setItem("blogTitle", $(element).parent().find("a.ui.header").text())
    window.open("http://47.119.131.193/blog/blog")
}

// 热门文章跳转博客详情页
function hotToBlog(element) {
    sessionStorage.setItem("blogTitle", $(element).text())
    window.open("http://47.119.131.193/blog/blog")
}

// 根据分类筛选当前博客
function reloadBlog(element) {
    // 获取筛选的分类
    let category = $(element).children('.category').text();
    if (category != "全部文章") sessionStorage.setItem("curCategory", category);
    else sessionStorage.removeItem("curCategory");
    sessionStorage.removeItem("search")
    console.log(category)
    location.reload();
}

// 根据search框筛选当前博客
function searchBlog(element) {
    console.log($(element).parent())
    if ($(element).prev().val() == "") {
        cocoMessage.warning("搜索条件不能为空", 3000)
    } else {
        sessionStorage.setItem("search", $(element).prev().val());
        location.reload();
    }
}

