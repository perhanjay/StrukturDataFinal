//import RBTree;
import java.util.ArrayList;
import java.util.List;

public class RBHashMap {

    // Array ini sekarang menyimpan OBJECT RBTree, bukan Node lagi.
    private RBTree[] buckets;
    private int capacity;

    public RBHashMap(int capacity) {
        this.capacity = capacity;
        // Inisialisasi array of RBTree
        this.buckets = new RBTree[capacity];
    }

    private int getHash(String key) {
        int hash = 0;
        int prime = 31;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash * prime) + key.charAt(i);
        }
        return Math.abs(hash) % capacity;
    }

    public void put(String key, String value, Gimmick gimmick) {
        int index = getHash(key);

        // Lazy Initialization:
        // Hanya buat objek pohon jika bucket tersebut benar-benar mau dipakai
        if (buckets[index] == null) {
            buckets[index] = new RBTree();
        }

        // Delegasikan tugas insert ke objek RBTree di bucket tersebut
        buckets[index].insert(key, value, gimmick);
    }

    public SearchResult get(String key) {
        int index = getHash(key);

        if (buckets[index] == null) {
            return null; // Bucket kosong
        }

        // Delegasikan tugas pencarian ke objek RBTree
        return buckets[index].search(key);
    }

    public void printMap() {
        System.out.println("\n=== VISUALISASI KAMUS ===");
        for (int i = 0; i < capacity; i++) {
            if (buckets[i] != null) {
                System.out.println("Bucket [" + i + "]:");
                // Memanggil fungsi print milik RBTree
                buckets[i].printStructure("");
            }
        }
        System.out.println("=========================\n");
    }

    public List<Suggestion> getSuggestions(String prefix, int limit) {
        List<Suggestion> results = new ArrayList<>();
        
        // Loop ke SEMUA bucket (Laci)
        for (int i = 0; i < capacity; i++) {
            if (buckets[i] != null) {
                // Suruh setiap pohon di laci untuk mencari kata berawalan prefix
                buckets[i].collectSuggestions(prefix, results, limit);
                
                // Jika sudah dapat cukup banyak (misal 10), berhenti agar tidak berat
                if (results.size() >= limit) break;
            }
        }
        
        // Opsional: Sort hasil akhir secara alfabetis (karena hashmap tidak urut)
        results.sort((a, b) -> a.word().compareTo(b.word()));
        
        return results;
    }

}