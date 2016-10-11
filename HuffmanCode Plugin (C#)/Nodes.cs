using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace HuffmanPlugin
{

    // Noeuds: valeur nominale ou feuille terminale [paire clé-valeur]. Potentiels noeuds-fils, dans une liste.
    class Nodes
    {
        private KeyValuePair<byte, int> _valPair;
        private int _value;
        private Boolean _hasKey;
        private List<Nodes> _children;

        public Nodes()
        {
            _value = 0;
            _hasKey = false;
            _children = new List<Nodes>();
        }

        public Nodes(KeyValuePair<byte, int> val)
        {
            _valPair = val;
            _hasKey = true;
            _children = new List<Nodes>();
        }

        public Nodes(int val)
        {
            _value = val;
            _hasKey = false;
            _children = new List<Nodes>();
        }

        public bool isKeyValuePair()
        {
            return _hasKey;
        }

        public bool hasChildren()
        {
            return _children.Count >= 1;
        }

        public int getValue()
        {
            return (_hasKey) ? _valPair.Value : _value;
        }

        public KeyValuePair<byte, int> getKeyValuePair()
        {
            return (_hasKey) ? _valPair : new KeyValuePair<byte, int>();
        }

        public Nodes min(Nodes n1, Nodes n2)
        {
            int val1 = n1.getValue();
            int val2 = n2.getValue();
            return (val1<=val2)? n1 : n2;
        }

        public Nodes max(Nodes n1, Nodes n2)
        {
            int val1 = n1.getValue();
            int val2 = n2.getValue();
            return (val1 > val2) ? n1 : n2;
        }
  

        public void addChild(Nodes nd)
        {
            _children.Add(nd);
        }

        public void setChildren(List<Nodes> ln)
        {
            _children = ln;
        }

        public List<Nodes> getChildren()
        {
            return _children;
        }

        public override string ToString()
        {
            if (_hasKey)
            {
                return "[" + _valPair.Key.ToString() + ": " + _valPair.Value.ToString() + "]";
            }else
            {
                return "[" + _value.ToString() +"]";
            }
        }

    }
}
