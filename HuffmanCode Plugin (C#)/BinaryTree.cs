using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HuffmanPlugin
{
    // Arbre de noeuds

    class BinaryTree
    {
        List<Nodes> _tree; 

        public BinaryTree()
        {
            _tree = new List<Nodes>();
        }


        public void feedTree(TableFq tfq) // créee un arbre à partir d'une table de frèquence
        {
            _tree.Clear();
            foreach(KeyValuePair<byte, int> kVal in tfq.getTable())
            {
                _tree.Add(new Nodes(kVal));
            }
        }

        public bool ordered(Nodes n1, Nodes n2) // Vrai si la valeur du premier noeud est inférieure ou égale à celle du second
        {
            int val1 = n1.getValue();
            int val2 = n2.getValue();
            return (val1 <= val2) ? true : false;
        }


        /*crée un nouveau noeud à partir des 2 plus petits noeuds de l'arbre. La valeur du noeud est la somme des valeurs de ces deux noeuds. 
        * Ces deux noeuds seront désormais les fils du nouveau noeud crée, positionné à leur place dans l'arbre. 
        */
        public void createfromTwoSmaller()
        {

            Nodes n1 = new Nodes();
            Nodes n2 = new Nodes();
            int count = 0;
            List<Nodes> res = new List<Nodes>();
            foreach (Nodes nt in _tree)
            {
                if (count == 0)
                {
                    n1 = nt;
                }
                else if (count == 1)
                {
                    if (ordered(n1, nt))
                    {
                        n2 = nt;
                    }
                    else
                    {
                        n2 = n1;
                        n1 = nt;
                    }
                }
                else
                {
                    if (ordered(nt, n1))
                    {
                        n2 = n1;
                        n1 = nt;
                    }
                    else if (ordered(nt, n2))
                    {
                        n2 = nt;
                    }
                }
                count++;
            }

            res.Add(n1);
            res.Add(n2);
            Nodes nd = new Nodes(n1.getValue() + n2.getValue());
            nd.setChildren(res);
            _tree.Remove(n1);
            _tree.Remove(n2);
            _tree.Add(nd);

        }

        public void computeTree()
        { //Tant qu'il y a plusieurs noeuds, on remplace les deux plus petits noeuds par l'arbre binaire pondéré de ces deux noeuds.
            while (_tree.Count > 1)
            {
                createfromTwoSmaller();
            }
        }

        public override string ToString()
        {
            return recursiveToString(_tree);
        }


        // Création du code de Huffman à partir de l'arbre binaire
        public HuffmanCode createHuffman()
        {
            HuffmanCode hC = new HuffmanCode();
            byte b = new byte();
            hC.setCode(recursiveHuffman(hC.getCode(), _tree, b, 0));
            return hC;
        }

        // fonction récursive pour créer le code de Huffman, on remplit un même dictionnaire au fur et à mesure du parcours des éléments de l'arbre
        // Le dictionnaire associe chaque byte [chaine d'entrée] a un couple(byte,int) qui correspond à la compression huffman de ce byte et sa longueur en bits.
        private Dictionary<byte, Tuple<byte, int>> recursiveHuffman(Dictionary<byte, Tuple<byte, int>> dicoHuffman, List<Nodes> nodeList, byte b, int len)
        {
            Dictionary<byte, Tuple<byte, int>> resHuf = dicoHuffman;
            if (nodeList.Count > 1)
            {
                Nodes nd1 = nodeList.ElementAt(0);
                Nodes nd2 = nodeList.ElementAt(1);
                Tuple<byte, int> t;
                byte nb = (byte)(b << 1); 
                if (nd1.hasChildren())
                {
                    resHuf = recursiveHuffman(resHuf, nd1.getChildren(), (byte)(nb & ~1), len+=1);
                }else
                {
                    if (nd1.isKeyValuePair())
                    {
                        t = new Tuple<byte, int>((byte)(nb & ~1), len+=1);
                        resHuf.Add(nd1.getKeyValuePair().Key, t);
                    }
                }

                if (nd2.hasChildren())
                {
                    recursiveHuffman(resHuf, nd2.getChildren(), (byte)(nb | 1), len++);
                }
                else
                {
                    if (nd2.isKeyValuePair())
                    {
                        t = new Tuple<byte, int>((byte)(nb | 1), len++);
                        resHuf.Add(nd2.getKeyValuePair().Key, t);
                    }
                }                
            }else if (nodeList.Count == 1)
            {
                Nodes n = nodeList.ElementAt(0);
                if (n.hasChildren())
                {
                    recursiveHuffman(resHuf, n.getChildren(), b, len);
                }
                else if (len == 0)
                {
                    Tuple<byte, int> t = new Tuple<byte, int>((byte)((b << 1) & ~1), len += 1);
                    resHuf.Add(n.getKeyValuePair().Key, t);
                }
            }

            return resHuf;
        }

        private string recursiveToString(List<Nodes> nodeList)
        {
            string s = "";
            string childS = "";
            if (nodeList.Count > 0)
            {
                foreach (Nodes nd in nodeList)
                {
                    s += nd.ToString();
                    if (nd.hasChildren())
                    {
                        childS += recursiveToString(nd.getChildren())+" ";
                    }
                }
                if (childS.Length > 0)
                {
                    s += "\n";
                }
                s += childS;
            }
            return s;
        }
    }


}
