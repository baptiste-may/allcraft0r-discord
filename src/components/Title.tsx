"use client";

export function Title({content}: {
    content: string;
}) {
    return <h1
        className="text-5xl md:text-6xl font-bold text-center mb-4"
    >{content}</h1>;
}