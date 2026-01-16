import { motion, useInView } from 'motion/react';
import { useRef } from 'react';
import { Hand, MousePointer2, Timer, Activity } from 'lucide-react';

export function GameplaySection() {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true, margin: '-100px' });

  const mechanics = [
    {
      icon: Hand,
      title: 'Swipe Precision',
      description: 'Master the art of perfect directional swipes',
      stat: '±5ms',
      statLabel: 'Timing Window',
    },
    {
      icon: MousePointer2,
      title: 'Tap Accuracy',
      description: 'Hit targets with pixel-perfect precision',
      stat: '99.9%',
      statLabel: 'Required Accuracy',
    },
    {
      icon: Timer,
      title: 'Reaction Speed',
      description: 'Train your reflexes to superhuman levels',
      stat: '<200ms',
      statLabel: 'Elite Response',
    },
    {
      icon: Activity,
      title: 'Flow State',
      description: 'Find your rhythm in chaos',
      stat: '∞',
      statLabel: 'Skill Ceiling',
    },
  ];

  return (
    <section id="gameplay" ref={ref} className="relative py-32 px-4">
      <div className="max-w-7xl mx-auto">
        {/* Title */}
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
              background: 'linear-gradient(135deg, #a855f7 0%, #00f0ff 100%)',
              WebkitBackgroundClip: 'text',
              WebkitTextFillColor: 'transparent',
              backgroundClip: 'text',
            }}
          >
            Gameplay Experience
          </h2>
        </motion.div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          {/* Left: Animated Phone Mockup */}
          <motion.div
            initial={{ opacity: 0, x: -50 }}
            animate={isInView ? { opacity: 1, x: 0 } : {}}
            transition={{ duration: 1 }}
            className="relative"
          >
            {/* Phone Frame */}
            <div className="relative mx-auto max-w-[300px]">
              <motion.div
                className="glass-card p-4 rounded-[3rem]"
                style={{
                  borderWidth: '8px',
                  borderColor: 'rgba(255, 255, 255, 0.1)',
                }}
                animate={{
                  y: [0, -10, 0],
                }}
                transition={{
                  duration: 4,
                  repeat: Infinity,
                  ease: 'easeInOut',
                }}
              >
                {/* Screen */}
                <div 
                  className="relative aspect-[9/19] rounded-[2.5rem] overflow-hidden"
                  style={{
                    background: 'linear-gradient(135deg, #0a0a0f 0%, #1a1a2e 100%)',
                  }}
                >
                  {/* Gameplay Simulation */}
                  <div className="absolute inset-0 flex items-center justify-center">
                    {/* Center Target */}
                    <motion.div
                      className="absolute"
                      animate={{
                        scale: [1, 1.2, 1],
                        rotate: [0, 180, 360],
                      }}
                      transition={{
                        duration: 3,
                        repeat: Infinity,
                        ease: 'linear',
                      }}
                    >
                      <div className="w-20 h-20 rounded-full border-4 border-cyan-400/50" />
                    </motion.div>

                    {/* Orbiting Particles */}
                    {[0, 1, 2, 3].map((i) => (
                      <motion.div
                        key={i}
                        className="absolute w-3 h-3 rounded-full bg-violet-400"
                        style={{
                          boxShadow: '0 0 20px rgba(168, 85, 247, 0.8)',
                        }}
                        animate={{
                          x: [0, 60 * Math.cos((i * Math.PI) / 2), 0],
                          y: [0, 60 * Math.sin((i * Math.PI) / 2), 0],
                          opacity: [0.3, 1, 0.3],
                        }}
                        transition={{
                          duration: 2,
                          repeat: Infinity,
                          delay: i * 0.5,
                        }}
                      />
                    ))}

                    {/* Tap Ripple Effect */}
                    <motion.div
                      className="absolute w-32 h-32 rounded-full border-2 border-cyan-400/30"
                      animate={{
                        scale: [0, 1.5],
                        opacity: [1, 0],
                      }}
                      transition={{
                        duration: 1.5,
                        repeat: Infinity,
                      }}
                    />
                  </div>

                  {/* Stats Overlay */}
                  <div className="absolute top-4 left-4 right-4">
                    <div className="glass-card px-4 py-2 inline-block">
                      <motion.span
                        className="text-cyan-400 font-mono"
                        animate={{ opacity: [1, 0.5, 1] }}
                        transition={{ duration: 1, repeat: Infinity }}
                      >
                        120 FPS
                      </motion.span>
                    </div>
                  </div>
                </div>
              </motion.div>

              {/* Floating Indicators */}
              <motion.div
                className="absolute -right-8 top-1/4 glass-card px-4 py-2"
                animate={{ x: [0, 10, 0] }}
                transition={{ duration: 2, repeat: Infinity }}
              >
                <span className="text-sm text-violet-400">Swipe →</span>
              </motion.div>

              <motion.div
                className="absolute -left-8 bottom-1/4 glass-card px-4 py-2"
                animate={{ x: [0, -10, 0] }}
                transition={{ duration: 2, repeat: Infinity, delay: 1 }}
              >
                <span className="text-sm text-cyan-400">← Tap</span>
              </motion.div>
            </div>
          </motion.div>

          {/* Right: Mechanics */}
          <div className="space-y-6">
            {mechanics.map((mechanic, index) => {
              const Icon = mechanic.icon;
              return (
                <motion.div
                  key={index}
                  initial={{ opacity: 0, x: 50 }}
                  animate={isInView ? { opacity: 1, x: 0 } : {}}
                  transition={{ delay: 0.2 * index, duration: 0.8 }}
                  className="glass-card p-6 group hover:bg-white/5 transition-glass"
                >
                  <div className="flex items-start gap-6">
                    {/* Icon */}
                    <div className="flex-shrink-0">
                      <div 
                        className="w-12 h-12 rounded-lg flex items-center justify-center"
                        style={{
                          background: 'linear-gradient(135deg, rgba(0, 240, 255, 0.1) 0%, rgba(168, 85, 247, 0.1) 100%)',
                          border: '1px solid rgba(0, 240, 255, 0.3)',
                        }}
                      >
                        <Icon className="w-6 h-6 text-cyan-400" />
                      </div>
                    </div>

                    {/* Content */}
                    <div className="flex-1">
                      <h3 
                        className="text-xl mb-2"
                        style={{ 
                          fontFamily: 'Orbitron, sans-serif',
                          fontWeight: 600,
                        }}
                      >
                        {mechanic.title}
                      </h3>
                      <p className="text-gray-400 mb-3">
                        {mechanic.description}
                      </p>
                      
                      {/* Progress Line */}
                      <div className="flex items-center gap-4">
                        <div className="flex-1 h-1 bg-gray-800 rounded-full overflow-hidden">
                          <motion.div
                            className="h-full rounded-full"
                            style={{
                              background: 'linear-gradient(90deg, #00f0ff 0%, #a855f7 100%)',
                            }}
                            initial={{ width: '0%' }}
                            animate={isInView ? { width: '100%' } : {}}
                            transition={{ delay: 0.5 + 0.2 * index, duration: 1 }}
                          />
                        </div>
                        <div className="text-right min-w-[80px]">
                          <div 
                            className="text-lg"
                            style={{ 
                              fontFamily: 'Orbitron, sans-serif',
                              fontWeight: 700,
                              color: '#00f0ff',
                            }}
                          >
                            {mechanic.stat}
                          </div>
                          <div className="text-xs text-gray-500">
                            {mechanic.statLabel}
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </motion.div>
              );
            })}
          </div>
        </div>
      </div>
    </section>
  );
}