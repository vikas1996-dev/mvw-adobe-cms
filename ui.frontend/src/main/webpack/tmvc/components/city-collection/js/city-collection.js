import { registerComponent, Component } from '../../../../site/js/mvw.js';
import DOMPurify from 'dompurify';

class CityCollection extends Component {
  init() {
    this.grid = this.element.querySelector('.resort-grid');
    this.apiEndpoint =
      this.element.dataset.apiEndpoint || this.buildApiEndpoint();

    if (!this.grid) {
      console.error('Resort grid not found');
      return;
    }

    this.showLoader();
    this.loadData();
  }

  buildApiEndpoint() {
    const pathname = window.location.pathname || '';

    if (!pathname || pathname === '/') {
      return '/_jcr_content.data.json?collection=City%20Collection';
    }

    if (pathname.endsWith('.html')) {
      return `${pathname.replace(/\.html$/, '/_jcr_content.data.json')}?collection=City%20Collection`;
    }

    return `${pathname.replace(/\/$/, '')}/_jcr_content.data.json?collection=City%20Collection`;
  }

  showLoader() {
    this.grid.innerHTML = `
      <div class="resort-loader">
        <div class="loader-spinner"></div>
      </div>
    `;
  }

  loadData() {
    fetch(this.apiEndpoint)
      .then((r) => {
        if (!r.ok) throw new Error('Network response was not ok');
        return r.json();
      })
      .then((data) => this.start(data))
      .catch((err) => {
        console.error('Failed to load city collection resorts', err);
        const errorHtml = '<div class="error">Failed to load resorts.</div>';
        this.grid.innerHTML = DOMPurify.sanitize(errorHtml);
      });
  }

  start(data) {
    this.data = data;

    if (!Array.isArray(this.data?.resorts)) {
      console.error('Invalid API response format');
      this.grid.innerHTML =
        '<div class="error">Invalid data format.</div>';
      return;
    }

    this.render();
  }

  createCard(resort, index) {
    const article = document.createElement('article');
    article.className = 'resort-card';
    article.setAttribute('aria-labelledby', `title-${resort.universalPropertyCode}`);

    // Build URL
    const url =
      (resort.slug
        ? `/content/tmvcs/us/en/experiences/resorts/${resort.slug}.html`
        : '#');

    article.setAttribute('data-href', url);

    // Build location string
    const location = [resort.city, resort.state, resort.country]
      .filter(Boolean)
      .join(', ');

      const cardHTML = `
      <div class="resort-image">
              <img 
                src="${resort.image || ''}" 
                alt="${resort.imageAlt || resort.name || 'Resort Image'}" 
                loading="lazy"
              >
              ${resort.collection
              ? `
                <div class="collection-badge">
                  <i class="fa-solid fa-building" aria-hidden="true"></i>
                  ${resort.collection}
                </div>
              `
              : ''
            }
            </div>
      
            <div class="resort-content">
              <div class="resort-info">
                <h3 class="resort-title" id="title-${resort.universalPropertyCode}">
                  ${resort.name || ''}
                </h3>
                <p class="resort-location">
                  ${location}
                </p>
              </div>
      
              <div class="resort-actions">
                <a 
                  href="${url}"
                  class="view-resort"
                  aria-label="View details for ${resort.name || ''}"
                >
                  View Resort
                </a>
      
              ${!resort.tripadvisorId || !resort.ratingImage || !resort.webUrl
              ? ''
              : `
                    <a 
                      href="${resort.webUrl}" 
                      target="_blank" 
                      rel="noopener noreferrer" 
                      class="tripadvisor-rating third-party-link"
                      aria-label="View ${resort.name || ''} reviews on TripAdvisor"
                    >
                      <img 
                        src="${resort.ratingImage}" 
                        alt="${resort.rating || ''} rating"
                        class="rating-image"
                      />
                    </a>
                  `
            }
              </div>
            </div>
      `;

    article.innerHTML = DOMPurify.sanitize(cardHTML);
    return article;
  }

  render() {
    this.grid.innerHTML = '';

    this.data.resorts.forEach((resort, index) => {
      const card = this.createCard(resort, index);
      this.grid.appendChild(card);
    });

    this.grid.querySelectorAll('.resort-card').forEach((card) => {
      card.addEventListener('click', (e) => {
        if (!e.target.closest('.tripadvisor-rating') && !e.target.closest('.view-resort')) {
          const href = card.getAttribute('data-href');
          if (href && href !== '#') {
            window.location.href = href;
          }
        }
      });
    });
  }

}

registerComponent('city-collection-cmp', CityCollection);
export default CityCollection;
