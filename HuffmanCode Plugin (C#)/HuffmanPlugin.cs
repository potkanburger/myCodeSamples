using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Huffman;

namespace HuffmanPlugin
{
    public class HuffmanPlugin : MarshalByRefObject, Huffman.IPlugin
    {
        
        bool Huffman.IPlugin.Compress(ref Huffman.HuffmanData hf){
            try
            {
                TableFq tf = new TableFq(hf.uncompressedData); // Création de la table de frèquence à partir des données d'entrée
                BinaryTree bnTree = new BinaryTree(); // nouvel arbre binaire vide
                bnTree.feedTree(tf);  // initialisation de l'arbre avec la table de fréquence
                bnTree.computeTree(); //génération de l'arbre binaire final à partir des données qu'il contient
                HuffmanCode hfCode = new HuffmanCode();
                hfCode = bnTree.createHuffman(); //génération d'un code huffman pour chaque byte différent des données d'entrée à l'aide de l'arbre binaire (stockage dans un dictionnaire) 

                hf.sizeOfUncompressedData = hf.uncompressedData.Length; 
                hf.frequency = tf.getTable().ToList();
                hf.compressedData = hfCode.concat(hf.uncompressedData); /* compression à l'aide du code de Huffman. Stockage dans la structure HuffmanData 
                                                                         qui est échangée entre le plugin et le programme par le biais de MarshalByRefObject */                                                                                                 
            }
            catch (Exception e)
            {
                return false; // la compression a échouée si une exception est levée
            }
            return true;
        }

        bool Huffman.IPlugin.Decompress(ref Huffman.HuffmanData hf){

            
            try
            {
                TableFq tf = new TableFq(hf.frequency); // Création de la table de frèquence à partir des données d'entrée
                BinaryTree bnTree = new BinaryTree(); // nouvel arbre binaire vide
                bnTree.feedTree(tf); // initialisation de l'arbre avec la table de fréquence
                bnTree.computeTree(); //génération de l'arbre binaire final à partir des données qu'il contient
                HuffmanCode hfCode = new HuffmanCode();
                hfCode = bnTree.createHuffman(); //génération d'un code huffman pour chaque byte différent des données d'entrée à l'aide de l'arbre binaire (stockage dans un dictionnaire) 

                hf.uncompressedData = hfCode.decompress(hf.compressedData, hf.sizeOfUncompressedData);  /* décompression à l'aide du code de Huffman. Stockage dans la structure HuffmanData 
                                                                                                           qui est échangée entre le plugin et le programme par le biais de MarshalByRefObject */
            }
            catch (Exception e)
            {
                return false; // la décompression a échouée si une exception est levée
            }
            return true;

        }

        string Huffman.IPlugin.PluginName{
            get{
                return "GenouxHuTual_plugin"; // Nom du plugin
            }
        }

    }
}
