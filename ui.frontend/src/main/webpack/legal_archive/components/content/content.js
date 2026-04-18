(function (document, $) {
    "use strict";

    var ITEM_SELECTOR = ".foundation-collection-item";
    var TAG_TO_CHECK = "legal:archive";
    var LEGAL_ROOT = "/content/dam/legal";

    function checkAssets() {
        // Only proceed if the current URL path contains our target directory
        if (window.location.pathname.indexOf(LEGAL_ROOT) === -1 && window.location.pathname.indexOf("aem/search.html")=== -1) {
            return;
        }

        $(ITEM_SELECTOR).each(function () {
            var $item = $(this);
            var assetPath = $item.attr("data-foundation-collection-item-id");

            if (assetPath && !$item.hasClass("legal-processed") && assetPath.indexOf(LEGAL_ROOT)>-1) {
                $item.addClass("legal-processed");

                $.get(assetPath + ".2.json").done(function (data) {
                    var tags = (data && data["jcr:content"] && data["jcr:content"]["metadata"]) 
                               ? data["jcr:content"]["metadata"]["cq:tags"] || [] 
                               : [];
                    
                    var tagArray = Array.isArray(tags) ? tags : [tags];

                    if (tagArray.indexOf(TAG_TO_CHECK) !== -1) {
                        var tag = new Coral.Tag();
                        tag.color = "red";
                        tag.label.innerHTML = "ARCHIVED";
                        
                        $(tag).css({
                            "position": "absolute",
                            "top": "15px",
                            "left": "15px",
                            "z-index": "10",
                            "pointer-events": "none"
                        });

                        var $target = $item.find("coral-card-asset");
                        if ($target.length > 0) {
                            $target.css("position", "relative").append(tag);
                        }
                    }
                });
            }
        });
    }

    // Handle navigation within the Assets SPA
    $(document).on("foundation-contentloaded", checkAssets);
    
    // Initial check for the first page load
    $(function() {
        checkAssets();
    });

})(document, Granite.$);