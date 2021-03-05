$(function () {
    initialBlogComment();
})

// 初始化该文章对应的留言列表
function initialBlogComment() {
    $.post('http://47.119.131.193/blog/admin/getBlogComments',function (res) {
        let data = res.data.comments;

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
                        "   <a class=\"avatar\"> <img src=\"/images/matt.jpg\" /> </a> \n" +
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
            blogTitle: null,
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