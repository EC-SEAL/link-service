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

function clearRequestData()
{
    $('#request-attr-1 div.attr-content table').text('');
    $('#request-attr-2 div.attr-content table').text('');
    $('#request-docs').text('');
    $('#messages-space').text('');
}

function initRequestDivLogic()
{
    $('#hide-request').click(function (e) {
        e.preventDefault();
        $("#request-div").hide();
        $('#hide-div').hide();
        clearRequestData();
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

function fillRequestAttributes(attributtes, properties, attributeId, table)
{
    for (i=0; i < attributtes.length; i++)
    {
        var attr = '<tr><td class="attr-name">' + attributtes[i].friendlyName + '</td>'
            + '<td class="attr-value';

        if (attributtes[i].name == attributeId)
        {
            attr = attr + ' attr-id';
        }

        attr = attr + '">' + attributtes[i].values.toString() + '</td>';
        $(table).append(attr);
    }

    $.each(properties, function(key, value) {
        var prop = '<tr class="property"><td class="attr-name">' + key + '</td>' +
            '<td class="attr-value">' + value + '</td></tr>';
        $(table).append(prop);
    });
}

function getRequestCurrentStatus(requestId)
{
    var status = '';

    $.ajax( {
        type: 'GET',
        url: '/link/module/request/' + requestId + '/info',
        async: false
    }).done(function (data, textStatus, jqXHR) {
       status = data.status;
    });

    return status;
}

function getRequestInfo(requestId)
{
    $.ajax({
        type: 'GET',
        url: '/link/' + requestId + '/get',
        async: true,
        beforeSend: function () {

        }
    }).done(function (data, textStatus, jqXHR) {
        console.log(data);
        $('#current-request-id').text(requestId);
        $('#request-date').text(data.issued.substring(0, 10));

        // Attributes section A
        $('#request-attr-1 h3').text(data.datasetA.issuerId);
        $('#request-attr-1 .level').text(data.datasetA.loa);
        $('#request-attr-1 .level').addClass(data.datasetA.loa.toLowerCase())
        fillRequestAttributes(data.datasetA.attributes, data.datasetA.properties, data.datasetA.subjectId, $('#request-attr-1 div.attr-content table'));

        // Attributes section B
        $('#request-attr-2 h3').text(data.datasetB.issuerId);
        $('#request-attr-2 .level').text(data.datasetB.loa);
        $('#request-attr-2 .level').addClass(data.datasetB.loa.toLowerCase())
        fillRequestAttributes(data.datasetB.attributes, data.datasetB.properties, data.datasetB.subjectId, $('#request-attr-2 div.attr-content table'));

        //Files list
        for (var i=0; i < data.evidence.length; i++)
        {
            var file = '<li><a href="#" file-id="' + data.evidence[i].fileID + '">' + data.evidence[i].filename + '</a></li>';
            $('#request-docs').append(file);
        }

        //Messages list
        for (var i=0; i < data.conversation.length; i++)
        {
            var sender = '';
            if (data.conversation[i].senderType == "requester")
            {
                sender = 'user-message';
            }
            else {
                sender = 'officer-message';
            }

            var message = '<div class="' +  sender + '">' + data.conversation[i].message + '</div>';
            $('#messages-space').append(message);
        }
        $('#messages-space').scrollTop($('#messages-space')[0].scrollHeight);

        var status = getRequestCurrentStatus(requestId);
        var infoStatus;

        switch (status) {
            case "PENDING":
                infoStatus = 'Validation pending by official';
                break;
            case "LOCKED":
                infoStatus = 'Request locked, waiting validation';
                break;
            case "ACCEPTED":
                infoStatus = "Request accepted";
                break;
            default:
                infoStatus = "Request rejected";
        }
        $('#request-status').text(infoStatus);
        $('#request-status').addClass(status.toLowerCase());
    });
}