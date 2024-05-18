import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class Main {
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        profile.setParameter(Profile.GUI, "true"); 

        AgentContainer mainContainer = rt.createMainContainer(profile);

        try {
            Object[] arbitratorArgs = {"50"}; 
            AgentController arbitrator = mainContainer.createNewAgent("arbitrator", "ArbitratorAgent", arbitratorArgs);
            arbitrator.start();

            String[] prisoner1Args = {"AlwaysCooperate"};
            String[] prisoner2Args = {"AlwaysDefect"};

            AgentController prisoner1 = mainContainer.createNewAgent("prisoner1", "Prisoner", prisoner1Args);
            prisoner1.start();

            AgentController prisoner2 = mainContainer.createNewAgent("prisoner2", "Prisoner", prisoner2Args);
            prisoner2.start();

            AgentController sniffer = mainContainer.createNewAgent("sniffer", "jade.tools.sniffer.Sniffer", null);
            sniffer.start();

        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}