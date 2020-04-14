var statusClass;
var level1Class;
var level2Class;

var messages = [];

$(document).ready(function () {
    getUserData();
    getRequestList();
    initMainLogic();
    initRequestDivLogic();
});

function getUserData() {
    $.ajax({
        type: "GET",
        url: "/link/module/user/data",
        async: true
    }).done(function (data, textStatus, jqXHR) {
        //console.log(data);
        $('#user-data-access').text(data.authName);
        $('#user-data-name').text(data.userName);

        if (data.userPhoto != '') {
            $('#user-img').attr('src', data.userPhoto);
        }
    });
}

function getRequestList() {
    $.ajax({
        type: "GET",
        url: "/link/module/requests",
        async: true,
        beforeSend: function () {
            $('.content').addClass('disabled');
        }
    }).done(function (data, textStatus, jqXHR) {
        for (i = 0; i < data.length; i++) {
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

function refreshRequestList() {
    $('#table-requests tbody').remove();
    getRequestList();
}

function initMainLogic() {
    $('#refresh-list').click(function (e) {
        e.preventDefault();
        refreshRequestList();
    });

    setInterval(refreshRequestList, 60000);
}

function clearRequestData() {
    $('#request-attr-1 div.attr-content table').text('');
    $('#request-attr-2 div.attr-content table').text('');
    $('#request-docs').text('');
    $('#messages-space').text('');
    $('#request-status').removeClass(statusClass);
    $('#request-attr-1 .level').removeClass(level1Class);
    $('#request-attr-2 .level').removeClass(level2Class);

    $('#lock-request').show();
    $('#unlock-request').show();
    $('#validate-request').attr('disabled', false);
    $('#validate-request').removeClass("button-disabled");
    $('#reject-request').attr('disabled', false);
    $('#reject-request').removeClass("button-disabled");
    $('#send-message').attr('disabled', false);

    messages = [];
}

function initRequestDivLogic() {
    $('#hide-request').click(function (e) {
        e.preventDefault();
        $("#request-div").hide();
        $('#hide-div').hide();
        clearRequestData();
        refreshRequestList();
    });

    $('#lock-request').click(function () {
        lockRequest($('#request-id').val());
    });

    $('#unlock-request').click(function () {
        unlockRequest($('#request-id').val());
    });

    $('#validate-request').click(function () {
        approveRequest($('#request-id').val());
    });

    $('#reject-request').click(function () {
        rejectRequest($('#request-id').val());
    });

    setInterval(getNewMessages, 10000);
}

function showRequestInfoLogic() {
    $('#table-requests input[type=button]').click(function () {
        requestId = $(this).attr('request-id');
        $('#hide-div').show();

        requestId = $(this).attr('request-id');
        getRequestInfo(requestId);
        $("#request-div").show();

    });
}

function fillRequestAttributes(attributtes, properties, attributeId, table) {
    for (i = 0; i < attributtes.length; i++) {
        var attr = '<tr><td class="attr-name">' + attributtes[i].friendlyName + '</td>'
            + '<td class="attr-value';

        if (attributtes[i].name == attributeId) {
            attr = attr + ' attr-id';
        }

        attr = attr + '">' + attributtes[i].values.toString() + '</td>';
        $(table).append(attr);
    }

    $.each(properties, function (key, value) {
        var prop = '<tr class="property"><td class="attr-name">' + key + '</td>' +
            '<td class="attr-value">' + value + '</td></tr>';
        $(table).append(prop);
    });
}

function getRequestCurrentStatus(requestId) {
    var status = '';

    $.ajax({
        type: 'GET',
        url: '/link/module/request/' + requestId + '/info',
        async: false
    }).done(function (data, textStatus, jqXHR) {
        status = data.status;
    });

    return status;
}

function showStatusOptions(status) {
    if (status == "PENDING") {
        $('#unlock-request').hide();
    } else if (status == "LOCKED") {
        $('#lock-request').hide();
    } else {
        $('#unlock-request').hide();
        $('#lock-request').hide();
    }

    if (status != 'LOCKED') {
        $('#validate-request').attr('disabled', true);
        $('#validate-request').addClass("button-disabled");
        $('#reject-request').attr('disabled', true);
        $('#reject-request').addClass("button-disabled");
        $('#send-message').attr('disabled', true);
    }
}

function setInfoStatus(status) {

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
    statusClass = status.toLowerCase();
    $('#request-status').removeClass("pending locked accepted rejected");
    $('#request-status').addClass(statusClass);
}

function getRequestInfo(requestId) {
    $.ajax({
        type: 'GET',
        url: '/link/' + requestId + '/get',
        async: true,
        beforeSend: function () {

        }
    }).done(function (data, textStatus, jqXHR) {
        //console.log(data);
        $('#request-id').val(requestId);
        $('#current-request-id').text(requestId);
        $('#request-date').text(data.issued.substring(0, 10));

        // Attributes section A
        $('#request-attr-1 h3').text(data.datasetA.issuerId);
        $('#request-attr-1 .level').text(data.datasetA.loa);
        level1Class = data.datasetA.loa.toLowerCase();
        $('#request-attr-1 .level').addClass(level1Class);
        fillRequestAttributes(data.datasetA.attributes, data.datasetA.properties, data.datasetA.subjectId, $('#request-attr-1 div.attr-content table'));

        // Attributes section B
        $('#request-attr-2 h3').text(data.datasetB.issuerId);
        $('#request-attr-2 .level').text(data.datasetB.loa);
        level2Class = data.datasetB.loa.toLowerCase();
        $('#request-attr-2 .level').addClass(level2Class)
        fillRequestAttributes(data.datasetB.attributes, data.datasetB.properties, data.datasetB.subjectId, $('#request-attr-2 div.attr-content table'));

        //Files list
        for (var i = 0; i < data.evidence.length; i++) {
            var content = "data:" + data.evidence[i].contentType + ";base64," + data.evidence[i].content;
            var filename = data.evidence[i].filename;
            var file = '<li><a href="' + content + '" file-id="' + data.evidence[i].fileID + '" download="' + filename + '">' + filename + '</a></li>';
            $('#request-docs').append(file);
        }

        //Messages list
        for (var i = 0; i < data.conversation.length; i++) {
            showMessage(data.conversation[i]);
            messages.push(data.conversation[i].timestamp + data.conversation[i].sender);
        }
        $('#messages-space').scrollTop($('#messages-space')[0].scrollHeight);

        var status = getRequestCurrentStatus(requestId);
        setInfoStatus(status);

        showStatusOptions(status, requestId);
    });
}

function lockRequest(requestId) {

    $.ajax({
        type: 'GET',
        url: '/link/' + requestId + '/lock',
        async: true,
        beforeSend: function () {

        }
    }).done(function (data, textStatus, jqXHR) {
        $('#unlock-request').show();
        $('#lock-request').hide();

        $('#validate-request').attr('disabled', false);
        $('#validate-request').removeClass("button-disabled");
        $('#reject-request').attr('disabled', false);
        $('#reject-request').removeClass("button-disabled");
        $('#send-message').attr('disabled', false);

        setInfoStatus("LOCKED");

    }).fail(function (data, textStatus, jqXHR) {
        alert(data.responseJSON.message);
    });
}

function unlockRequest(requestId) {

    $.ajax({
        type: 'GET',
        url: '/link/' + requestId + '/unlock',
        async: true,
        beforeSend: function () {

        }
    }).done(function (data, textStatus, jqXHR) {
        $('#unlock-request').hide();
        $('#lock-request').show();

        $('#validate-request').attr('disabled', true);
        $('#validate-request').addClass("button-disabled");
        $('#reject-request').attr('disabled', true);
        $('#reject-request').addClass("button-disabled");
        $('#send-message').attr('disabled', true);

        setInfoStatus("PENDING");

    }).fail(function (data, textStatus, jqXHR) {
        alert(data.responseJSON.message);
    });

}

function approveRequest(requestId) {

    $.ajax({
        type: 'GET',
        url: '/link/' + requestId + '/approve',
        async: true,
        beforeSend: function () {

        }
    }).done(function (data, textStatus, jqXHR) {
        $('#unlock-request').hide();
        $('#lock-request').hide();

        $('#validate-request').attr('disabled', true);
        $('#validate-request').addClass("button-disabled");
        $('#reject-request').attr('disabled', true);
        $('#reject-request').addClass("button-disabled");
        $('#send-message').attr('disabled', true);

        setInfoStatus("ACCEPTED");

    }).fail(function (data, textStatus, jqXHR) {
        alert(data.responseJSON.message);
    });

}

function rejectRequest(requestId) {

    $.ajax({
        type: 'GET',
        url: '/link/' + requestId + '/reject',
        async: true,
        beforeSend: function () {

        }
    }).done(function (data, textStatus, jqXHR) {
        $('#unlock-request').hide();
        $('#lock-request').hide();

        $('#validate-request').attr('disabled', true);
        $('#validate-request').addClass("button-disabled");
        $('#reject-request').attr('disabled', true);
        $('#reject-request').addClass("button-disabled");
        $('#send-message').attr('disabled', true);

        setInfoStatus("REJECTED");

    }).fail(function (data, textStatus, jqXHR) {
        alert(data.responseJSON.message);
    });

}

function getNewMessages()
{
    var requestId = $('#request-id').val()

    if (requestId != '')
    {
        $.ajax( {
            type: 'GET',
            url: '/link/' + requestId + '/messages/receive',
            async: true
        }).done(function (data, textStatus, jqXHR) {

            for (var i=0; i < data.length; i++)
            {
                var checkValue = data[i].timestamp + data[i].sender;
                if ($.inArray(checkValue, messages) == -1) {
                    showMessage(data[i]);
                    messages.push(checkValue);
                }
            }

            $('#messages-space').scrollTop($('#messages-space')[0].scrollHeight);
        });
    }
}

function showMessage(messageData)
{
    var sender = '';
    if (messageData.senderType == "requester") {
        sender = 'user-message';
    } else {
        sender = 'officer-message';
    }

    var date = new Date(messageData.timestamp);

    var message = '<div class="' + sender + '"><div>' + messageData.message + '</div>' +
        '<div class="time-message">' + date.toLocaleDateString() + ' ' + date.toLocaleTimeString() + '</div></div>';
    $('#messages-space').append(message);
}