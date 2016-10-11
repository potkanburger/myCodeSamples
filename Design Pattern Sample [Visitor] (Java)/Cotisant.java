package question1;
import java.util.List;

public abstract class Cotisant {

    protected String nom;
    protected Cotisant parent;

    public Cotisant(String nom){this(nom,null);}
    public Cotisant(String nom,Cotisant parent){this.nom=nom;this.parent=parent;}

    public abstract void debit(int somme) throws SoldeDebiteurException;
    public abstract void credit(int somme);
    public abstract int solde();
    public abstract int nombreDeCotisants();
    
    public void affecterSolde(int somme){};
    
    public List<Cotisant> getChildren(){return null;}

    public String nom(){return nom;}
    public boolean equals(Object o){return nom.equals(((Cotisant)o).nom);}
    public void setParent(Cotisant parent){this.parent=parent;}
    public Cotisant getParent(){return parent;}

    public abstract <T> T accepter(Visiteur<T> visiteur);
}
