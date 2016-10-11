using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HuffmanPlugin
{
    // Table de frèquence [Dictionnaire byte(key), int(nombre d'occurences)]
    class TableFq
    {
        Dictionary<byte, int> _elements;

        public TableFq()
        {
            _elements = new Dictionary<byte, int>();
        }

        public TableFq(byte[] initString)
        {
            _elements = new Dictionary<byte, int>();
            this.fillTable(initString);
        }

        public TableFq(List<KeyValuePair<byte, int>> initFqTable)
        {
            _elements = new Dictionary<byte, int>();
            this.fillTable(initFqTable);
        }


        public void fillTable(params KeyValuePair<byte, int>[] valuePairs)
        {
            foreach (KeyValuePair<byte, int> valuePair in valuePairs)
            {
                if(!_elements.Keys.Contains(valuePair.Key))
                _elements.Add(valuePair.Key, valuePair.Value);
            }
        }

        public void fillTable(byte[] text)
        {
            foreach (byte c in text)
            {
                if (!_elements.ContainsKey(c))
                {
                    _elements.Add(c, 1);
                }
                else
                {
                    _elements[c] += 1;
                }
            }
        }

        public void fillTable(List<KeyValuePair<byte, int>> valuePairs){
             foreach (KeyValuePair<byte, int> valuePair in valuePairs)
            {
                if(!_elements.Keys.Contains(valuePair.Key))
                _elements.Add(valuePair.Key, valuePair.Value);
            }
        }

        public override string ToString()
        {
            string s = "";
            foreach(KeyValuePair<byte, int> elm in _elements)
            {
                s += "["+elm.Key.ToString() +": "+ elm.Value.ToString()+"] ";
            }
            return s;
        }

        public Dictionary<byte, int> getTable()
        {
            return _elements;
        }
    }
}
