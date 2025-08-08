import {CardName, Hand, PlayingCard, Suit} from "typedeck";

const strCardName: Record<CardName, string> = {
    [CardName.Ace]: "A",
    [CardName.Two]: "2",
    [CardName.Three]: "3",
    [CardName.Four]: "4",
    [CardName.Five]: "5",
    [CardName.Six]: "6",
    [CardName.Seven]: "7",
    [CardName.Eight]: "8",
    [CardName.Nine]: "9",
    [CardName.Ten]: "10",
    [CardName.Jack]: "V",
    [CardName.Queen]: "D",
    [CardName.King]: "R",
    [CardName.Joker]: ""
};
const strSuit: Record<Suit, string> = {
    [Suit.Hearts]: "♥️",
    [Suit.Diamonds]: "♦️",
    [Suit.Clubs]: "♣️",
    [Suit.Spades]: "♠️"
};

export function strCard(card: PlayingCard) {
    return strCardName[card.cardName] + " " + strSuit[card.suit];
}

export function strHand(hand: Hand) {
    return (hand.getCards() as PlayingCard[]).map(strCard).join(" | ");
}

export function delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

export function randomNumber(max: number) {
    return Math.floor(Math.random() * (max + 1));
}