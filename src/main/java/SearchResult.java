public class SearchResult {
    public String key;
    public String definition;
    public String foreignKey;
    public String foreignDefinition;
    public Gimmick gimmick;

    public SearchResult(String key, String definition, String foreignKey, String foreignDefinition, Gimmick gimmick){
        this.key = key;
        this.definition = definition;
        this.foreignKey = foreignKey;
        this.foreignDefinition = foreignDefinition;
        this.gimmick = gimmick;
    }
}
