import { motion, useInView } from 'motion/react';
import { useRef } from 'react';
import { Download, Smartphone, MonitorSmartphone, Clock } from 'lucide-react';

const platforms = [
  {
    name: 'Android',
    icon: Smartphone,
    status: 'Available Now',
    available: true,
    description: 'Download on Google Play',
    color: 'cyan',
  },
  {
    name: 'iOS',
    icon: Smartphone,
    status: 'Coming Soon',
    available: false,
    description: 'App Store submission in progress',
    color: 'violet',
  },
  {
    name: 'Tablet',
    icon: MonitorSmartphone,
    status: 'Optimized',
    available: true,
    description: 'Enhanced for larger screens',
    color: 'blue',
  },
];

export function DownloadSection() {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true, margin: '-100px' });

  const colorMap: Record<string, string> = {
    cyan: '#00f0ff',
    violet: '#a855f7',
    blue: '#3b82f6',
  };

  return (
    <section id="download" ref={ref} className="relative py-32 px-4">
      <div className="max-w-6xl mx-auto">
        {/* Title */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-20"
        >
          <div className="flex items-center justify-center gap-4 mb-6">
            <Download className="w-12 h-12 text-cyan-400" />
            <h2 
              className="text-5xl md:text-6xl"
              style={{ 
                fontFamily: 'Orbitron, sans-serif',
                fontWeight: 700,
                background: 'linear-gradient(135deg, #00f0ff 0%, #a855f7 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text',
              }}
            >
              Get Paradox One
            </h2>
          </div>
          <p className="text-xl text-gray-400">
            Available now. Test your limits today.
          </p>
        </motion.div>

        {/* Platform Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
          {platforms.map((platform, index) => {
            const Icon = platform.icon;
            return (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 30 }}
                animate={isInView ? { opacity: 1, y: 0 } : {}}
                transition={{ delay: 0.2 * index, duration: 0.8 }}
                whileHover={platform.available ? { scale: 1.05 } : {}}
                className={`glass-card p-8 text-center relative overflow-hidden ${
                  platform.available ? 'cursor-pointer' : 'opacity-60'
                } transition-glass`}
                style={{
                  borderColor: `${colorMap[platform.color]}40`,
                }}
              >
                {/* Animated Progress Glow for Available */}
                {platform.available && (
                  <motion.div
                    className="absolute inset-0 opacity-20 pointer-events-none"
                    style={{
                      background: `radial-gradient(circle at 50% 50%, ${colorMap[platform.color]} 0%, transparent 70%)`,
                    }}
                    animate={{
                      scale: [1, 1.2, 1],
                      opacity: [0.1, 0.3, 0.1],
                    }}
                    transition={{
                      duration: 3,
                      repeat: Infinity,
                    }}
                  />
                )}

                {/* Icon */}
                <div className="mb-6 flex justify-center relative z-10">
                  <div 
                    className="w-20 h-20 rounded-2xl flex items-center justify-center"
                    style={{
                      background: `linear-gradient(135deg, ${colorMap[platform.color]}30 0%, transparent 100%)`,
                      border: `2px solid ${colorMap[platform.color]}50`,
                    }}
                  >
                    <Icon 
                      className="w-10 h-10"
                      style={{ color: colorMap[platform.color] }}
                    />
                  </div>
                </div>

                {/* Platform Name */}
                <h3 
                  className="text-3xl mb-3 relative z-10"
                  style={{ 
                    fontFamily: 'Orbitron, sans-serif',
                    fontWeight: 700,
                    color: colorMap[platform.color],
                  }}
                >
                  {platform.name}
                </h3>

                {/* Status */}
                <div className="mb-4 relative z-10">
                  <span 
                    className="inline-block px-4 py-1 rounded-full text-sm"
                    style={{
                      background: platform.available 
                        ? `${colorMap[platform.color]}20`
                        : 'rgba(255, 255, 255, 0.05)',
                      border: `1px solid ${
                        platform.available 
                          ? `${colorMap[platform.color]}50`
                          : 'rgba(255, 255, 255, 0.1)'
                      }`,
                      color: platform.available 
                        ? colorMap[platform.color]
                        : '#888',
                    }}
                  >
                    {platform.status}
                  </span>
                </div>

                {/* Description */}
                <p className="text-gray-400 mb-6 relative z-10">
                  {platform.description}
                </p>

                {/* Action Button */}
                {platform.available ? (
                  <motion.button
                    className="w-full py-3 rounded-lg relative z-10 overflow-hidden group"
                    style={{
                      background: `linear-gradient(135deg, ${colorMap[platform.color]}30 0%, ${colorMap[platform.color]}10 100%)`,
                      border: `1px solid ${colorMap[platform.color]}50`,
                    }}
                    whileHover={{ borderColor: colorMap[platform.color] }}
                  >
                    <div 
                      className="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity"
                      style={{
                        background: `linear-gradient(90deg, transparent 0%, ${colorMap[platform.color]}20 50%, transparent 100%)`,
                        animation: 'shimmer 2s infinite',
                      }}
                    />
                    <span 
                      className="relative font-semibold"
                      style={{ 
                        fontFamily: 'Orbitron, sans-serif',
                        color: colorMap[platform.color],
                      }}
                    >
                      Install Now →
                    </span>
                  </motion.button>
                ) : (
                  <div 
                    className="w-full py-3 rounded-lg flex items-center justify-center gap-2 relative z-10"
                    style={{
                      background: 'rgba(255, 255, 255, 0.02)',
                      border: '1px solid rgba(255, 255, 255, 0.1)',
                    }}
                  >
                    <Clock className="w-4 h-4 text-gray-500" />
                    <span className="text-gray-500 text-sm">In Development</span>
                  </div>
                )}
              </motion.div>
            );
          })}
        </div>

        {/* System Requirements */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ delay: 0.8, duration: 0.8 }}
          className="glass-card p-8"
        >
          <h3 
            className="text-2xl mb-6 text-center"
            style={{ 
              fontFamily: 'Orbitron, sans-serif',
              fontWeight: 600,
              color: '#a855f7',
            }}
          >
            System Requirements
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 text-gray-400">
            <div>
              <h4 className="text-cyan-400 font-semibold mb-3">Minimum</h4>
              <ul className="space-y-2 text-sm">
                <li>• Android 8.0 or higher</li>
                <li>• 2GB RAM</li>
                <li>• 100MB storage space</li>
                <li>• Touch screen with 60Hz refresh rate</li>
              </ul>
            </div>
            <div>
              <h4 className="text-violet-400 font-semibold mb-3">Recommended</h4>
              <ul className="space-y-2 text-sm">
                <li>• Android 12 or higher</li>
                <li>• 4GB+ RAM</li>
                <li>• 120Hz+ display for optimal experience</li>
                <li>• Low-latency touch digitizer</li>
              </ul>
            </div>
          </div>
        </motion.div>
      </div>
    </section>
  );
}