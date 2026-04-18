// footer.js - small accessibility JS
(function () {
  "use strict";
  var footer = document.querySelector(".site-footer");
  if (!footer) return;

  // Make each column title toggleable on small screens
  var columns = footer.querySelectorAll(".footer-column");
  columns.forEach(function (col) {
    var title = col.querySelector(".footer-column__title");
    if (!title) return;

    // ensure attribute for accessibility
    col.setAttribute("aria-expanded", "false");
    title.setAttribute("role", "button");
    title.setAttribute("tabindex", "0");
    title.setAttribute("aria-controls", col.id || "");

    function toggle(e) {
      var expanded = col.getAttribute("aria-expanded") === "true";
      col.setAttribute("aria-expanded", expanded ? "false" : "true");
    }

    title.addEventListener("click", toggle);
    title.addEventListener("keydown", function (e) {
      if (e.key === "Enter" || e.key === " ") {
        e.preventDefault();
        toggle();
      }
    });
  });

  // Optional: improve external links
  footer.querySelectorAll('a[href^="http"]').forEach(function (a) {
    if (!a.getAttribute("rel")) a.setAttribute("rel", "noopener noreferrer");
  });
})();
