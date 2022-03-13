package agent.behavior.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import java.awt.Color;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;
import environment.Coordinate;
import environment.world.packet.Packet;


public class Deliver extends Behavior {
    Random rand = new Random(42);



    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // No communication
    }


    //Todo: make this work with obstacles;

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {

        var perception = agentState.getPerception();
        String des = agentState.getMemoryFragment(dropOff);

        // get current position
        int x = agentState.getX();
        int y = agentState.getY();
        // intiate step
        int stepx = 0 ,stepy = 0;

        List<Coordinate> moves = new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));

        // if the dropoff hasn't been found search around for it
        if (des == null){
            // search

            for (var move : moves) {
                int checkX = move.getX() + agentState.getX();
                int checkY = move.getY() + agentState.getY();
                if (perception.getCellPerceptionOnAbsPos(checkX, checkY) != null
                        && perception.getCellPerceptionOnAbsPos(checkX,checkY).containsAnyDestination()){
                    des = vectorToString(checkX,checkY);
                    agentState.addMemoryFragment(dropOff,des);
                    agentAction.putPacket(checkX,checkY);
                    return;
                }
            }

            // move

            // systematically cross the board
            if (y % 2 == 0 || (x == 1 && y != 1) || aboutToHitAWall(agentState))
                stepy = 1; // go down
            else if ((y-1)/2 % 2 == 0)
                stepx = +1; // go to the right
            else
                stepy =-1; // go left

            // check if you're at the edge, this should only happen when you hit the right or top wall
            // immediate solution: take a step to the left and set y coordinate for right edge
            // so the right wall can be avoided in the future. algorithm should be finished when hitting the top wall;
            if (perception.getCellPerceptionOnAbsPos(x+stepx, y+stepy) == null){
                stepx = -1;
                agentState.addMemoryFragment(rightEdge,Integer.toString(x));
            }
        }else{
            // get destination coordinates
            var paresedDes = stringToVector(des);
            int xdes = paresedDes.get(0);
            int ydes = paresedDes.get(1);
            // calculate difference
            int xdiff = xdes-x;
            int ydiff = ydes-y;


            // if you aren't 1 away (diagonally counts as 1) from the destination (the dropoff point)
            // go to destination
            if (Math.abs(xdiff)>1 || Math.abs(ydiff)>1){
                // set a step of size 1 in direction of destination
                stepx = xdiff == 0 ? 0 : xdiff/Math.abs(xdiff);
                stepy = ydiff == 0 ? 0 : ydiff/Math.abs(ydiff);

            } else { // you can place down the package
                agentAction.putPacket(xdes, ydes);
                return;
            }

        }

        //take step
        var steppedInCell = perception.getCellPerceptionOnAbsPos(x+stepx, y+stepy);
        if (steppedInCell != null && steppedInCell.isWalkable()) {
            agentAction.step(x+stepx, y+stepy);

            return;
        } else { //TODO: doe dit beter
            if (stepx != 0)
                stepy = -1;
            else
                stepx =1; // go left
            steppedInCell = perception.getCellPerceptionOnAbsPos(x+stepx, y+stepy);
            if (steppedInCell != null && steppedInCell.isWalkable()) {
                agentAction.step(x + stepx, y + stepy);
                return;
            }
        }

        // No viable moves, skip turn
        agentAction.skip();
    }
}
