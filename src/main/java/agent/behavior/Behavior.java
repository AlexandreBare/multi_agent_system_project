package agent.behavior;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentImp;
import agent.AgentState;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;


/**
 * This class represents a role for an agent. It contains the actions the agent
 * does when playing this role.
 */
abstract public class Behavior {

    protected boolean hasHandled;
    private String description;
    private boolean closing = false;

    Random rand = new Random(42);

    //memoryKeys
    protected String target = "tar";
    protected String pickUpTarget = "tarp";
    protected String storedTarget = "tars";
    protected String dropOff = "dof";
    protected String rightEdge = "rw";

    protected Behavior() {}


    /**
     * This method handles the actions of the behavior that are communication
     * related. This does not include answering messages, only sending them.
     */
    public abstract void communicate(AgentState agentState, AgentCommunication agentCommunication);

    /**
     * This method handles the actions of the behavior that are action related
     */
    public abstract void act(AgentState agentState, AgentAction agentAction);


    /**
     * Implements the general behavior method each cycle.
     * This method first handles communication in agents, and afterwards executes actions for the agents
     *  (cfr. abstract methods {@link #communicate(AgentState, AgentCommunication)} and {@link #act(AgentState, AgentAction)})
     */
    public final void handle(AgentImp agent) {
        
        if (this.hasHandled()) {
            return;
        }
        if (agent.inCommPhase()) {
            this.communicate(agent, agent);
            agent.closeCommunication();
        } else if (agent.inActionPhase()) {
            agent.resetCommittedAction();
            this.act(agent, agent);

            if (!agent.hasCommittedAction()) {
                throw new RuntimeException(String.format("Agent with ID=%d did not perform any action in its current behavior. Make sure the agent performs exactly one action each turn (taking the skip action into account as well).", agent.getActiveItemID().getID()));
            }
        }
    }


    
    /**
     * An optional precondition that can be defined for this behavior (checked before evaluating any behavior changes).
     * @param agentState The agent's perception with which the precondition is evaluated.
     * @return {@code true} if the precondition is fulfilled, {@code false} otherwise.
     */
    public boolean preCondition(AgentState agentState) {
        return true;
    }

    /**
     * An optional post condition that can be defined for this behavior (checked before taking actual behavior transitions).
     * @return {@code true} if the post condition is fulfilled, {@code false} otherwise.
     */
    public boolean postCondition() {
        return true;
    }



    /**
     * Optional actions to take before leaving this role
     */
    public void leave(AgentState agentState) {}




    /**
     * Returns a description of this role
     */
    public final String getDescription() {
        return description;
    }


    /**
     * Sets a description for this role
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Checks if this role has done for this synchronization cycle
     */
    private boolean hasHandled() {
        return this.hasHandled;
    }


    /**
     * DESTRUCTOR. Clears this role.
     */
    public void finish() {
        if (closing) {
            return;
        }
        closing = true;
        description = null;
    }


    protected boolean aboutToHitAWall(AgentState agent){
        boolean goingToTheRight = (agent.getY()-1)/2 % 2 == 0;
        if (goingToTheRight) {
            String rightEdgeX = agent.getMemoryFragment(rightEdge);
            if (rightEdgeX != null)
                return agent.getX() == Integer.parseInt(rightEdgeX);
        }
        else
            return agent.getX() == 1;

        return false;
    }

    protected String coordinatesToString(int x, int y) {
        return x + "," + y;
    }
    protected String coordinatesToString(Coordinate c) {
        return coordinatesToString(c.getX(),c.getY());
    }

    protected Coordinate stringToCoordinates(String vector) {
        var splitVector = vector.split(",");
        return  new Coordinate(
                Integer.parseInt(splitVector[0]),
                Integer.parseInt(splitVector[1])
        );
    }

    protected void walkToTarget(AgentState agent, AgentAction action, Coordinate target) {
        // get the difference
        int xDiff = target.getX() - agent.getX();
        int yDiff = target.getY() - agent.getY();

        // try to take a step directly towards the target
        int xStep = xDiff == 0 ? 0 : xDiff/Math.abs(xDiff);
        int yStep = yDiff == 0 ? 0 : yDiff/Math.abs(yDiff);

        takeStep(agent, action, xStep, yStep);
    }

    protected void takeStep(AgentState agent, AgentAction action, int xStep,int yStep){
        // get cell perception
        CellPerception cellPerception =
                agent.getPerception().getCellPerceptionOnAbsPos(agent.getX() + xStep, agent.getY() + yStep);
        // try and take a step
        if (cellPerception != null) {
            if (cellPerception.isWalkable())
                action.step(agent.getX() + xStep, agent.getY() + yStep);
            else {// try going around the obstacle (for now by random avoidance)
                xStep = rand.nextInt(0,3) -1; //int between -1 and 1
                yStep = rand.nextInt(0,3) -1;
                //retry to take step in (maybe) different direction
                takeStep(agent,action,xStep,yStep);
            }
        } else { // if a perception towards the destination is out of bounds then the destination is out of bounds
            // panic
            action.skip();
        }
    }

    protected void setZigZagTarget(AgentState agent){
        // initialize target
        String currentTargetString = agent.getMemoryFragment(target);
        if (currentTargetString == null){
            currentTargetString = coordinatesToString(1,1);
            agent.addMemoryFragment(target,currentTargetString);
        }
        // convert current target to coordinate
        Coordinate currentTarget  = stringToCoordinates(currentTargetString);

        // only change target when you got to the previous target
        if (currentTarget.getX() == agent.getX() && currentTarget.getY() == agent.getY()){
            String rightEdgeCoordinate = agent.getMemoryFragment(rightEdge);
            if (rightEdgeCoordinate == null)
                findRightEdge(agent);
            else {
                int y = agent.getY();
                int x = agent.getX();
                if (y % 2 == 0 || aboutToHitAWall(agent))
                    y += 2; // go down 2 or more with higher vis
                else if ((y-1)/2 % 2 == 0)
                    x = Integer.parseInt(rightEdgeCoordinate); // set x to right edge
                else
                    x = 1; // set x to left edge
                // set new target
                String newTarget = coordinatesToString(x,y);
                agent.addMemoryFragment(target,newTarget);
            }
        }
    }

    protected void findRightEdge(AgentState agent){
        // get cellPerception of the cell to the right
        CellPerception cellPerception =
                agent.getPerception().getCellPerceptionOnAbsPos(agent.getX() + 1, agent.getY());
        if (cellPerception == null){
            int currentX = agent.getX();
            // set edge to current y - 1 (or visibility range) so that everything can be seen
            agent.addMemoryFragment(rightEdge,Integer.toString(currentX-1));
            String newTarget = coordinatesToString(currentX-1,agent.getY());
            agent.addMemoryFragment(target,newTarget);
        }
        else if (!cellPerception.isWalkable()){
            String newTarget = coordinatesToString(agent.getX()+2,agent.getY());
            agent.addMemoryFragment(target,newTarget);
        }
        else{
            String newTarget = coordinatesToString(agent.getX()+1,agent.getY());
            agent.addMemoryFragment(target,newTarget);
        }
    }

    protected void lookAround (AgentState agent){
        List<Coordinate> offset= new ArrayList<>(List.of(
                new Coordinate(0,-1),
                new Coordinate(-1,0),
                new Coordinate(0,1),
                new Coordinate(1,0)
        ));
        int distanceFromCenter = 1;
        Perception perception = agent.getPerception();
        System.out.println("width: " + perception.getWidth() + " height: " + perception.getHeight());
        boolean inFieldOfView = true;
        while (inFieldOfView){
            inFieldOfView =false;
            int x = distanceFromCenter + agent.getX();
            int y = distanceFromCenter + agent.getY();
            System.out.println("---------- "+ distanceFromCenter +" ----------");
            for (int direction = 0; direction < 4; direction++){
                for(int i = 0; i < 2*distanceFromCenter; i++){
                    x += offset.get(direction).getX();
                    y += offset.get(direction).getY();
                    CellPerception cellPerception = perception.getCellPerceptionOnAbsPos(x,y);

                    if (cellPerception != null){
                        inFieldOfView = true;
                        if (cellPerception.containsPacket()){
                            String newPickUpTarget = coordinatesToString(x,y);
                            agent.addMemoryFragment(pickUpTarget,newPickUpTarget);
                            return;
                        }
                    }
                    System.out.println(x + " : " + y);

                }
            }
            distanceFromCenter++;
        }
    }
    protected boolean onTarget(AgentState agent){
        String targetString = agent.getMemoryFragment(target);
        if (targetString == null)
            return false;
        Coordinate target = stringToCoordinates(targetString);
        return target.getX() == agent.getX() || target.getY() == agent.getY();
    }

    protected void setNotNullTarget(AgentState agent, String newTarget){
        if (target != null)
            agent.addMemoryFragment(target, newTarget);
    }

}
