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


public class Pickup extends Behavior {



    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }


    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        var perception = agentState.getPerception();


        //----------------------------------------------------------------------------------
        //                              SEARCHING
        //----------------------------------------------------------------------------------
        // Potential moves an agent can make (radius of 1 around the agent)
        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        // check around you
        for (var move : moves) {
            int x = move.getX() + agentState.getX();
            int y = move.getY() + agentState.getY();
            CellPerception cellPerception = perception.getCellPerceptionOnAbsPos(x, y);
            if (cellPerception != null){
                if (cellPerception.containsPacket()) {
                    agentState.removeMemoryFragment(pickUpTarget);
                    setNotNullTarget(agentState, agentState.getMemoryFragment(dropOff));
                    agentAction.pickPacket(x, y);
                    return;
                }
                if (cellPerception.containsAnyDestination()){
                    String des = coordinatesToString(x,y);
                    agentState.addMemoryFragment(dropOff,des);
                }
            }
        }

        //-------------------------------------------------------------------------------
        //                             MOVEMENT
        //-------------------------------------------------------------------------------
        setZigZagTarget(agentState);
        Coordinate targetCoordinates = stringToCoordinates(agentState.getMemoryFragment(target));

        walkToTarget(agentState,agentAction,targetCoordinates);


        if (agentState.getMemoryFragment(pickUpTarget) == null) {
            String currentPos = coordinatesToString(agentState.getX(), agentState.getY());
            agentState.addMemoryFragment(storedTarget, currentPos);
        }
    }


}
