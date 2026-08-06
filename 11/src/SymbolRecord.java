public class SymbolRecord {
    String type;
    String kind;
    int index;

    public SymbolRecord(String type, String kind, int index){
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    public String getInfo() {
        return "type='" + type + "', kind='" + kind + "', index=" + index;
    }
}
