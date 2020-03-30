$(document).ready(function()
{
    getUserData();
    getRequestList();
    initMainLogic();
});

function getUserData()
{
    $.ajax({
        type: "GET",
        url: "/link/module/user/data",
        async: true
    }).done(function (data, textStatus, jqXHR) {
        //console.log(data);
        $('#user-data-access').text(data.authName);
        $('#user-data-name').text(data.userName);

        if (data.userPhoto != '')
        {
            $('#user-img').attr('src', data.userPhoto);
        }
    });
}

function getRequestList()
{
    $.ajax({
        type: "GET",
        url: "/link/module/requests",
        async: true,
        beforeSend: function () {
            $('.content').addClass('disabled');
        }
    }).done(function (data, textStatus, jqXHR) {
        console.log(data);
        for (i=0; i < data.length; i++)
        {
            requestInfo = '<tbody>\n' +
                '  <td class="first">' + data[i].id + '</td>\n' +
                '  <td>' + data[i].date + '</td>\n' +
                '  <td class="last">' + data[i].status + '</td>\n' +
                '  <td class="button"><input type="button" value="Review" request-id="' + data[i].id + '" /></td>\n' +
                '</tbody>'
            $('#table-requests').append(requestInfo);
        }
        $('.content').removeClass('disabled');
    });
}

function refreshRequestList()
{
    $('#table-requests tbody').remove();
    getRequestList();
}

function initMainLogic()
{
    $('#refresh-list').click(function (e) {
       e.preventDefault();
       refreshRequestList();
    });

    setInterval(refreshRequestList, 60000);
}