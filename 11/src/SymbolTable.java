import java.util.HashMap;

public class SymbolTable {
    HashMap<String, SymbolRecord> classTable;
    HashMap<String, SymbolRecord> subroutineTable;


    public SymbolTable(){
        classTable = new HashMap<>();
        subroutineTable = new HashMap<>();
    }

    public void printClassTables(String className) {
        System.out.println("=== " + className + " Class Table ===");
        classTable.forEach((key, record) -> {
            // 객체를 그냥 출력하지 않고 직접 만든 메서드(.getInfo()) 호출
            System.out.println("Name: " + key + " -> " + record.getInfo());
        });
    }

    public void printSubroutineTables(String subroutineName) {
        System.out.println("=== " + subroutineName +  " subroutine Table ===");
        subroutineTable.forEach((key, record) -> {
            // 객체를 그냥 출력하지 않고 직접 만든 메서드(.getInfo()) 호출
            System.out.println("Name: " + key + " -> " + record.getInfo());
        });
    }

    public void reset(){
        subroutineTable.clear();
    }

    public void define(String name, String type, String kind){
        if(kind.equals("static") | kind.equals("field")){
            int index = varCount(kind);
            SymbolRecord record = new SymbolRecord(type, kind, index);
            classTable.put(name, record);
        }

        if(kind.equals("arg") | kind.equals("var")){
            int index = varCount(kind);
            SymbolRecord record = new SymbolRecord(type, kind, index);
            subroutineTable.put(name, record);
        }
    }


    public int varCount(String kind){
        int result = 0;
        if(kind.equals("static") | kind.equals("field")){
            for(SymbolRecord record: classTable.values()){
                if(record.kind.equals(kind)){
                    result += 1;
                }
            }
        }

        if(kind.equals("arg") | kind.equals("var")){
            for(SymbolRecord record: subroutineTable.values()){
                if(record.kind.equals(kind)){
                    result += 1;
                }
            }
        }
        return result;
    }

    public String kindOf(String name){
        String result;
        if(classTable.containsKey(name)){
            result = classTable.get(name).kind;
        } else if (subroutineTable.containsKey(name)){
            result = subroutineTable.get(name).kind;
        } else {
            result = "NONE";
        }
        return result;
    }

    public String typeOf(String name){
        String result;
        if(classTable.containsKey(name)){
            result = classTable.get(name).type;
        } else if (subroutineTable.containsKey(name)){
            result = subroutineTable.get(name).type;
        } else {
            result = "NONE";
        }
        return result;
    }

    public int indexOf(String name){
        int result;
        if(classTable.containsKey(name)){
            result = classTable.get(name).index;
        } else if (subroutineTable.containsKey(name)){
            result = subroutineTable.get(name).index;
        } else {
            result = -1;
        }
        return result;
    }

    public String[] nameToSegment(String name){
        String segment;
        String kind = kindOf(name);
        int index = indexOf(name);
        if(!kind.equals("NONE")){
            segment = switch (kind) {
                case "arg" -> "argument";
                case "var" -> "local";
                case "field" -> "this";
                default -> "static";
            };
        } else {
            segment = "error";
        }
        return new String[]{segment, String.valueOf(index)};
    }


}
