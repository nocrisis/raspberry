/**
 *
 */
function changeVerifyCode() {
    $('#kaptcha_img').attr("src", "/kaptcha?" + Math.floor(Math.random() * 100));
}

/*
* url的地址取出参数值
* */
/*
function getQueryString(name) {
    var r = window.location.search.match(/\d+(\.\d+)?/g)[0];
    if (r != null) {
        return r;
    }
    return '';
}*/

function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if (r != null) {
        return decodeURIComponent(r[2]);
    }
    return '';
}
