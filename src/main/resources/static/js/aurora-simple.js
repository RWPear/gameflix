// Simple Aurora Background Effect
// src/main/resources/static/js/aurora-simple.js

class AuroraSimple {
  constructor(canvasId, options = {}) {
    this.canvas = document.getElementById(canvasId);
    if (!this.canvas) return;

    this.ctx = this.canvas.getContext('2d');
    this.options = {
      colors: options.colors || ['#5227FF', '#63f5c1', '#7ac8ff'],
      speed: options.speed || 0.3,
      amplitude: options.amplitude || 1.2,
      ...options
    };

    this.time = 0;
    this.width = 0;
    this.height = 0;

    this.resize();
    this.animate();

    window.addEventListener('resize', () => this.resize());
  }

  resize() {
    this.width = window.innerWidth;
    this.height = window.innerHeight;
    this.canvas.width = this.width;
    this.canvas.height = this.height;
  }

  hexToRgb(hex) {
    const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
    return result ? {
      r: parseInt(result[1], 16),
      g: parseInt(result[2], 16),
      b: parseInt(result[3], 16)
    } : { r: 0, g: 0, b: 0 };
  }

  getColor(x) {
    const colors = this.options.colors.map(c => this.hexToRgb(c));
    const segment = 1 / (colors.length - 1);
    let index = Math.floor(x / segment);
    let localX = (x % segment) / segment;

    if (index >= colors.length - 1) {
      index = colors.length - 2;
      localX = 1;
    }

    const c1 = colors[index];
    const c2 = colors[index + 1];

    return {
      r: Math.round(c1.r + (c2.r - c1.r) * localX),
      g: Math.round(c1.g + (c2.g - c1.g) * localX),
      b: Math.round(c1.b + (c2.b - c1.b) * localX)
    };
  }

  noise(x, y) {
    const n = Math.sin(x * 12.9898 + y * 78.233) * 43758.5453;
    return n - Math.floor(n);
  }

  smoothNoise(x, y) {
    const corners = (this.noise(x-1, y-1) + this.noise(x+1, y-1) + 
                    this.noise(x-1, y+1) + this.noise(x+1, y+1)) / 16;
    const sides = (this.noise(x-1, y) + this.noise(x+1, y) + 
                  this.noise(x, y-1) + this.noise(x, y+1)) / 8;
    const center = this.noise(x, y) / 4;
    return corners + sides + center;
  }

  draw() {
    this.ctx.clearRect(0, 0, this.width, this.height);

    // Create gradient background
    const gradient = this.ctx.createLinearGradient(0, 0, this.width, this.height);
    
    for (let i = 0; i < this.options.colors.length; i++) {
      const position = i / (this.options.colors.length - 1);
      gradient.addColorStop(position, this.options.colors[i]);
    }

    // Draw multiple wave layers
    for (let layer = 0; layer < 3; layer++) {
      this.ctx.save();
      this.ctx.globalAlpha = 0.15 + (layer * 0.05);
      
      this.ctx.beginPath();
      
      const layerOffset = layer * 100;
      const waveHeight = this.height * (0.3 + layer * 0.1);
      const waveSpeed = this.time * (0.5 + layer * 0.2);
      
      for (let x = 0; x <= this.width; x += 10) {
        const normalizedX = x / this.width;
        const wave1 = Math.sin((normalizedX * 2 + waveSpeed) * Math.PI) * waveHeight;
        const wave2 = Math.sin((normalizedX * 3 - waveSpeed * 0.7) * Math.PI) * (waveHeight * 0.5);
        const wave3 = Math.sin((normalizedX * 5 + waveSpeed * 1.3) * Math.PI) * (waveHeight * 0.3);
        
        const y = this.height * 0.5 + wave1 + wave2 + wave3 + layerOffset;
        
        if (x === 0) {
          this.ctx.moveTo(x, y);
        } else {
          this.ctx.lineTo(x, y);
        }
      }
      
      this.ctx.lineTo(this.width, this.height);
      this.ctx.lineTo(0, this.height);
      this.ctx.closePath();
      
      this.ctx.fillStyle = gradient;
      this.ctx.fill();
      
      this.ctx.restore();
    }

    // Add soft glow overlay
    this.ctx.save();
    this.ctx.globalAlpha = 0.3;
    this.ctx.globalCompositeOperation = 'screen';
    
    const glowGradient = this.ctx.createRadialGradient(
      this.width / 2, this.height / 2, 0,
      this.width / 2, this.height / 2, this.width * 0.6
    );
    glowGradient.addColorStop(0, 'rgba(99, 245, 193, 0.3)');
    glowGradient.addColorStop(0.5, 'rgba(122, 200, 255, 0.2)');
    glowGradient.addColorStop(1, 'transparent');
    
    this.ctx.fillStyle = glowGradient;
    this.ctx.fillRect(0, 0, this.width, this.height);
    
    this.ctx.restore();
  }

  animate() {
    this.time += 0.008 * this.options.speed;
    this.draw();
    requestAnimationFrame(() => this.animate());
  }
}

// Export to window
window.AuroraSimple = AuroraSimple;