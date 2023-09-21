var startNum = 2;
var scrolling = true;
var goodsResult = true;

$(window).scroll(function () { // 스크롤을 통한 페이징 처리
    if ($(window).scrollTop() >= ($(document).height() - 1200) && goodsResult === true && scrolling === true) { // 스크롤값
        const urlParams = new URL(location.href).searchParams;
        const tag = urlParams.get('tag');
        const search = urlParams.get('search');
        scrolling = false;
        $.ajax({
            type: "GET",
            url: "/blogList/"+ startNum + (tag != null ? "?tag=" + tag : "") + (search != null ? "?search=" + search : ""),
            async: false,
            success: function (data) {
                if (($.isEmptyObject(data) === false) && data.split('\n').length > 3) {
                    $("#contentsWrap").append(data);
                    startNum += 1;// 지속적으로 추가하여 끝까지 나오도록
                    scrolling = true;
                } else {
                    goodsResult = false;
                }
            },
            error: function (e) {
                alert("에러가 발생하였습니다. 새로고침 하시길 바랍니다.")
                scrolling = true;
            }
        });
    }
});