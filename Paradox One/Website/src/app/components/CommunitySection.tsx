import { motion, useInView } from 'motion/react';
import { useRef, useState } from 'react';
import { MessageSquare, TrendingUp, AlertCircle, Lightbulb, ChevronDown, Users } from 'lucide-react';

const discussions = [
  {
    category: 'Player Discussions',
    icon: MessageSquare,
    color: 'cyan',
    topics: [
      { title: 'Just hit Level 47... this game is insane', replies: 234, hot: true },
      { title: 'Tips for improving swipe accuracy?', replies: 89, hot: false },
      { title: 'Finally broke into top 100 global!', replies: 156, hot: true },
    ],
  },
  {
    category: 'Top Strategies',
    icon: Lightbulb,
    color: 'violet',
    topics: [
      { title: 'Advanced techniques for Level 30+', replies: 445, hot: true },
      { title: 'Finger positioning guide for maximum speed', replies: 298, hot: false },
      { title: 'How to master the impossible sections', replies: 521, hot: true },
    ],
  },
  {
    category: 'Hardest Levels',
    icon: TrendingUp,
    color: 'blue',
    topics: [
      { title: 'Level 50: Only 0.1% have beaten it', replies: 667, hot: true },
      { title: 'Which level made you quit (temporarily)?', replies: 402, hot: false },
      { title: 'Level 38 Strategy Megathread', replies: 289, hot: false },
    ],
  },
  {
    category: 'Bug Reports & Feedback',
    icon: AlertCircle,
    color: 'red',
    topics: [
      { title: 'Input lag on older devices - investigating', replies: 124, hot: false },
      { title: 'Feature request: Practice mode', replies: 892, hot: true },
      { title: 'Leaderboard sync issues - FIXED', replies: 67, hot: false },
    ],
  },
];

export function CommunitySection() {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true, margin: '-100px' });
  const [expandedCategory, setExpandedCategory] = useState<number | null>(0);

  const colorMap: Record<string, string> = {
    cyan: '#00f0ff',
    violet: '#a855f7',
    blue: '#3b82f6',
    red: '#ef4444',
  };

  return (
    <section id="community" ref={ref} className="relative py-32 px-4">
      <div className="max-w-6xl mx-auto">
        {/* Title */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-20"
        >
          <div className="flex items-center justify-center gap-3 mb-6">
            <Users className="w-10 h-10 text-cyan-400" />
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
              Community
            </h2>
          </div>
          <p className="text-xl text-gray-400">
            Join thousands of elite players pushing the limits
          </p>
        </motion.div>

        {/* Discussion Categories */}
        <div className="space-y-4">
          {discussions.map((discussion, index) => {
            const Icon = discussion.icon;
            const isExpanded = expandedCategory === index;

            return (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 30 }}
                animate={isInView ? { opacity: 1, y: 0 } : {}}
                transition={{ delay: 0.1 * index, duration: 0.8 }}
                className="glass-card overflow-hidden transition-glass"
              >
                {/* Category Header */}
                <button
                  onClick={() => setExpandedCategory(isExpanded ? null : index)}
                  className="w-full p-6 flex items-center justify-between hover:bg-white/5 transition-colors"
                >
                  <div className="flex items-center gap-4">
                    <div 
                      className="w-12 h-12 rounded-lg flex items-center justify-center"
                      style={{
                        background: `linear-gradient(135deg, ${colorMap[discussion.color]}20 0%, transparent 100%)`,
                        border: `1px solid ${colorMap[discussion.color]}40`,
                      }}
                    >
                      <Icon 
                        className="w-6 h-6"
                        style={{ color: colorMap[discussion.color] }}
                      />
                    </div>
                    <h3 
                      className="text-2xl text-left"
                      style={{ 
                        fontFamily: 'Orbitron, sans-serif',
                        fontWeight: 600,
                      }}
                    >
                      {discussion.category}
                    </h3>
                  </div>
                  <motion.div
                    animate={{ rotate: isExpanded ? 180 : 0 }}
                    transition={{ duration: 0.3 }}
                  >
                    <ChevronDown className="w-6 h-6 text-gray-500" />
                  </motion.div>
                </button>

                {/* Topics */}
                <motion.div
                  initial={false}
                  animate={{
                    height: isExpanded ? 'auto' : 0,
                    opacity: isExpanded ? 1 : 0,
                  }}
                  transition={{ duration: 0.3 }}
                  className="overflow-hidden"
                >
                  <div className="px-6 pb-6 space-y-3">
                    {discussion.topics.map((topic, topicIndex) => (
                      <motion.div
                        key={topicIndex}
                        initial={{ opacity: 0, x: -20 }}
                        animate={isExpanded ? { opacity: 1, x: 0 } : {}}
                        transition={{ delay: 0.1 * topicIndex }}
                        className="group p-4 rounded-lg border border-white/5 hover:border-white/10 hover:bg-white/5 cursor-pointer transition-all"
                      >
                        <div className="flex items-center justify-between">
                          <div className="flex-1">
                            <div className="flex items-center gap-3 mb-1">
                              <h4 className="text-gray-200 group-hover:text-white transition-colors">
                                {topic.title}
                              </h4>
                              {topic.hot && (
                                <span 
                                  className="px-2 py-0.5 rounded text-xs"
                                  style={{
                                    background: `${colorMap[discussion.color]}20`,
                                    color: colorMap[discussion.color],
                                    border: `1px solid ${colorMap[discussion.color]}40`,
                                  }}
                                >
                                  HOT
                                </span>
                              )}
                            </div>
                            <p className="text-sm text-gray-500">
                              {topic.replies} replies
                            </p>
                          </div>
                          <div 
                            className="w-2 h-2 rounded-full"
                            style={{
                              background: colorMap[discussion.color],
                              boxShadow: `0 0 10px ${colorMap[discussion.color]}`,
                            }}
                          />
                        </div>
                      </motion.div>
                    ))}
                  </div>
                </motion.div>
              </motion.div>
            );
          })}
        </div>

        {/* Call to Action */}
        <motion.div
          initial={{ opacity: 0, y: 30 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ delay: 0.6, duration: 0.8 }}
          className="mt-12 text-center"
        >
          <button 
            className="glass-card px-8 py-4 hover:bg-white/5 transition-glass"
            style={{
              border: '1px solid rgba(0, 240, 255, 0.3)',
            }}
          >
            <span 
              className="text-lg"
              style={{ 
                fontFamily: 'Orbitron, sans-serif',
                fontWeight: 600,
                color: '#00f0ff',
              }}
            >
              Join the Discussion →
            </span>
          </button>
        </motion.div>
      </div>
    </section>
  );
}