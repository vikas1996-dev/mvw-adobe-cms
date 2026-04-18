(function (document, $) {
    'use strict';
    // When dialog gets injected
    $(document).on('foundation-contentloaded', function () {
        $('.cq-dialog-showhide').each(function () {
            showhideHandler(this);
        });
    });
    // When input changes
    $(document).on('change', ':not(input[type=text]).cq-dialog-showhide', function () {
        showhideHandler(this);
    });
    // When textfield changes
    $(document).on('input', 'input[type=text].cq-dialog-showhide', function () {
        showhideHandler(this);
    });
 
    function showhideHandler(input) {
        // Targets that will be shown/hidden
        var target = $(input).data('cqDialogShowhideTarget');
        // If the input is inside a multifield, target only the elements inside the multifield item
        if ($(input).closest('coral-multifield-item').length) target = $(input).closest('coral-multifield-item').find(target);
        // Current selected value
        var value = $(input).val();
        if ($(input).is('coral-checkbox')) value = (!!input.checked).toString();
 
        $(target).each((_, elem) => {
            // If data-showhidetargetvalue is different from the selected value
            // Element should be hidden
            var isHidden = $(elem).attr('data-showhidetargetvalue') !== value;
            $(elem).toggleClass('hide', isHidden);
            // If target is a coral-tab
            if ($(elem).parent().parent().is('coral-panel')) {
                // Show/Hide tab label
                var tabIndex = $(elem).closest('coral-panel').index();
                $(elem)
                    .closest('coral-tabview')
                    .find('coral-tablist>coral-tab:nth-child(' + (tabIndex + 1) + ')')
                    .toggleClass('hide', isHidden);
 
                // Adjust Tab underline position (cloud only)
                // This behaviour can be seen when opening dialog in full-width
                var tab = $(elem).closest('coral-tabview').find('coral-tablist>coral-tab[aria-selected="true"]');
                if (tab.length) {
                    var line = $(elem).closest('coral-tabview').find('coral-tablist>div[handle="line"]');
                    var width = tab.width();
                    var position = tab.position().left + parseInt(tab.css('marginLeft').replace('px', ''));
                    line.css('transform', 'translate(' + Math.round(position) + 'px, 0px)');
                    line.css('width', Math.round(width) + 'px');
                }
            }
            // If element is hidden, remove all required inputs
            // To track which inputs were required, we store that info in the data-required/data-aria-required
            if (isHidden) {
                $(elem).find('[required]').attr('data-required', 'true').removeAttr('required')
                $(elem).find('[aria-required]').attr('data-aria-required', 'true').removeAttr('aria-required');
            }
            else {
                $(elem).find('[data-required]').attr('required', 'true').removeAttr('data-required');
                $(elem).find('[data-aria-required]').attr('aria-required', 'true').removeAttr('data-aria-required');
            }
        });
    }
})(document, Granite.$);