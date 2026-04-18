(function ($, $document) {
    "use strict";

    $.validator.register("minlength", {
        selector: "[data-minlength]",

        validate: function (el) {
            // el may be a DOM element or a jQuery wrapper depending on Granite call
            var $el = $(el);
            var dom = $el[0];

            var minLength = parseInt($el.attr("data-minlength"), 10);
            var value = dom.value ? dom.value.trim() : "";

            if (value.length < minLength) {
                return "Please enter minimum " + minLength + " characters.";
            }
        }
    });

    // Re-validate on typing
    $document.on("keyup change", "[data-minlength]", function () {
        $(this).checkValidity();
    });

})($, $(document));
