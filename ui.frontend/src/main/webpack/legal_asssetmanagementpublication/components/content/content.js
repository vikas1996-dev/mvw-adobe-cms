(function (document, $) {
    "use strict";

    let pastDateAlertTimer = null;
    let activationSessionActive = false;
    let activationSessionInitialValue = null;
    let activationSessionModified = false;
    let todayDateConfirmationConfirmedValue = null;
    let todayDateConfirmationDeclinedValue = null;
    let todayDateConfirmationInteractionStarted = false;

    // Listen for the change event on the datepicker
    $(document).on("change", "coral-datepicker[name='activationDate']", function (e) {
        const $picker = $(e.currentTarget);
        const currentValue = $picker.val();

        // ONLY proceed if we haven't already set the default
        if ($picker.data("default-set")) {
            return;
        }

        if (currentValue && currentValue.indexOf('T') !== -1) {
            const datePart = currentValue.split('T')[0];
            
            // Regex to robustly capture the timezone offset (e.g., -04:00 or +05:30)
            const tzMatch = currentValue.match(/([+-]\d{2}:\d{2}|Z)$/);
            const timezonePart = tzMatch ? tzMatch[0] : "";

            // Construct 12:00 AM (Midnight) ISO string
            const midnightValue = `${datePart}T00:00:00.000${timezonePart}`;

            if (currentValue !== midnightValue) {
                $picker.data("default-set", true);
                
                // Update the component value directly via the DOM element
                e.currentTarget.value = midnightValue;
                
                // Refresh UI components
                setTimeout(() => {
                    $picker.trigger("change");
                }, 10);
            }
        }
    });

    // Initial UI cleanup on click
    $(document).on("click", "coral-datepicker[name='activationDate']", function (e) {
        const $picker = $(e.currentTarget);
        beginActivationSession(e.currentTarget, "click");
        
        if (!$picker.data("default-set")) {
            setTimeout(() => {
                const $hourInput = $("._coral-Clock-hour");
                const $minuteInput = $("._coral-Clock-minute");
                const $periodSelect = $("._coral-Clock-period");

                if ($hourInput.val() !== "12") $hourInput.val("12").trigger("change");
                if ($minuteInput.val() !== "00") $minuteInput.val("00").trigger("change");
                
                if ($periodSelect.length && $periodSelect[0].value !== "am") {
                    $periodSelect[0].value = "am";
                    $periodSelect.trigger("change");
                }
            }, 100);
        }
    });

    function getActivationContext($picker) {
        const $dialog = $picker.closest("coral-dialog").length
            ? $picker.closest("coral-dialog")
            : $("coral-dialog").has("coral-datepicker[name='activationDate']").last();
        const $wizard = $picker.closest("form, .foundation-wizard").length
            ? $picker.closest("form, .foundation-wizard")
            : $("form, .foundation-wizard").has("coral-datepicker[name='activationDate']").last();

        return {
            $container: $dialog.length ? $dialog : ($wizard.length ? $wizard : $(document)),
            $messageContainer: $dialog.find("coral-dialog-content").first().length
                ? $dialog.find("coral-dialog-content").first()
                : ($wizard.length ? $wizard : $picker.parent())
        };
    }

    function beginActivationSession(sourceElement, logContext) {
        const $picker = sourceElement ? $(sourceElement) : $("coral-datepicker[name='activationDate']").last();

        if (!$picker.length || activationSessionActive) {
            return;
        }

        activationSessionActive = true;
        activationSessionInitialValue = $picker.val() || $picker[0].value || "";
        activationSessionModified = false;
        console.log(`[assetmanagepublication] activation session started${logContext ? ` on ${logContext}` : ""}`, {
            activationSessionInitialValue
        });
    }

    function syncPastActivationPublishState(sourceElement) {
        const $picker = sourceElement ? $(sourceElement) : $("coral-datepicker[name='activationDate']").last();
        console.log("[assetmanagepublication] syncPastActivationPublishState called", {
            hasSourceElement: !!sourceElement,
            pickerCount: $picker.length
        });

        if (!$picker.length) {
            console.log("[assetmanagepublication] no activationDate picker found");
            return;
        }

        const pickerValue = $picker.val() || $picker[0].value;
        const selectedDateTime = pickerValue ? new Date(pickerValue) : null;
        const shouldDisableNext = shouldDisableNextButtonForScheduling($picker);
        const context = getActivationContext($picker);
        const $container = context.$container;

        console.log("[assetmanagepublication] evaluated datetime", {
            pickerValue,
            selectedDateTime: selectedDateTime instanceof Date && !Number.isNaN(selectedDateTime.getTime())
                ? selectedDateTime.toISOString()
                : null,
            now: new Date().toISOString(),
            shouldDisableNext
        });
        console.log(`[assetmanagepublication] next button should be ${shouldDisableNext ? "disabled" : "enabled"}`);

        $container
            .find("button.foundation-wizard-control[data-foundation-wizard-control-action='next']")
            .each(function () {
                this.disabled = shouldDisableNext;
                $(this).attr("aria-disabled", shouldDisableNext ? "true" : "false");
                console.log("[assetmanagepublication] updated next button state", {
                    buttonLabel: ($(this).text() || "").trim(),
                    disabled: this.disabled
                });
            });
    }

    function getSchedulingMode() {
        return $("input[type='radio'][name='scheduling']:checked").val()
            || $("coral-radio[name='scheduling'][checked]").attr("value")
            || "";
    }

    function shouldDisableNextButtonForScheduling(sourceElement) {
        const $picker = sourceElement ? $(sourceElement) : $("coral-datepicker[name='activationDate']").last();
        const schedulingMode = getSchedulingMode();

        if (schedulingMode === "now") {
            console.log("[assetmanagepublication] scheduling is set to now; next button can stay enabled");
            return false;
        }

        if (!$picker.length) {
            console.log("[assetmanagepublication] scheduling is later but activationDate picker is missing; disabling next button");
            return true;
        }

        const pickerElement = $picker[0];
        const isPickerHidden = !pickerElement.offsetParent && !$(pickerElement).is(":visible");
        const isPickerDisabled = !!pickerElement.disabled || $picker.attr("disabled") !== undefined;

        if (isPickerHidden || isPickerDisabled) {
            console.log("[assetmanagepublication] scheduling is later and activationDate picker is not ready; disabling next button");
            return true;
        }

        const pickerValue = $picker.val() || $picker[0].value;
        const selectedDateTime = pickerValue ? new Date(pickerValue) : null;
        const isPastDateTime = !(selectedDateTime instanceof Date)
            || Number.isNaN(selectedDateTime.getTime())
            || selectedDateTime.getTime() < Date.now();

        console.log(`[assetmanagepublication] scheduling is later and selected date/time is ${isPastDateTime ? "in the past/invalid" : "in the future"}`);
        return isPastDateTime;
    }

    function isSelectedActivationInPast(sourceElement) {
        return shouldDisableNextButtonForScheduling(sourceElement);
    }

    $(document).on("change input", "coral-datepicker[name='activationDate']", function (e) {
        setTimeout(() => {
            console.log("[assetmanagepublication] activationDate change/input event triggered");
            syncPastActivationPublishState(e.currentTarget);
        }, 0);
    });

    $(document).on("input change", "._coral-Clock-hour, ._coral-Clock-minute, ._coral-Clock-period", function () {
        setTimeout(() => {
            console.log("[assetmanagepublication] clock input changed");
            syncPastActivationPublishState();
        }, 0);
    });

    $(document).on("change click", "coral-radio[name='scheduling'], input[type='radio'][name='scheduling']", function () {
        setTimeout(() => {
            const schedulingMode = getSchedulingMode();
            console.log("[assetmanagepublication] scheduling selection changed", {
                schedulingMode
            });
            syncPastActivationPublishState();
            setTimeout(() => {
                syncPastActivationPublishState();
            }, 200);
        }, 0);
    });

    $(document).on("coral-overlay:open", "coral-dialog, coral-popover", function () {
        setTimeout(() => {
            console.log("[assetmanagepublication] dialog/popover opened");
            syncPastActivationPublishState();
        }, 0);
    });

    function getPastDateWarningDialog() {
        let dialog = document.querySelector("#past-date-warning-dialog");

        if (dialog) {
            return dialog;
        }

        dialog = document.createElement("coral-dialog");
        dialog.id = "past-date-warning-dialog";
        dialog.setAttribute("variant", "warning");
        dialog.innerHTML = `
            <coral-dialog-header>Warning</coral-dialog-header>
            <coral-dialog-content>
                Selected date/time is in the past. Please choose a future date/time.
            </coral-dialog-content>
            <coral-dialog-footer>
                <button is="coral-button" variant="primary" coral-close>OK</button>
            </coral-dialog-footer>
        `;

        document.body.appendChild(dialog);
        return dialog;
    }

    function setPastDateWarningDialogState(shouldShow) {
        const dialog = getPastDateWarningDialog();

        if (shouldShow) {
            if (!dialog.hasAttribute("open")) {
                dialog.setAttribute("open", "");
                console.log("[assetmanagepublication] past date warning popup shown");
            }
            return;
        }

        if (dialog.hasAttribute("open")) {
            dialog.removeAttribute("open");
            console.log("[assetmanagepublication] past date warning popup removed");
        }
    }

    function syncPastActivationAlert(sourceElement) {
        const $picker = sourceElement ? $(sourceElement) : $("coral-datepicker[name='activationDate']").last();

        if (!$picker.length) {
            return;
        }

        const pickerValue = $picker.val() || $picker[0].value;
        const selectedDateTime = pickerValue ? new Date(pickerValue) : null;
        const isPastDateTime = selectedDateTime instanceof Date
            && !Number.isNaN(selectedDateTime.getTime())
            && selectedDateTime.getTime() < Date.now();

        if (!activationSessionActive) {
            console.log("[assetmanagepublication] popup skipped because no activation session is active");
            return;
        }

        console.log("[assetmanagepublication] activation session evaluated", {
            activationSessionInitialValue,
            pickerValue,
            activationSessionModified,
            isPastDateTime
        });

        activationSessionActive = false;
        activationSessionInitialValue = null;
        const wasModified = activationSessionModified;
        activationSessionModified = false;

        if (!wasModified) {
            console.log("[assetmanagepublication] popup skipped because activation value was not modified in this session");
            setPastDateWarningDialogState(false);
            return;
        }

        if (isPastDateTime) {
            setPastDateWarningDialogState(true);
            return;
        }

        setPastDateWarningDialogState(false);
    }

    function schedulePastActivationAlert(sourceElement, delay) {
        clearTimeout(pastDateAlertTimer);
        pastDateAlertTimer = setTimeout(() => {
            syncPastActivationAlert(sourceElement);
        }, delay);
    }

    $(document).on("change input", "coral-datepicker[name='activationDate'], ._coral-Clock-hour, ._coral-Clock-minute, ._coral-Clock-period", function () {
        beginActivationSession(
            $(this).is("coral-datepicker[name='activationDate']") ? this : undefined,
            "change/input"
        );
        activationSessionModified = true;
        console.log("[assetmanagepublication] activation session marked as modified");
        setPastDateWarningDialogState(false);
    });

    $(document).on("focusout", "coral-datepicker[name='activationDate']", function (e) {
        schedulePastActivationAlert(e.currentTarget, 250);
    });

    $(document).on("coral-overlay:close", "coral-popover", function () {
        schedulePastActivationAlert(undefined, 250);
    });

    $(document).on("coral-overlay:open", "coral-popover", function () {
        beginActivationSession(undefined, "popover open");
    });

    $(document).on("focusin", "coral-datepicker[name='activationDate'], ._coral-Clock-hour, ._coral-Clock-minute, ._coral-Clock-period", function () {
        beginActivationSession(
            $(this).is("coral-datepicker[name='activationDate']") ? this : undefined,
            "focusin"
        );
    });

    function getActivationPicker(sourceElement) {
        return sourceElement ? $(sourceElement) : $("coral-datepicker[name='activationDate']").last();
    }

    function getActivationPickerValue(sourceElement) {
        const $picker = getActivationPicker(sourceElement);

        if (!$picker.length) {
            return "";
        }

        return $picker.val() || $picker[0].value || "";
    }

    function getTodayLocalDateString() {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, "0");
        const day = String(now.getDate()).padStart(2, "0");

        return `${year}-${month}-${day}`;
    }

    function isTodayDateConfirmationRequired(sourceElement) {
        const $picker = getActivationPicker(sourceElement);

        if (!$picker.length || getSchedulingMode() !== "later") {
            return false;
        }

        if (shouldDisableNextButtonForScheduling($picker)) {
            return false;
        }

        const pickerValue = getActivationPickerValue($picker);
        const selectedDatePart = pickerValue.split("T")[0];

        return selectedDatePart === getTodayLocalDateString();
    }

    function getTodayDateConfirmationDialog() {
        let dialog = document.querySelector("#today-date-confirmation-dialog");

        if (dialog) {
            return dialog;
        }

        dialog = document.createElement("coral-dialog");
        dialog.id = "today-date-confirmation-dialog";
        dialog.setAttribute("variant", "warning");
        dialog.innerHTML = `
            <coral-dialog-header>Confirmation</coral-dialog-header>
            <coral-dialog-content>
                Are you sure? You have selected today's date.
            </coral-dialog-content>
            <coral-dialog-footer>
                <button is="coral-button" variant="default" id="today-date-confirmation-cancel">Cancel</button>
                <button is="coral-button" variant="primary" id="today-date-confirmation-confirm">Confirm</button>
            </coral-dialog-footer>
        `;

        document.body.appendChild(dialog);
        return dialog;
    }

    function setTodayDateConfirmationDialogState(shouldShow) {
        const dialog = getTodayDateConfirmationDialog();

        if (shouldShow) {
            if (!dialog.hasAttribute("open")) {
                dialog.setAttribute("open", "");
                console.log("[assetmanagepublication] today date confirmation popup shown");
            }
            return;
        }

        if (dialog.hasAttribute("open")) {
            dialog.removeAttribute("open");
            console.log("[assetmanagepublication] today date confirmation popup removed");
        }
    }

    function syncTodayDateConfirmationState(sourceElement, shouldPrompt) {
        const $picker = getActivationPicker(sourceElement);
        const pickerValue = getActivationPickerValue($picker);

        if (!$picker.length) {
            return;
        }

        if (!isTodayDateConfirmationRequired($picker)) {
            if (pickerValue !== todayDateConfirmationConfirmedValue) {
                todayDateConfirmationConfirmedValue = null;
            }

            if (pickerValue !== todayDateConfirmationDeclinedValue) {
                todayDateConfirmationDeclinedValue = null;
            }

            todayDateConfirmationInteractionStarted = false;
            setTodayDateConfirmationDialogState(false);
            return;
        }

        const context = getActivationContext($picker);
        const $nextButtons = context.$container
            .find("button.foundation-wizard-control[data-foundation-wizard-control-action='next']");
        const isConfirmed = pickerValue === todayDateConfirmationConfirmedValue;

        $nextButtons.each(function () {
            this.disabled = !isConfirmed;
            $(this).attr("aria-disabled", !isConfirmed ? "true" : "false");
        });

        console.log("[assetmanagepublication] today date confirmation evaluated", {
            pickerValue,
            isConfirmed,
            shouldPrompt,
            todayDateConfirmationInteractionStarted
        });

        if (isConfirmed) {
            todayDateConfirmationInteractionStarted = false;
            setTodayDateConfirmationDialogState(false);
            return;
        }

        if (shouldPrompt && todayDateConfirmationInteractionStarted && pickerValue !== todayDateConfirmationDeclinedValue) {
            setTodayDateConfirmationDialogState(true);
        }
    }

    $(document).on("change input", "coral-datepicker[name='activationDate'], ._coral-Clock-hour, ._coral-Clock-minute, ._coral-Clock-period", function () {
        const pickerValue = getActivationPickerValue();

        todayDateConfirmationInteractionStarted = true;

        if (pickerValue !== todayDateConfirmationConfirmedValue) {
            todayDateConfirmationConfirmedValue = null;
        }

        if (pickerValue !== todayDateConfirmationDeclinedValue) {
            todayDateConfirmationDeclinedValue = null;
        }

        setTimeout(() => {
            syncTodayDateConfirmationState(undefined, false);
        }, 0);
    });

    $(document).on("focusout", "coral-datepicker[name='activationDate']", function (e) {
        setTimeout(() => {
            syncTodayDateConfirmationState(e.currentTarget, true);
        }, 250);
    });

    $(document).on("coral-overlay:close", "coral-popover", function () {
        setTimeout(() => {
            syncTodayDateConfirmationState(undefined, true);
        }, 250);
    });

    $(document).on("change click", "coral-radio[name='scheduling'], input[type='radio'][name='scheduling']", function () {
        const pickerValue = getActivationPickerValue();

        todayDateConfirmationInteractionStarted = true;

        if (pickerValue !== todayDateConfirmationConfirmedValue) {
            todayDateConfirmationConfirmedValue = null;
        }

        if (pickerValue !== todayDateConfirmationDeclinedValue) {
            todayDateConfirmationDeclinedValue = null;
        }

        setTimeout(() => {
            syncTodayDateConfirmationState(undefined, true);
        }, 250);
    });

    $(document).on("click", "#today-date-confirmation-confirm", function () {
        todayDateConfirmationConfirmedValue = getActivationPickerValue();
        todayDateConfirmationDeclinedValue = null;
        todayDateConfirmationInteractionStarted = false;
        setTodayDateConfirmationDialogState(false);
        syncPastActivationPublishState();
        syncTodayDateConfirmationState(undefined, false);
        console.log("[assetmanagepublication] today date confirmation accepted");
    });

    $(document).on("click", "#today-date-confirmation-cancel", function () {
        todayDateConfirmationDeclinedValue = getActivationPickerValue();
        todayDateConfirmationConfirmedValue = null;
        todayDateConfirmationInteractionStarted = false;
        setTodayDateConfirmationDialogState(false);
        syncPastActivationPublishState();
        syncTodayDateConfirmationState(undefined, false);
        console.log("[assetmanagepublication] today date confirmation cancelled");
    });

})(document, Granite.$);
