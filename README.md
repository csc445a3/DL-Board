# DL-Board
Multiclient Message Board Program

This program creats a new instance of a client per instance of the program. The GUI creates a new client so that multiple 
clients can receive all messages sent through the program. The program requires a password when you enter the room this sets
up the secrety key for AES encryption. All clients must be running on the same network as they connect through a multicast IP
address and socket. Each client joins the group and the group forwards messages to all clients. It by design doesnt keep
messages as it is meant to be an instance of a group message board and then loses it when the program terminates.
