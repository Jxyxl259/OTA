$(function(){



});


var uploadOTAFile = function(){
    var form = new FormData(document.getElementById("fileupload_form"));
    $.ajax({
        url:"/uploadOTA",
        type:"post",
        data:form,
        processData:false,
        contentType:false,
        async:false,
        success:function(data){
            // window.clearInterval(timer);
            console.log("over..");
        },
        error:function(e){
            alert("错误！！");
            //window.clearInterval(timer);
        }
    });
    return false;
}

var upGrade = function(){
    $.ajax({
        url:"/upgradeOTA",
        type:"post",
        data:{
            "ota_name": $("#ota_upgrade_file_name").val(),
            "ota_destination": $("#ota_upgrade_destination").val()
        },
        async:false,
        success:function(data){
            // window.clearInterval(timer);
            console.log("start..");
        },
        error:function(e){
            alert("错误！！");
            //window.clearInterval(timer);
        }
    });
    return false;
}