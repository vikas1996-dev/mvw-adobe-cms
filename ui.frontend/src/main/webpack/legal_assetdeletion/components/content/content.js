(function ($, document) {
    "use strict";

    // Listen for the specific Coral Dialog ID being opened
    $(document).on("coral-overlay:open", "#deleteAssetDialog", function (e) {
        
        // Only trigger if the current window path is under /content/dam/legal
        const currentPath = window.location.pathname;
        const targetPath = "/assets.html/content/dam/legal";

        if (currentPath.indexOf(targetPath) === 0) {
            const $dialog = $(e.target);
            const $content = $dialog.find('coral-dialog-content');

            // Safety check: Don't add the warning twice
            if ($content.find('.publish-warning-msg').length === 0) {
                
                const warningMsg = `
                    <coral-alert variant="warning" size="S" class="publish-warning-msg" style="margin-top: 15px;">
                        <coral-alert-header>Warning</coral-alert-header>
                        <coral-alert-content>
                            This action cannot be undone. Please ensure the asset(s) is unpublished before deleting.
                        </coral-alert-content>
                    </coral-alert>
                `;
                
                $content.append(warningMsg);
            }
        }
    });
})(Granite.$, document);