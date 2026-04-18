/**
 * Sitemap Column Divider
 * Divides navigation at 2rd index - 1 item goes left, 2rd element and after goes right
 */
class SitemapColumnDividerAdvanced {
  constructor(navigationSelector = '.sitemapnavigation', config = {}) {
     this.navigation = (typeof navigationSelector === 'string') 
      ? document.querySelector(navigationSelector) 
      : navigationSelector;

    this.config = {
      fullWidthCount: 1, // First 1 items full width
      splitAtIndex: 2, // Split at 2rd index (0-based, so this is the 4th item)
      ...config
    };
    this.init();
  }

  init() {
    if (!this.navigation) {
      return;
    }
    
    if (this.navigation.querySelector('.cmp-navigation__group--divided')) {
      return;
    }

    const mainGroup = this.navigation.querySelector('.cmp-navigation__group');
    if (!mainGroup) {
      return;
    }

    this.divideIntoColumns(mainGroup);
  }

  divideIntoColumns(mainGroup) {
    // Get only level-0 items (top-level navigation items)
    const allItems = Array.from(mainGroup.children).filter(child => 
      child.classList.contains('cmp-navigation__item--level-0')
    );
    
    if (allItems.length === 0) {
      return;
    }

    // Get full-width items (first 2)
    const fullWidthItems = allItems.slice(0, this.config.fullWidthCount);
    const itemsToDivide = allItems.slice(this.config.fullWidthCount);

    // Items before 3rd index go to left column (only 1 item)
    // 3rd index and after go to right column
    const splitIndex = this.config.splitAtIndex - this.config.fullWidthCount;
    
    const leftColumnItems = itemsToDivide.slice(0, splitIndex);
    const rightColumnItems = itemsToDivide.slice(splitIndex);
    // Rebuild DOM
    this.rebuildDOM(mainGroup, fullWidthItems, leftColumnItems, rightColumnItems);
  }

  rebuildDOM(mainGroup, fullWidthItems, leftColumnItems, rightColumnItems) {
    // Create new container
    const newContainer = document.createElement('div');
    newContainer.className = 'cmp-navigation__group cmp-navigation__group--divided';

    // Create full-width section
    const fullWidthSection = document.createElement('ul');
    fullWidthSection.className = 'cmp-navigation__group cmp-navigation__group--full';
    fullWidthItems.forEach(item => fullWidthSection.appendChild(item.cloneNode(true)));

    // Create left column
    const leftColumn = document.createElement('ul');
    leftColumn.className = 'cmp-navigation__group cmp-navigation__group--left';
    leftColumnItems.forEach(item => leftColumn.appendChild(item.cloneNode(true)));

    // Create right column
    const rightColumn = document.createElement('ul');
    rightColumn.className = 'cmp-navigation__group cmp-navigation__group--right';
    rightColumnItems.forEach(item => rightColumn.appendChild(item.cloneNode(true)));

    // Wrap left and right columns
    const twoColumnWrapper = document.createElement('div');
    twoColumnWrapper.className = 'cmp-navigation__two-columns';
    twoColumnWrapper.appendChild(leftColumn);
    twoColumnWrapper.appendChild(rightColumn);

    // Assemble
    newContainer.appendChild(fullWidthSection);
    newContainer.appendChild(twoColumnWrapper);
    
    // Replace original
    mainGroup.parentNode.replaceChild(newContainer, mainGroup);
  }
}

// Initialize function
function initSitemapColumns() {
  const allNavs = document.querySelectorAll('.sitemapnavigation, .cmp-navigation');
  
  allNavs.forEach(nav => {
    new SitemapColumnDividerAdvanced(nav, {
      fullWidthCount: 1,
      splitAtIndex: 2
    });
  });
}


// Method 1: DOMContentLoaded
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', function() {
    initSitemapColumns();
  });
} else {
  // Method 2: Already loaded
  initSitemapColumns();
}

// Method 3: Window load as backup
window.addEventListener('load', function() {
  initSitemapColumns();
});

// Export for module usage
if (typeof module !== 'undefined' && module.exports) {
  module.exports = { SitemapColumnDividerAdvanced };
}