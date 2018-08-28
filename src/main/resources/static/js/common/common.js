/**
 *
 */
function changeVerifyCode() {
    jQuery('#kaptcha_img').attr("src", "/kaptcha?" + Math.floor(Math.random() * 100));
}

/*
* url的地址取出参数值
* */
function getQueryString(name) {
    var r = window.location.search.substr(1).match(/\d+(\.\d+)?/g);
    if (r != null) {
        return r;
    }
    return '';
}