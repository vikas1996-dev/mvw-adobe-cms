import { registerComponent, Component } from '../.././../../site/js/mvw.js';
import { getVideoModal } from '../../../shared/videomodal/js/videomodal.js';

class CtaContainer extends Component {
    init() {
        this.ctaButtons = this.element.querySelectorAll('[data-cta-type="video"]');
        this.addEventListeners();
    }

    addEventListeners() {
        this.ctaButtons.forEach((button) => {
            button.addEventListener('click', (e) => {
                e.preventDefault();
                this.handleVideoClick(button);
            });
        });
    }

    handleVideoClick(button) {
        const videoId = button.getAttribute('data-video-id');
        if (videoId) {
            const videoModal = getVideoModal();
            videoModal.open(videoId);
        }
    }
}

registerComponent('ctacontainer', CtaContainer);

export default CtaContainer;
