package net.melonia.coinsapi.test;

import net.melonia.coinsapi.CoinsAPI;
import net.melonia.coinsapi.provider.CoinsProvider;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Test {

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {

        final CoinsAPI coinsAPI = new CoinsProvider(null,"users"); //Lade Daten aus der Config und verwende die users-Datenbank
        final UUID randomUUID = UUID.randomUUID(); // Zufällige UUID für einen Fake-Spieler

        coinsAPI.setCoins(randomUUID,3); //Setze Coins auf 3
        coinsAPI.incrementCoins(randomUUID,5); //Inkrementiere Coins auf 5
        System.out.println(coinsAPI.getCoins(randomUUID)); //Return 8
    }

}
