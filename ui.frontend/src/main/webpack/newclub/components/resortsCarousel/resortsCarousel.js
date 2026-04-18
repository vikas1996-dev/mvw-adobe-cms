import Splide from '@splidejs/splide';

function initCarousel() {
  var carousels = document.querySelectorAll('.resort-carousel');

  carousels.forEach(function (elem) {

    if (elem.classList.contains('splide--initialized')) return;

    var activeClassFromAEM = 'active-slide';
    var cells = elem.querySelectorAll('.resort-carousel-cell');
    if (cells.length === 1) {
      elem.classList.add('single-slide');
      return;
    }
    var startIndex = 0;

    for (var i = 0; i < cells.length; i++) {
      if (cells[i].classList.contains(activeClassFromAEM)) {
        startIndex = i;
        break;
      }
    }

    if (!elem.classList.contains('splide')) {
      elem.classList.add('splide');
      var list = elem.querySelector('.splide__list') || (function () {
        var ul = document.createElement('div');
        ul.className = 'splide__list';
        while (elem.firstChild) ul.appendChild(elem.firstChild);
        var wrapper = document.createElement('div');
        wrapper.className = 'splide__track';
        wrapper.appendChild(ul);
        elem.appendChild(wrapper);
        return ul;
      })();
      cells.forEach(function (cell) { cell.classList.add('splide__slide'); });
    }

    var splide = new Splide(elem, {
      type: 'loop',
      focus: 'center',
      perPage: 1,
      autoWidth: true,
      pagination: true,
      arrows: true,
      start: startIndex
    });
    splide.mount();

    var imgs = elem.querySelectorAll('img');
    imgs.forEach(function (img) {
      if (img.complete) {
        splide.refresh();
      } else {
        img.addEventListener('load', function () { splide.refresh(); });
        img.addEventListener('error', function () { splide.refresh(); });
      }
    });
  });
}

if (document.readyState !== "loading") {
  initCarousel();
} else {
  document.addEventListener("DOMContentLoaded", initCarousel);
}