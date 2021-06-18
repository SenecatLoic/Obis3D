package com.geosis.app.controlTools;

import com.geosis.api.loader.LoaderZoneSpecies;
import com.geosis.api.response.ApiZoneSpeciesResponse;
import com.geosis.app.Controller;
import com.geosis.app.exception.EmptyException;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread qui va lancer les requêtes pour former un graphique d'évolution d'une espèce
 */
public class TaskGraph extends Task {
    /**
     * Données de la requête
     */
    private ArrayList<CompletableFuture<Object>> completableFutures;


    private int yearStartInt;
    /**
     * Permet de connaitre ou on en es dans le calcul
     */
    private volatile AtomicInteger finalCurrentYear;

    /**
     * Savoir si il faut arrêté la tâche
     */
    private volatile AtomicBoolean isExit;

    private Controller controller;

    private String name;

    public TaskGraph(AtomicInteger finalCurrentYear,int yearStartInt, Controller controller, String name,AtomicBoolean isExit){
        this.controller = controller;
        this.name = name;
        this.finalCurrentYear = finalCurrentYear;
        this.yearStartInt = yearStartInt;
        this.isExit = isExit;
    }

    public void setCompletableFutures(ArrayList<CompletableFuture<Object>> completableFutures) {
        this.completableFutures = completableFutures;
    }

    @Override
    protected Object call() throws Exception {
        if(completableFutures == null){
            throw new NullPointerException();
        }

        ApiZoneSpeciesResponse zoneSpeciesResponse = null;
        try {
            for (CompletableFuture<Object> zone : completableFutures) {
                if(isExit.get()){
                    isExit.set(false);
                    return zoneSpeciesResponse;
                }
                zoneSpeciesResponse = (ApiZoneSpeciesResponse) zone.get(10, TimeUnit.SECONDS);
                ApiZoneSpeciesResponse finalZoneSpeciesResponse = zoneSpeciesResponse;

                int currentYear = yearStartInt + 5 * finalCurrentYear.get();

                ApiZoneSpeciesResponse second = LoaderZoneSpecies.createLoaderSpecies().getZoneSpeciesByTime(name, currentYear, currentYear);

                //nécessaire pour modifier un element javafx

                Platform.runLater(() -> {
                    controller.drawGraph(finalZoneSpeciesResponse,second,name,currentYear);
                });
                finalCurrentYear.incrementAndGet();
            }
        } catch (Exception e) {
            //quand on stoppe le processus on va catch une exception

        }
        return zoneSpeciesResponse;
    }
}
