"use client";

import {Progress, Table, TableBody, TableCell, TableColumn, TableHeader, TableRow, User} from "@nextui-org/react";
import {useEffect, useState} from "react";
import {Title} from "@/components/Title";
import {useAuth} from "@/components/AuthProvider";

const columns: {
    label: string;
    id: string;
}[] = [
    {label: "RANG", id: "rank"},
    {label: "UTILISATEUR", id: "id"},
    {label: "ARGENT", id: "money"},
];

export default function Page() {

    const [isLoading, setIsLoading] = useState(true);
    const [progress, setProgress] = useState(0);
    const [data, setData] = useState<{
        id: number;
        money: number;
        user: {
            global_name: string;
            username: string;
            avatar: string;
        }
    }[]>([]);
    const auth = useAuth();

    useEffect(() => {
        const eventSource = new EventSource("/api/discord/leaderboard");

        eventSource.onmessage = (event: MessageEvent) => {
            const data = JSON.parse(event.data);
            if (data.complete) {
                setIsLoading(false);
                setData(data.res);
            } else setProgress(data.progress);
        };

        eventSource.onerror = () => {
            eventSource.close();
        };

        return () => eventSource.close();
    }, []);

    return (
        <>
            <Title content="Classement"/>
            {isLoading ? (
                <Progress value={progress * 100} size="md" className="w-1/3 mx-auto" aria-label="..."/>
            ) : (
                <Table
                    className="w-min mx-auto" hideHeader
                    defaultSelectedKeys={auth.isLogged ? [auth.data?.id as string] : undefined}
                    selectionMode="single" color="primary"
                    aria-label="Leaderboard"
                >
                    <TableHeader>
                        {columns.map(({label, id}) => <TableColumn key={id}>
                            {label}
                        </TableColumn>)}
                    </TableHeader>
                    <TableBody>
                        {data.map(({id, money, user: {global_name, username, avatar}}, index) => <TableRow key={id}>
                            <TableCell className="text-end text-2xl font-extrabold">
                                {index + 1}
                            </TableCell>
                            <TableCell>
                                <User
                                    avatarProps={{
                                        src: `https://cdn.discordapp.com/avatars/${id}/${avatar}.png`
                                    }}
                                    name={global_name}
                                    description={"@" + username}
                                />
                            </TableCell>
                            <TableCell className="text-2xl font-light">
                                {money}
                            </TableCell>
                        </TableRow>)}
                    </TableBody>
                </Table>
            )}
        </>
    );
}