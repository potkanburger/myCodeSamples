package question1;


public class Contributeur extends Cotisant{
  private int solde;
  
  public Contributeur(String nom, int somme){
    super(nom);
    affecterSolde(somme);
  }
  
  public int solde(){
    return this.solde;
}
  
  public int nombreDeCotisants(){
    return 1;
  }
    public void debit(int somme) throws SoldeDebiteurException{
       if(somme<0) throw new RuntimeException("nombre n�gatif !!!");
       int tmp = this.solde-somme;
       if(tmp<0) throw new SoldeDebiteurException();
       this.solde = tmp;
    }
    
    /**
     * throws RuntimeException new RuntimeException("nombre n�gatif !!!");
     */
  public  void credit(int somme){
      if(somme<0) throw new RuntimeException("nombre n�gatif !!!");
      this.solde+=somme;
    }
    
    /**
     * throws RuntimeException new RuntimeException("nombre n�gatif !!!");
     */
  public void affecterSolde(int somme){
    if(somme <0) throw new RuntimeException("nombre n�gatif !!!");
    try{
      debit(solde()); credit(somme);// mode �l�gant ... 
    }catch(SoldeDebiteurException sde){ 
      // exception peu probable
      this.solde = somme; // mode efficace ...
    }
  }
  
  public <T> T accepter(Visiteur<T> visiteur){
    return visiteur.visite(this);
  }
  
  public String toString(){
    return "<Contributeur : " + nom + "," + solde + ">";
  }

}
