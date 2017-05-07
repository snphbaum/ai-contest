ai-contest
===============

This project is mainly a private coding practice and not trying to be of general use.

Several things will be implemented:

- A game library that handles a simple Bomberman clone
- A game server that lets clients play via REST
- A web UI (probably Angular) to play games against another player
- A deep q learning AI that is able to play the game


Prerequisites
----------------

- Installing JavaCPP
    - https://github.com/bytedeco/javacpp
    - For Windows users, I would recommend clang  
        - http://clang.llvm.org
- Install Nd4j
    - http://nd4j.org/getstarted
    - For Windows users, I would recommend installing MKL
    - MKL needs to be added to the path
        
**Note: Anaconda users should be aware, that the MKL library 
of their Anaconda installation might cause problems.** 

Running the Code
-----------------

The server can be started using maven

    mvn jetty:run
    
Additionally, the GridWorld example can be run. 
It is based on the following tutorial:

http://outlace.com/rlpart3.html

