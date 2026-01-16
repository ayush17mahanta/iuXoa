import { motion } from 'motion/react';
import { useInView } from 'motion/react';
import { useRef } from 'react';
import { Zap, Target, TrendingUp, Trophy, Boxes, Gauge } from 'lucide-react';

const features = [
  {
    icon: Gauge,
    title: '120 FPS',
    description: 'Ultra-smooth gameplay that matches your reflexes',
    color: 'cyan',
  },
  {
    icon: Target,
    title: 'Precision Mechanics',
    description: 'Every tap and swipe demands perfect timing',
    color: 'violet',
  },
  {
    icon: Zap,
    title: 'Hardcore Difficulty',
    description: 'No hand-holding. Pure skill-based challenge',
    color: 'blue',
  },
  {
    icon: Trophy,
    title: 'Global Leaderboard',
    description: 'Compete with the world\'s best players',
    color: 'cyan',
  },
  {
    icon: Boxes,
    title: 'Minimalist Design',
    description: 'Brutal elegance in every level',
    color: 'violet',
  },
  {
    icon: TrendingUp,
    title: 'Progressive Mastery',
    description: 'Each level pushes you to your limits',
    color: 'blue',
  },
];

export function AboutGame() {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true, margin: '-100px' });

  return (
    <section id="about" ref={ref} className="relative py-32 px-4">
      <div className="max-w-7xl mx-auto">
        {/* Section Title */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-20"
        >
          <h2 
            className="text-5xl md:text-6xl mb-6"
            style={{ 
              fontFamily: 'Orbitron, sans-serif',
              fontWeight: 700,
              background: 'linear-gradient(135deg, #00f0ff 0%, #ffffff 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text',
            }}
          >
            The Ultimate Test
          </h2>
          <motion.p
            initial={{ opacity: 0 }}
            animate={isInView ? { opacity: 1 } : {}}
            transition={{ delay: 0.2, duration: 0.8 }}
            className="text-xl md:text-2xl text-gray-300 max-w-3xl mx-auto leading-relaxed"
          >
            Paradox One is a high-precision reflex game where{' '}
            <span className="text-cyan-400">every swipe</span>,{' '}
            <span className="text-violet-400">every tap</span>, and{' '}
            <span className="text-blue-400">every millisecond</span> matters.
          </motion.p>
        </motion.div>

        {/* Feature Grid */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {features.map((feature, index) => {
            const Icon = feature.icon;
            const colorMap: Record<string, string> = {
              cyan: '#00f0ff',
              violet: '#a855f7',
              blue: '#3b82f6',
            };
            const borderColorMap: Record<string, string> = {
              cyan: 'rgba(0, 240, 255, 0.3)',
              violet: 'rgba(168, 85, 247, 0.3)',
              blue: 'rgba(59, 130, 246, 0.3)',
            };

            return (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 30 }}
                animate={isInView ? { opacity: 1, y: 0 } : {}}
                transition={{ delay: 0.1 * index, duration: 0.8 }}
                whileHover={{ 
                  scale: 1.05,
                  boxShadow: `0 0 30px ${colorMap[feature.color]}33`,
                }}
                className="glass-card p-8 cursor-pointer group transition-glass"
                style={{
                  borderColor: borderColorMap[feature.color],
                }}
              >
                {/* Icon */}
                <div 
                  className="w-14 h-14 rounded-xl mb-6 flex items-center justify-center transition-glass"
                  style={{
                    background: `linear-gradient(135deg, ${colorMap[feature.color]}20 0%, transparent 100%)`,
                    border: `1px solid ${borderColorMap[feature.color]}`,
                  }}
                >
                  <Icon 
                    className="w-7 h-7 transition-transform group-hover:scale-110"
                    style={{ color: colorMap[feature.color] }}
                  />
                </div>

                {/* Title */}
                <h3 
                  className="text-2xl mb-3"
                  style={{ 
                    fontFamily: 'Orbitron, sans-serif',
                    fontWeight: 600,
                    color: colorMap[feature.color],
                  }}
                >
                  {feature.title}
                </h3>

                {/* Description */}
                <p className="text-gray-400 leading-relaxed">
                  {feature.description}
                </p>

                {/* Hover Effect Line */}
                <motion.div
                  className="h-0.5 mt-6 rounded-full"
                  style={{ 
                    background: `linear-gradient(90deg, ${colorMap[feature.color]} 0%, transparent 100%)`,
                  }}
                  initial={{ width: '0%' }}
                  whileHover={{ width: '100%' }}
                  transition={{ duration: 0.3 }}
                />
              </motion.div>
            );
          })}
        </div>

        {/* Bottom Quote */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={isInView ? { opacity: 1 } : {}}
          transition={{ delay: 0.8, duration: 0.8 }}
          className="mt-20 text-center"
        >
          <p 
            className="text-3xl italic text-gray-500"
            style={{ fontFamily: 'Orbitron, sans-serif', fontWeight: 300 }}
          >
            "The Hardest Path Has One Rule:{' '}
            <span className="text-cyan-400 not-italic font-semibold">Control</span>."
          </p>
        </motion.div>
      </div>
    </section>
  );
}