import { useEffect, useState } from 'react';
import { motion, useScroll, useTransform } from 'motion/react';
import { Navigation } from '@/app/components/Navigation';
import { AnimatedStars } from '@/app/components/AnimatedStars';
import { HeroSection } from '@/app/components/HeroSection';
import { AboutGame } from '@/app/components/AboutGame';
import { GameplaySection } from '@/app/components/GameplaySection';
import { CommunitySection } from '@/app/components/CommunitySection';
import { StudioSection } from '@/app/components/StudioSection';
import { DownloadSection } from '@/app/components/DownloadSection';
import { RoadmapSection } from '@/app/components/RoadmapSection';
import { Footer } from '@/app/components/Footer';

export default function App() {
  const [mousePosition, setMousePosition] = useState({ x: 0, y: 0 });
  const { scrollYProgress } = useScroll();

  useEffect(() => {
    const handleMouseMove = (e: MouseEvent) => {
      setMousePosition({ x: e.clientX, y: e.clientY });
    };

    window.addEventListener('mousemove', handleMouseMove);
    return () => window.removeEventListener('mousemove', handleMouseMove);
  }, []);

  return (
    <div 
      className="min-h-screen bg-gradient-to-b from-[#0a0a0f] via-[#0f1629] to-[#0a0a0f] text-white overflow-hidden"
      style={{ fontFamily: 'Inter, sans-serif' }}
    >
      {/* Navigation */}
      <Navigation />

      {/* Animated Stars */}
      <AnimatedStars />

      {/* Animated Background Noise */}
      <div 
        className="fixed inset-0 opacity-[0.03] pointer-events-none animate-noise"
        style={{
          backgroundImage: 'url("data:image/svg+xml,%3Csvg viewBox=\'0 0 400 400\' xmlns=\'http://www.w3.org/2000/svg\'%3E%3Cfilter id=\'noiseFilter\'%3E%3CfeTurbulence type=\'fractalNoise\' baseFrequency=\'0.9\' numOctaves=\'4\' /%3E%3C/filter%3E%3Crect width=\'100%25\' height=\'100%25\' filter=\'url(%23noiseFilter)\' /%3E%3C/svg%3E")',
          backgroundRepeat: 'repeat',
        }}
      />

      {/* Parallax Orbs */}
      <motion.div
        className="fixed top-20 left-10 w-96 h-96 rounded-full blur-[120px] opacity-20 pointer-events-none"
        style={{
          background: 'radial-gradient(circle, #00f0ff 0%, transparent 70%)',
          x: useTransform(scrollYProgress, [0, 1], [0, -200]),
          y: useTransform(scrollYProgress, [0, 1], [0, 300]),
        }}
      />
      <motion.div
        className="fixed bottom-20 right-10 w-96 h-96 rounded-full blur-[120px] opacity-20 pointer-events-none"
        style={{
          background: 'radial-gradient(circle, #a855f7 0%, transparent 70%)',
          x: useTransform(scrollYProgress, [0, 1], [0, 200]),
          y: useTransform(scrollYProgress, [0, 1], [0, -300]),
        }}
      />

      {/* Main Content */}
      <HeroSection mousePosition={mousePosition} />
      <AboutGame />
      <GameplaySection />
      <CommunitySection />
      <StudioSection />
      <DownloadSection />
      <RoadmapSection />
      <Footer />
    </div>
  );
}