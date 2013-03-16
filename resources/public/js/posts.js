/* Post page */

$(function() {

    var converter = new Showdown.converter();

    $(".reply-post").on('click', function() {
        $(".new-post-form").toggle();
    })

    $(".vote-button").on('click', function() {
        var postId = $(this).data('post-id');
        var voteDir = $(this).data('dir');
        $.ajax({
            url : "/post/vote",
            type : 'POST',
            data : { direction : voteDir, id : postId},
            dataType : 'json'
        }).done(function(data) {
            $(".vote-count-" + postId).text(data["votes"]);
        });
    })

    $(".post-text").each(function() {
        var content = converter.makeHtml($(this).html());
        $(this).html(content);
    });

    $(".reply-text").on('keyup', function() {
        var content = converter.makeHtml($(this).val());
        $("#preview").html(content);
    })
});