(function initRuleBasedClickTracking() {
  const TRACKED_FLAG = "__analyticsTracked";
  const SKIP_ATTR = "data-analytics-skip";

  // Helper to detect active tab for destinationPage
  function getActiveTabKey() {
    const selected = document.querySelector(".tabs [role='tab'][aria-selected='true']");
    const id = selected?.id || "";

    if (id === "tab-archive") return "archive";
    return "active";
  }

  // 1) Define tracking rules (your example style)
  const RULES = [
    {
      selector: ".header_logo",
      getEvent: () => "click",
      getName: () => "Logo Icon",
      getRegion: () => "Main Level Navigation",
      getDestinationPage: (el) => el.getAttribute("href") || el.dataset.url || null,
    },
    {
      selector: "a.language-link.file-link",
      getEvent: () => "Select Document Language",
      getLinkPage: (el) => {
        const row = el.closest("tr");
        const titleCell = row?.querySelector(".document-title");
        return (titleCell?.textContent || "").trim() || "";
      },
      getName: (el) => (el.getAttribute("aria-label") || "").trim() || "",
      getRegion: () => "Download",
      getDestinationPage: (el) =>
        (el.getAttribute("href") || el.href || "") || ""
    },
    {
      selector: "#download-all, .download-all",
      getEvent: () => "download all legal docs",
      getName: () => "Download All",
      getRegion: () => "Download",
      // ✅ FIX: should accept (el) and return string, not return a function
      getLinkPage: (el) => (el.getAttribute("data-document-name") || "").trim(),
      getDestinationPage: () => getActiveTabKey()
    },
    {
      selector: "#footer-link, .footer-link",
      getEvent: () => "Footer Link Click",
      getName: (el) => (el.getAttribute("aria-label") || "").trim() || "",
      getRegion: () => "Footer",
      getDestinationPage: (el) => el.getAttribute("href") || el.href || ""
    },
    {
      selector: "#brand-logo, .brand-logo",
      getEvent: () => "Logo Click",
      getName: (el) => (el.getAttribute("aria-label") || "").trim() || "",
      getRegion: () => "Footer",
      getDestinationPage: (el) => el.getAttribute("href") || el.href || ""
    },
    {
      selector: "#support-link, .support-link",
      getEvent: () => "Support Link Click",
      getName: (el) => {
        const anchor = el.closest("a[href]") || (el.matches("a[href]") ? el : null);
        const href = (anchor?.getAttribute("href") || "").trim().toLowerCase();

        if (href.startsWith("mailto:")) return "Email Link";
        if (href.startsWith("tel:")) return "Phone Link";

        return (anchor?.getAttribute("aria-label") || el.getAttribute("aria-label") || "").trim() || "";
      },
      getRegion: () => "Footer",
      getDestinationPage: (el) => {
        const anchor = el.closest("a[href]") || (el.matches("a[href]") ? el : null);
        return anchor?.getAttribute("href") || anchor?.href || el.getAttribute("href") || el.href || "";
      }
    }
  ];

  // 2) Push to your Adobe analytics layer
  function pushToAnalyticsLayer(payload) {
    if (window.adobeDataLayer && typeof window.adobeDataLayer.push === "function") {
      window.adobeDataLayer.push(payload);
      return Promise.resolve();
    }

    window.dataLayer = window.dataLayer || [];
    window.dataLayer.push(payload);
    return Promise.resolve();
  }

  function safe(fn, fallback = null) {
    try { return fn(); } catch (_) { return fallback; }
  }

  // ✅ Updated: add linkPage ONLY when it has a real value
  function buildPayload(rule, el) {
    // If rule provides a custom payload builder, use it
    if (typeof rule.buildPayloadCustom === "function") {
      return safe(() => rule.buildPayloadCustom(el), {});
    }

    const linkPageRaw = rule.getLinkPage ? safe(() => rule.getLinkPage(el), null) : null;
    const linkPageValue = (linkPageRaw ?? "").toString().trim();

    return {
      event: safe(() => rule.getEvent?.(el), "click"),
      linkName: safe(() => rule.getName?.(el), null),
      linkRegion: safe(() => rule.getRegion?.(el), null),
      destinationPage: rule.getDestinationPage ? safe(() => rule.getDestinationPage(el), null) : null,

      // ✅ only render if non-empty
      ...(linkPageValue ? { linkPage: linkPageValue } : {})
    };
  }

  function findRuleMatch(target) {
    for (const rule of RULES) {
      const matchedEl = target.closest(rule.selector);
      if (matchedEl) return { rule, el: matchedEl };
    }
    return null;
  }

  function shouldTrack(el) {
    if (!el) return false;
    if (el.closest(`[${SKIP_ATTR}]`)) return false;
    if (el[TRACKED_FLAG]) return false;
    return true;
  }

  function continueOriginalAction(el, originalEvent) {
    const anchor = el.closest("a[href]");
    if (anchor) {
      const href = anchor.href;
      const target = anchor.getAttribute("target");
      const newTab = originalEvent.metaKey || originalEvent.ctrlKey || target === "_blank";

      if (newTab) window.open(href, target || "_blank");
      else window.location.assign(href);
      return;
    }

    // Submit support
    if (el.matches("button, input[type='submit']")) {
      const form = el.closest("form");
      if (form) {
        if (typeof form.requestSubmit === "function") form.requestSubmit(el);
        else form.submit();
        return;
      }
    }

    // Replay click for JS handlers
    const replay = new MouseEvent("click", { bubbles: true, cancelable: true, view: window });
    el.dispatchEvent(replay);
  }

  function onClickCapture(e) {
    if (e.button !== 0) return;
    if (e.defaultPrevented) return;

    const target = e.target instanceof Element ? e.target : null;
    if (!target) return;

    const match = findRuleMatch(target);
    if (!match) return;

    const { rule, el } = match;
    if (!shouldTrack(el)) return;

    el[TRACKED_FLAG] = true;

    const payload = buildPayload(rule, el);

    try {
      // Don't await. Keep it in the same user gesture and don't block other handlers.
      pushToAnalyticsLayer(payload);
    } catch (err) {
      console.warn("Analytics push failed:", err);
    }

    // Allow the original click to continue so 3rd party JS runs normally.
    queueMicrotask(() => {
      try { delete el[TRACKED_FLAG]; } catch (_) { el[TRACKED_FLAG] = false; }
    });
  }

  document.addEventListener("click", onClickCapture, true);

})();
