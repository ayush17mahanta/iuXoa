import { motion, useInView } from 'motion/react';
import { useRef } from 'react';
import { Twitter, MessageCircle, Mail, Youtube } from 'lucide-react';

export function Footer() {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true });

  const socials = [
    { icon: Twitter, label: 'Twitter', color: '#00f0ff' },
    { icon: MessageCircle, label: 'Discord', color: '#a855f7' },
    { icon: Youtube, label: 'YouTube', color: '#ef4444' },
    { icon: Mail, label: 'Contact', color: '#3b82f6' },
  ];

  return (
    <footer ref={ref} className="relative py-20 px-4 border-t border-white/5">
      <div className="max-w-7xl mx-auto">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-12 mb-12">
          {/* Left: Studio Info */}
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={isInView ? { opacity: 1, y: 0 } : {}}
            transition={{ duration: 0.8 }}
          >
            <h3 
              className="text-3xl mb-4"
              style={{ 
                fontFamily: 'Orbitron, sans-serif',
                fontWeight: 800,
                background: 'linear-gradient(135deg, #a855f7 0%, #00f0ff 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text',
              }}
            >
              iuXoa
            </h3>
            <p className="text-gray-400 text-sm leading-relaxed">
              An indie game studio creating precision-driven mobile experiences. 
              We make games that challenge, inspire, and respect player skill.
            </p>
          </motion.div>

          {/* Center: Quick Links */}
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={isInView ? { opacity: 1, y: 0 } : {}}
            transition={{ delay: 0.2, duration: 0.8 }}
          >
            <h4 
              className="text-lg mb-4 text-cyan-400"
              style={{ fontFamily: 'Orbitron, sans-serif', fontWeight: 600 }}
            >
              Quick Links
            </h4>
            <ul className="space-y-2 text-sm text-gray-400">
              <li>
                <a href="#" className="hover:text-cyan-400 transition-colors">
                  Privacy Policy
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-cyan-400 transition-colors">
                  Terms of Service
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-cyan-400 transition-colors">
                  Support
                </a>
              </li>
              <li>
                <a href="#" className="hover:text-cyan-400 transition-colors">
                  Press Kit
                </a>
              </li>
            </ul>
          </motion.div>

          {/* Right: Social Links */}
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={isInView ? { opacity: 1, y: 0 } : {}}
            transition={{ delay: 0.4, duration: 0.8 }}
          >
            <h4 
              className="text-lg mb-4 text-violet-400"
              style={{ fontFamily: 'Orbitron, sans-serif', fontWeight: 600 }}
            >
              Connect With Us
            </h4>
            <div className="flex gap-3">
              {socials.map((social, index) => {
                const Icon = social.icon;
                return (
                  <motion.a
                    key={index}
                    href="#"
                    className="glass-card w-12 h-12 rounded-full flex items-center justify-center hover:bg-white/10 transition-glass"
                    whileHover={{ 
                      scale: 1.1,
                      boxShadow: `0 0 20px ${social.color}50`,
                    }}
                    whileTap={{ scale: 0.95 }}
                    style={{
                      borderColor: `${social.color}40`,
                    }}
                  >
                    <Icon 
                      className="w-5 h-5"
                      style={{ color: social.color }}
                    />
                  </motion.a>
                );
              })}
            </div>
          </motion.div>
        </div>

        {/* Divider */}
        <div className="h-px bg-gradient-to-r from-transparent via-white/10 to-transparent mb-8" />

        {/* Bottom Row */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={isInView ? { opacity: 1 } : {}}
          transition={{ delay: 0.6, duration: 0.8 }}
          className="flex flex-col md:flex-row justify-between items-center gap-4 text-sm text-gray-500"
        >
          <div>
            © 2026 iuXoa. All rights reserved.
          </div>
          <motion.div
            animate={{ opacity: [0.5, 1, 0.5] }}
            transition={{ duration: 3, repeat: Infinity }}
            className="italic"
            style={{ fontFamily: 'Orbitron, sans-serif' }}
          >
            "Only Skill. No Mercy."
          </motion.div>
        </motion.div>
      </div>

      {/* Fade Out Effect */}
      <div 
        className="absolute bottom-0 left-0 right-0 h-32 pointer-events-none"
        style={{
          background: 'linear-gradient(to top, #0a0a0f 0%, transparent 100%)',
        }}
      />
    </footer>
  );
}
