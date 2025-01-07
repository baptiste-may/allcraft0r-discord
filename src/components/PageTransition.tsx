"use client";

import {AnimatePresence, motion} from "framer-motion";
import {ReactNode, Suspense, useContext, useEffect, useRef} from "react";
import {LayoutRouterContext} from "next/dist/shared/lib/app-router-context.shared-runtime";
import {useSelectedLayoutSegment} from "next/navigation";

function usePreviousValue<T>(value: T): T | undefined {
    const prevValue = useRef<T>(undefined);

    useEffect(() => {
        prevValue.current = value;
        return () => {
            prevValue.current = undefined;
        };
    });

    return prevValue.current;
}

function FrozenRouter(props: { children: ReactNode }) {
    const context = useContext(LayoutRouterContext);
    const prevContext = usePreviousValue(context) || null;

    const segment = useSelectedLayoutSegment();
    const prevSegment = usePreviousValue(segment);

    const changed =
        segment !== prevSegment &&
        segment !== undefined &&
        prevSegment !== undefined;

    return (
        <LayoutRouterContext.Provider value={changed ? prevContext : context}>
            {props.children}
        </LayoutRouterContext.Provider>
    );
}

export default function PageTransitionHandler({ children }: { children: ReactNode }) {

    const segment = useSelectedLayoutSegment();

    return (
        <AnimatePresence mode="wait">
            <motion.div
                key={segment}
                initial={{opacity: 0, y: -25}}
                animate={{opacity: 1, y: 0}}
                exit={{opacity: 0, y: 25}}
                transition={{ease: "easeInOut"}}
            >
                <FrozenRouter>
                    <Suspense>
                        {children}
                    </Suspense>
                </FrozenRouter>
            </motion.div>
        </AnimatePresence>
    );
}