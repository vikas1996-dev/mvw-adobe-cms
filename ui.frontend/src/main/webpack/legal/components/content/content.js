const initLegalDisclosureContent = () => {
  // ==========================
  // CONFIG
  // ==========================
  const ERROR_404_URL = "/errors/404.html";
  const ERROR_500_URL = "/errors/500.html";

  const FETCH_RETRIES = 2;        // 2 retries + 1 initial = 3 attempts
  const RETRY_BACKOFF_MS = 500;   // 500ms, then 1000ms, etc.
  const ZIP_CONCURRENCY = 3;      // parallel downloads per batch
  const JSZIP_CDN_URL = "https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js";

  let jsZipLoaderPromise;

  // ==========================
  // TAB SWITCHING
  // ==========================
  const tabButtons = document.querySelectorAll(".tab-button");
  const tabPanels = document.querySelectorAll('[role="tabpanel"]');
  const downloadAllButton = document.getElementById("download-all");
  const downloadAllContainer = downloadAllButton?.closest(".button-container");

  const updateDownloadAllVisibility = () => {
    if (!downloadAllContainer) return;

    const activePanel = [...tabPanels].find((panel) => !panel.classList.contains("hidden"));
    const hasFiles = Boolean(activePanel?.querySelector(".file-link"));

    downloadAllContainer.hidden = !hasFiles;
  };

  tabButtons.forEach((button) => {
    button.addEventListener("click", () => {
      const targetId = button.dataset.tabTarget;

      tabButtons.forEach((btn) => {
        btn.classList.remove("active-tab");
        btn.setAttribute("aria-selected", "false");
        btn.setAttribute("tabindex", "-1");
      });
      button.classList.add("active-tab");
      button.setAttribute("aria-selected", "true");
      button.setAttribute("tabindex", "0");

      tabPanels.forEach((panel) => {
        if (panel.id === targetId) {
          panel.classList.remove("hidden");
          panel.removeAttribute("hidden");
        } else {
          panel.classList.add("hidden");
          panel.setAttribute("hidden", "");
        }
      });

      updateDownloadAllVisibility();
    });
  });

  // ==========================
  // HELPERS
  // ==========================
  const sleep = (ms) => new Promise((r) => setTimeout(r, ms));

  // Natural alphanumeric sorting: A2 < A10
  const collator = new Intl.Collator(undefined, { numeric: true, sensitivity: "base" });

  const safeName = (name) =>
    (name || "")
      .replace(/[\\/:*?"<>|]/g, "-") // Windows-illegal chars
      .replace(/\s+/g, " ")
      .trim();

  const stripExt = (s) => (s || "").replace(/\.(pdf|docx?|xlsx?)$/i, "");

  const triggerDownload = (blob, fileName) => {
    const tempUrl = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = tempUrl;
    a.download = fileName;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    URL.revokeObjectURL(tempUrl);
  };

  const loadJsZip = () => {
    if (typeof JSZip !== "undefined") return Promise.resolve(JSZip);
    if (jsZipLoaderPromise) return jsZipLoaderPromise;

    const existingScript = document.querySelector(`script[src="${JSZIP_CDN_URL}"]`);
    if (existingScript) {
      jsZipLoaderPromise = new Promise((resolve, reject) => {
        existingScript.addEventListener("load", () => resolve(JSZip), { once: true });
        existingScript.addEventListener("error", () => reject(new Error("Failed to load JSZip.")), { once: true });
      });
      return jsZipLoaderPromise;
    }

    jsZipLoaderPromise = new Promise((resolve, reject) => {
      const script = document.createElement("script");
      script.src = JSZIP_CDN_URL;
      script.async = true;
      script.onload = () => resolve(JSZip);
      script.onerror = () => reject(new Error("Failed to load JSZip."));
      document.head.appendChild(script);
    });

    return jsZipLoaderPromise;
  };

  // Redirect based on issue
  const redirectByError = (err) => {
    const status = err?.status;

    if (status === 404) return window.location.assign(ERROR_404_URL);

    // Treat forbidden as not-found (common for DAM permissions)
    if (status === 403) return window.location.assign(ERROR_404_URL);

    // Everything else (network/5xx/429/etc) -> 500
    return window.location.assign(ERROR_500_URL);
  };

  // IMPORTANT: your servlet is /bin/...?...path=/content/dam/...
  // '+' problems happen inside the "path" query param, NOT the URL pathname.
  const normalizePdfViewUrl = (url) => {
    try {
      const u = new URL(url, window.location.origin);
      if (u.searchParams.has("path")) {
        const p = u.searchParams.get("path") || "";
        // replace '+' with space, then URLSearchParams will encode as %20
        if (p.includes("+")) u.searchParams.set("path", p.replace(/\+/g, " "));
      }
      return u.toString();
    } catch (e) {
      return url;
    }
  };

  const makeHttpError = (status, url, noRetry = false) => {
    const err = new Error(`HTTP ${status} for ${url}`);
    err.status = status;
    err.noRetry = noRetry;
    return err;
  };

  // Retry fetch: retry network + 5xx + 429; no retry for 404/403
  const fetchBlobWithRetry = async (url, retries = FETCH_RETRIES, backoffMs = RETRY_BACKOFF_MS) => {
    const finalUrl = normalizePdfViewUrl(url);
    let lastErr;

    for (let attempt = 0; attempt <= retries; attempt++) {
      try {
        const resp = await fetch(finalUrl, { credentials: "include" });

        if (!resp.ok) {
          if (resp.status === 404 || resp.status === 403) {
            throw makeHttpError(resp.status, resp.url, true); // permanent -> no retry
          }
          throw makeHttpError(resp.status, resp.url, false);
        }

        return await resp.blob();
      } catch (e) {
        lastErr = e;

        if (e?.noRetry) throw e;
        if (attempt >= retries) break;

        await sleep(backoffMs * (attempt + 1));
      }
    }

    throw lastErr;
  };

  // Build per-file metadata from DOM row/link
  const buildDownloadMeta = (link, tdTitle) => {
    const docTitle = stripExt(tdTitle?.textContent?.trim() || "Document");

    // Prefer first text node (avoids sr-only text/icons if present)
    const language =
      (link.childNodes && link.childNodes[0]?.textContent?.trim()) ||
      link.textContent?.trim() ||
      "Unknown";

    const fileName = safeName(`${docTitle} - ${language}.pdf`);
    const fileUrl = new URL(link.getAttribute("href"), window.location.origin).toString();

    return { fileName, fileUrl, docTitle, language };
  };

  // ==========================
  // DOWNLOAD ALL AS ZIP (ONLY CURRENT TAB)
  // ==========================
  const downloadAllAsZip = async () => {
    // Visible panel = whichever tab is active
    const activePanel = [...tabPanels].find((p) => !p.classList.contains("hidden"));
    if (!activePanel) return;

    const rows = activePanel.querySelectorAll("tr");
    const files = [];

    rows.forEach((row) => {
      const tdTitle = row.querySelector("td.document-title");
      const links = row.querySelectorAll(".file-link");
      if (!tdTitle || links.length === 0) return;

      links.forEach((link) => files.push(buildDownloadMeta(link, tdTitle)));
    });

    if (files.length === 0) return;

    // ✅ Ascending alphanumeric: title, then language
    files.sort((a, b) => {
      const byTitle = collator.compare(a.docTitle, b.docTitle);
      if (byTitle !== 0) return byTitle;
      return collator.compare(a.language, b.language);
    });

    const btn = document.getElementById("download-all");
    const originalText = btn ? btn.textContent : null;

    if (btn) {
      btn.disabled = true;
      btn.textContent = `Preparing ZIP (0/${files.length})...`;
    }

    try {
      const JSZipLib = await loadJsZip();
      const zip = new JSZipLib();
      let completed = 0;

      for (let i = 0; i < files.length; i += ZIP_CONCURRENCY) {
        const batch = files.slice(i, i + ZIP_CONCURRENCY);

        await Promise.all(
          batch.map(async ({ fileName, fileUrl }) => {
            const blob = await fetchBlobWithRetry(fileUrl);
            zip.file(fileName, blob);

            completed++;
            if (btn) btn.textContent = `Preparing ZIP (${completed}/${files.length})...`;
          })
        );
      }

      if (btn) btn.textContent = "Generating ZIP...";

      const zipBlob = await zip.generateAsync({ type: "blob" });
      const zipName = safeName(`documents-${new Date().toISOString().slice(0, 10)}.zip`);
      triggerDownload(zipBlob, zipName);
    } catch (err) {
      console.error("ZIP download failed:", err);
      redirectByError(err);
    } finally {
      if (btn) {
        btn.disabled = false;
        btn.textContent = originalText;
      }
    }
  };

  document.getElementById("download-all")?.addEventListener("click", (e) => {
    e.preventDefault();
    downloadAllAsZip();
  });

  updateDownloadAllVisibility();

  // Optional tooltip for truncated titles
  document.querySelectorAll("td.document-title").forEach((td) => {
    if (td.scrollWidth > td.clientWidth) td.setAttribute("title", td.textContent.trim());
  });
};

if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", initLegalDisclosureContent);
} else {
  initLegalDisclosureContent();
}
