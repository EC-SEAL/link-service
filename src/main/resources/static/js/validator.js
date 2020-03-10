$(document).ready(function()
{
    getUserData();
});

function getUserData()
{
    $.ajax({
        type: "GET",
        url: "/link/user/data",
        async: true
    }).success(function (data, textStatus, jqXHR) {
        console.log(data);
    });
}