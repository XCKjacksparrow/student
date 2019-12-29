// 当进入success页面时显示五条记录
var currentPage;
var totalPage;
var currentStudentId;
$(function () {
    getFirstPage();
    getPagination();

    addEvents();
});

function getFirstPage() {               // 选取首页5个记录
    $.ajax({
        url:"/student/getPage/1",
        type:"get",
        success:function(result){
            console.log(result);
            var tbody = $("tbody");
            for (var i = 0; i < result.length; i++){
                // console.log(result[i]);//id firstName,lastName,userName,score
                var tr = $("<tr></tr>");
                $("<th></th>").text(result[i].id).appendTo(tr);
                $("<td></td>").text(result[i].firstName).appendTo(tr);
                $("<td></td>").text(result[i].lastName).appendTo(tr);
                $("<td></td>").text(result[i].userName).appendTo(tr);
                $("<td></td>").text(result[i].score).appendTo(tr);

                var td = $("<td></td>");
                var deleteBtn = $("<button>delete</button>").addClass("btn btn-default").attr("id",result[i].id).attr("data-toggle","modal").attr("data-target",".bs-example-modal-sm");
                deleteBtn.appendTo(td);
                (function (i) {
                    deleteBtn.click(function () {
                        currentStudentId = i;
                    })
                }(result[i].id));
                td.append(" ");
                var modifyBtn = $("<button>modify</button>").addClass("btn btn-primary").attr("id",result[i].id).attr("data-toggle","modal").attr("data-target","#modifyModal");
                modifyBtn.appendTo(td);
                (function (i) {
                    modifyBtn.click(function () {
                        currentStudentId = i;
                    })
                }(result[i].id));
                td.appendTo(tr);
                tr.appendTo(tbody);
                currentPage = 1;
            }
        }
    })
}

function getPagination() {              // 构造底部导航
    $.ajax({
        url:"/student/getPagination",
        type:"get",
        success:function(result) {
            totalPage = result;
            var ul = $(".pagination");

            var pre = $("<li></li>");
            var preA = $("<a></a>").attr("href","#");
            $("<span></span>").text("<<").appendTo(preA);
            preA.appendTo(pre);
            pre.appendTo(ul);
            preA.click(function () {
                toPage(currentPage - 1);
            });

            for (var i = 1; i <= result; i++){
                var li = $("<li></li>");
                if (i == 1){
                    li.addClass("active");
                }
                var A = $("<a></a>").attr("href","#");
                $("<span></span>").text(i).appendTo(A);
                A.appendTo(li);
                li.appendTo(ul);
                (function (j) {
                    A.click(function () {
                        toPage(j);
                    })
                }(i));
            }

            var next = $("<li></li>");
            var nextA = $("<a></a>").attr("href","#");
            $("<span></span>").text(">>").appendTo(nextA);
            nextA.appendTo(next);
            next.appendTo(ul);
            nextA.click(function () {
                if (currentPage != totalPage)
                    toPage(currentPage + 1);
            });
        }
    })
}

function toPage(n){
    $.ajax({
        url:"/student/getPage/" + n,
        type:"get",
        success:function(result){
            var tbody = $("tbody");
            tbody.empty();
            for (var i = 0; i < result.length; i++){
                // console.log(result[i]);//id firstName,lastName,userName,score
                var tr = $("<tr></tr>");
                $("<th></th>").text(result[i].id).appendTo(tr);
                $("<td></td>").text(result[i].firstName).appendTo(tr);
                $("<td></td>").text(result[i].lastName).appendTo(tr);
                $("<td></td>").text(result[i].userName).appendTo(tr);
                $("<td></td>").text(result[i].score).appendTo(tr);

                var td = $("<td></td>");
                var deleteBtn = $("<button>delete</button>").addClass("btn btn-default").attr("id",result[i].id).attr("data-toggle","modal").attr("data-target",".bs-example-modal-sm");
                deleteBtn.appendTo(td);
                (function (i) {
                   deleteBtn.click(function () {
                       currentStudentId = i;
                   })
                }(result[i].id));
                td.append(" ");
                var modifyBtn = $("<button>modify</button>").addClass("btn btn-primary").attr("id",result[i].id).attr("data-toggle","modal").attr("data-target","#modifyModal");
                modifyBtn.appendTo(td);
                (function (i) {
                    modifyBtn.click(function () {
                        currentStudentId = i;
                    })
                }(result[i].id));
                td.appendTo(tr);
                tr.appendTo(tbody);
                currentPage = n;
            }
        }
    });
    $.ajax({
        url:"/student/getPagination",
        type:"get",
        success:function(result){
            var ul = $(".pagination");
            ul.empty();

            var pre = $("<li></li>");
            var preA = $("<a></a>").attr("href","#");
            $("<span></span>").text("<<").appendTo(preA);
            preA.appendTo(pre);
            pre.appendTo(ul);
            preA.click(function () {
                toPage(currentPage - 1);
            });
            for (var i = 1; i <= result; i++){
                var li = $("<li></li>");
                if (i == currentPage){
                    li.addClass("active");
                }
                var A = $("<a></a>").attr("href","#");
                $("<span></span>").text(i).appendTo(A);
                A.appendTo(li);
                li.appendTo(ul);
                (function (j) {
                    A.click(function () {
                        toPage(j);
                    })
                }(i));
            }

            var next = $("<li></li>");
            var nextA = $("<a></a>").attr("href","#");
            $("<span></span>").text(">>").appendTo(nextA);
            nextA.appendTo(next);
            next.appendTo(ul);
            nextA.click(function () {
                if (currentPage != totalPage)
                    toPage(currentPage + 1);
            });
        }
    })
}

function addEvents(){
    $("#addBtn").click(function () {
        var firstName = $("#firstName").val();
        var lastName= $("#lastName").val();
        var userName = $("#userName").val();
        var score = $("#score").val();
        $.ajax({
            url:"/student/addOrUpdate",
            type:"post",
            data:{
                firstName:firstName,
                lastName:lastName,
                userName:userName,
                score:score
            },
            success:function (result) {
                alert(result);
            }
        })
    });


    $("#deleteBtn").click(function () {
        $.ajax({
            url:"/student/delete/" + currentStudentId,
            type:"get",
            success:function (result) {
                alert(result);
                location.reload();
            }
        })
    });

    $("#modifyBtn").click(function () {
        var id = currentStudentId;
        var firstName = $("#mfirstName").val();
        var lastName= $("#mlastName").val();
        var userName = $("#muserName").val();
        var score = $("#mscore").val();
        $.ajax({
            url:"/student/addOrUpdate",
            type:"post",
            data:{
                id:id,
                firstName:firstName,
                lastName:lastName,
                userName:userName,
                score:score
            },
            success:function (result) {
                alert(result);
                location.reload();
            }
        })
    });
}