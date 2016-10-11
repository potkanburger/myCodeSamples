using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HuffmanPlugin
{
    class HuffmanCode
    {
        private Dictionary<byte, Tuple<byte, int>> _huffman;

        public HuffmanCode()
        {
            _huffman = new Dictionary<byte, Tuple<byte, int>>();
        }

        public override string ToString()
        {
            string s = "";
            foreach (KeyValuePair<byte, Tuple<byte, int>> elm in _huffman)
            {
                s += "[" + elm.Key.ToString() + ": ";
                byte tmp;
                for(int i= elm.Value.Item2-1; i>=0; i--)
                {
                    tmp = (byte) (elm.Value.Item1 >> i);
                    tmp = (byte) (tmp & 1); 
                    s += tmp.ToString();
                }
                s += "] ";
            }
            return s;
        }

        public Dictionary<byte, Tuple<byte, int>> getCode()
        {
            return _huffman;
        }

        public void setCode(Dictionary<byte, Tuple<byte, int>> huffman)
        {
            _huffman = huffman;
        }

        public byte[] concat(byte[] str)
        {
            byte[] concatenation = new byte[str.Length]; //la taille de la concaténation par code Huffman sera nécessairement inférieure à la taille de la string
            int count = 0;
            int curBits = 0;
            byte curByte = (byte) 0;
            byte tmp;
            /* pour chaque byte de l'entrée, on ajoute les bits correspondant à son code de huffman au résultat en cours, 
             * à l'aide de la valeur du couple (byte huffman, longueur) qui correspond au byte de l'entrée en temps que clé dans le dictionnaire de Huffmann
             */
            foreach(byte c in str){                
                for(int i = _huffman[c].Item2-1; i >= 0; i--){
                    if(curBits>=8){
                        concatenation[count] = curByte;
                        count += 1;
                        curByte = (byte) 0;
                        curBits = 0;
                    }
                   
                    curByte = (byte) (curByte << 1);
                    curBits++;
                    tmp = (byte) ((_huffman[c].Item1 >> i) & 1);
                    curByte = (byte) (curByte | tmp);
                }
                
            }

            if (curBits > 0)
            {
                curByte = (byte)(curByte << (8 - curBits));           
                concatenation[count] = curByte;
                count+=1;
            }

            byte[] result = new byte[count];
            for (int i = 0; i < count; i++)
            {
                result[i] = concatenation[i];
            }

            return result;
        }

        //Décompression d'un tableau de byte, pour un résultat d'une longueur dataLength
        public byte[] decompress(byte[] concat, int dataLength)
        {
            byte[] s = new byte[dataLength];
            bool b;

            // Création d'un dictionnaire de décompression, dont la clé est la longueur du code Huffman [nombre de bits], 
            // et la valeur est un Dictionnaire dont la clé est le byte compressé par Huffman et la valeur le byte décompressé

            Dictionary<int, Dictionary<byte, byte>> decompressDico = new Dictionary<int, Dictionary<byte, byte>>();
            foreach (byte c in _huffman.Keys)
            {
                if (!decompressDico.ContainsKey(_huffman[c].Item2))
                {
                    decompressDico.Add(_huffman[c].Item2, new Dictionary<byte, byte>());
                }
                decompressDico[_huffman[c].Item2].Add(_huffman[c].Item1, c);           
            }

            // la décompression s'effectuera sur un tableau de bits, l'utilisation de booléens permet de simplifier les opérations.
            bool[] boolTest = new bool[concat.Length * 8];
            byte loaded = (byte)0;
            for (int i = 0; i < concat.Length; i++)
            {
                for (int j = 0; j < 8; j++)
                {
                    loaded = (byte) (concat[i] >> (7 - j));
                    loaded = (byte)(loaded & 1);
                    if(loaded.Equals(0)){
                        b = false;
                    }else{
                        b = true;
                    }
                    boolTest[i * 8 + j] = b;
                }   
            }
            int countBits = 0;
            byte testByte = (byte)0;
            int decryptedValues = 0;

            try
            {
                 for (int i = 0; i < concat.Length*8; i++) //parcours de l'ensemble des bits du tableau de booléens à décompresser
                 {
                    // le bit est ajouté à la suite de la séquence de bits en cours
                    testByte = (byte)(testByte << 1);
                    if (boolTest[i])
                        testByte = (byte)(testByte | 1);
                    else
                        testByte = (byte)(testByte & ~1);
                    
                    countBits++;
                    
                    // Si une séquence de cette longueur est un code Huffman, puis si ce code de Huffman est référencé dans notre dictionnaire de décompression, 
                    // alors on ajoute la décompression de cette séquence au résultat actuel, et si on a pas atteint la longueur de résultat attendue, on continue avec la séquence suivante
                    if (decompressDico.ContainsKey(countBits))
                    {
                        if (decompressDico[countBits].ContainsKey(testByte))
                        {
                            s[decryptedValues] = decompressDico[countBits][testByte];
                            decryptedValues += 1;
                            countBits = 0;
                            testByte = (byte)0;
                            if(decryptedValues>=dataLength){
                                break;
                            }
                        }
                    }

                }
            }
            catch(Exception e)
            {
                Console.Write(e.ToString());
            }

                 
            return s;
            
        }
    }
}
