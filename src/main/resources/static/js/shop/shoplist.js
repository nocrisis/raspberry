jQuery(function () {

    function getlist(e) {
        console.log('getlist');
        jQuery.ajax({
            url: "/shop/getshoplist",
            type: "get",
            dataType: "json",
            success: function (data) {
                if (data.success) {
                    handleList(data.shopList);
                    handleUser(data.shopList);
                }
            error:
                if (!data.success) {
                    $.toast('请求错误');
                }
            }
        });
    }

    function handleUser(data) {
        console.log('handleUser');
        jQuery('#user-name').text(data[0].user.name);
    }

    function handleList(data) {
        console.log('handleList');
        var html = '';
        data.map(function (item, index) {
            html += '<div class="row row-shop"><div class="col-40">' + item.shopName + '</div><div class="col-40">'
                + shopStatus(item.enableStatus) + '</div><div class="col-20">' + goShop(item.enableStatus, item.shopId) + '</div></div>';

        });
        jQuery('.shop-wrap').html(html);
    }

    function goShop(status, id) {
        console.log('goShop');

        if (status != 0 && status != -1) {
            return '<a href="/shopadmin/shopmanagement?shopId=' + id + '">进入</a>';
        } else {
            return '';
        }
    }

    function shopStatus(status) {
        console.log('shopStatus');

        if (status == 0) {
            return '审核中';
        } else if (status == -1) {
            return '店铺非法';
        } else {
            return '审核通过';
        }
    }


    /*jQuery('#log-out').click(function () {
        jQuery.ajax({
            url: "/shop/logout",
            type: "post",
            contentType: false,
            processData: false,
            cache: false,
            success: function (data) {
                if (data.success) {
                    window.location.href = '/shop/ownerlogin';
                }
            },
            error: function (data, error) {
                alert(error);
            }
        });
    });*/

    getlist();
});
