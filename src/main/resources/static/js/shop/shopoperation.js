$(function () {
    var shopId = getQueryString('shopId');
    var isEdit = shopId ? true : false;
    var initUrl = '/shop/getshopinitinfo';
    var registerShopUrl = '/shop/registershop';
    var shopInfoUrl = "/shop/getshopbyid?shopId=" + shopId;
    var editShopUrl = '/shop/modifyshop';
    if (!isEdit) {
        getShopInitInfo();//ShopManagementController中定义返回json串形式的modelmap
    } else {
        getShopInfo(shopId);
    }

    function getShopInfo(shopId) {
        $.getJSON(shopInfoUrl, function (data) {
            if (data.success) {
                var shop = data.shop;
                $('#shop-name').val(shop.shopName);
                $('#shop-addr').val(shop.shopAddr);
                $('#shop-phone').val(shop.phone);
                $('#shop-desc').val(shop.shopDesc);
                var shopCategory = '<option data-id="'
                    + shop.shopCategory.shopCategoryId + '"selected>'
                    + shop.shopCategory.shopCategoryName + '</option>';
                var tempAreaHtml = '';
                data.areaList.map(function (item, index) {
                    tempAreaHtml += '<option data-id="' + item.areaId + '">'
                        + item.areaName + '</option>';
                });
                $('#shop-category').html(shopCategory);
                $('#shop-category').attr('disabled', 'disabled');
                $('#area').html(tempAreaHtml);
                $("#area option[data-id='" + shop.area.areaId + "']").attr("selected", "selected");
            }
        });

    }

    function getShopInitInfo() {
        $.getJSON(initUrl, function (data) {//第2个参数返回后回调的方法
            if (data.success) {//填充area和shopcatogery
                var tempHtml = '';
                var tempAreaHtml = '';
                data.shopCategoryList.map(function (item, index) {
                    tempHtml += '<option data-id="' + item.shopCategoryId + '">'
                        + item.shopCategoryName + '</option>';
                });
                data.areaList.map(function (item, index) {
                    tempAreaHtml += '<option data-id="' + item.areaId + '">'
                        + item.areaName + '</option>';
                });
                $('#shop-category').html(tempHtml);
                $('#area').html(tempAreaHtml);

            }
            else console.log("area和shopcatogery接收失败");
        });

    }

    $('#submit').click(function () {//获取表单内容
        var shop = {};
        if (isEdit) {
            shop.shopId = shopId;
        }
        shop.shopName = $('#shop-name').val();
        shop.shopAddr = $('#shop-addr').val();
        shop.phone = $('#shop-phone').val();
        shop.shopDesc = $('#shop-desc').val();
        shop.shopCategory = {
            shopCategoryId: $('#shop-category').find('option').not(function () {
                return !this.selected;
            }).data('id')
        };
        shop.area = {
            areaId: $('#area').find('option').not(function () {
                return !this.selected;
            }).data('id')
        };
        var shopImg = $('#shop-img')[0].files[0];
        var formData = new FormData();
        formData.append('shopImg', shopImg);
        formData.append('shopStr', JSON.stringify(shop));//将一个JavaScript值(对象或者数组)转换为一个 JSON字符串,JSON.parse()
        var verifyCodeActual = $('#j_kaptcha').val();
        if (!verifyCodeActual) {
            $.toast('请输入验证码！');
            return;
        }
        formData.append('verifyCodeActual', verifyCodeActual);
        $.ajax({
            url: (isEdit ? editShopUrl : registerShopUrl),
            type: 'POST',
            data: formData,
            contentType: false,
            processData: false,
            cache: false,
            success: function (data) {
                if (data.success) {
                    $.toast('店铺信息提交成功！');
                } else {
                    $.toast('店铺信息提交失败！' + data.errMsg);
                }
                $("#kaptcha_img").click();
            }
        });
    })
});
/*var shop = data.shop;
$('#shop-name').val(shop.shopName);
$('#shop-addr').val(shop.shopAddr);
$('#shop-phone').val(shop.phone);
$('#shop-desc').val(shop.shopDesc);
var shopCategory = '<option data-id="'
		+ shop.shopCategory.shopCategoryId + '" selected>'
		+ shop.shopCategory.shopCategoryName + '</option>';
var tempAreaHtml = '';
data.areaList.map(function(item, index) {
	tempAreaHtml += '<option data-id="' + item.areaId + '">'
			+ item.areaName + '</option>';
});
$('#shop-category').html(shopCategory);
$('#shop-category').attr('disabled','disabled');
$('#area').html(tempAreaHtml);
$('#area').attr('data-id',shop.areaId);*/