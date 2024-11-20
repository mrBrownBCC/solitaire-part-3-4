package solitaire;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GameState {
    private Stack<Card> deck; // Full deck of cards
    private Stack<Card>[] gamePiles; // Seven piles on the tableau
    private Stack<Card> visibleCards; // Stack for visible cards
    private Stack<Card> discardedCards; // Discard pile
    private Stack<Card>[] foundationPiles; // Four foundation piles

    @SuppressWarnings("unchecked")
    public GameState() {
        // Initialize the game state
        deck = new Stack<>();
        gamePiles = new Stack[7]; // Array of 7 stacks
        visibleCards = new Stack<>();
        discardedCards = new Stack<>();

        // Initialize each game pile
        for (int i = 0; i < gamePiles.length; i++) {
            gamePiles[i] = new Stack<>();
        }
        foundationPiles = new Stack[4];
        for (int i = 0; i < foundationPiles.length; i++) {
            foundationPiles[i] = new Stack<>();
        }

        initializeDeck();
        shuffleDeck();
        dealInitialCards();
    }

    // Creates a full deck of cards with all combinations of suits and ranks
    private void initializeDeck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.push(new Card(suit, rank)); // Create a card and add to the deck
            }
        }
    }

    // Shuffles the deck
    private void shuffleDeck() {
        java.util.Collections.shuffle(deck);
    }

    // Deals cards to the 7 game piles
    private void dealInitialCards() {
        for (int i = 0; i < gamePiles.length; i++) {
            for (int j = 0; j <= i; j++) { // Deal increasing number of cards to each pile
                Card card = deck.pop();
                gamePiles[i].push(card);
            }
            // Flip the top card of each pile face up
            if (!gamePiles[i].isEmpty()) {
                gamePiles[i].peek().flip();
            }
        }
    }

    // Draws up to three cards from the deck into visibleCards
    public void drawFromDeck() {
        // Move visible cards to the discarded pile
        while (!visibleCards.isEmpty()) {
            Card card = visibleCards.pop();
            discardedCards.push(card);
        }

        // If the deck is empty, refill it from the discarded cards
        if (deck.isEmpty()) {
            while (!discardedCards.isEmpty()) {
                Card card = discardedCards.pop();
                card.setFaceUp(false); // Flip the card face down
                deck.push(card);
            }
            // No need to shuffle the deck in Solitaire when refilling
        } else {
            // Draw up to 3 cards from the deck
            for (int i = 0; i < 3; i++) {
                if (!deck.isEmpty()) {
                    Card card = deck.pop();
                    card.setFaceUp(true); // Flip the card face up
                    visibleCards.push(card);
                }
            }
        }

    }

    // new methods from part 3/4
    public boolean moveCardFromVisibleCardsToPile(int toPileIndex) {
        Stack<Card> fromPile = visibleCards;
        if (fromPile.isEmpty()) {
            return false;
        }

        Card cardToMove = fromPile.peek();
        Stack<Card> toPile = gamePiles[toPileIndex];

        // Implement Solitaire move rules
        if (toPile.isEmpty()) {
            if (cardToMove.getRank() == Rank.KING) {
                fromPile.pop();
                toPile.push(cardToMove);
                return true;
            }
            return false;
        } else {
            Card topCard = toPile.peek();
            if (cardToMove.getColor() != topCard.getColor()
                    && cardToMove.getRank().ordinal() == topCard.getRank().ordinal() - 1) {
                fromPile.pop();
                toPile.push(cardToMove);
                return true;
            }
            return false;
        }
    }

    // Move a card from one tableau pile to another
    public boolean moveCards(int fromPileIndex, int cardIndex, int toPileIndex) {
        Stack<Card> fromPile = gamePiles[fromPileIndex];
        Stack<Card> toPile = gamePiles[toPileIndex];

        // Create a sub-stack of cards to move
        List<Card> cardsToMove = new ArrayList<>(fromPile.subList(cardIndex, fromPile.size()));

        Card bottomCard = cardsToMove.get(0);

        // Check if move is valid according to Solitaire rules
        if (toPile.isEmpty()) {
            if (bottomCard.getRank() == Rank.KING) {
                // Move the cards
                for (int i = cardIndex; i < fromPile.size();) {
                    toPile.push(fromPile.remove(cardIndex));
                }
                flipNextCard(fromPileIndex);
                return true;
            } else {
                return false;
            }
        } else {
            Card topCard = toPile.peek();
            if (bottomCard.getColor() != topCard.getColor()
                    && bottomCard.getRank().ordinal() == topCard.getRank().ordinal() - 1) {
                // Move the cards
                for (int i = cardIndex; i < fromPile.size();) {
                    toPile.push(fromPile.remove(cardIndex));
                }
                flipNextCard(fromPileIndex);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean moveToFoundation(int fromPileIndex, int foundationIndex) {
        Stack<Card> fromPile = gamePiles[fromPileIndex];
        Stack<Card> foundationPile = foundationPiles[foundationIndex];

        if (fromPile.isEmpty()) {
            return false;
        }

        Card cardToMove = fromPile.peek();

        // Check if the move is valid according to Solitaire rules
        if (foundationPile.isEmpty()) {
            // Foundation pile is empty, can only place an Ace
            if (cardToMove.getRank() == Rank.ACE) {
                fromPile.pop();
                foundationPile.push(cardToMove);
                flipNextCard(fromPileIndex);
                return true;
            } else {
                return false;
            }
        } else {
            Card topFoundationCard = foundationPile.peek();
            // Check if the card is the next rank and same suit
            if (cardToMove.getSuit() == topFoundationCard.getSuit()
                    && cardToMove.getRank().ordinal() == topFoundationCard.getRank().ordinal() + 1) {
                fromPile.pop();
                foundationPile.push(cardToMove);
                flipNextCard(fromPileIndex);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean moveToFoundationFromVisibleCards(int foundationIndex) {
        if (visibleCards.isEmpty()) {
            return false;
        }

        Card cardToMove = visibleCards.peek();
        Stack<Card> foundationPile = foundationPiles[foundationIndex];

        // Check if the move is valid according to Solitaire rules
        if (foundationPile.isEmpty()) {
            // Foundation pile is empty, can only place an Ace
            if (cardToMove.getRank() == Rank.ACE) {
                visibleCards.pop();
                foundationPile.push(cardToMove);
                return true;
            } else {
                return false;
            }
        } else {
            Card topFoundationCard = foundationPile.peek();
            // Check if the card is the next rank and same suit
            if (cardToMove.getSuit() == topFoundationCard.getSuit()
                    && cardToMove.getRank().ordinal() == topFoundationCard.getRank().ordinal() + 1) {
                visibleCards.pop();
                foundationPile.push(cardToMove);
                return true;
            } else {
                return false;
            }
        }
    }

    private void flipNextCard(int pileIndex) {
        Stack<Card> pile = gamePiles[pileIndex];
        if (!pile.isEmpty() && !pile.peek().isFaceUp()) {
            pile.peek().flip();
        }
    }

    // Don't change this, used for testing
    public void printState() {
        System.out.println("Deck size: " + deck.size());

        System.out.print("Visible cards: ");
        if (visibleCards.isEmpty()) {
            System.out.println("None");
        } else {
            for (Card card : visibleCards) {
                System.out.print(card + " ");
            }
            System.out.println();
        }

        System.out.println("Discarded cards: " + discardedCards.size());

        System.out.println("Game piles:");
        for (int i = 0; i < gamePiles.length; i++) {
            System.out.print("Pile " + (i + 1) + ": ");
            if (gamePiles[i].isEmpty()) {
                System.out.println("Empty");
            } else {
                for (Card card : gamePiles[i]) {
                    System.out.print(card + " ");
                }
                System.out.println();
            }
        }
    }

    // getters
    public Stack<Card> getGamePile(int index) {
        return gamePiles[index];
    }

    public Stack<Card> getFoundationPile(int index) {
        return foundationPiles[index];
    }

    public Stack<Card> getDeck() {
        return deck;
    }

    public Stack<Card> getVisibleCards() {
        return visibleCards;
    }
}
