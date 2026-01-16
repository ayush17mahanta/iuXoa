import { motion, useInView } from 'motion/react';
import { useRef } from 'react';
import { Map, Layers, Zap, Trophy } from 'lucide-react';

const milestones = [
  {
    title: 'New Levels',
    description: 'Levels 51-75 with even more impossible geometry',
    quarter: 'Q1 2026',
    status: 'in-progress',
    icon: Layers,
    color: 'cyan',
  },
  {
    title: 'New Game Modes',
    description: 'Endless mode, Time Trial, and Daily Challenges',
    quarter: 'Q2 2026',
    status: 'planned',
    icon: Zap,
    color: 'violet',
  },
  {
    title: 'Performance Enhancements',
    description: 'Even smoother gameplay with advanced optimizations',
    quarter: 'Q2 2026',
    status: 'planned',
    icon: Zap,
    color: 'blue',
  },
  {
    title: 'Competitive Seasons',
    description: 'Ranked seasons with exclusive rewards and achievements',
    quarter: 'Q3 2026',
    status: 'planned',
    icon: Trophy,
    color: 'cyan',
  },
];

export function RoadmapSection() {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true, margin: '-100px' });

  const colorMap: Record<string, string> = {
    cyan: '#00f0ff',
    violet: '#a855f7',
    blue: '#3b82f6',
  };

  return (
    <section id="roadmap" ref={ref} className="relative py-32 px-4">
      <div className="max-w-7xl mx-auto">
        {/* Title */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-20"
        >
          <div className="flex items-center justify-center gap-4 mb-6">
            <Map className="w-12 h-12 text-violet-400" />
            <h2 
              className="text-5xl md:text-6xl"
              style={{ 
                fontFamily: 'Orbitron, sans-serif',
                fontWeight: 700,
                background: 'linear-gradient(135deg, #a855f7 0%, #00f0ff 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text',
              }}
            >
              Roadmap
            </h2>
          </div>
          <p className="text-xl text-gray-400">
            What's coming next to Paradox One
          </p>
        </motion.div>

        {/* Timeline */}
        <div className="relative">
          {/* Horizontal Line */}
          <div className="hidden md:block absolute top-24 left-0 right-0 h-0.5 bg-gradient-to-r from-transparent via-cyan-400/30 to-transparent" />

          {/* Milestones */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8 md:gap-4">
            {milestones.map((milestone, index) => {
              const Icon = milestone.icon;
              const isActive = milestone.status === 'in-progress';

              return (
                <motion.div
                  key={index}
                  initial={{ opacity: 0, y: 50 }}
                  animate={isInView ? { opacity: 1, y: 0 } : {}}
                  transition={{ delay: 0.2 * index, duration: 0.8 }}
                  className="relative"
                >
                  {/* Timeline Dot */}
                  <div className="hidden md:flex justify-center mb-8">
                    <motion.div
                      className="relative"
                      animate={isActive ? {
                        scale: [1, 1.3, 1],
                      } : {}}
                      transition={{
                        duration: 2,
                        repeat: Infinity,
                      }}
                    >
                      <div 
                        className="w-6 h-6 rounded-full relative z-10"
                        style={{
                          background: isActive 
                            ? `linear-gradient(135deg, ${colorMap[milestone.color]} 0%, ${colorMap[milestone.color]}80 100%)`
                            : 'rgba(255, 255, 255, 0.1)',
                          border: `2px solid ${
                            isActive 
                              ? colorMap[milestone.color]
                              : 'rgba(255, 255, 255, 0.2)'
                          }`,
                          boxShadow: isActive 
                            ? `0 0 20px ${colorMap[milestone.color]}80`
                            : 'none',
                        }}
                      />
                      {isActive && (
                        <motion.div
                          className="absolute inset-0 rounded-full"
                          style={{
                            background: colorMap[milestone.color],
                          }}
                          animate={{
                            scale: [1, 2],
                            opacity: [0.5, 0],
                          }}
                          transition={{
                            duration: 2,
                            repeat: Infinity,
                          }}
                        />
                      )}
                    </motion.div>
                  </div>

                  {/* Card */}
                  <motion.div
                    className="glass-card p-6 h-full hover:bg-white/5 transition-glass"
                    style={{
                      borderColor: isActive 
                        ? `${colorMap[milestone.color]}50`
                        : 'rgba(255, 255, 255, 0.1)',
                    }}
                    whileHover={{ 
                      y: -8,
                      boxShadow: `0 0 30px ${colorMap[milestone.color]}20`,
                    }}
                  >
                    {/* Icon */}
                    <div className="mb-4 flex items-center justify-between">
                      <div 
                        className="w-12 h-12 rounded-lg flex items-center justify-center"
                        style={{
                          background: `linear-gradient(135deg, ${colorMap[milestone.color]}20 0%, transparent 100%)`,
                          border: `1px solid ${colorMap[milestone.color]}40`,
                        }}
                      >
                        <Icon 
                          className="w-6 h-6"
                          style={{ color: colorMap[milestone.color] }}
                        />
                      </div>
                      {isActive && (
                        <span 
                          className="px-3 py-1 rounded-full text-xs"
                          style={{
                            background: `${colorMap[milestone.color]}20`,
                            border: `1px solid ${colorMap[milestone.color]}50`,
                            color: colorMap[milestone.color],
                          }}
                        >
                          IN PROGRESS
                        </span>
                      )}
                    </div>

                    {/* Quarter */}
                    <div className="text-sm text-gray-500 mb-2">
                      {milestone.quarter}
                    </div>

                    {/* Title */}
                    <h3 
                      className="text-xl mb-3"
                      style={{ 
                        fontFamily: 'Orbitron, sans-serif',
                        fontWeight: 600,
                        color: colorMap[milestone.color],
                      }}
                    >
                      {milestone.title}
                    </h3>

                    {/* Description */}
                    <p className="text-gray-400 text-sm leading-relaxed">
                      {milestone.description}
                    </p>

                    {/* Progress Bar (for active items) */}
                    {isActive && (
                      <div className="mt-4 pt-4 border-t border-white/5">
                        <div className="h-1 bg-gray-800 rounded-full overflow-hidden">
                          <motion.div
                            className="h-full rounded-full"
                            style={{
                              background: `linear-gradient(90deg, ${colorMap[milestone.color]} 0%, ${colorMap[milestone.color]}80 100%)`,
                            }}
                            initial={{ width: '0%' }}
                            animate={isInView ? { width: '65%' } : {}}
                            transition={{ delay: 0.5 + 0.2 * index, duration: 1.5 }}
                          />
                        </div>
                        <div className="text-xs text-gray-500 mt-2">65% Complete</div>
                      </div>
                    )}
                  </motion.div>
                </motion.div>
              );
            })}
          </div>
        </div>

        {/* Bottom Note */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={isInView ? { opacity: 1 } : {}}
          transition={{ delay: 1, duration: 0.8 }}
          className="mt-16 text-center"
        >
          <p className="text-gray-500 text-sm">
            Roadmap subject to change. We prioritize quality over timelines.
          </p>
        </motion.div>
      </div>
    </section>
  );
}