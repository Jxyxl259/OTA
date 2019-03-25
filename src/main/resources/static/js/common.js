
$(function(){

});

/** 获取条件查询参数 */
var search_form_data = function(){
    var cond_data = {};
    var conds = $("#condition_search_form input.condition_search_input");
    conds.each(function(index, input_ele){
        cond_data[input_ele.name] = input_ele.value;
    });
    return cond_data;
};


/** 初始化数据表格 */
var init_data_table = function(data_format, table_data){
    var loop_times = data_format.col_num;
    var data_models = data_format.col_attr;

    // 组织thead
    var thead = "<thead><tr>";
    for(var j = 0 ; j < loop_times; j++){
        var data_model = data_models[j];
        if("none" === data_model.display){
            thead += "<th style='display: none' >" + data_model.col_header + "</th>";
        }else{
            thead += "<th style='width: "+ data_model.width +"' >" + data_model.col_header + "</th>";
        }
    }
    thead += "</tr></thead>";

    // 组织tbody
    var tbody = "</tbody>";
    for(var key in table_data){
        var item = table_data[key];
        tbody += "<tr>";
        for(var i = 0; i < loop_times; i++){

            var data_model = data_models[i];
            var attr = data_model.col_name;
            if(!data_model.ext){// dto属性
                if("none" === data_model.display){
                    tbody += "<td style='display: none' >" + item[attr] + "</td>";
                }else{
                    tbody += "<td style='width: "+ data_model.width +"' >" + item[attr] + "</td>";
                }
            }else{// html代码

                tbody += "<td style='width: "+ data_model.width +"' >" + data_model.html_content + "</td>";
            }
        }
        tbody += "</tr>";
    };
    tbody += "</tbody>";
    return thead + tbody;
};


/** 回显分页信息 */
var fill_pagination_info = function (paginationInfo){
    if(paginationInfo == null){
        return false;
    }
    var currentPage = paginationInfo.pageNum;
    /*  */
    var pageTotal = paginationInfo.pages;
    /* 总页数 */
    var pageSize = paginationInfo.pageSize;
    var totalItemNums = paginationInfo.recordsTotal;


    // 左下角显示 当前分页信息
    var currentPagenationInfo = "<b>";
    if(1 != pageTotal){
        currentPagenationInfo += "当前显示第 " + ((currentPage -1 ) * pageSize + 1 ) + " ~ " + ( (currentPage * pageSize) > totalItemNums ? totalItemNums : (currentPage * pageSize) ) + " 条，共 " + totalItemNums + " 条记录";
    }else{
        currentPagenationInfo += "当前显示第 1 ~ " + totalItemNums + " 条，共 " + totalItemNums + " 条记录";
    }
    currentPagenationInfo += "</b>";

    // 设置右下角分页页签
    var pagenationContent ="<ul class='pagination_area_ul pagination pagination-sm'>";
    if(currentPage != 1){
        pagenationContent += "<li><a href='javascript:void(0);' onclick='fetch_table_data(" + (currentPage - 1) + ", " + pageSize + " )'>previous</a></li>";
    }

    // 总页数小于等于 5页 直接循环遍历出页签
    if( pageTotal <= 5 ){
        for(var i = 1; i <= pageTotal; i++){
            if( i == currentPage){
                pagenationContent += "<li><a class = 'current_page_tag' href='javascript:void(0);'>" + i + "</a></li>";
            }else{
                pagenationContent += "<li><a href='javascript:void(0);' onclick='fetch_table_data(" + i + ", " + pageSize + " )'>" + i + "</a></li>";
            }
        }
    }else{
        if(currentPage <= 3){
            for(var i = 1; i <= 3; i++){
                if( i == currentPage){
                    pagenationContent += "<li><a class = 'current_page_tag' href='javascript:void(0);'>" + i + "</a></li>";
                }else{
                    pagenationContent += "<li><a href='javascript:void(0);' onclick='fetch_table_data(" + i + ", " + pageSize + " )'>" + i + "</a></li>";
                }
            }
            pagenationContent += "<li><a href='javascript:void(0);' onclick='fetch_table_data(" + 4 + ", " + pageSize + " )'>" + 4 + "</a></li>";
            pagenationContent += "<li><a href='javascript:void(0);' onclick='fetch_table_data(" + 5 + ", " + pageSize + " )'>" + 5 + "</a></li>";
        } else if( currentPage <= pageTotal - 2 ){ // 当前页从第三页到倒数第三页，都以中间数为基准
            pagenationContent += "<li><a href='javascript:void(0);' onclick='fetch_table_data(" + currentPage - 2 + ", " + pageSize + " )'>" + currentPage - 2 + "</a></li>";
            pagenationContent += "<li><a href='javascript:void(0);' onclick='fetch_table_data(" + currentPage - 1 + ", " + pageSize + " )'>" + currentPage - 1 + "</a></li>";
            pagenationContent += "<li><a href='javascript:void(0);' class = 'current_page_tag'>" + currentPage + "</a></li>";
            pagenationContent += "<li><a href='javascript:void(0);' onclick='fetch_table_data(" + currentPage + 1 + ", " + pageSize + " )'>" + currentPage + 1 + "</a></li>";
            pagenationContent += "<li><a href='javascript:void(0);' onclick='fetch_table_data(" + currentPage + 2 + ", " + pageSize + " )'>" + currentPage + 2 + "</a></li>";
        } else {
            for(var i = 4 ; i >= 2 ; i++){
                pagenationContent += "<li><a href='javascript:void(0);' onclick='fetch_table_data(" + pageTotal - i + ", " + pageSize + " )'>" + pageTotal - i + "</a></li>";
            }
            if( pageTotal == currentPage ){
                pagenationContent += "<li><a href='javascript:void(0);' onclick='fetch_table_data(" + pageTotal - 1 + ", " + pageSize + " )'>" + currentPage -1 + "</a></li>";
                pagenationContent += "<li><a class = 'current_page_tag' href='javascript:void(0);'>" + currentPage + "</a></li>";
            }else{
                pagenationContent += "<li><a class = 'current_page_tag' href='javascript:void(0);'>" + currentPage + "</a></li>";
                pagenationContent += "<li><a href='javascript:void(0);' onclick='fetch_table_data(" + pageTotal + ", " + pageSize + " )'>" + pageTotal + "</a></li>";
            }
        }
    }
    if(currentPage != pageTotal){
        pagenationContent += "<li><a href='javascript:void(0);' onclick='fetch_table_data(" + (currentPage - 1) + ", " + pageSize + " )'>next</a></li>";
    }
    pagenationContent += "</ul>";
    pagenationContent += "<b>跳转到第 <input type='text' width='30px' id='targetPage'/> 页 <button id='goToTargetPage' class='btn btn-success btn-xs' onclick='gotoTargetPage(this)'>Go</button><b>";

    // 如果没有分页数据
    if(currentPage === 0 || pageTotal === 0  ||totalItemNums === 0){
        currentPagenationInfo = "";
        pagenationContent = "";
    }

    $("#pagination_info_span").html(currentPagenationInfo);
    $("div[id='pagination_tag_div']").html(pagenationContent);
};


/** 去指定页面 */
var gotoTargetPage = function(btn){
    itemNumPerPage = parseInt($.trim($("#pageSizeVal").val()));
    var targetPageNumber = parseInt($.trim($("#targetPage").val()));
    loadList(itemNumPerPage, targetPageNumber)
};

