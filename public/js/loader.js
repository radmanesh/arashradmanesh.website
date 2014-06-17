$(function(){

    if (an_config.checkCookie == false){
        var cookie = 0;
    } else {
        var cookie = $.cookie('an-htsurveydone');
    }


    console.log(an_config);

    if(cookie == 1) {
//        console.log('survey was already done.');
    } else {
        $.ajax({
            url : an_config.hostUrl + "/satellite/" + an_config.questionId,
            context : document.body,
            success : function(response) {
                $('#an-satelliteContainer').html(response);
            }
        });
        $.ajax({
            url : an_config.hostUrl + "/survey/" + an_config.questionId,
            context : document.body,
            success : function(response) {
                $('#an-modalContainer').html(response);
            }
        });
    }

});

