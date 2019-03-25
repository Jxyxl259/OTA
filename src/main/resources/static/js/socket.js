//监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
// window.onbeforeunload = function (e) {
//     // 保存聊天tab到session
//     var _chat_content = $("#chat_record_container").html();
//
//     if( trim(_chat_content) != ""){
//         window.sessionStorage["chat_contant"] = _chat_content;
//     }
//
//     _web_socket.closeSocket();
// };

var allUnDealNotifyMsg = [];




$(function() {

    // 如果浏览器session缓存中有之前保存的，打开的聊天窗口，直接显示出来
    // var _chat_content = window.sessionStorage["chat_contant"];
    // if (_chat_content && !trim( _chat_content)) {
    //     $("#chat_record_container").html(_chat_content)
    // }




});


/* -------------------------------------- WebSocket --------------------------------------*/
var _web_socket = {
    websocket : null,
    isSupport : function(){
        //判断当前浏览器是否支持WebSocket
        if ('WebSocket' in window) {
            console.log('当前浏览器支持 websocket');
        } else {
            alert('当前浏览器 Not support websocket')
        }
    },
    initSocket : function(userId, userName, _server_addr){
        // 初始化 socket连接
        this.websocket = new WebSocket("ws://" + _server_addr + "/dispatcher/" + userId + "/" + userName);

        // 连接成功建立的回调方法
        this.websocket.onopen = function () {
            setMessageInnerHTML("WebSocket连接成功...");
        };
        // 接收到消息的回调方法
        this.websocket.onmessage = function (event) {
            var _sock_msg = JSON.parse(event.data);
            if( _sock_msg.msgType === "0") {
                // 好友消息
                dealMsg(_sock_msg);
            }else{
                // 系统消息（目前只有下线通知，刷新好友列表）
                systemNotification(_sock_msg);
            }
        };
        // 连接关闭的回调方法
        this.websocket.onclose = function () {
            setMessageInnerHTML("WebSocket连接关闭...");
        };
        // 发生连接错误时候的回调方法
        this.websocket.onerror = function () {
            setMessageInnerHTML("WebSocket连接发生错误...");
        };
    },
    //关闭WebSocket连接
    closeSocket : function closeWebSocket() {
        this.websocket.close();
    },
    //发送消息
    sendChattingMsg : function (msg) {
        this.websocket.send(msg);
    }
};


//将消息显示在网页上
function setMessageInnerHTML(content) {
    //document.getElementById('message').innerHTML += content + '<br/>';
    console.log(content);
}


/**
 * 收到聊天消息后处理函数
 * @param _sock_msg
 */
function dealMsg( _sock_msg ){
    var msg = _sock_msg.msg;
    var uid = _sock_msg.contactId;
    var name = $.trim(_sock_msg.contactName);
    var temp_contacter_iframe = _iframe["temp_contacter"];

    // 无最近联系人窗口 || 临时会话窗口没有该好友，将消息放到队列，并显示消息未读小红点
    if(onMessageNotify(msg, uid, name, temp_contacter_iframe)) return;

    // 有最近联系人窗口 判断是否为当前联系人
    appendMessageAndRemind(uid, name, msg, layer.getChildFrame('body', temp_contacter_iframe._iframe_index))
}


/**
 * 收到消息后提示消息未读
 */
function onMessageNotify( _msg, _f_id, _f_name, temp_contacter_iframe){

    // 没有临时对话窗口
    if( !temp_contacter_iframe ){
        notify();
        return true;
    }

    // 有临时对话窗口，但该好友不存在于临时会话列表
    var _chat_iframe_body = layer.getChildFrame('body', temp_contacter_iframe._iframe_index);
    if( _chat_iframe_body.children("div.temp_contacter").children("ul#tmp_list").children("li#" + _f_name).length === 0){
        notify();
        return true;
    }

    function notify (){
        // 先将消息 保存到map
        if(allUnDealNotifyMsg[_f_name]){
            allUnDealNotifyMsg[_f_name].count+=1;
            var _msg_queue = allUnDealNotifyMsg[_f_name].msgQueue;
            // 怼到数组最后面
            _msg_queue.splice(_msg_queue.length, 0, _msg);
        }else{
            var _msg_notify = {};
            _msg_notify.count = 1;
            _msg_notify.msgQueue = [];
            _msg_notify.msgQueue[0] = _msg;
            allUnDealNotifyMsg[_f_name] = _msg_notify;
        }
        // 改变样式，显示消息未读小红点
        $("#"+_f_id).children("span._msg_notification").removeClass("vanish").html(allUnDealNotifyMsg[_f_name].count);
    }

    return false;
}


/**
 * 收到好友消息，临时会话列表中有该好友，
 * @param _uid
 * @param _name
 * @param _msg
 * @param _chat_iframe_body
 */
function appendMessageAndRemind(_uid, _name, _msg, _chat_iframe_body){
    var _temp_contacter_ul = _chat_iframe_body.children("div.temp_contacter").children("ul#tmp_list");
    var _temp_chat_log_container = _chat_iframe_body.children("div.center_content").children("div.chat_record_container_div");

    // 判断当前的会话是否是该好友
    var _f_name = _temp_contacter_ul.children("li._current_contacter").attr("id");
    if(_f_name === _name){
        // 是 直接拼接消息
        appendMsg(_name, false);
    }else{
        // 不是 拼接消息并在联系人列表设置新消息提示
        appendMsg(_name, true);
    }

    /**
     *
     * @param _target_user_id
     * @param _need_remind 是否提示
     */
    function appendMsg(_target_user_name, _need_remind){
        var _represent_chat_log_div = _temp_chat_log_container.children("div#chat_record_with_" + _target_user_name)[0];

        var _msg_content =
            '   <div style="display:block;float:left;width:100%;">'+
            '        <p style="margin:2px 0px 2px 20px;width: fit-content;max-width: 40%;border-radius:5px;border:1px solid #d5d5d5;padding: 2px 5px;"> ' + _msg + '</p>'+
            '    </div>';
        $(_represent_chat_log_div).append(_msg_content);

        // 设置消息提示
        if(_need_remind){

        }
    }
}

/**
 * 系统通知该用户好友，该用户已下线
 *
 * @param _sock_msg
 */
function systemNotification(_sock_msg ){
    // 用户点过消息按钮(好友列表iframe 已弹出)，更改好友上下线状态（头像），还要更改临时会话列表中的联系人头像
    if(_iframe["friend_list"]){
        var user = JSON.parse(_sock_msg.msg);
        var _f_list_li_ele = layer.getChildFrame('#' + user.id,  _iframe["friend_list"]._iframe_index);
        _f_list_li_ele.children("div._friend_info_container_div").children("img").toggleClass('_offline_gray');

        // 更改临时会话列表中的联系人状态
        if(_temp_list[user.userName])
            layer
                .getChildFrame('#' + user.userName,  _iframe["temp_contacter"]._iframe_index)
                .children("div._tmp_contacter_div ")
                .toggleClass('_offline_gray');

    }else{
        // TODO 消息按钮处显示小红点
    }

}


