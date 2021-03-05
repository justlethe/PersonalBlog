var blogTitle;

$(function () {
    initialBlog();
})

// 初始化博客文章函数
function initialBlog() {
    $.post('http://47.119.131.193/blog/admin/getBlog?title='+sessionStorage.getItem("blogTitle")+"&addView=true",function (res) {
        let blog = res.data.blog;
        // console.log(blog);
        blogTitle = blog.title;
        // 得到博客标题后再去拿对应的博客留言
        initialBlogComment(blogTitle);
        // 根据得到的博客数据渲染页面
        $('#blog-header').text(blog.title);
        $('#creatDate').text(blog.createDate.substring(0,19).replace("T","  "));
        $('#views').text(blog.views);
        $('#type').text(blog.type);
        if (blog.type=="转载"){
            $('#type').removeClass("teal").addClass("orange");
        }
        $('#content').append(blog.content.replace(/pre/g,"pre class=\"language-java\"").replace(/code/g,"code class=\"language-java\""));
        Prism.highlightAll();
        $(".blog-title").text(blog.title);
        let categoryArray = blog.category.split(",")
        for (let i=0;i<categoryArray.length;i++){
            $("#blog-category").append("<div class=\"ui label\">"+categoryArray[i]+"</div>");
        }
    })
    return true;
}

// 初始化该文章对应的留言列表
function initialBlogComment(title) {
    console.log("查找留言列表的博客id为"+title)
    $.post('http://47.119.131.193/blog/admin/getBlogComments?blogTitle='+title,function (res) {
        let data = res.data.comments;
        // console.log(data)
        for (let i=0;i<data.length;i++){
            let lis = "";
            lis = "<div class=\"comment\"> \n" +
                "   <a class=\"avatar\"> <img src=\"/images/matt.jpg\" /> </a> \n" +
                "   <div class=\"content\"> \n" +
                "    <a class=\"author\">"+data[i].userName+"</a> \n" +
                "    <div class=\"metadata\"> \n" +
                "     <span class=\"date\">"+data[i].createDate.substring(0,19).replace("T","  ")+"</span> \n" +
                "     <span class=\"related_id\" style=\"display:none !important\">"+data[i].id+"</span> \n" +
                "    </div> \n" +
                "    <div class=\"text\">\n" +
                "     "+data[i].content+"\n" +
                "    </div> \n" +
                "    <div class=\"actions\"> \n" +
                "     <a class=\"reply\" onclick=\"popReplyForm(this)\">回复</a> \n" +
                "    </div> \n" +
                "   </div> \n"
            if (data[i].commentList.length != 0){
                lis += "<div class=\"comments\">";
                let list = data[i].commentList;
                for (let j=0;j<list.length;j++){
                    lis += "<div class=\"comment\"> \n" +
                        "   <a class=\"avatar\"> <img src=\"/images/avatar/small/jenny.jpg\" /> </a> \n" +
                        "   <div class=\"content\"> \n" +
                        "    <a class=\"author\">"+list[j].userName+"</a><span> 回复 </span><b>"+list[i].replyUser+"</b> \n" +
                        "    <div class=\"metadata\"> \n" +
                        "     <span class=\"date\">"+list[i].createDate.substring(0,19).replace("T","  ")+"</span> \n" +
                        "    </div> \n" +
                        "    <div class=\"text\">\n" +
                        "     "+list[i].content+"\n" +
                        "    </div> \n" +
                        "    <div class=\"actions\"> \n" +
                        "     <a class=\"reply\" onclick=\"popReplyForm(this)\">回复</a> \n" +
                        "    </div> \n" +
                        "   </div> \n" +
                        "  </div>"
                }
                lis += "</div>"
            }
            lis += "  </div>"

            $('#comments').append(lis)
        }
    })
}

// 发表评论功能定义
function submitComment(element) {
    if (sessionStorage.getItem("code")!=200){
        cocoMessage.warning("未登录！请登录后发表评论>0<",3000)
    }else if ($(element).prev().children().val()==""){
        cocoMessage.warning("评论不能为空",3000);
        return false;
    }
    let a = $(element).parent().parent().prevAll("a.author");
    console.log(a)
    let replyUser = null,relatedId=null;
    if (a.length!=0) replyUser = a.text();
    if ($(element).parents(".comment").length!=0){
        relatedId = $(element).parents(".comment").last().find(".related_id").text();
        console.log(relatedId)
    }
    $.ajax({
        url: 'http://47.119.131.193/blog/admin/saveBlogComments',
        type: 'post',
        data: {
            blogTitle: blogTitle,
            userName: JSON.parse(sessionStorage.getItem("user")).username,
            content: $(element).prev().children().val(),
            replyUser: replyUser,
            relatedId: relatedId
        },
        success: function (res) {
            if (res.code==200){
                $('textarea').val("")
                cocoMessage.success("留言提交成功，待审核后将会发布",4000);
            }else {
                cocoMessage.error(res.data.errorMsg,3000);
            }
        }
    })
}

// 弹出回复框
function popReplyForm(element) {
    $("#replyForm").remove();
    $(element).parent().append("<form id=\"replyForm\" class=\"ui reply form animate__animated animate__flipInX\">\n" +
        "            <div class=\"field\">\n" +
        "              <textarea placeholder='请输入回复信息' autofocus></textarea>\n" +
        "            </div>\n" +
        "            <div class=\"ui primary submit labeled icon button\" onclick=\"submitComment(this)\">\n" +
        "              <i class=\"icon edit\"></i> 回复\n" +
        "            </div>\n" +
        "          </form>")
}

