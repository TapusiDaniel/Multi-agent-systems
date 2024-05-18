import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.AID;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Prisoner extends Agent {
    private static final long serialVersionUID = 1L;
	private String strategy;
    private int roundsPlayed = 0;
    private List<String> opponentDecisionHistory = new ArrayList<>();
    private Random random = new Random();
    private int finalScore = 0;

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            strategy = (String) args[0];
        } else {
            strategy = "TitForTat"; 
        }

        System.out.println("Prisoner " + getAID().getName() + " using strategy: " + strategy);

        addBehaviour(new CommunicateDecision());
    }

    private class CommunicateDecision extends Behaviour {
        private static final long serialVersionUID = 1L;
    	private boolean stopped = false;

        public void action() {
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            msg.addReceiver(new AID("arbitrator", AID.ISLOCALNAME));
            msg.setContent(chooseNextDecision());  
            myAgent.send(msg);

            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage reply = myAgent.blockingReceive(mt);
            if (reply != null) {
                String replyContent = reply.getContent();
                if (replyContent.equals("stop")) {
                    stopped = true;
                    System.out.println("Prisoner " + getAID().getName() + " received stop signal");
                    myAgent.addBehaviour(new OneShotBehaviour() {
                        private static final long serialVersionUID = 1L;

    					public void action() {
                            ((Prisoner) myAgent).printFinalScore();
                        }
                    });
                } else {
                    String opponentDecision = extractOpponentDecision(replyContent);
                    opponentDecisionHistory.add(opponentDecision);
                    roundsPlayed++;

                    String nextDecision = chooseNextDecision();
                    System.out.println("Prisoner " + getAID().getName() + " received: " + replyContent);
                    System.out.println("Prisoner " + getAID().getName() + " next decision: " + nextDecision);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("Prisoner " + getAID().getName() + " did not receive any reply");
            }
        }

        private String extractOpponentDecision(String replyContent) {
            String opponentDecision = "";
            if (replyContent.contains("Each gets 2 years")) {
                opponentDecision = "betray";
                finalScore += 2; 
            }
            else if (replyContent.contains("Each gets 1 year")) {
                opponentDecision = "cooperate";
                finalScore += 1; 
            }
            else if (replyContent.contains("is released")) {
                if (replyContent.contains(getAID().getLocalName() + " is released")) {
                    opponentDecision = "cooperate"; 
                    finalScore += 0; 
                } else {
                    opponentDecision = "betray"; 
                    finalScore += 3; 
                }
            }
            return opponentDecision;
        }

        private String chooseNextDecision() {
            switch (strategy) {
                case "AlwaysCooperate":
                    return "cooperate";
                case "AlwaysDefect":
                    return "betray";
                case "TitForTat":
                    return titForTatStrategy();
                case "Random":
                    return randomStrategy();
                case "Grudger":
                    return grudgerStrategy();
                case "Pavlov":
                    return pavlovStrategy();
                default:
                    return "cooperate";
            }
        }

        private String randomStrategy() {
            String decision = random.nextBoolean() ? "cooperate" : "betray";
            System.out.println("Random strategy decision: " + decision);
            return decision;
        }

        private String grudgerStrategy() {
            if (opponentDecisionHistory.contains("betray")) {
                return "betray";
            } else {
                return "cooperate";
            }
        }

        private String pavlovStrategy() {
            if (roundsPlayed == 0) {
                return "cooperate";
            } else {
                String lastOpponentDecision = opponentDecisionHistory.get(opponentDecisionHistory.size() - 1);
                String myLastDecision = lastOpponentDecision;
                if (lastOpponentDecision.equals(myLastDecision)) {
                    return "cooperate";
                } else {
                    return "betray";
                }
            }
        }

        private String titForTatStrategy() {
            if (!opponentDecisionHistory.isEmpty()) {
                return opponentDecisionHistory.get(opponentDecisionHistory.size() - 1);
            } else {
                return "cooperate";
            }
        }

        public boolean done() {
            return stopped;
        }
    }

    private void printFinalScore() {
        System.out.println("Final score of " + getAID().getName() + ": " + finalScore + " years");
    }
}