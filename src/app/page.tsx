"use client";

import {useSearchParams, useRouter} from "next/navigation";
import {useEffect} from "react";
import {Title} from "@/components/Title";

export default function Page() {

    const router = useRouter();
    const searchParams = useSearchParams();

    useEffect(() => {
        const token = searchParams.get("token");
        if (!token) return;
        localStorage.setItem("token", token);
        router.push("/");
    }, [router, searchParams]);

    return (
        <Title content="Bienvenue !"/>
    );
}