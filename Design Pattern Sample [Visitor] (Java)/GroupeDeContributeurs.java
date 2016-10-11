package question1;

import java.util.Iterator;
import java.util.NoSuchElementException;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

public class GroupeDeContributeurs extends Cotisant implements Iterable<Cotisant>{
  private List<Cotisant> liste;
  
  public GroupeDeContributeurs(String nomDuGroupe){
    super(nomDuGroupe);
    this.liste = new ArrayList<Cotisant>();
  }
  
  public void ajouter(Cotisant cotisant){
    Cotisant c = cotisant;
    c.setParent(this);
    this.liste.add(c);
  }
  
  
  public int nombreDeCotisants(){
    int nombre = 0;
    Iterator<Cotisant> it = this.liste.iterator();
    while(it.hasNext()){
        Cotisant c = it.next();
        nombre+=c.nombreDeCotisants();
    }
    return nombre;
  }
  
  public String toString(){
    String str = new String();
    Iterator<Cotisant> it = this.liste.iterator();
    int i=0;
    while(it.hasNext()){
        Cotisant c = it.next();
        str+=c.nom() +" ";
        i++;
    }
    if(i!=0){
        str = str.substring(0, str.length()-2);
    }
    return str;
  }
  
  public List<Cotisant> getChildren(){
   return this.liste;
  }
  
  public void debit(int somme) throws SoldeDebiteurException{
    Iterator<Cotisant> it = this.liste.iterator();
    while(it.hasNext()){
        Cotisant c = it.next();
        try{
            c.debit(somme);
        }catch(SoldeDebiteurException e){
            throw e;
        }
    }
  }
  
  public void credit(int somme){
    Iterator<Cotisant> it = this.liste.iterator();
    while(it.hasNext()){
        Cotisant c = it.next();
        c.credit(somme);
    }
  }
  
  public int solde(){
    int solde = 0;
    for (Cotisant c : liste){ 
        solde += c.solde(); 
    }
    return solde;
  }
  
  // méthodes fournies
  
 public Iterator<Cotisant> iterator(){
    return new GroupeIterator(liste.iterator());
  }
  
  private class GroupeIterator implements Iterator<Cotisant>{
    private Stack<Iterator<Cotisant>> stk;
    
    public GroupeIterator(Iterator<Cotisant> iterator){
      this.stk = new Stack<Iterator<Cotisant>>();
      this.stk.push(iterator);
    }
    
    public boolean hasNext(){
      if(stk.empty()){
        return false;
      }else{
         Iterator<Cotisant> iterator = stk.peek();
         if( !iterator.hasNext()){
           stk.pop();
           return hasNext();
         }else{
           return true;
         }
      }
    }
    
    public Cotisant next(){
      if(hasNext()){
        Iterator<Cotisant> iterator = stk.peek();
        Cotisant cpt = iterator.next();
        if(cpt instanceof GroupeDeContributeurs){
          GroupeDeContributeurs gr = (GroupeDeContributeurs)cpt;
          stk.push(gr.liste.iterator());
        }
        return cpt;
      }else{
        throw new NoSuchElementException();
      }
    }
    public void remove(){
      throw new UnsupportedOperationException();
    }
  }
  

  public <T> T accepter(Visiteur<T> visiteur){
    return visiteur.visite(this);
  }
  

}
