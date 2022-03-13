package agent.behavior.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.Coordinate;


public class Pickup extends Behavior {
    Random rand = new Random(42);



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

        for (var move : moves) {
            int x = move.getX() + agentState.getX();
            int y = move.getY() + agentState.getY();
            if (perception.getCellPerceptionOnAbsPos(x, y) != null){
                if (perception.getCellPerceptionOnAbsPos(x, y).containsPacket()) {
                    agentAction.pickPacket(x, y);
                    return;
                }
                if (perception.getCellPerceptionOnAbsPos(x,y).containsAnyDestination()){
                    String des = vectorToString(x,y);
                    agentState.addMemoryFragment(dropOff,des);
                }
            }
        }

        //-------------------------------------------------------------------------------
        //                             MOVEMENT
        //-------------------------------------------------------------------------------

        // get current position
        int x = agentState.getX();
        int y = agentState.getY();
        int stepx = 0 ,stepy = 0;

        // get destination (if there is one otherwise set it to 1,1)
        String des = agentState.getMemoryFragment(destination);
        if (des == null) {
            des = vectorToString(1,1);
            agentState.addMemoryFragment(destination,des);
        }
        var paresedDes = stringToVector(des);
        int xdes = paresedDes.get(0);
        int ydes = paresedDes.get(1);

        // if you aren't at the destination go to destination
        if (x != xdes && y != ydes){
            int xdiff = xdes-x;
            int ydiff = ydes-y;
            // set a step of size 1 in direction of destination
            stepx = xdiff/Math.abs(xdiff);
            stepy = ydiff/Math.abs(ydiff);
            //Todo: make this work with obstacles;
        }
        else{
            // systematically cross the board
            if (y % 2 == 0 || aboutToHitAWall(agentState))
                stepy = 1; // go down
            else if ((y-1)/2 % 2 == 0)
                stepx = 1; // go to the right
            else
                stepx = -1; // go left

            // check if you're at the edge, this should only happen when you hit the right or top wall
            // immediate solution: take a step to the left and set y coordinate for right edge
            // so the right wall can be avoided in the future. algorithm should be finished when hitting the top wall;
            if (perception.getCellPerceptionOnAbsPos(x + stepx, y + stepy) == null){
                stepx = -1;
                agentState.addMemoryFragment(rightEdge,Integer.toString(x));
            }
            agentState.addMemoryFragment(destination,vectorToString(x+stepx,y+stepy));

        }


        var steppedInCell = perception.getCellPerceptionOnAbsPos(x+stepx, y+stepy);
        if (steppedInCell != null && steppedInCell.isWalkable()) {
            agentAction.step(x+stepx, y+stepy);
            return;
        } else { //TODO: doe dit beter
            if (x == xdes && y == ydes){
                agentState.addMemoryFragment(destination,vectorToString(x+stepx*2,y+stepy*2));
            }
            if (stepx != 0)
                stepy = -1;
            else
                stepx =1;

            steppedInCell = perception.getCellPerceptionOnAbsPos(x+stepx, y+stepy);
            if (steppedInCell != null && steppedInCell.isWalkable()) {
                agentAction.step(x + stepx, y + stepy);
                return;
            }
        }

        // No viable moves, skip turn and panic
        agentAction.skip();
    }


}
