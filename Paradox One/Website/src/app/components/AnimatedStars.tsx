import { motion } from 'motion/react';
import { useEffect, useState } from 'react';

interface Star {
  id: number;
  size: number;
  startX: number;
  startY: number;
  duration: number;
  delay: number;
  color: string;
}

export function AnimatedStars() {
  const [stars, setStars] = useState<Star[]>([]);

  useEffect(() => {
    // Generate random stars
    const generateStars = () => {
      const newStars: Star[] = [];
      const colors = ['#00f0ff', '#a855f7', '#3b82f6', '#ffffff'];
      
      for (let i = 0; i < 50; i++) {
        newStars.push({
          id: i,
          size: Math.random() * 3 + 1, // 1-4px
          startX: Math.random() * 30, // Start within 30% from left
          startY: 100 + Math.random() * 20, // Start below viewport
          duration: Math.random() * 3 + 2, // 2-5 seconds
          delay: Math.random() * 5, // 0-5 seconds delay
          color: colors[Math.floor(Math.random() * colors.length)],
        });
      }
      
      setStars(newStars);
    };

    generateStars();
  }, []);

  return (
    <div className="fixed inset-0 pointer-events-none overflow-hidden z-[1]">
      {stars.map((star) => (
        <motion.div
          key={star.id}
          className="absolute rounded-full"
          style={{
            width: star.size,
            height: star.size,
            left: `${star.startX}%`,
            top: `${star.startY}%`,
            background: star.color,
            boxShadow: `0 0 ${star.size * 4}px ${star.color}`,
          }}
          initial={{
            x: 0,
            y: 0,
            opacity: 0,
            scale: 0,
          }}
          animate={{
            x: ['0vw', '120vw'], // Move from left to right
            y: ['0vh', '-120vh'], // Move from bottom to top
            opacity: [0, 1, 1, 0], // Fade in, stay visible, fade out
            scale: [0, 1, 1, 0.5],
          }}
          transition={{
            duration: star.duration,
            delay: star.delay,
            repeat: Infinity,
            repeatDelay: Math.random() * 3,
            ease: 'linear',
          }}
        />
      ))}
    </div>
  );
}
