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
    }).done(function (data, textStatus, jqXHR) {
        console.log(data);
        $('#user-data-access').text(data.authName);
        $('#user-data-name').text(data.userName);

        if (data.userPhoto != '')
        {
            $('#user-img').attr('src', data.userPhoto);
        }
    });
}