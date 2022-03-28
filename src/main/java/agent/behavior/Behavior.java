package agent.behavior;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentImp;
import agent.AgentState;
import environment.CellPerception;
import environment.Coordinate;
import environment.Perception;
import environment.world.destination.DestinationRep;
import environment.world.packet.PacketRep;

import java.util.ArrayList;
import java.util.Random;
import java.util.List;
import java.util.Set;


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
    protected String targetsKey = "targets";
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
                return agent.getX() >= Integer.parseInt(rightEdgeX);
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


    protected void takeStep(AgentState agent, AgentAction action, int xStep,int yStep){
        // get cell perception
        CellPerception cellPerception =
                agent.getPerception().getCellPerceptionOnAbsPos(agent.getX() + xStep, agent.getY() + yStep);
        // try and take a step
        if (cellPerception != null) {
            if (cellPerception.isWalkable() && !cellPerception.containsAgent())
                action.step(agent.getX() + xStep, agent.getY() + yStep);
            else {// try going around the obstacle (for now by random avoidance)
                Coordinate tar = stringToCoordinates(agent.getMemoryFragment(target));
                boolean targetInaccessible  =
                        (tar.getX() == agent.getX()+xStep) &&
                        (tar.getY() == agent.getY()+yStep);
                xStep = rand.nextInt(0,3) -1; //int between -1 and 1
                yStep = rand.nextInt(0,3) -1;
                if (targetInaccessible){
                    String newTarget = coordinatesToString(agent.getX() + xStep, agent.getY() + yStep);
                    agent.addMemoryFragment(target,newTarget);
                }
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
                    y += 1; // go down 1 or more with higher vis
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
        System.out.println("searching right edge " + agent.getName());
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
        agent.memorizeAllPerceivableRepresentations();
        agent.forgetAllUnperceivableRepresentations();
    }
    protected boolean onTarget(AgentState agent){
        String targetString = agent.getMemoryFragment(target);
        if (targetString == null)
            return false;
        Coordinate target = stringToCoordinates(targetString);
        return target.getX() == agent.getX() || target.getY() == agent.getY();
    }

    protected void setNotNullTarget(AgentState agent, String newTarget) {
        if (newTarget != null) {
            agent.addMemoryFragment(target, newTarget);
            System.out.println(newTarget);
        }
    }


    protected void walkToTarget(AgentState agent, AgentAction action, Coordinate target) {
        // get the difference
        int xDiff = target.getX() - agent.getX();
        int yDiff = target.getY() - agent.getY();

        // try to take a step directly towards the target
        int xStep = xDiff == 0 ? 0 : xDiff/Math.abs(xDiff);
        int yStep = yDiff == 0 ? 0 : yDiff/Math.abs(yDiff);

        //try to take a step
        if (!agent.getPerception().getCellPerceptionOnAbsPos(agent.getX() + xStep,agent.getY() + yStep).isWalkable()){
            agent.removeMemoryFragment(targetsKey);
            action.skip();
            return;
        }

        action.step(agent.getX()+xStep,agent.getY()+yStep);
    }

    public void setPathToTarget(AgentState agent, Coordinate target){
        List<Coordinate> path = pathToTarget(agent,target);
        if (path == null)
            return;
        agent.addMemoryFragment(targetsKey,coordinatesToString(path));
    }

    public String coordinatesToString(List<Coordinate> coordinates){
        String result = "";
        for (int i = coordinates.size()-1; i>0; i--){
            result += coordinates.get(i).toString();
        }
        return result;
    }

    public List<Coordinate> pathToTarget(AgentState agent,Coordinate target){
        Coordinate agentPos = agent.getPosition();
        Coordinate previousPos = agent.getPosition();
        List<Coordinate> path = new ArrayList<>();
        while (!agentPos.equals(target)){
            Coordinate newPos = stepToTarget(agentPos,target);
            System.out.println("new pos " + newPos);
            System.out.println("agent pos " + agentPos);
            if (newPos.equals(target))
                return path;
            else if (!isWalkableInMemory(agent,newPos) || path.contains(newPos)) {
                System.out.println("target " + target);
                System.out.println("new pos " + newPos);
                if (agentPos == previousPos){
                    Coordinate diff = agentPos.diff(target);

                    int x = diff.getX() == 0 ? 0 : diff.getX()/Math.abs(diff.getX());
                    int y = diff.getY() == 0 ? 0 : diff.getY()/Math.abs(diff.getY());
                    Coordinate pseudoPreviousPos = new Coordinate(agentPos.getX()-x, agentPos.getY()-y);
                    newPos = hugWall(agent,pseudoPreviousPos,agentPos);
                }
                else
                    newPos = hugWall(agent,previousPos,agentPos);
            }
            if (newPos == null)
                return null;
            path.add(newPos);
            // update agentpos
            previousPos = agentPos;
            agentPos = newPos;
        }
        return path;
    }

    public Coordinate hugWall(AgentState agentState, Coordinate prevPos, Coordinate currentPos){
        Perception perception = agentState.getPerception();
        System.out.println(currentPos);
        System.out.println(prevPos);
        Coordinate front = currentPos.diff(prevPos).add(currentPos);
        System.out.println(front);
        Coordinate right = getCoordinateToTheRight(perception,front,currentPos.diff(agentState.getPosition()));

        for(int i = 0; i<8; i++){
            if (isWalkableInMemory(agentState,right))
                return right;
            right = getCoordinateToTheRight(perception,right,currentPos.diff(agentState.getPosition()));
        }
        return null;
    }

    public Coordinate getCoordinateToTheRight(Perception perception, Coordinate front, Coordinate offset){
        CellPerception[] neighbours = perception.getNeighboursInOrder();
        int neighbourToTheRight =-1;
        front = front.diff(offset);
        System.out.println(front);
        System.out.println(perception.getSelfX() + " " + perception.getSelfY());
        for(int i= 0; i<8; i++){
            if (neighbours[i].getX() == front.getX() && neighbours[i].getY() == front.getY())
                neighbourToTheRight = (i+1)%8;
        }
        CellPerception rightNeighbour = neighbours[neighbourToTheRight];
        return new Coordinate(rightNeighbour.getX(), rightNeighbour.getY()).add(offset);
    }

    public Coordinate stepToTarget(Coordinate pos, Coordinate target){
        // get the difference
        int xDiff = target.getX() - pos.getX();
        int yDiff = target.getY() - pos.getY();
        System.out.println(xDiff +" _ " + yDiff);

        int xStep = xDiff == 0 ? 0 : xDiff/Math.abs(xDiff);
        int yStep = yDiff == 0 ? 0 : yDiff/Math.abs(yDiff);

        return new Coordinate(xStep+ pos.getX(),yStep+ pos.getY());
    }

    public boolean isWalkableInMemory(AgentState agentState,  Coordinate pos){
        for (String key : agentState.getMemoryFragmentKeys()){
            if (key != targetsKey && agentState.getMemoryFragment(key).contains(pos.toString()))
                return false;
        }
        return true;
    }

    protected List<Coordinate> getMoves(){
        return new ArrayList<>(List.of(
                new Coordinate(1, 1), new Coordinate(-1, -1),
                new Coordinate(1, 0), new Coordinate(-1, 0),
                new Coordinate(0, 1), new Coordinate(0, -1),
                new Coordinate(1, -1), new Coordinate(-1, 1)
        ));
    }

    public Coordinate getClosestTargetFromMemory(String subKey, AgentState agentState){
        Set<String> keys = agentState.getMemoryFragmentKeysContaining(subKey);
        Coordinate agentPos = agentState.getPosition();
        int minDistance = -1;
        Coordinate closestTarget = null;

        for (String key: keys){
            String memoryFragment = agentState.getMemoryFragment(key);
            if(memoryFragment != null && keyIsOkForSelection(key,agentState) )
            for (Coordinate coordinate: Coordinate.string2Coordinates(memoryFragment)){
                int distance = getDistance(coordinate,agentPos);
                if (minDistance<0 ||minDistance>distance){
                    minDistance = distance;
                    closestTarget = coordinate;
                }
            }
        }
        return closestTarget;
    }

    public boolean keyIsOkForSelection(String key, AgentState agentState){
        String[] keyParts = key.split("_");
        if (keyParts.length != 2 || keyParts[0] != PacketRep.class.toString())
            return true;
        String color = keyParts[1];
        String correspondingDestination = DestinationRep.class + "_" + color;
        if (agentState.getMemoryFragment(correspondingDestination) == null)
            return false;
        return true;
    }

    public int getDistance(Coordinate pos1, Coordinate pos2){
        int diffx = Math.abs(pos1.getX()-pos2.getX());
        int diffy = Math.abs(pos1.getY()-pos2.getY());

        return Math.max(diffx,diffy);
    }

    public Coordinate popCoordinateFromMemory(AgentState agent, String key){
        String memoryFragment = agent.getMemoryFragment(key);
        if (memoryFragment == null)
            return null;
        List<Coordinate> targets = Coordinate.string2Coordinates(memoryFragment);
        Coordinate target = targets.get(0);
        targets.remove(0);
        agent.addMemoryFragment(key,coordinatesToString(targets));
        return target;
    }


}
