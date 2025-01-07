"use client";

import {createContext, ReactNode, useContext, useEffect, useState} from "react";

type User = {
    id: string;
    avatar: string;
    global_name: string;
}

const AuthContext = createContext<{
    isLogged: undefined | boolean;
    data: undefined | User;
} | undefined>(undefined);

export function AuthProvider({children}: {
    children: ReactNode
}) {

    const [isLogged, setIsLogged] = useState<undefined | boolean>(undefined);
    const [data, setData] = useState<undefined | User>();
    const token = typeof window !== "undefined" ? localStorage.getItem("token") : undefined;

    useEffect(() => {
        if (token === undefined) return;
        if (token === null) setIsLogged(false);
        else fetch("https://discord.com/api/v10/users/@me", {
            headers: {
                authorization: `Bearer ${token}`,
            }
        })
            .then(res => res.ok ? res.json() : Promise.reject())
            .then(data => {
                setData(data);
                setIsLogged(true);
            })
            .catch(() => {
                localStorage.removeItem("token");
                setIsLogged(false);
            });
    }, [token]);

    return (
        <AuthContext.Provider value={{isLogged, data}}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth must be used within the AuthProvider");
    return context;
}