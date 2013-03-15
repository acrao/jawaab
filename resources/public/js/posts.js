/* Post page */

$(function() {
    $(".reply-post").on('click', function() {
        $(".new-post-form").toggle();
    })
});

$(function() {
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
});