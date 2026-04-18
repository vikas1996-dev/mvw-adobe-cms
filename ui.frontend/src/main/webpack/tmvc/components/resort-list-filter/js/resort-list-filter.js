import { registerComponent, Component } from '../.././../../site/js/mvw.js';

const resortListData = /* paste your JSON here */ null;

const norm = (v) => (v || '').toLowerCase().replace(/[^a-z0-9]+/g, '');

class ResortListFilter extends Component {
  init() {
    this.regionSelect = this.element.querySelector('#regionFilter');
    this.brandSelect = this.element.querySelector('#brandFilter');
    this.leftColumn = this.element.querySelector('#leftColumn');
    this.rightColumn = this.element.querySelector('#rightColumn');
    this.apiEndpoint = '/bin/mvw/resort-list';

    if (resortListData) {
      this.start(resortListData);
    } else {
      fetch(this.apiEndpoint)
        .then((r) => r.json())
        .then((data) => this.start(data))
        .catch((err) => console.error('Failed to load resortList.json', err));
    }
  }

  start(data) {
    this.data = data;
    this.buildFilters();
    this.regionSelect.addEventListener('change', () => this.render());
    this.brandSelect.addEventListener('change', () => this.render());
    this.render();
  }

  buildFilters() {
    const { regionFilter, brandsFilter } = this.data;

    this.regionSelect.innerHTML = '';
    this.regionSelect.appendChild(new Option('All Locations', ''));
    regionFilter.forEach((region) => {
      const og = document.createElement('optgroup');
      og.label = region.header;
      region.subHeader.forEach((sub) => og.appendChild(new Option(sub, sub)));
      this.regionSelect.appendChild(og);
    });

    this.brandSelect.innerHTML = '';
    this.brandSelect.appendChild(new Option('All Brands', ''));
    brandsFilter.forEach((brand) => {
      this.brandSelect.appendChild(new Option(brand.name, brand.name));
    });
  }

  filterResorts(resorts, selectedRegion, selectedBrand) {
    const wantedRegion = norm(selectedRegion);
    return resorts.filter((r) => {
      const regionCandidates = [r.state, r.subLocale?.name, r.locale?.name, r.region].map(norm);
      const regionMatch = !wantedRegion || regionCandidates.includes(wantedRegion);
      const brandMatch = !selectedBrand || r.dcmBrand?.name === selectedBrand;
      return regionMatch && brandMatch;
    });
  }

  renderColumn(container, columnData, selectedRegion, selectedBrand) {
    container.innerHTML = '';
    let count = 0;

    const groups = [];
    const groupIndex = new Map();

    columnData.forEach((section) => {
      const filteredResorts = this.filterResorts(section.resorts, selectedRegion, selectedBrand);
      if (!filteredResorts.length) return;
      count += filteredResorts.length;

      let group = groupIndex.get(section.mainHeader);
      if (!group) {
        group = { mainHeader: section.mainHeader, subs: [] };
        groupIndex.set(section.mainHeader, group);
        groups.push(group);
      }

      const subMap = new Map();
      filteredResorts.forEach((r) => {
        const key = r.state || r.subLocale?.name || section.subHeader;
        if (!subMap.has(key)) subMap.set(key, []);
        subMap.get(key).push(r);
      });

      subMap.forEach((list, key) => {
        group.subs.push({ sub: key, resorts: list });
      });
    });

    groups.forEach((group) => {
      group.subs.sort((a, b) => a.sub.localeCompare(b.sub, 'en', { sensitivity: 'base' }));
    });

    groups.forEach((group) => {
      const sectionEl = document.createElement('div');
      sectionEl.className = 'section';

      const mainHeader = document.createElement('h4');
      mainHeader.className = 'main-header heading4';
      mainHeader.textContent = group.mainHeader;
      sectionEl.appendChild(mainHeader);

      group.subs.forEach(({ sub, resorts }) => {
        const subH = document.createElement('h5');
        subH.className = 'sub-header heading5';
        subH.textContent = sub; 
        sectionEl.appendChild(subH);

        resorts.forEach((resort) => {
          const item = document.createElement('div');
          item.className = 'resort';
          const a = document.createElement('a');
          a.textContent = resort.name;
          a.href = '/content/tmvcs/us/en/experiences/resorts/' + resort.slug +'.html';
          a.setAttribute('aria-label', resort.name);
          item.appendChild(a);
          sectionEl.appendChild(item);
        });
      });

      container.appendChild(sectionEl);
    });

    return count;
  }

  render() {
    const selectedRegion = this.regionSelect.value;
    const selectedBrand = this.brandSelect.value;

    const leftCount = this.renderColumn(this.leftColumn, this.data.leftColumn, selectedRegion, selectedBrand);
    const rightCount = this.renderColumn(this.rightColumn, this.data.rightColumn, selectedRegion, selectedBrand);

    if (leftCount === 0 && rightCount > 0) {
      this.leftColumn.innerHTML = this.rightColumn.innerHTML;
      this.rightColumn.innerHTML = '';
    }

    if (leftCount + rightCount === 0) {
      this.leftColumn.innerHTML = '<div class="empty">No resorts match this filter.</div>';
      this.rightColumn.innerHTML = '';
    }
  }
}

registerComponent('resort-list-filter-cmp', ResortListFilter);
export default ResortListFilter;