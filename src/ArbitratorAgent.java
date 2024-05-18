import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.Map;

public class ArbitratorAgent extends Agent {
    private static final long serialVersionUID = 1L; 
    private Map<String, ACLMessage> decisions = new HashMap<>();
    private int maxRounds;
    private int currentRound = 0;
    private Map<String, Integer> prisonerScores = new HashMap<>();

    protected void setup() {
        System.out.println("Arbitrator-agent " + getAID().getName() + " is ready.");

        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            maxRounds = Integer.parseInt((String) args[0]);
        } else {
            maxRounds = 10; 
        }
        addBehaviour(new HandlePrisonerDecisions());
    }

    private class HandlePrisonerDecisions extends Behaviour {
        private static final long serialVersionUID = 1L;
		private boolean isDone = false;

        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                decisions.put(msg.getSender().getLocalName(), msg);

                if (decisions.size() == 2) { 
                    evaluateDecisions();
                    currentRound++;

                    if (currentRound >= maxRounds) {
                        sendStopSignal();
                        sendStatistics();
                        isDone = true;
                    }
                }
            } else {
                block();
            }
        }

        private void evaluateDecisions() {
            ACLMessage firstDecision = (ACLMessage) decisions.values().toArray()[0];
            ACLMessage secondDecision = (ACLMessage) decisions.values().toArray()[1];

            String firstContent = firstDecision.getContent();
            String secondContent = secondDecision.getContent();

            String result;
            if (firstContent.equals("cooperate") && secondContent.equals("cooperate")) {
                result = "Each gets 1 year";
                updateScore(firstDecision.getSender().getLocalName(), 1);
                updateScore(secondDecision.getSender().getLocalName(), 1);
            } else if (firstContent.equals("cooperate") && secondContent.equals("betray")) {
                result = firstDecision.getSender().getLocalName() + " gets 3 years, " + secondDecision.getSender().getLocalName() + " is released";
                updateScore(firstDecision.getSender().getLocalName(), 3);
                updateScore(secondDecision.getSender().getLocalName(), 0);
            } else if (firstContent.equals("betray") && secondContent.equals("cooperate")) {
                result = firstDecision.getSender().getLocalName() + " is released, " + secondDecision.getSender().getLocalName() + " gets 3 years";
                updateScore(firstDecision.getSender().getLocalName(), 0);
                updateScore(secondDecision.getSender().getLocalName(), 3);
            } else { 
                result = "Each gets 2 years";
                updateScore(firstDecision.getSender().getLocalName(), 2);
                updateScore(secondDecision.getSender().getLocalName(), 2);
            }

            sendResult(firstDecision, result);
            sendResult(secondDecision, result);

            decisions.clear();
        }

        private void updateScore(String prisonerName, int score) {
            int currentScore = prisonerScores.getOrDefault(prisonerName, 0);
            prisonerScores.put(prisonerName, currentScore + score);
        }

        private void sendResult(ACLMessage decision, String result) {
            ACLMessage reply = decision.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(result);
            myAgent.send(reply);
        }

        private void sendStopSignal() {
            ACLMessage stopMessage = new ACLMessage(ACLMessage.INFORM);
            stopMessage.setContent("stop");
            for (String prisonerName : prisonerScores.keySet()) {
                stopMessage.addReceiver(new AID(prisonerName, AID.ISLOCALNAME));
            }
            myAgent.send(stopMessage);
        }
        
        private void sendStatistics() {
            for (String prisonerName : prisonerScores.keySet()) {
                ACLMessage statisticsMessage = new ACLMessage(ACLMessage.INFORM);
                statisticsMessage.setContent("Your total score: " + prisonerScores.get(prisonerName));
                statisticsMessage.addReceiver(new AID(prisonerName, AID.ISLOCALNAME));
                myAgent.send(statisticsMessage);
            }
        }

        public boolean done() {
            return isDone;
        }
    }

    protected void takeDown() {
        System.out.println("Arbitrator-agent " + getAID().getName() + " terminating.");
    }
}