import { motion, useInView } from 'motion/react';
import { useRef } from 'react';
import { Eye, Zap, Heart } from 'lucide-react';

const values = [
  {
    icon: Eye,
    title: 'Vision',
    description: 'Create games that challenge, not coddle. Every mechanic must earn its place.',
  },
  {
    icon: Zap,
    title: 'Performance',
    description: 'Polish over content. 120 FPS isn\'t a feature—it\'s the baseline.',
  },
  {
    icon: Heart,
    title: 'Indie Spirit',
    description: 'No compromise on creativity. No following trends. Pure innovation.',
  },
];

export function StudioSection() {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true, margin: '-100px' });

  return (
    <section ref={ref} className="relative py-32 px-4">
      <div className="max-w-5xl mx-auto">
        {/* Studio Logo/Name */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-12"
        >
          <div className="inline-block glass-card px-12 py-8 mb-8">
            <h2 
              className="text-6xl md:text-7xl"
              style={{ 
                fontFamily: 'Orbitron, sans-serif',
                fontWeight: 900,
                background: 'linear-gradient(135deg, #a855f7 0%, #00f0ff 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text',
                letterSpacing: '0.1em',
              }}
            >
              iuXoa
            </h2>
          </div>
          
          <motion.p
            initial={{ opacity: 0 }}
            animate={isInView ? { opacity: 1 } : {}}
            transition={{ delay: 0.3, duration: 0.8 }}
            className="text-2xl md:text-3xl text-gray-400 italic mb-8"
            style={{ fontFamily: 'Orbitron, sans-serif', fontWeight: 300 }}
          >
            "We don't make easy games.{' '}
            <span className="text-cyan-400 not-italic font-semibold">We make meaningful ones.</span>"
          </motion.p>
        </motion.div>

        {/* Values Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
          {values.map((value, index) => {
            const Icon = value.icon;
            return (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 30 }}
                animate={isInView ? { opacity: 1, y: 0 } : {}}
                transition={{ delay: 0.2 * index, duration: 0.8 }}
                className="glass-card p-8 text-center group hover:bg-white/5 transition-glass"
              >
                <div className="mb-6 flex justify-center">
                  <div 
                    className="w-16 h-16 rounded-full flex items-center justify-center transition-transform group-hover:scale-110"
                    style={{
                      background: 'linear-gradient(135deg, rgba(168, 85, 247, 0.2) 0%, rgba(0, 240, 255, 0.2) 100%)',
                      border: '1px solid rgba(168, 85, 247, 0.3)',
                    }}
                  >
                    <Icon className="w-8 h-8 text-violet-400" />
                  </div>
                </div>
                <h3 
                  className="text-2xl mb-4"
                  style={{ 
                    fontFamily: 'Orbitron, sans-serif',
                    fontWeight: 600,
                    color: '#a855f7',
                  }}
                >
                  {value.title}
                </h3>
                <p className="text-gray-400 leading-relaxed">
                  {value.description}
                </p>
              </motion.div>
            );
          })}
        </div>

        {/* Studio Background Card */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ delay: 0.6, duration: 0.8 }}
          className="glass-card p-10 relative overflow-hidden"
        >
          {/* Watermark */}
          <div 
            className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 opacity-[0.02] pointer-events-none"
            style={{
              fontSize: '200px',
              fontFamily: 'Orbitron, sans-serif',
              fontWeight: 900,
              whiteSpace: 'nowrap',
            }}
          >
            iuXoa
          </div>

          <div className="relative z-10">
            <p className="text-xl text-gray-300 leading-relaxed mb-6">
              <span className="text-cyan-400 font-semibold">iuXoa</span> is an indie game studio 
              focused on creating{' '}
              <span className="text-violet-400">precision-driven</span> mobile experiences. 
              We believe in respecting our players' intelligence and skill—no pay-to-win, 
              no cheap tricks, just pure gameplay.
            </p>
            <p className="text-lg text-gray-400 leading-relaxed">
              Paradox One is our first release, and it represents everything we stand for: 
              uncompromising difficulty, flawless performance, and a design philosophy that 
              puts <span className="text-cyan-400">skill above everything</span>.
            </p>
          </div>
        </motion.div>

        {/* Timeline Hint */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={isInView ? { opacity: 1 } : {}}
          transition={{ delay: 0.9, duration: 0.8 }}
          className="mt-12 text-center"
        >
          <p className="text-gray-500">
            Est. 2025 • Currently a one-person studio with a singular vision
          </p>
        </motion.div>
      </div>
    </section>
  );
}
