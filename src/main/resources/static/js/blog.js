// 검색
function search() {
    let form = document.getElementById("searchForm");
    let searchValue = form.search.value;
    if (searchValue.replace(/\s/g, "") === "") {
        alert("내용을 입력하시기 바랍니다.");
        return;
    }
    if (searchValue.split(/\s/g).length > 15) {
        alert("15자 이내로 입력해주세요.");
        return;
    }
    if (searchValue.split(/\s/g).length > 1) {
        alert("공백 없이 1개 단어만 입력해주세요.");
        return;
    }
    form.submit();
}

function modalSearch() {
    let form = document.getElementById("searchModalForm");
    let searchValue = form.search.value;
    if (searchValue.replace(/\s/g, "") === "") {
        alert("내용을 입력하시기 바랍니다.");
        return;
    }
    if (searchValue.split(/\s/g).length > 15) {
        alert("15자 이내로 입력해주세요.");
        return;
    }
    if (searchValue.split(/\s/g).length > 1) {
        alert("공백 없이 1개 단어만 입력해주세요.");
        return;
    }
    form.submit();
}

// 블로그 좋아요 JS
function blogLike(blogId) {
    let result = plusMinusBlogLike(blogId);
    if (result.result === 200) {
        document.getElementById("like" + blogId).innerText = "좋아요 " + result.count +"개";
    } else if(result.result === 210) {
        document.getElementById("like" + blogId).innerText = "좋아요 " + result.count +"개";
    }
}

function plusMinusBlogLike(id) {
    let resultCode = 300;
    let count;
    $.ajax({
        type: "POST",
        contentType: "application/json; charset=utf-8",
        url: "/member/like",
        data: JSON.stringify({"id": id}),
        async: false,
        success: function (data) {
            if (data.result === "200") {
                alert("좋아요가 추가하였습니다.");
                resultCode = 200;
                count = data.count;
            } else if (data.result === "210") {
                alert("좋아요가 취소되였습니다.");
                count = data.count;
                resultCode = 210;
            } else if (data.result === "300") {
                alert("문제가 발생하였습니다. 다시 시도해주세요.");
            } else {
                alert("서버 오류로 처리가 지연되고있습니다. 잠시 후 다시 시도해주세요");
            }
        },
        errors: function (e) {
            console.log(e)
            alert("error")
        }
    });
    return {"result" : resultCode, "count" : count}
}


// 댓글 좋아요 JS
function commentLike(commentId) {
    let result = plusMinusCommentLike(commentId);
    if (result.result === 200) {
        document.getElementById("clike" + commentId).innerText = "좋아요 " + result.count +"개";
    } else if(result.result === 210) {
        document.getElementById("clike" + commentId).innerText = "좋아요 " + result.count +"개";
    }
}

function plusMinusCommentLike(id) {
    let resultCode = 300;
    let count;
    $.ajax({
        type: "POST",
        contentType: "application/json; charset=utf-8",
        url: "/member/comment/like",
        data: JSON.stringify({"id": id}),
        async: false,
        success: function (data) {
            if (data.result === "200") {
                alert("좋아요가 추가하였습니다.");
                resultCode = 200;
                count = data.count;
            } else if (data.result === "210") {
                alert("좋아요가 취소되였습니다.");
                count = data.count;
                resultCode = 210;
            } else if (data.result === "300") {
                alert("문제가 발생하였습니다. 다시 시도해주세요.");
            } else if (data.result === "320") {
                alert("본인 댓글에는 좋아요를 누를 수 없습니다.");
            } else {
                alert("서버 오류로 처리가 지연되고있습니다. 잠시 후 다시 시도해주세요");
            }
        },
        errors: function () {
            alert("error")
        }
    });
    return {"result" : resultCode, "count" : count}
}


// 댓글 저장 JS
function commentSave(blogId) {
    let comment = document.getElementById("cSave" + blogId).value;
    if (comment.replace(/\s/g, "") === "") {
        alert("내용을 입력하시기 바랍니다.");
        return;
    }
    if (comment.length > 150) {
        alert("댓글은 150자 이하입니다. 입력한 글자수 : " + comment.length);
        return;
    }

    let result = saveComment(blogId, comment);
    if (result.result === 200) {
        document.getElementById("cmCount" + blogId).innerText = "댓글 " + result.count;

        var appendItems = "";
        appendItems += '<li class="mb-2" id="cHead'+ result.comment.id + '">'
        appendItems += '<div class="d-flex">'
        appendItems += '<div class="user-img">'
        appendItems += '<img src="' + result.comment.commentProfile + '" alt="userimg" class="avatar-35 rounded-circle img-fluid" loading="lazy">'
        appendItems += '</div>'
        appendItems += '<div class="comment-data-block ms-3">'
        appendItems += '<h6>' + escapeHtml(result.comment.commentNickname) + '</h6>'
        $.each(result.comment.commentContent, function (index, commentContent) {
            appendItems += '<p class="mb-0">' + escapeHtml(commentContent) + '</p>'
        });
        appendItems += '<div class="d-flex flex-wrap align-items-center comment-activity">'
        appendItems += '<a href="javascript:commentLike('+ result.comment.id + ')" id="clike'+ result.comment.id + '">좋아요 ' + result.comment.commentLikes + ' 개</a>'
        appendItems += '<a href="javascript:commentDelete(' + blogId + ',' + result.comment.id + ')" id="cdelete'+ result.comment.id + '">삭제</a>'
        appendItems += '<span>' + result.comment.commentDate + '</span>'
        appendItems += '</div>'
        appendItems += '</div>'
        appendItems += '</div>'
        appendItems += '</li>'
        $('#cmUl' + blogId).append(appendItems);
        document.getElementById("cSave" + blogId).value = "";
    }
}

var entityMap = {
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    '"': '&quot;',
    "'": '&#39;',
    '/': '&#x2F;',
    '`': '&#x60;',
    '=': '&#x3D;'
};

function escapeHtml (string) {
    return String(string).replace(/[&<>"'`=\/]/g, function (s) {
        return entityMap[s];
    });
}

function saveComment(id, comment) {
    let resultCode = 300;
    let count;
    let commentObject;
    $.ajax({
        type: "POST",
        contentType: "application/json; charset=utf-8",
        url: "/member/comment",
        data: JSON.stringify({"id": id, "comment": comment}),
        async: false,
        success: function (data) {
            if (data.result === "200") {
                alert("댓글이 저장되었습니다.");
                resultCode = 200;
                count = data.count;
                commentObject = data.commentResult;
            } else if (data.result === "300") {
                alert("문제가 발생하였습니다. 다시 시도해주세요.");
            } else {
                alert("서버 오류로 처리가 지연되고있습니다. 잠시 후 다시 시도해주세요");
            }
        },
        errors: function () {
            alert("error")
        }
    });
    return {"result" : resultCode, "count" : count, "comment" : commentObject}
}


// 댓글 삭제 JS
function commentDelete(blogId, commentId) {
    let result = deleteComment(commentId);
    if (result.result === 200) {
        document.getElementById("cmCount" + blogId).innerText = "댓글 " + result.count;
        document.getElementById("cHead" + commentId).remove();
    }
}

function deleteComment(id) {
    let resultCode = 300;
    let count;
    $.ajax({
        type: "POST",
        contentType: "application/json; charset=utf-8",
        url: "/member/comment/delete",
        data: JSON.stringify({"id": id}),
        async: false,
        success: function (data) {
            if (data.result === "200") {
                alert("댓글이 삭제되었습니다.");
                resultCode = 200;
                count = data.count;
            } else if (data.result === "300") {
                alert("문제가 발생하였습니다. 다시 시도해주세요.");
            } else if (data.result === "310") {
                alert("작성자만 삭제할 수 있습니다.");
            } else {
                alert("서버 오류로 처리가 지연되고있습니다. 잠시 후 다시 시도해주세요");
            }
        },
        errors: function () {
            alert("error")
        }
    });
    return {"result" : resultCode, "count" : count}
}