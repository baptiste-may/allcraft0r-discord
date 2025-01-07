"use client";

import {
    Avatar,
    Navbar,
    NavbarBrand,
    NavbarContent,
    NavbarItem,
    Link,
    Dropdown,
    DropdownTrigger,
    DropdownMenu,
    DropdownItem,
    Button,
    NavbarMenu,
    NavbarMenuToggle,
    NavbarMenuItem,
    User
} from "@nextui-org/react";
import NextLink from "next/link";
import Icon from "feather-icons-react";
import {usePathname} from "next/navigation";
import {useTheme} from "next-themes";
import {useEffect, useState} from "react";
import {useAuth} from "@/components/AuthProvider";
import {RedstoneEmoji} from "@/components/RedstoneEmoji";

export default function Header() {

    const pathname = usePathname();
    const [mounted, setMounted] = useState(false);
    const {theme, setTheme} = useTheme();
    const {isLogged, data} = useAuth();
    const [money, setMoney] = useState(0);
    const [isMenuOpen, setIsMenuOpen] = useState(false);

    const pages: {
        path: string;
        label: string;
        icon: string;
    }[] = [
        {
            path: "/leaderboard",
            label: "Classement",
            icon: "trending-up"
        },
        {
            path: "/commands",
            label: "Commandes",
            icon: "terminal"
        }
    ];

    useEffect(() => {
        setMounted(true);
    }, []);

    useEffect(() => {
        if (!data) return;
        fetch(`/api/discord/money/${data?.id}`)
            .then(res => res.json())
            .then(nb => setMoney(nb));
    }, [data]);

    return (
        <Navbar shouldHideOnScroll isBordered isMenuOpen={isMenuOpen} onMenuOpenChange={setIsMenuOpen}>
            <NavbarContent justify="start">
                <NavbarMenuToggle className="sm:hidden"/>
                <NavbarBrand className="flex gap-4" as={NextLink} href="/">
                    <Avatar src="/logo.webp" radius="sm"/>
                    <p className="font-bold text-inherit">{"Allcraft0r's Discord"}</p>
                </NavbarBrand>
            </NavbarContent>
            <NavbarContent justify="center" className="hidden sm:flex">
                {pages.map(({path, label, icon}) => <NavbarItem
                    key={path}
                    isActive={path === pathname}
                    aria-label={label}
                >
                    <Link
                        as={NextLink}
                        href={path}
                        color={path !== pathname ? "foreground" : "primary"}
                        className="flex gap-2"
                    >
                        <Icon icon={icon}/>
                        {label}
                    </Link>
                </NavbarItem>)}
            </NavbarContent>
            <NavbarContent justify="end">
                {mounted && <Button
                    isIconOnly
                    variant="flat"
                    color="primary"
                    onPress={() => setTheme(theme => theme === "dark" ? "light" : "dark")}
                >
                    <Icon icon={theme !== "light" ? "sun" : "moon"}/>
                </Button>}
                {isLogged ? <Dropdown>
                    <DropdownTrigger>
                        <Avatar
                            isBordered
                            color="primary"
                            src={`https://cdn.discordapp.com/avatars/${data?.id}/${data?.avatar}.png`}
                        />
                    </DropdownTrigger>
                    <DropdownMenu>
                        <DropdownItem key="id" isReadOnly aria-label="user">
                            <User avatarProps={{
                                src: `https://cdn.discordapp.com/avatars/${data?.id}/${data?.avatar}.png`,
                                size: "sm"
                            }} name={data?.global_name} description={<>
                                {money}
                                <RedstoneEmoji size={16}/>
                            </>} className="[&_span]:flex [&_span]:gap-1"/>
                        </DropdownItem>
                        <DropdownItem
                            key="logout"
                            startContent={<Icon icon="log-out"/>}
                            color="danger"
                            className="text-danger"
                            as={NextLink} href={`/api/discord/logout?token=${localStorage.getItem("token")}`}
                            aria-label="logout"
                        >
                            Se déconnecter
                        </DropdownItem>
                    </DropdownMenu>
                </Dropdown> : <Button
                    isDisabled={isLogged === undefined}
                    variant="flat"
                    color="primary"
                    isIconOnly
                    as={NextLink}
                    href="/api/discord/auth"
                >
                    <Icon icon="log-in"/>
                </Button>}
            </NavbarContent>
            <NavbarMenu>
                {pages.map(({path, label, icon}) => <NavbarMenuItem
                    key={path}
                    aria-label={label}
                >
                    <Link
                        as={NextLink}
                        size="lg"
                        href={path}
                        color={path === pathname ? "primary" : "foreground"}
                        className="flex gap-2 w-full"
                        onPress={() => setIsMenuOpen(false)}
                    >
                        <Icon icon={icon}/>
                        {label}
                    </Link>
                </NavbarMenuItem>)}
            </NavbarMenu>
        </Navbar>
    );
}