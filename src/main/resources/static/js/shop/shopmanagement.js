jQuery(function () {
    var shopId = getQueryString('shopId');
    var shopInfoUrl = '/shop/getshopmanagementinfo?shopId=' + shopId;
    jQuery.getJSON(shopInfoUrl, function (data) {
        if (data.redirect) {
            window.location.href = data.url;
        } else {
            if (data.shopId != undefined && data.shopId != null) {
                shopId = data.shopId;
            }
            jQuery('#shopInfo').attr('href', '/shopadmin/shopoperation?shopId=' + shopId);
        }
    });
});