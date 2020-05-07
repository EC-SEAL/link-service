
$(document).ready(function () {
    getAuthSources();
});

function getAuthSources() {
    $.ajax( {
        type: "GET",
        url: "/link/auth/sources",
        async: true,
        beforeSend: function () {
            $('#hide-div').show();
        }
    }).done(function (data, textStatus, jqXHR) {
       for (i=0; i < data.length; i++)
       {
           if (data[i].logo == '' || data[i].logo == null) {
               logo = '/images/lock.svg';
           }
           else {
               logo = data[i].logo;
           }

           authsource = '<div class="source">\n' +
               ' <div source-id="' + data[i].id + '" class="img-content">\n' +
               '  <img src="' + logo + '" />\n' +
               ' </div>\n' +
               ' <p>' + data[i].defaultDisplayName + '</p>\n' +
               '</div>';

           $('#authsources').append(authsource);
       }

       $('.img-content').click(function () {
           callAuthSource($(this).attr('source-id'));
       });

        $('#hide-div').hide();
    }).fail(function (data, textStatus, jqXHR) {
        $('#hide-div').hide();
        alert(data.responseJSON.message);
    });
}

function callAuthSource(sourceId) {

    $.ajax({
        type: "GET",
        url: "/link/auth/service/"+sourceId,
        asybc: true,
        beforeSend: function () {
            $('#hide-div').show();
        }
    }).done(function(data, textStatus, jqXHR) {
        var form = $('<form action="' + data.endpoint + '" method="'+ data.connectionType +'">' +
            '<input type="hidden" name="msToken" value="' + data.msToken + '" />' +
            '</form>');
        $('body').append(form);
        $(form).submit();
    }).fail(function (data, textStatus, jqXHR) {
        $('#hide-div').hide();
        alert(data.responseJSON.message);
    });
}
