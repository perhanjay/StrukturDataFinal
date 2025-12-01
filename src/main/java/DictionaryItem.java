// Class sederhana untuk mencocokkan struktur JSON
public class DictionaryItem {
    private String word;
    private String definition;
    private String gimmick; // Masih String, nanti dikonversi

    // Constructor Kosong (Wajib untuk Jackson)
    public DictionaryItem() {}

    // Getter dan Setter (Wajib untuk Jackson)
    public String getWord() { return word; }
    public void setWord(String word) { this.word = word; }

    public String getDefinition() { return definition; }
    public void setDefinition(String definition) { this.definition = definition; }

    public String getGimmick() { return gimmick; }
    public void setGimmick(String gimmick) { this.gimmick = gimmick; }
}