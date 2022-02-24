package gui.video;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.eventbus.Subscribe;

import org.json.JSONArray;
import org.json.JSONObject;

import environment.ApplicationRunner;
import environment.Coordinate;
import environment.EnergyValues;
import environment.Environment;
import environment.world.generator.PacketGenerator;
import util.event.AgentActionEvent;
import util.event.EnergyUpdateEvent;

public class EventTracker {

    private final Consumer<ActionUpdate> callback;
    private final ApplicationRunner applicationRunner;

    private int totalPackets;
    private int energySpent;

    // Cycle -> packet pickup, put or delivery
    private final List<PacketAction> historyPackets;
    // Cycle -> agent move
    private final List<AgentMove> historyMoves;
    // Cycle -> energy update
    private final List<EnergyUpdate> historyEnergy;




    public EventTracker(Consumer<ActionUpdate> consumer, ApplicationRunner applicationRunner) {
        this.callback = consumer;
        this.applicationRunner = applicationRunner;

        this.historyPackets = new ArrayList<>();
        this.historyMoves = new ArrayList<>();
        this.historyEnergy = new ArrayList<>();

        this.reset();
        
        this.applicationRunner.getEventBus().register(this);
    }


    /**
     * Add the given agent action to the history of actions (if actions should be logged).
     *
     * @param event The AgentAction event.
     */
    @Subscribe
    private void addAgentAction(AgentActionEvent event) {
        int time = this.getEnvironment().getTime();


        var packet = event.getPacket();
        var agentName = event.getAgent() == null ? "" : event.getAgent().getName();

        switch (event.getAction()) {
            case AgentActionEvent.PICK_PACKET -> {
                var action = new PacketAction(packet.getX(), packet.getY(),
                        PacketAction.Mode.Pickup, agentName, time);
                this.historyPackets.add(action);
                this.callback.accept(action);
            }
            case AgentActionEvent.PUT_PACKET -> {
                var action = new PacketAction(event.getToX(), event.getToY(),
                        PacketAction.Mode.Drop, agentName, time);
                this.historyPackets.add(action);
                this.callback.accept(action);
            }
            case AgentActionEvent.DELIVER_PACKET -> {
                var action = new PacketAction(event.getToX(), event.getToY(),
                        PacketAction.Mode.Delivery, agentName, time);
                this.historyPackets.add(action);
                this.callback.accept(action);
            }
            case AgentActionEvent.STEP -> {
                var action = new AgentMove(event.getFromX(), event.getFromY(),
                        event.getToX(), event.getToY(), agentName, time);
                this.historyMoves.add(action);
                this.callback.accept(action);
            }

        }

        // Agent energy cost (discount charging)
        this.energySpent += EnergyValues.calculateEnergyCost(event, false);
    }


    /**
     * Add the given energy update to the history of energy updates.
     *
     * @param event The event containing the energy update.
     */
    @Subscribe
    private void addEnergyEvent(EnergyUpdateEvent event) {
        int time = this.getEnvironment().getTime();

        var action = new EnergyUpdate(event.getEnergyPercentage(), event.isIncreased(), event.getAgent().getName(), time);
        this.historyEnergy.add(action);
        this.callback.accept(action);
    }


    public JSONObject getHistoryJSON() {
        JSONObject head = new JSONObject();

        // Meta information about the run
        JSONObject meta = new JSONObject();
        meta.put("TotalCycles", getEnvironment().getTime());
        meta.put("TotalPackets", this.totalPackets);
        meta.put("PacketsDelivered", this.historyPackets.stream()
                .filter(p -> p.mode == PacketAction.Mode.Delivery)
                .count());
        meta.put("EnergyConsumed", this.energySpent);
        meta.put("Environment", applicationRunner.getEnvFile());
        meta.put("Implementation", applicationRunner.getImplementation());

        JSONObject moves = new JSONObject();
        moves.put("Key", new JSONArray(new String[] {"Cycle", "AgentName", "FromX", "FromY", "ToX", "ToY"}));
        moves.put("Data", new JSONArray(historyMoves.stream()
                .map(AgentMove::toJSONArray)
                .collect(Collectors.toList())));


        JSONObject packetPickup = new JSONObject();
        packetPickup.put("Key", new JSONArray(new String[] {"Cycle", "AgentName", "PacketX", "PacketY"}));
        packetPickup.put("Data", new JSONArray(historyPackets.stream()
                .filter(p -> p.mode == PacketAction.Mode.Pickup)
                .map(PacketAction::toJSONArray)
                .collect(Collectors.toList())));


        JSONObject packetDelivery = new JSONObject();
        packetDelivery.put("Key", new JSONArray(new String[] {"Cycle", "AgentName", "DestinationX", "DestinationY"}));
        packetDelivery.put("Data", new JSONArray(historyPackets.stream()
                .filter(p -> p.mode == PacketAction.Mode.Delivery)
                .map(PacketAction::toJSONArray)
                .collect(Collectors.toList())));


        JSONObject packetDrop = new JSONObject();
        packetDrop.put("Key", new JSONArray(new String[] {"Cycle", "AgentName", "DropX", "DropY"}));
        packetDrop.put("Data", new JSONArray(historyPackets.stream()
                .filter(p -> p.mode == PacketAction.Mode.Drop)
                .map(PacketAction::toJSONArray)
                .collect(Collectors.toList())));


        JSONObject energyUpdate = new JSONObject();
        energyUpdate.put("Key", new JSONArray(new String[] {"Cycle", "AgentName", "Operator", "Percentage"}));
        energyUpdate.put("Data", new JSONArray(historyEnergy.stream()
                .map(EnergyUpdate::toJSONArray)
                .collect(Collectors.toList())));


        head.put("Meta", meta);
        head.put("Moves", moves);
        head.put("PacketPickups", packetPickup);
        head.put("PacketDeliveries", packetDelivery);
        head.put("PacketDrops", packetDrop);
        head.put("EnergyUpdates", energyUpdate);

        return head;
    }


    private Environment getEnvironment() {
        return this.applicationRunner.getEnvironment();
    }

    public int getEnergySpent() {
        return this.energySpent;
    }

    public boolean isRunFinished() {
    	return this.totalPackets == this.historyPackets.stream()
    		.filter(p -> p.mode == PacketAction.Mode.Delivery)
    		.count();
    }


    public void reset() {
        this.historyMoves.clear();
        this.historyPackets.clear();
        this.historyEnergy.clear();
        this.totalPackets = getEnvironment().getPacketWorld().getNbPackets()
                + getEnvironment().getPacketGeneratorWorld().getItemsFlat().stream()
                        .mapToInt(PacketGenerator::getPacketsLeftToGenerate)
                        .sum();
        this.energySpent = 0;
    }







    public abstract static class ActionUpdate {
        String agentName;
        int cycle;

        public abstract String toString();

        public String getAgentName() {
            return this.agentName;
        }

        public int getCycle() {
            return this.cycle;
        }
    }

    private static class AgentMove extends ActionUpdate {
        final Coordinate moveFrom;
        final Coordinate moveTo;

        AgentMove(int fromX, int fromY, int toX, int toY, String agentName, int cycle) {
            this.agentName = agentName;
            this.cycle = cycle;
            this.moveFrom = new Coordinate(fromX, fromY);
            this.moveTo = new Coordinate(toX, toY);
        }

        @Override
        public String toString() {
            return "Moved from " + this.moveFrom + " to " + this.moveTo;
        }

        public JSONArray toJSONArray() {
            return new JSONArray(new Object[] {this.cycle, this.agentName, this.moveFrom.getX(), this.moveFrom.getY(),
                    this.moveTo.getX(), this.moveTo.getY()});
        }
    }

    private static class PacketAction extends ActionUpdate {
        final Coordinate packetLocation;
        final PacketAction.Mode mode;

        PacketAction(int x, int y, PacketAction.Mode mode, String agentName, int cycle) {
            this.agentName = agentName;
            this.cycle = cycle;
            this.packetLocation = new Coordinate(x, y);
            this.mode = mode;
        }

        @Override
        public String toString() {
            return this.mode.toString() + " of packet at location " + this.packetLocation;
        }

        public JSONArray toJSONArray() {
            return new JSONArray(new Object[] {this.cycle, this.agentName, this.packetLocation.getX(), this.packetLocation.getY()});
        }

        enum Mode {
            Pickup,
            Drop,
            Delivery
        }
    }

    private static class EnergyUpdate extends ActionUpdate {
        final int percentage;
        final boolean isIncreased;

        EnergyUpdate(int percentage, boolean isIncreased, String agentName, int cycle) {
            this.agentName = agentName;
            this.cycle = cycle;
            this.percentage = percentage;
            this.isIncreased = isIncreased;
        }

        @Override
        public String toString() {
            return String.format("Energy %s %d%%", this.isIncreased ? "increased above (or equal to)" : "dropped below", this.percentage);
        }

        public JSONArray toJSONArray() {
            return new JSONArray(new Object[] {this.cycle, this.agentName, this.isIncreased ? ">=" : "<", this.percentage});
        }
    }
}
