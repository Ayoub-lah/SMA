package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;

public class PharmacyAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("Pharmacy Agent Started");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();

                if (msg != null) {
                    String content = msg.getContent();
                    System.out.println("Ordonnance reçue : " + content);

                    // Parser médicament
                    String medicament = "Paracétamol 1000mg";
                    String[] parts = content.split("\\|");
                    for (String part : parts) {
                        part = part.trim();
                        if (part.startsWith("Médicament:"))
                            medicament = part.substring(11).trim();
                    }

                    System.out.println("Préparation : " + medicament);
                    System.out.println("✓ Médicament prêt");

                    // Transmettre TOUT le contenu à Admin
                    ACLMessage adminMsg = new ACLMessage(ACLMessage.INFORM);
                    adminMsg.addReceiver(new AID("Admin", AID.ISLOCALNAME));
                    adminMsg.setContent(content); // ← contenu complet
                    send(adminMsg);

                    System.out.println("Dossier transmis à Admin");

                } else {
                    block();
                }
            }
        });
    }
}