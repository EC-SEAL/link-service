$(document).ready(function()
{
    getUserData();
    getRequestList();
    initMainLogic();
    initRequestDivLogic();
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
        showRequestInfoLogic();
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

function initRequestDivLogic()
{
    $('#hide-request').click(function (e) {
        e.preventDefault();
        $("#request-div").hide();
        $('#hide-div').hide();
    })
}

function showRequestInfoLogic()
{
    $('#table-requests input[type=button]').click(function()
    {
       requestId = $(this).attr('request-id');
       $('#hide-div').show();

        requestId = $(this).attr('request-id');
        getRequestInfo(requestId);
        $("#request-div").show();

    });
}

function getRequestInfo(requestId)
{
    $.ajax({
        type: 'GET',
        url: '/test/link/' + requestId + '/result/get?msToken=1', //To change
        async: true,
        beforeSend: function () {

        }
    }).done(function (data, textStatus, jqXHR) {
        console.log(data);
        $('#current-request-id').text(requestId);
        $('#request-date').text(data.issued.substring(0, 10));
    });
}