package agent;

import java.util.Collection;

import environment.Mail;
import environment.world.agent.AgentRep;



public interface AgentCommunication {
    

    /**
     * Create a mail from this AgentImp to the specified receiver with the given message.
     * 
     * @param receiver  The agent representation of the agent to write the message to.
     * @param message   The message to send.
     */
    void sendMessage(AgentRep receiver, String message);


    /**
     * Broadcast a message to all other agents.
     * @param message  The message to transmit.
     */
    void broadcastMessage(String message);







    /**
     * Get the number of messages in the incoming message queue.
     */
    int getNbMessages();

    /**
     * Gets message at the given index from the message queue.
     * @param index  The index of the desired message.
     */
    Mail getMessage(int index);

    /**
     * Removes the message at the specified index from the incoming message queue.
     * @param index  The index of the message to remove.
     */
    void removeMessage(int index);


    /**
     * Retrieve all the incoming messages.
     * @return A collection with the received messages from other agents.
     */
    Collection<Mail> getMessages();

    /**
     * Clear the incoming message queue.
     */
    void clearMessages();
}
