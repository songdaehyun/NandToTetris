import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    Map<String, Integer> map = new HashMap<>();

    public SymbolTable() {
        for(int i = 0; i < 16; i++){
            String s = String.valueOf(i);
            map.put("R" + s, i);
        }
        map.put("SCREEN", 16384);
        map.put("KBD", 24576);
        map.put("SP", 0);
        map.put("LCL", 1);
        map.put("ARG", 2);
        map.put("THIS", 3);
        map.put("THAT", 4);
    }

    public void addEntry(String symbol, int address) {
        map.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return map.containsKey(symbol);
    }

    public int getAddress(String symbol){
        return map.get(symbol);
    }
}
