package villagegaulois;

import personnages.Chef;
import personnages.Gaulois;

public class Village {
	private String nom;
	private Chef chef;
	private Gaulois[] villageois;
	private int nbVillageois = 0;
	private Marche marche;

	public Village(String nom, int nbVillageoisMaximum, int nbEtals) {
		this.nom = nom;
		villageois = new Gaulois[nbVillageoisMaximum];
		marche = new Marche(nbEtals);
	}

	private static class Marche {
		private Etal[] etals;

		private Marche(int nbEtals) {
			etals = new Etal[nbEtals];

			// Initialiser le tableau d'étals :
			for (int i = 0; i < nbEtals; i++) {
				etals[i] = new Etal();
			}
		}

		private void utiliserEtals(int indiceEtal, Gaulois vendeur, String produit, int nbProduit) {
			Etal etalAOccuper = etals[indiceEtal];

			etalAOccuper.occuperEtal(vendeur, produit, nbProduit);
		}

		private int trouverEtalLibre() {
			for (int i = 0; i < etals.length; i++) {
				if (!etals[i].isEtalOccupe()) {
					return i;
				}
			}
			return -1;
		}

		private Etal[] trouverEtals(String produit) {
			// Compter le nombre d'étals vendant le produit "produit" :
			int nbEtalsVendantProduit = 0;
			for (int i = 0; i < etals.length; i++) {
				if (etals[i].contientProduit(produit)) {
					nbEtalsVendantProduit++;
				}
			}

			// Créer le tableau contenant exactement n étals vendant le produit "produit" :
			Etal[] etalsVendantProduit = new Etal[nbEtalsVendantProduit];
			for (int i = 0, j = 0; (nbEtalsVendantProduit != j) && (i < etals.length); i++) {
				// "j" correspond a l'indice du j-ième étal vendant le produit "produit".
				if (etals[i].contientProduit(produit)) {
					etalsVendantProduit[j] = etals[i];
					j++;
				}
			}

			return etalsVendantProduit;
		}

		private Etal trouverVendeur(Gaulois gaulois) {
			for (int i = 0; i < etals.length; i++) {
				if (etals[i].getVendeur() == gaulois) {
					return etals[i];
				}
			}
			return null;
		}

		private String afficherMarche() {
			StringBuilder chaine = new StringBuilder();
			int nbEtalVide = 0;

			// Récupérer tous les étals occupés dans le village :
			for (int i = 0; i < etals.length; i++) {
				if (etals[i].isEtalOccupe()) {
					chaine.append("- " + etals[i].afficherEtal());
				} else {
					nbEtalVide++;
				}
			}

			// Afficher (potentiellement) le nombre d'étals vides restants sur le marché :
			if (nbEtalVide != 0) {
				chaine.append("Il reste " + nbEtalVide + " étals non utilisés dans le marché.\n");
			}

			return chaine.toString();
		}
	}

	public String getNom() {
		return nom;
	}

	public void setChef(Chef chef) {
		this.chef = chef;
	}

	public void ajouterHabitant(Gaulois gaulois) {
		if (nbVillageois < villageois.length) {
			villageois[nbVillageois] = gaulois;
			nbVillageois++;
		}
	}

	public Gaulois trouverHabitant(String nomGaulois) {
		if (nomGaulois.equals(chef.getNom())) {
			return chef;
		}
		for (int i = 0; i < nbVillageois; i++) {
			Gaulois gaulois = villageois[i];
			if (gaulois.getNom().equals(nomGaulois)) {
				return gaulois;
			}
		}
		return null;
	}

	public String afficherVillageois() throws VillageSansChefException {
		if (chef == null) {
			throw new VillageSansChefException("Le village n'a pas de chef.");
		}

		StringBuilder chaine = new StringBuilder();
		if (nbVillageois < 1) {
			chaine.append("Il n'y a encore aucun habitant au village du chef " + chef.getNom() + ".\n");
		} else {
			chaine.append("Au village du chef " + chef.getNom() + " vivent les légendaires gaulois :\n");
			for (int i = 0; i < nbVillageois; i++) {
				chaine.append("- " + villageois[i].getNom() + "\n");
			}
		}
		return chaine.toString();
	}

	public String installerVendeur(Gaulois vendeur, String produit, int nbProduit) {
		StringBuilder chaine = new StringBuilder(
				vendeur.getNom() + " cherche un endroit pour vendre " + nbProduit + " " + produit + ".\n");

		int indiceEtal = marche.trouverEtalLibre();
		if (indiceEtal != -1) {
			marche.utiliserEtals(indiceEtal, vendeur, produit, nbProduit);
			chaine.append("Le vendeur " + vendeur.getNom() + " vend des " + produit + " à l'étal n° " + (indiceEtal + 1)
					+ ".\n");
		} else {
			chaine.append("Le vendeur " + vendeur.getNom() + " n'a pas trouvé d'étal libre.");
		}

		return chaine.toString();
	}

	public String rechercherVendeursProduit(String produit) {
		StringBuilder chaine = new StringBuilder();

		Etal[] etalsVendantProduit = marche.trouverEtals(produit);
		int nbEtalsVendantProduit = etalsVendantProduit.length;

		switch (nbEtalsVendantProduit) {
		case 0:
			chaine.append("Il n'y a pas de vendeur qui propose des " + produit + " au marché.\n");
			break;
		case 1:
			Gaulois vendeur = etalsVendantProduit[0].getVendeur();
			chaine.append("Seul le vendeur " + vendeur.getNom() + " propose des " + produit + " au marché.\n");
			break;
		default:
			chaine.append("Les vendeurs qui proposent des " + produit + " sont :\n");
			for (int i = 0; i < nbEtalsVendantProduit; i++) {
				vendeur = etalsVendantProduit[i].getVendeur();
				chaine.append("- " + vendeur.getNom() + "\n");
			}
			break;
		}

		return chaine.toString();
	}

	public Etal rechercherEtal(Gaulois vendeur) {
		return marche.trouverVendeur(vendeur);
	}

	public String partirVendeur(Gaulois vendeur) {
		StringBuilder chaine = new StringBuilder();

		Etal etalALiberer = rechercherEtal(vendeur);
		etalALiberer.libererEtal();

		return chaine.toString();
	}

	public String afficherMarche() {
		StringBuilder chaine = new StringBuilder("Le marché du village \"" + nom + "\" possède plusieurs étals :\n");

		chaine.append(marche.afficherMarche());

		return chaine.toString();
	}
}
