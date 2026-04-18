$(document).on("foundation-contentloaded", function(e) {
    console.log("Multifield validation called");
    $(window).adaptTo("foundation-registry").register("foundation.validation.validator", {
        selector: "coral-multifield.mf-validate",
        validate: function(el) {
            var $el = $(el);
            var min = parseInt($el.data("min"));
            var max = parseInt($el.data("max"));
            var itemsCount = el.items ? el.items.length : $el.children("coral-multifield-item").length;

            if (!isNaN(min) && itemsCount < min) return "Minimum allowed items is " + min;
            if (!isNaN(max) && itemsCount > max) return "Maximum allowed items is " + max;
        }
    });
});
