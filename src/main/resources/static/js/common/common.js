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
    var r = window.location.search.match(/\d+(\.\d+)?/g)[0];
    if (r != null) {
        return r;
    }
    return '';
}