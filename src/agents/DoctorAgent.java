package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.core.behaviours.CyclicBehaviour;

public class DoctorAgent extends Agent {

    @Override
    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String patientData = msg.getContent();

                    String nom     = "Patient";
                    String consult = "Générale";
                    for (String part : patientData.split("\\|")) {
                        part = part.trim();
                        if (part.startsWith("Nom:"))
                            nom = part.substring(4).trim();
                        else if (part.startsWith("Consultation:"))
                            consult = part.substring(13).trim();
                    }

                    String diagnostic = detectDiagnostic(consult);
                    String medicament = detectMedicament(consult);

                    System.out.println();
                    System.out.println(
                            "┌─────────────────────────────────────────────────────┐");
                    System.out.println(
                            "│  DoctorAgent — Consultation Médicale                │");
                    System.out.println(
                            "├─────────────────────────────────────────────────────┤");
                    System.out.printf(
                            "│  Patient     : %-37s│%n", nom);
                    System.out.printf(
                            "│  Service     : %-37s│%n", consult);
                    System.out.printf(
                            "│  Diagnostic  : %-37s│%n", diagnostic);
                    System.out.printf(
                            "│  Médicament  : %-37s│%n", medicament);
                    System.out.println(
                            "│  → Ordonnance envoyée à PharmacyAgent               │");
                    System.out.println(
                            "│  → Dossier transmis à AdminAgent                    │");
                    System.out.println(
                            "└─────────────────────────────────────────────────────┘");

                    // Ordonnance → Pharmacy
                    ACLMessage ordonnance =
                            new ACLMessage(ACLMessage.INFORM);
                    ordonnance.addReceiver(
                            new AID("Pharmacy", AID.ISLOCALNAME));
                    ordonnance.setContent(
                            "Nom:" + nom +
                                    " | Consultation:" + consult +
                                    " | Diagnostic:" + diagnostic +
                                    " | Médicament:" + medicament
                    );
                    send(ordonnance);

                    // Dossier → Admin
                    ACLMessage adminMsg =
                            new ACLMessage(ACLMessage.INFORM);
                    adminMsg.addReceiver(
                            new AID("Admin", AID.ISLOCALNAME));
                    adminMsg.setContent(
                            patientData + " | Diagnostic:" + diagnostic);
                    send(adminMsg);

                } else {
                    block();
                }
            }
        });
    }

    private String detectDiagnostic(String consultation) {
        switch (consultation.toLowerCase().trim()) {
            case "cardiologie":       return "Hypertension artérielle";
            case "neurologie":        return "Migraine chronique";
            case "orthopédie":
            case "orthopedie":        return "Fracture — bilan radiologique";
            case "pédiatrie":
            case "pediatrie":         return "Infection virale infantile";
            case "dermatologie":      return "Dermatite atopique";
            case "pneumologie":       return "Bronchite aiguë";
            case "gastroentérologie":
            case "gastroenterologie": return "Gastrite chronique";
            case "ophtalmologie":     return "Myopie — correction optique";
            case "urgence":           return "Trauma — bilan complet requis";
            default:                  return "Consultation générale — bilan standard";
        }
    }

    private String detectMedicament(String consultation) {
        switch (consultation.toLowerCase().trim()) {
            case "cardiologie":       return "Amlodipine 5mg";
            case "neurologie":        return "Sumatriptan 50mg";
            case "orthopédie":
            case "orthopedie":        return "Ibuprofène 400mg";
            case "pédiatrie":
            case "pediatrie":         return "Paracétamol Pédiatrique";
            case "dermatologie":      return "Hydrocortisone crème 1%";
            case "pneumologie":       return "Amoxicilline 500mg";
            case "gastroentérologie":
            case "gastroenterologie": return "Oméprazole 20mg";
            case "ophtalmologie":     return "Collyre Vitamine A";
            case "urgence":           return "Morphine 10mg IV";
            default:                  return "Paracétamol 1000mg";
        }
    }
}