let startNum = 2;
let scrolling = true;
let goodsResult = true;

$(window).scroll(function () { // 스크롤을 통한 페이징 처리
    if ($(window).scrollTop() >= ($(document).height() - 1200) && goodsResult === true && scrolling === true) { // 스크롤값
        scrolling = false;
        $.ajax({
            type: "GET",
            url: "/member/blogList/" + startNum,
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
                console.log(e);
                scrolling = true;
            }
        });
    }
});

$(function () {
    $('#profileImgEdit').click(function (e) {
        e.preventDefault();
        $('#chooseFile').click();
    });
});

$(function () {
    $('#profileButton').click(function (e) {
        e.preventDefault();

        let nickName = document.querySelector("#fname");
        if (nickName.value === "") {
            alert("닉네임을 입력하세요.");
        }
        if (nickName.value.length < 2 && nickName.value.length > 20) {
            alert("닉네임은 2~20 사이입니다.");
        }

        let form = document.getElementById("profileForm");
        let formData = new FormData(form);
        formData.append("image", (fileBob.size > 1 ? fileBob : null));
        let jsonData = {
            "nickName": nickName.value,
        }
        formData.append("profile", JSON.stringify(jsonData));

        $.ajax({
            type: "POST",
            enctype: "multipart/form-data",
            url: "/member/profile",
            data: formData,
            processData: false,
            contentType: false,
            async: false,
            success: function (data) {
                if (data.result === "200") {
                    alert("변경 완료");
                    window.location.replace("/member/profile");
                } else if (data.result === "210") {
                    alert("중복되는 닉네임입니다.");
                } else if (data.result === "310") {
                    alert(data.message);
                } else if (data.result === "300") {
                    alert("파일 올바르지 않습니다!");
                } else {
                    // alert(data.)
                    alert("문제가 발생하였습니다. 다시 시도해주세요.");
                }
            },
            errors: function (e) {
                alert("error")
            }
        });
        return false;
    });
});

// input file 파일 첨부시 크기 리사이즈, fileArray에 추가
$(document).ready(function () {
    $("#chooseFile").on("change", select);
});

let fileBob = new Blob();

function select() {
    // fileArray = [];

    $.each(this.files, function (index, file) {
        let reader = new FileReader();
        resizeImage({
            file: file,
            maxSize: 512
        }).then(function (resizedImage) {
            if (resizedImage.size > 512000) {
                alert("파일이 너무 큽니다. 더 작은 용량의 이미지를 사용해주시기 바랍니다.")
            }
            const url = window.URL.createObjectURL(resizedImage);
            reader.onload = function (e) {
                document.getElementById('showImg').src = url;
            };
            reader.readAsDataURL(file);

            fileBob = resizedImage;
        });
    });
}

let resizeImage = function (settings) {
    let file = settings.file;
    let reader = new FileReader();
    let image = new Image();
    let canvas = document.createElement('canvas');
    let dataURItoBlob = function (dataURI) {
        let bytes = dataURI.split(',')[0].indexOf('base64') >= 0 ?
            atob(dataURI.split(',')[1]) :
            unescape(dataURI.split(',')[1]);
        let max = bytes.length;
        let ia = new Uint8Array(max);
        for (let i = 0; i < max; i++)
            ia[i] = bytes.charCodeAt(i);
        return new Blob([ia], {type: 'image/jpeg'});
    };
    let resize = function () {
        let width = 512;
        let height = 512;
        canvas.width = width;
        canvas.height = height;
        canvas.getContext('2d').drawImage(image, 0, 0, width, height);
        let dataUrl = canvas.toDataURL('image/jpeg');
        return dataURItoBlob(dataUrl);
    };

    return new Promise(function (ok, no) {
        if (!file.type.match(/image.*/)) {
            no(new Error("Not an image"));
            return;
        }
        reader.onload = function (readerEvent) {
            image.onload = function () {
                return ok(resize());
            };
            image.src = readerEvent.target.result;
        };
        reader.readAsDataURL(file);
    });
};