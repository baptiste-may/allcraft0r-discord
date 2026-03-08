"use client";

import { AnimatePresence, motion } from "framer-motion";
import { ReactNode } from "react";
import { useSelectedLayoutSegment } from "next/navigation";

export default function PageTransitionHandler({
  children,
}: {
  children: ReactNode;
}) {
  const segment = useSelectedLayoutSegment();

  return (
    <AnimatePresence mode="wait">
      <motion.div
        key={segment}
        initial={{ opacity: 0, y: -25 }}
        animate={{ opacity: 1, y: 0 }}
        exit={{ opacity: 0, y: 25 }}
        transition={{ ease: "easeInOut", duration: 0.3 }}
      >
        {children}
      </motion.div>
    </AnimatePresence>
  );
}
