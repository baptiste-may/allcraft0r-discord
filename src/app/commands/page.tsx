"use client";

import {Card, CardBody, CardHeader} from "@nextui-org/card";
import {Chip, Divider, Listbox, ListboxItem, Skeleton, Tooltip} from "@nextui-org/react";
import {useEffect, useState} from "react";
import {SlashCommandBuilder} from "discord.js";
import Icon from "feather-icons-react";
import {Title} from "@/components/Title";

function slashCommandToString(cmd: SlashCommandBuilder) {
    return (
        <div className="flex items-center gap-1">
            <span>{"/" + cmd.name}</span>
            {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
            {/* @ts-expect-error */}
            {cmd.options.map(({name, description, required}: {
                name: string;
                description: string;
                required?: boolean;
            }) => <Tooltip
                key={name}
                content={description + (required ? "" : " (facultatif)")}
                color="primary"
            >
                <Chip
                    size="sm"
                    radius="sm"
                    color="primary"
                    variant={required ? "solid" : "bordered"}
                    className=""
                >{name}</Chip>
            </Tooltip>)}
        </div>
    )
}

const STANDARD_SKELETONS = 4;

export default function Page() {

    const [isLoading, setIsLoading] = useState(true);
    const [data, setData] = useState<Record<string, {
        name: string;
        description: string;
        icon: string;
        commands: {
            data: SlashCommandBuilder
        }[];
    }>>({});

    useEffect(() => {
        fetch("/api/getCommands")
            .then(res => res.json())
            .then(data => setData(data))
            .finally(() => setIsLoading(false));
    }, []);

    return (
        <>
            <Title content="Commandes"/>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 md:gap-8 w-full">
                {isLoading ? [...Array(STANDARD_SKELETONS).keys()].map(i => <Card key={i}>
                <CardHeader className="flex flex-col items-start gap-2">
                    <Skeleton className="w-1/3 h-5 rounded-lg"/>
                    <Skeleton className="w-2/3 h-4 rounded-lg"/>
                </CardHeader>
                <Divider/>
                <CardBody>
                    <Listbox aria-label="Chargement...">
                        {[...Array(i % 3 === 0 ? 2 : 3).keys()].map(j => <ListboxItem
                            key={j}
                            description={<Skeleton className="w-1/2 h-3 rounded-lg"/>}
                            className="[&>div]:gap-1 [&_span]:w-full"
                            textValue="..."
                        >
                            <Skeleton className="w-1/4 h-4 rounded-lg"/>
                        </ListboxItem>)}
                    </Listbox>
                </CardBody>
            </Card>) : Object.entries(data).map(([cat, {name, description, icon, commands}]) => <Card key={cat}>
                    <CardHeader className="flex gap-3">
                        <Icon icon={icon} className="w-10 h-10"/>
                        <div className="flex flex-col">
                            <p className="text-md">{name}</p>
                            <p className="text-small text-default-500">{description}</p>
                        </div>
                    </CardHeader>
                    <Divider/>
                    <CardBody>
                    <Listbox aria-label={name}>
                            {commands.map(({data}) => <ListboxItem
                                key={data.name}
                                description={data.description}
                                textValue={"/" + data.name}
                            >
                                {slashCommandToString(data)}
                            </ListboxItem>)}
                        </Listbox>
                    </CardBody>
                </Card>)}
            </div>
        </>
    );
}