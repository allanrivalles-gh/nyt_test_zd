document.title = "the athletic"; // WebviewSilentCrashDetector looks for document.title.length > 0

function imageClicked(item) {
    var r = item.getBoundingClientRect();
    var rectangleString = '{{'+r.left+','+r.top+'},{'+r.width+','+r.height+'}}';
    window.location.href = "athleticimage://" + encodeURIComponent(item.getAttribute('src')) + "/" + encodeURIComponent(rectangleString);
}
