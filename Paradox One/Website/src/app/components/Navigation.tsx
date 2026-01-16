import { motion, useScroll, useTransform } from 'motion/react';
import { useState, useEffect } from 'react';
import appLogo from './image/app_logo.png';

export function Navigation() {
  const [isVisible, setIsVisible] = useState(false);
  const { scrollY } = useScroll();
  const opacity = useTransform(scrollY, [0, 100], [0, 1]);
  const y = useTransform(scrollY, [0, 100], [-100, 0]);

  useEffect(() => {
    const handleScroll = () => {
      setIsVisible(window.scrollY > 100);
    };

    window.addEventListener('scroll', handleScroll);
    return () => window.removeEventListener('scroll', handleScroll);
  }, []);

  const navItems = [
    { label: 'About', href: '#about' },
    { label: 'Gameplay', href: '#gameplay' },
    { label: 'Community', href: '#community' },
    { label: 'Download', href: '#download' },
    { label: 'Roadmap', href: '#roadmap' },
  ];

  return (
    <motion.nav
      className="fixed top-0 left-0 right-0 z-50 px-6 py-4"
      style={{ opacity, y }}
    >
      <div className="max-w-7xl mx-auto">
        <div 
          className="glass-card px-6 py-3 flex items-center justify-between"
          style={{
            borderColor: 'rgba(0, 240, 255, 0.2)',
          }}
        >
          {/* Logo */}
          <a href="#" className="flex items-center gap-3">
            <img 
              src={appLogo} 
              alt="Paradox One Logo" 
              className="w-10 h-10 object-contain"
              draggable="false"
            />
            <span 
              className="text-xl"
              style={{ 
                fontFamily: 'Orbitron, sans-serif',
                fontWeight: 800,
                background: 'linear-gradient(135deg, #00f0ff 0%, #a855f7 100%)',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text',
              }}
            >
              PARADOX ONE
            </span>
          </a>

          {/* Nav Links */}
          <div className="hidden md:flex items-center gap-8">
            {navItems.map((item, index) => (
              <motion.a
                key={index}
                href={item.href}
                className="text-sm text-gray-400 hover:text-cyan-400 transition-colors"
                style={{ fontFamily: 'Inter, sans-serif' }}
                whileHover={{ scale: 1.05 }}
                whileTap={{ scale: 0.95 }}
              >
                {item.label}
              </motion.a>
            ))}
            <motion.a
              href="#download"
              className="px-6 py-2 rounded-lg text-sm"
              style={{
                background: 'linear-gradient(135deg, rgba(0, 240, 255, 0.2) 0%, rgba(59, 130, 246, 0.2) 100%)',
                border: '1px solid rgba(0, 240, 255, 0.3)',
                fontFamily: 'Orbitron, sans-serif',
                fontWeight: 600,
              }}
              whileHover={{ 
                scale: 1.05,
                boxShadow: '0 0 20px rgba(0, 240, 255, 0.3)',
              }}
              whileTap={{ scale: 0.95 }}
            >
              Download
            </motion.a>
          </div>
        </div>
      </div>
    </motion.nav>
  );
}
