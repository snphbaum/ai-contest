package org.byteforce.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import javax.ws.rs.container.AsyncResponse;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.byteforce.server.DTOs.GameState;


/**
 * @author Philipp Baumgaertel
 */
// @Singleton //TODO doesn't work ==> fix
// @Startup
public class GameStorage
{
    private ConcurrentMap<Integer, GameState> gameStates;

    private ConcurrentSkipListSet<ImmutablePair<Integer, AsyncResponse>> sequenceObserver;

    private ConcurrentMap<String, ConcurrentSkipListSet<AsyncResponse>> playerObserver;

    public final static GameStorage INSTANCE = new GameStorage();

    private GameStorage()
    {
        gameStates = new ConcurrentHashMap<>();
        sequenceObserver = new ConcurrentSkipListSet<>();
        playerObserver = new ConcurrentHashMap<>();
    }



    public GameState getStateOfGame(int gameId)
    {
        return gameStates.get(gameId);
    }



    public void putStateOfGame(int gameId, GameState newState)
    {
        gameStates.put(gameId, newState);

        if(newState.getNextPlayer() != null) {
            ConcurrentSkipListSet<AsyncResponse> observerList = playerObserver.get(newState.getNextPlayer());
            if (observerList != null) {
                for (AsyncResponse observer : observerList) {
                    observer.resume(newState);
                }
            }
            playerObserver.remove(newState.getNextPlayer());
        }
        for (ImmutablePair<Integer, AsyncResponse> observerPair : sequenceObserver) {
            if(newState.getSequenceNumber() > observerPair.getLeft()){
                observerPair.getRight().resume(newState);
                sequenceObserver.remove(observerPair);
            }
        }
    }



    public void putObserver(int sequence, AsyncResponse observer)
    {
        sequenceObserver.add(new ImmutablePair<>(sequence, observer));
    }



    public void putObserver(String player, AsyncResponse observer)
    {
        ConcurrentSkipListSet<AsyncResponse> observerList = playerObserver.get(player);
        observerList.add(observer);
    }
}
