# Guess My Draw
Android application written in Java as an academic project for the subject "sviluppo applicazioni mobile"

# What is "Guess my Draw"?
As the name suggests, it consists of a game for two players in which the goal is to guess,
in the shortest possible time and no more than sixty seconds, the word that the opponent is drawing. 
Only the player who is guessing can obtain points, and the amount obtained depends on the speed in 
which the correct answer is given: in fact, each word guessed guarantees one point, to which is
then added a bonus value that depends on how many seconds are left since the timer expired.
At each turn, players reverse their roles. The number of turns is not fixed, but it is the two players that
decide when to end: at each turn, both players have the opportunity to request the end of the game, 
and as soon as two requests are reached (which can both be made by the same player in two different 
turns, or one request by each player), the game ends.

# Implementation
As required, the app covers three distinct frameworks seen in class: local communication via 
Wi-Fi Direct, device touch for drawing, and the use of a database for possible words to be guessed.
The app is developed with the Single-Activity Architecture, which involves the use of a single 
activity and a series of fragments that alternate during execution. The motivations behind this
solution is the various advantages that an app with this architecture possesses over one made from
many Activities:

* No need to update the manifest each time a new Activity is inserted, and thus the possibility 
  of a much "lighter" and less confusing manifest.

* Simplified navigation between the various fragments of the application thanks to the use of Navigation,
  a component of the Jetpack library that is perfect for this architecture. 

* Easier data sharing between fragments due to the use of a shared ViewModel.

The use of the latter component was critical to ensure that information was not lost with each recreation
of the fragment or activity, as it is able to survive this as well.
Three different ViewModels were created for the app: 

* **WordViewModel:** used to contain the words to be used during the match and in charge of acting as a 
  junction point between the WordRepository (in charge of accessing the database, implemented through Room 
  jetpack) and the activity/fragments. 
  
* **TimerViewModel:** used to store the necessary information for the timer so that it does not reset 
  following, for example, a screen rotation.

* **GameViewModel:** used to save within it all the information necessary for the performance of 
  an entire game and through which to make the various fragments communicate in a simple way.

The local connection type to be used is Wi-Fi Direct, and the actual communication is done by sending UDP packets.
For this part, two threads were used for sending messages (Sender and SenderInLoop) and two others for receiving
(MessageReader and Receiver). 
Taking care of the various callbacks (used to indicate the arrival of new messages) is the MessageReader.
Only the current fragment registers to it, so that there is no risk of calling two fragments at the same time.
Instead, the reception of the various messages is delegated to the Receiver, which is responsible for placing 
the new messages in a queue to which the MessageReder itself will have access.

To start a new game, players access the list of available devices nearby. The list, displayed in the DeviceList
fragmet is implemented with a RecyclerView that is updated whenever the onPeersAvailable() callback is called.
A swipeRefreshLayout has also been inserted to allow the player to be able to do a refresh of the available 
devices. Selecting the opponent's device will start the attempt, by which one of the two devices will try to 
establish a connection with the other.
If successful, after a few seconds the two devices will both be in the Loading fragment, in which the two 
will exchange the Handshake message, used both to exchange the nickname chosen for the game and to allow 
the GroupOwner to know the Ip address of the other device. Once this first phase is completed, we arrive 
at the GameLobby fragment, from which the game loop begins. For convenience, it has been determined that 
the GroupOwner of the group is also the first to draw.

Two Custom Views were created for the drawing phase: the first one used by the current player 
(the player who has to draw) and a second one used in the opponent's device. The latter simply replicates
the drawing that the current player is doing on his device, thanks to the information contained in the 
DrawMessage type messages.
