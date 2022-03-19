package agent.behavior.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;


public class Deliver extends Behavior {



    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {

    }


    //Todo: make this work with obstacles;

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {


        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        Perception perception = agentState.getPerception();
        // if the dropoff hasn't been found search around for it
        // search

        for (var move : moves) {
            int checkX = move.getX() + agentState.getX();
            int checkY = move.getY() + agentState.getY();
            CellPerception cellPerception = perception.getCellPerceptionOnAbsPos(checkX, checkY);
            if (cellPerception != null){
                String checkingCoordinates = coordinatesToString(checkX,checkY);
                if ( cellPerception.containsPacket())
                    agentState.addMemoryFragment(pickUpTarget,checkingCoordinates);
                if (cellPerception.containsAnyDestination()) {
                    agentState.addMemoryFragment(dropOff,checkingCoordinates);
                    //if (agentState.getMemoryFragment(pickUpTarget) == null)
                    //    lookAround(agentState); // this can find a new pickup target
                    setNotNullTarget(agentState,agentState.getMemoryFragment(storedTarget));
                    setNotNullTarget(agentState,agentState.getMemoryFragment(pickUpTarget));
                    System.out.println("Stored target: " + agentState.getMemoryFragment(storedTarget));
                    System.out.println("Pick up target: " + agentState.getMemoryFragment(pickUpTarget));

                    agentAction.putPacket(checkX,checkY);
                    return;
                }

            }
        }

        setZigZagTarget(agentState);
        Coordinate targetCoordinates = stringToCoordinates(agentState.getMemoryFragment(target));

        walkToTarget(agentState,agentAction,targetCoordinates);

    }
}
