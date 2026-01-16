import { motion } from 'motion/react';
import { Download, Play } from 'lucide-react';

interface HeroSectionProps {
  mousePosition: { x: number; y: number };
}

export function HeroSection({ mousePosition }: HeroSectionProps) {
  const parallaxX = (mousePosition.x - window.innerWidth / 2) / 50;
  const parallaxY = (mousePosition.y - window.innerHeight / 2) / 50;

  return (
    <section className="relative min-h-screen flex items-center justify-center overflow-hidden">
      {/* Paradox Shapes Background */}
      <div className="absolute inset-0 flex items-center justify-center opacity-10">
        <motion.div
          className="absolute w-[600px] h-[600px]"
          style={{
            x: parallaxX * 2,
            y: parallaxY * 2,
          }}
        >
          {/* Impossible Triangle */}
          <svg viewBox="0 0 200 200" className="w-full h-full">
            <defs>
              <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style={{ stopColor: '#00f0ff', stopOpacity: 0.3 }} />
                <stop offset="100%" style={{ stopColor: '#a855f7', stopOpacity: 0.3 }} />
              </linearGradient>
            </defs>
            <path
              d="M100,30 L170,150 L30,150 Z M100,70 L140,135 L60,135 Z"
              fill="none"
              stroke="url(#grad1)"
              strokeWidth="2"
              className="animate-pulse"
            />
          </svg>
        </motion.div>

        <motion.div
          className="absolute w-[400px] h-[400px]"
          style={{
            x: parallaxX * -1.5,
            y: parallaxY * -1.5,
          }}
        >
          {/* Geometric Rings */}
          {[0, 1, 2].map((i) => (
            <motion.div
              key={i}
              className="absolute inset-0 rounded-full border border-cyan-400/20"
              style={{ 
                scale: 1 - i * 0.3,
              }}
              animate={{
                rotate: 360,
              }}
              transition={{
                duration: 20 + i * 5,
                repeat: Infinity,
                ease: 'linear',
              }}
            />
          ))}
        </motion.div>
      </div>

      {/* Main Glass Card */}
      <motion.div
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 1, ease: 'easeOut' }}
        className="relative z-10 glass-card p-12 md:p-16 max-w-4xl mx-4 text-center"
      >
        {/* Logo / Game Name */}
        <motion.div
          initial={{ opacity: 0, scale: 0.9 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ delay: 0.3, duration: 0.8 }}
        >
          <h1 
            className="text-6xl md:text-8xl mb-4 tracking-wider"
            style={{ 
              fontFamily: 'Orbitron, sans-serif',
              fontWeight: 800,
              background: 'linear-gradient(135deg, #00f0ff 0%, #a855f7 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text',
              textShadow: '0 0 80px rgba(0, 240, 255, 0.3)',
            }}
          >
            PARADOX ONE
          </h1>
        </motion.div>

        {/* Tagline */}
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.6, duration: 0.8 }}
          className="text-xl md:text-2xl mb-2 text-cyan-300/80"
          style={{ fontFamily: 'Inter, sans-serif', fontWeight: 300 }}
        >
          Where Precision Becomes Survival
        </motion.p>

        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.8, duration: 0.8 }}
          className="text-lg md:text-xl mb-10 text-violet-300/60"
          style={{ fontFamily: 'Inter, sans-serif', fontWeight: 300 }}
        >
          Only Skill. No Mercy.
        </motion.p>

        {/* CTA Buttons */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 1, duration: 0.8 }}
          className="flex flex-col sm:flex-row gap-6 justify-center items-center"
        >
          {/* Download Button */}
          <motion.button
            className="group relative px-10 py-4 rounded-xl overflow-hidden transition-glass"
            style={{
              background: 'linear-gradient(135deg, rgba(0, 240, 255, 0.2) 0%, rgba(59, 130, 246, 0.2) 100%)',
              border: '1px solid rgba(0, 240, 255, 0.3)',
            }}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.98 }}
          >
            <div className="absolute inset-0 bg-gradient-to-r from-cyan-500/0 via-cyan-500/20 to-cyan-500/0 translate-x-[-100%] group-hover:translate-x-[100%] transition-transform duration-700" />
            <span className="relative flex items-center gap-3 font-semibold" style={{ fontFamily: 'Orbitron, sans-serif' }}>
              <Download className="w-5 h-5" />
              Download Now
            </span>
          </motion.button>

          {/* Watch Gameplay Button */}
          <motion.button
            className="group relative px-10 py-4 rounded-xl overflow-hidden glass-card-hover"
            style={{
              background: 'rgba(255, 255, 255, 0.03)',
              border: '1px solid rgba(168, 85, 247, 0.3)',
            }}
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.98 }}
          >
            <div className="absolute inset-0 bg-gradient-to-r from-violet-500/0 via-violet-500/20 to-violet-500/0 translate-x-[-100%] group-hover:translate-x-[100%] transition-transform duration-700" />
            <span className="relative flex items-center gap-3 font-semibold" style={{ fontFamily: 'Orbitron, sans-serif' }}>
              <Play className="w-5 h-5" />
              Watch Gameplay
            </span>
          </motion.button>
        </motion.div>

        {/* Subtle Info */}
        <motion.p
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 1.2, duration: 0.8 }}
          className="mt-10 text-sm text-gray-500"
        >
          Developed by <span className="text-cyan-400">iuXoa</span>
        </motion.p>
      </motion.div>

      {/* Scroll Indicator */}
      <motion.div
        className="absolute bottom-10 left-1/2 transform -translate-x-1/2"
        animate={{ y: [0, 10, 0] }}
        transition={{ duration: 2, repeat: Infinity }}
      >
        <div className="w-6 h-10 rounded-full border-2 border-cyan-400/30 flex items-start justify-center p-2">
          <motion.div
            className="w-1.5 h-1.5 bg-cyan-400 rounded-full"
            animate={{ y: [0, 16, 0] }}
            transition={{ duration: 2, repeat: Infinity }}
          />
        </div>
      </motion.div>
    </section>
  );
}
