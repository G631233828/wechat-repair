
function login(url,toIndex){
    var username = $.trim($("#username").val());
    var password = $.trim($("#password").val());
    if(username == ""){
    	$("#msg").html("请输入用户名")
    	$("#username").focus;
    }else if(password == ""){
    	$("#msg").html("请输入密码")
    	$("#password").focus;
    }else{
    	$("#msg").html("")
    $.ajax({
            type: "POST",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            url: url,//url
            data: $('#form').serialize(),
            success: function (result) {
                alert(result.status);//打印服务端返回的数据(调试用)
                if (result.status == 200) {
                	var reurl=toIndex + result.data.id;
                	$("#msg").html(reurl)
                	location.href=reurl;
                }else if(result.status == 202){
                	$("#msg").html("帐号或密码错误")
                }else if(result.status == 203){
                	$("#msg").html("帐号密码不能为空")
                }
                ;
            },
            error : function() {
            	$("#msg").html("登录异常，请联系管理员s")
            }
        });
    }
}