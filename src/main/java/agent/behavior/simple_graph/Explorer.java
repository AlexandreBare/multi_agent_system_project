package agent.behavior.simple_graph;

import agent.AgentAction;
import agent.AgentCommunication;
import agent.AgentState;
import agent.behavior.Behavior;

public class Explorer extends Behavior {
    @Override
    public void communicate(AgentState agentState, AgentCommunication agentCommunication) {
        // no communication
    }

    @Override
    public void act(AgentState agentState, AgentAction agentAction) {
        // look()

        // no target?
            // set_new_target ()

        // walk_to_target ()
    }

    private void walk_to_target(AgentState state, AgentAction action){
        //go in a straight line
        // path blocked?
            // by wall
                // get_graph_path()
                // move straight to new target
            // by packet
                // FUCK FUCK FUCK maybe move it
            // by something else
                // skip?
    }

    private void get_graph_path(AgentState state){
        // get closest graph point to you
        // get closest same graph point to target
        // find way along graph
        // add way to targets
    }

    private void move_packet_out_of_the_way(){
        // holding a packet?
            // place packet behind you (on previous position)
        // pick packet up
    }

    private void set_new_target(AgentState agent){
        // get potential_targets
        // get closest_targets from potential_targets
        // set random point from closest_targets as target
    }
    private void look(AgentState agent) {
        // get lists form memory
            // checked_points
            // interesting_points

        // for each spot
            // check for target/destination + add to designed list
            // not in checked_points?
                // wall?
                    // add_to_checked() and go to next point
                // find neighbours
                // all neighbours visible/not walls?
                    //add_to_checked()
                // neighbours has walls/generators/destination?
                    // set_up_graph()
                // walkable?
                    // add to potential_targets
    }

    private void add_to_checked(){
        // remove from potential_targets
        // add to checked_points
    }
}
