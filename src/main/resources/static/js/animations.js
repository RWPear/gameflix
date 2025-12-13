// GameFlix Animations Library - Vanilla JS
// Add this to: src/main/resources/static/js/animations.js

// ============= FUZZY BLUR FADE IN =============
class FuzzyFadeIn {
  constructor(selector, options = {}) {
    this.elements = document.querySelectorAll(selector);
    this.options = {
      duration: options.duration || 1000,
      delay: options.delay || 0,
      blur: options.blur !== false,
      threshold: options.threshold || 0.1,
      translate: options.translate ?? 12,
      ...options
    };
    
    this.init();
  }

  init() {
    if ('IntersectionObserver' in window) {
      this.observer = new IntersectionObserver(
        entries => this.handleIntersect(entries),
        { threshold: this.options.threshold }
      );

      this.elements.forEach(el => {
        el.style.opacity = '0';
        if (this.options.blur) {
          el.style.filter = 'blur(10px)';
        }
        el.style.transform = `translateY(${this.options.translate}px)`;
        el.style.transition = `opacity ${this.options.duration}ms ease, filter ${this.options.duration}ms ease, transform ${this.options.duration}ms ease`;
        this.observer.observe(el);
      });
    }
  }

  handleIntersect(entries) {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        setTimeout(() => {
          entry.target.style.opacity = '1';
          entry.target.style.filter = 'blur(0px)';
          entry.target.style.transform = 'translateY(0)';
        }, this.options.delay);
        this.observer.unobserve(entry.target);
      }
    });
  }
}

// ============= STAR BORDER EFFECT =============
class StarBorder {
  constructor(selector, options = {}) {
    this.buttons = document.querySelectorAll(selector);
    this.options = {
      color: options.color || '#9cf9d8',
      speed: options.speed || 5200,
      thickness: options.thickness || 3,
      ...options
    };
    
    this.init();
  }

  init() {
    this.buttons.forEach(btn => {
      // Create border containers
      const topBorder = document.createElement('div');
      const bottomBorder = document.createElement('div');
      
      topBorder.className = 'star-border-top';
      bottomBorder.className = 'star-border-bottom';
      
      // Set styles
      const borderStyle = {
        position: 'absolute',
        left: '0',
        width: '100%',
        height: `${this.options.thickness}px`,
        overflow: 'hidden',
        pointerEvents: 'none',
        opacity: 0.9,
        mixBlendMode: 'screen'
      };
      
      Object.assign(topBorder.style, borderStyle, { top: '0' });
      Object.assign(bottomBorder.style, borderStyle, { bottom: '0' });
      
      // Create animated dots
      const topDot = this.createDot();
      const bottomDot = this.createDot();
      
      topBorder.appendChild(topDot);
      bottomBorder.appendChild(bottomDot);
      
      // Ensure button is positioned
      if (getComputedStyle(btn).position === 'static') {
        btn.style.position = 'relative';
      }
      btn.style.overflow = 'visible';
      
      btn.appendChild(topBorder);
      btn.appendChild(bottomBorder);
      
      // Start animations
      this.animateDot(topDot, this.options.speed);
      this.animateDot(bottomDot, this.options.speed, this.options.speed / 2);
    });
  }

  createDot() {
    const dot = document.createElement('div');
    dot.className = 'star-border-dot';
    dot.style.cssText = `
      position: absolute;
      width: 140px;
      height: ${this.options.thickness}px;
      background: radial-gradient(circle, ${this.options.color}, transparent 70%);
      box-shadow: 0 0 26px ${this.options.color};
      will-change: transform;
    `;
    return dot;
  }

  animateDot(dot, duration, delay = 0) {
    const animate = () => {
      dot.style.transition = 'none';
      dot.style.transform = 'translateX(-100px)';
      
      setTimeout(() => {
        dot.style.transition = `transform ${duration}ms linear`;
        dot.style.transform = `translateX(calc(100% + 100px))`;
      }, 10);
    };

    setTimeout(() => {
      animate();
      setInterval(animate, duration);
    }, delay);
  }
}

// ============= CARD TILT EFFECT =============
class CardTilt {
  constructor(selector, options = {}) {
    this.cards = document.querySelectorAll(selector);
    this.options = {
      maxTilt: options.maxTilt || 10,
      perspective: options.perspective || 1000,
      scale: options.scale || 1.02,
      ...options
    };
    
    this.init();
  }

  init() {
    this.cards.forEach(card => {
      card.style.transformStyle = 'preserve-3d';
      card.style.transition = 'transform 0.3s ease';
      
      card.addEventListener('mouseenter', () => {
        card.style.transform = `scale(${this.options.scale})`;
      });
      
      card.addEventListener('mousemove', (e) => this.handleMove(e, card));
      
      card.addEventListener('mouseleave', () => {
        card.style.transform = 'perspective(1000px) rotateX(0deg) rotateY(0deg) scale(1)';
      });
    });
  }

  handleMove(e, card) {
    const rect = card.getBoundingClientRect();
    const x = e.clientX - rect.left;
    const y = e.clientY - rect.top;
    
    const centerX = rect.width / 2;
    const centerY = rect.height / 2;
    
    const rotateX = ((y - centerY) / centerY) * this.options.maxTilt;
    const rotateY = ((centerX - x) / centerX) * this.options.maxTilt;
    
    card.style.transform = `
      perspective(${this.options.perspective}px) 
      rotateX(${rotateX}deg) 
      rotateY(${rotateY}deg)
      scale(${this.options.scale})
    `;
  }
}

// ============= MAGNETIC BUTTONS =============
class MagneticButton {
  constructor(selector, options = {}) {
    this.buttons = document.querySelectorAll(selector);
    this.options = {
      strength: options.strength || 0.3,
      ...options
    };
    
    this.init();
  }

  init() {
    this.buttons.forEach(btn => {
      btn.addEventListener('mousemove', (e) => this.handleMove(e, btn));
      btn.addEventListener('mouseleave', () => this.handleLeave(btn));
    });
  }

  handleMove(e, btn) {
    const rect = btn.getBoundingClientRect();
    const x = e.clientX - rect.left - rect.width / 2;
    const y = e.clientY - rect.top - rect.height / 2;
    
    const moveX = x * this.options.strength;
    const moveY = y * this.options.strength;
    
    btn.style.transform = `translate(${moveX}px, ${moveY}px)`;
  }

  handleLeave(btn) {
    btn.style.transform = 'translate(0, 0)';
  }
}

// ============= RIPPLE CLICK EFFECT =============
class RippleEffect {
  constructor(selector, options = {}) {
    this.elements = document.querySelectorAll(selector);
    this.options = {
      color: options.color || 'rgba(255, 255, 255, 0.5)',
      duration: options.duration || 600,
      ...options
    };
    
    this.init();
  }

  init() {
    this.elements.forEach(el => {
      el.style.position = 'relative';
      el.style.overflow = 'hidden';
      
      el.addEventListener('click', (e) => this.createRipple(e, el));
    });
  }

  createRipple(e, el) {
    const ripple = document.createElement('span');
    const rect = el.getBoundingClientRect();
    const size = Math.max(rect.width, rect.height);
    const x = e.clientX - rect.left - size / 2;
    const y = e.clientY - rect.top - size / 2;
    
    ripple.style.cssText = `
      position: absolute;
      width: ${size}px;
      height: ${size}px;
      border-radius: 50%;
      background: ${this.options.color};
      left: ${x}px;
      top: ${y}px;
      transform: scale(0);
      animation: ripple-animation ${this.options.duration}ms ease-out;
      pointer-events: none;
    `;
    
    el.appendChild(ripple);
    
    setTimeout(() => ripple.remove(), this.options.duration);
  }
}

// ============= TOAST NOTIFICATIONS =============
class Toast {
  static show(message, type = 'info', duration = 3000) {
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    
    toast.style.cssText = `
      position: fixed;
      bottom: 24px;
      right: 24px;
      padding: 16px 24px;
      border-radius: 12px;
      background: ${this.getColor(type)};
      color: #fff;
      font-weight: 600;
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
      transform: translateX(400px);
      transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      z-index: 9999;
      max-width: 350px;
    `;
    
    document.body.appendChild(toast);
    
    setTimeout(() => {
      toast.style.transform = 'translateX(0)';
    }, 10);
    
    setTimeout(() => {
      toast.style.transform = 'translateX(400px)';
      setTimeout(() => toast.remove(), 300);
    }, duration);
  }

  static getColor(type) {
    const colors = {
      success: 'linear-gradient(135deg, #8af0c8, #63f5c1)',
      error: 'linear-gradient(135deg, #ff9f7a, #ff6b6b)',
      info: 'linear-gradient(135deg, #7ac8ff, #5ea3d0)',
      warning: 'linear-gradient(135deg, #ffd700, #ffb700)'
    };
    return colors[type] || colors.info;
  }
}

// ============= STAGGERED ANIMATIONS =============
class StaggerAnimation {
  constructor(selector, options = {}) {
    this.elements = document.querySelectorAll(selector);
    this.options = {
      delay: options.delay || 100,
      duration: options.duration || 600,
      ...options
    };
    
    this.init();
  }

  init() {
    if ('IntersectionObserver' in window) {
      this.observer = new IntersectionObserver(
        entries => this.handleIntersect(entries),
        { threshold: 0.1 }
      );

      this.elements.forEach((el, index) => {
        el.style.opacity = '0';
        el.style.transform = 'translateY(30px)';
        el.style.transition = `opacity ${this.options.duration}ms ease, transform ${this.options.duration}ms ease`;
        el.dataset.animationDelay = index * this.options.delay;
        this.observer.observe(el);
      });
    }
  }

  handleIntersect(entries) {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        const delay = parseInt(entry.target.dataset.animationDelay);
        setTimeout(() => {
          entry.target.style.opacity = '1';
          entry.target.style.transform = 'translateY(0)';
        }, delay);
        this.observer.unobserve(entry.target);
      }
    });
  }
}

// ============= PARALLAX SCROLL =============
class ParallaxScroll {
  constructor(selector, options = {}) {
    this.elements = document.querySelectorAll(selector);
    this.options = {
      speed: options.speed || 0.5,
      ...options
    };
    
    this.init();
  }

  init() {
    window.addEventListener('scroll', () => this.handleScroll());
  }

  handleScroll() {
    const scrolled = window.pageYOffset;
    
    this.elements.forEach(el => {
      const speed = el.dataset.parallaxSpeed || this.options.speed;
      const yPos = -(scrolled * speed);
      el.style.transform = `translateY(${yPos}px)`;
    });
  }
}

// ============= CSS ANIMATIONS (Add to your CSS) =============
const animationStyles = `
@keyframes ripple-animation {
  to {
    transform: scale(4);
    opacity: 0;
  }
}

.magnetic-button {
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.card-tilt {
  will-change: transform;
}

.parallax-element {
  will-change: transform;
}
`;

// Inject animation styles once
(() => {
  if (!document.getElementById('gf-animation-styles')) {
    const style = document.createElement('style');
    style.id = 'gf-animation-styles';
    style.textContent = animationStyles;
    document.head.appendChild(style);
  }
})();

// ============= INITIALIZE ON PAGE LOAD =============
window.GameFlixAnimations = {
  FuzzyFadeIn,
  StarBorder,
  CardTilt,
  MagneticButton,
  RippleEffect,
  Toast,
  StaggerAnimation,
  ParallaxScroll,
  
  // Quick init function
  initAll() {
    // Fuzzy fade for game images
    new FuzzyFadeIn('.game-card', { duration: 900, blur: true, translate: 16 });
    new FuzzyFadeIn('.game-card img', { duration: 800, blur: true, translate: 12 });
    new FuzzyFadeIn('.game-hero img', { duration: 1000, blur: true, translate: 14 });
    new FuzzyFadeIn('.plan', { duration: 850, blur: true, translate: 14 });
    new FuzzyFadeIn('.feature', { duration: 850, blur: true, translate: 14 });
    
    // Star borders for CTA buttons
    new StarBorder('.btn.star-cta', { 
      color: '#9cf9d8',
      speed: 5200 
    });
    
    // Card tilt for game cards
    new CardTilt('.game-card', { maxTilt: 8, scale: 1.05 });
    new CardTilt('.plan', { maxTilt: 5, scale: 1.03 });
    
    // Magnetic buttons for main CTAs
    new MagneticButton('.hero-actions .btn', { strength: 0.2 });
    
    // Ripple effect on all buttons
    new RippleEffect('.btn');
    
    // Staggered animations for grids
    new StaggerAnimation('.grid > .card', { delay: 80, duration: 500 });
    new StaggerAnimation('.plan-grid > .plan', { delay: 120, duration: 600 });
    
    // Parallax for floating blurs
    new ParallaxScroll('.floating-blur', { speed: 0.3 });
  }
};

// Auto-initialize if DOM is ready
if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', () => {
    window.GameFlixAnimations.initAll();
  });
} else {
  window.GameFlixAnimations.initAll();
}

// GameFlix Bonus Animations - Additional Effects
// Add these to animations.js or use separately

// ============= 1. PARTICLE SYSTEM =============
class ParticleSystem {
  constructor(container, options = {}) {
    this.container = container;
    this.options = {
      count: options.count || 50,
      color: options.color || '#63f5c1',
      size: options.size || 2,
      speed: options.speed || 0.5,
      ...options
    };
    
    this.canvas = document.createElement('canvas');
    this.ctx = this.canvas.getContext('2d');
    this.particles = [];
    
    this.canvas.style.cssText = `
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      pointer-events: none;
      z-index: 1;
    `;
    
    this.container.style.position = 'relative';
    this.container.appendChild(this.canvas);
    
    this.resize();
    this.createParticles();
    this.animate();
    
    window.addEventListener('resize', () => this.resize());
  }

  resize() {
    this.width = this.container.offsetWidth;
    this.height = this.container.offsetHeight;
    this.canvas.width = this.width;
    this.canvas.height = this.height;
  }

  createParticles() {
    for (let i = 0; i < this.options.count; i++) {
      this.particles.push({
        x: Math.random() * this.width,
        y: Math.random() * this.height,
        vx: (Math.random() - 0.5) * this.options.speed,
        vy: (Math.random() - 0.5) * this.options.speed,
        size: Math.random() * this.options.size + 1,
        opacity: Math.random() * 0.5 + 0.3
      });
    }
  }

  animate() {
    this.ctx.clearRect(0, 0, this.width, this.height);
    
    this.particles.forEach(p => {
      p.x += p.vx;
      p.y += p.vy;
      
      if (p.x < 0 || p.x > this.width) p.vx *= -1;
      if (p.y < 0 || p.y > this.height) p.vy *= -1;
      
      this.ctx.fillStyle = this.options.color;
      this.ctx.globalAlpha = p.opacity;
      this.ctx.beginPath();
      this.ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
      this.ctx.fill();
    });
    
    requestAnimationFrame(() => this.animate());
  }
}

// ============= 2. TEXT SCRAMBLE EFFECT =============
class TextScramble {
  constructor(element, options = {}) {
    this.element = element;
    this.chars = '!<>-_\\/[]{}—=+*^?#________';
    this.options = {
      duration: options.duration || 2000,
      ...options
    };
  }

  setText(newText) {
    const oldText = this.element.innerText;
    const length = Math.max(oldText.length, newText.length);
    
    return new Promise(resolve => {
      const queue = [];
      
      for (let i = 0; i < length; i++) {
        const from = oldText[i] || '';
        const to = newText[i] || '';
        const start = Math.floor(Math.random() * 40);
        const end = start + Math.floor(Math.random() * 40);
        queue.push({ from, to, start, end });
      }
      
      let frame = 0;
      const duration = this.options.duration / 16; // 60fps
      
      const update = () => {
        let output = '';
        let complete = 0;
        
        for (let i = 0; i < queue.length; i++) {
          let { from, to, start, end } = queue[i];
          
          if (frame >= end) {
            complete++;
            output += to;
          } else if (frame >= start) {
            output += this.chars[Math.floor(Math.random() * this.chars.length)];
          } else {
            output += from;
          }
        }
        
        this.element.innerText = output;
        
        if (complete === queue.length) {
          resolve();
        } else {
          frame++;
          requestAnimationFrame(update);
        }
      };
      
      update();
    });
  }
}

// ============= 3. SMOOTH SCROLL TO SECTIONS =============
class SmoothScroll {
  constructor(selector, options = {}) {
    this.links = document.querySelectorAll(selector);
    this.options = {
      duration: options.duration || 800,
      offset: options.offset || 80,
      ...options
    };
    
    this.init();
  }

  init() {
    this.links.forEach(link => {
      link.addEventListener('click', (e) => {
        const href = link.getAttribute('href');
        if (href.startsWith('#')) {
          e.preventDefault();
          const target = document.querySelector(href);
          if (target) {
            this.scrollTo(target);
          }
        }
      });
    });
  }

  scrollTo(target) {
    const start = window.pageYOffset;
    const targetPosition = target.getBoundingClientRect().top + start - this.options.offset;
    const distance = targetPosition - start;
    let startTime = null;

    const animation = (currentTime) => {
      if (startTime === null) startTime = currentTime;
      const timeElapsed = currentTime - startTime;
      const progress = Math.min(timeElapsed / this.options.duration, 1);
      
      const ease = progress < 0.5
        ? 4 * progress * progress * progress
        : 1 - Math.pow(-2 * progress + 2, 3) / 2;
      
      window.scrollTo(0, start + distance * ease);
      
      if (timeElapsed < this.options.duration) {
        requestAnimationFrame(animation);
      }
    };

    requestAnimationFrame(animation);
  }
}

// ============= 4. COUNTER ANIMATION =============
class CountUp {
  constructor(element, options = {}) {
    this.element = element;
    this.options = {
      start: options.start || 0,
      end: options.end || 100,
      duration: options.duration || 2000,
      decimals: options.decimals || 0,
      prefix: options.prefix || '',
      suffix: options.suffix || '',
      ...options
    };
  }

  start() {
    const range = this.options.end - this.options.start;
    const increment = range / (this.options.duration / 16);
    let current = this.options.start;
    
    const timer = setInterval(() => {
      current += increment;
      
      if (current >= this.options.end) {
        current = this.options.end;
        clearInterval(timer);
      }
      
      const value = current.toFixed(this.options.decimals);
      this.element.textContent = `${this.options.prefix}${value}${this.options.suffix}`;
    }, 16);
  }
}

// ============= 5. LOADING SKELETON =============
class SkeletonLoader {
  static create(container, type = 'card') {
    const skeletons = {
      card: `
        <div class="skeleton-card">
          <div class="skeleton-img"></div>
          <div class="skeleton-text"></div>
          <div class="skeleton-text short"></div>
        </div>
      `,
      text: `
        <div class="skeleton-text"></div>
        <div class="skeleton-text"></div>
        <div class="skeleton-text short"></div>
      `
    };
    
    container.innerHTML = skeletons[type] || skeletons.card;
    container.classList.add('skeleton-loading');
  }

  static remove(container) {
    container.classList.remove('skeleton-loading');
  }
}

// ============= 6. CURSOR TRAIL EFFECT =============
class CursorTrail {
  constructor(options = {}) {
    this.options = {
      color: options.color || '#63f5c1',
      size: options.size || 6,
      count: options.count || 15,
      ...options
    };
    
    this.trail = [];
    this.mouseX = 0;
    this.mouseY = 0;
    
    this.init();
  }

  init() {
    for (let i = 0; i < this.options.count; i++) {
      const dot = document.createElement('div');
      dot.className = 'cursor-trail-dot';
      dot.style.cssText = `
        position: fixed;
        width: ${this.options.size}px;
        height: ${this.options.size}px;
        background: ${this.options.color};
        border-radius: 50%;
        pointer-events: none;
        z-index: 9999;
        opacity: ${1 - (i / this.options.count)};
        transition: transform 0.1s ease;
      `;
      document.body.appendChild(dot);
      this.trail.push(dot);
    }

    document.addEventListener('mousemove', (e) => {
      this.mouseX = e.clientX;
      this.mouseY = e.clientY;
    });

    this.animate();
  }

  animate() {
    let x = this.mouseX;
    let y = this.mouseY;

    this.trail.forEach((dot, index) => {
      const nextDot = this.trail[index + 1] || this.trail[0];
      
      dot.style.transform = `translate(${x - this.options.size / 2}px, ${y - this.options.size / 2}px)`;
      
      x += (nextDot.offsetLeft - x) * 0.3;
      y += (nextDot.offsetTop - y) * 0.3;
    });

    requestAnimationFrame(() => this.animate());
  }
}

// ============= 7. TYPEWRITER EFFECT =============
class Typewriter {
  constructor(element, options = {}) {
    this.element = element;
    this.words = options.words || [];
    this.speed = options.speed || 100;
    this.deleteSpeed = options.deleteSpeed || 50;
    this.pauseTime = options.pauseTime || 2000;
    this.currentWord = 0;
    this.currentText = '';
    this.isDeleting = false;
    
    this.type();
  }

  type() {
    const word = this.words[this.currentWord];
    
    if (this.isDeleting) {
      this.currentText = word.substring(0, this.currentText.length - 1);
    } else {
      this.currentText = word.substring(0, this.currentText.length + 1);
    }
    
    this.element.textContent = this.currentText;
    
    let speed = this.isDeleting ? this.deleteSpeed : this.speed;
    
    if (!this.isDeleting && this.currentText === word) {
      speed = this.pauseTime;
      this.isDeleting = true;
    } else if (this.isDeleting && this.currentText === '') {
      this.isDeleting = false;
      this.currentWord = (this.currentWord + 1) % this.words.length;
      speed = 500;
    }
    
    setTimeout(() => this.type(), speed);
  }
}

// ============= 8. PROGRESS RING =============
class ProgressRing {
  constructor(element, options = {}) {
    this.element = element;
    this.options = {
      radius: options.radius || 50,
      stroke: options.stroke || 4,
      color: options.color || '#63f5c1',
      duration: options.duration || 1000,
      ...options
    };
    
    this.createRing();
  }

  createRing() {
    const svg = document.createElementNS('http://www.w3.org/2000/svg', 'svg');
    const circle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
    
    const circumference = 2 * Math.PI * this.options.radius;
    
    svg.setAttribute('width', (this.options.radius * 2 + this.options.stroke));
    svg.setAttribute('height', (this.options.radius * 2 + this.options.stroke));
    
    circle.setAttribute('cx', this.options.radius + this.options.stroke / 2);
    circle.setAttribute('cy', this.options.radius + this.options.stroke / 2);
    circle.setAttribute('r', this.options.radius);
    circle.setAttribute('fill', 'none');
    circle.setAttribute('stroke', this.options.color);
    circle.setAttribute('stroke-width', this.options.stroke);
    circle.setAttribute('stroke-dasharray', circumference);
    circle.setAttribute('stroke-dashoffset', circumference);
    circle.setAttribute('stroke-linecap', 'round');
    circle.style.transition = `stroke-dashoffset ${this.options.duration}ms ease`;
    
    svg.appendChild(circle);
    this.element.appendChild(svg);
    
    this.circle = circle;
    this.circumference = circumference;
  }

  setProgress(percent) {
    const offset = this.circumference - (percent / 100) * this.circumference;
    this.circle.setAttribute('stroke-dashoffset', offset);
  }
}

// ============= EXPORT ALL BONUS ANIMATIONS =============
window.GameFlixBonusAnimations = {
  ParticleSystem,
  TextScramble,
  SmoothScroll,
  CountUp,
  SkeletonLoader,
  CursorTrail,
  Typewriter,
  ProgressRing
};

// ============= USAGE EXAMPLES =============

// Particles in hero section
// new ParticleSystem(document.querySelector('.hero'), {
//   count: 50,
//   color: '#63f5c1',
//   speed: 0.3
// });

// Scramble effect for hero title
// const scramble = new TextScramble(document.querySelector('.hero-title'));
// scramble.setText('Stream Premium Titles');

// Smooth scroll for nav links
// new SmoothScroll('a[href^="#"]', { duration: 800, offset: 80 });

// Counter for stats
// const counter = new CountUp(document.querySelector('.stat-number'), {
//   end: 1000,
//   duration: 2000,
//   suffix: '+'
// });
// counter.start();

// Skeleton loading
// SkeletonLoader.create(document.querySelector('.game-grid'), 'card');
// setTimeout(() => {
//   SkeletonLoader.remove(document.querySelector('.game-grid'));
//   // Load actual content
// }, 2000);

// Cursor trail (only on desktop)
// if (window.innerWidth > 768) {
//   new CursorTrail({ color: '#63f5c1', size: 6, count: 10 });
// }

// Typewriter for tagline
// new Typewriter(document.querySelector('.hero-tagline'), {
//   words: ['Stream Instantly', 'Play Anywhere', 'No Downloads'],
//   speed: 100,
//   pauseTime: 2000
// });

// Progress ring for plan features
// const ring = new ProgressRing(document.querySelector('.progress-container'), {
//   radius: 50,
//   stroke: 4,
//   color: '#63f5c1'
// });
// ring.setProgress(75);
