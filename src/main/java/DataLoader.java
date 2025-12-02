import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.IOException;
import java.util.List;

public class DataLoader {

    public static void loadFromJSON(RBHashMap dictionary) {
        // 1. Siapkan Mapper (Alat pembaca JSON)
        ObjectMapper mapper = new ObjectMapper();

        // 2. Ambil file dari folder resources
        InputStream inputStream = DataLoader.class.getResourceAsStream("/data.json");

        if (inputStream == null) {
            System.err.println("GAGAL: File data.json tidak ditemukan di resources!");
            return;
        }

        try {
            // 3. Baca JSON dan ubah menjadi List of DictionaryItem
            List<DictionaryItem> items = mapper.readValue(
                inputStream, 
                new TypeReference<List<DictionaryItem>>(){}
            );

            // 4. Masukkan satu per satu ke RBHashMap
            for (DictionaryItem item : items) {
                // Konversi String gimmick (dari JSON) ke Object Gimmick (dari Library)
                Gimmick realGimmick = getGimmickByName(item.getGimmick());
                
                // Panggil method put milik RBHashMap Anda
                dictionary.put(item.getWord(), item.getDefinition(), realGimmick);
            }
            
            System.out.println("SUKSES: " + items.size() + " kata berhasil dimuat dari JSON.");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("GAGAL membaca file JSON.");
        }
    }

    // Helper untuk menerjemahkan teks "ROTATE" menjadi kode animasi
    private static Gimmick getGimmickByName(String name) {
        if (name == null) return null;
        
        switch (name.toUpperCase()) {
            case "ROTATE": return GimmickLibrary.ROTATE;
            case "SHAKE":  return GimmickLibrary.SHAKE;
            case "FADE":   return GimmickLibrary.FADE;
            case "OPEN_CALCULATOR": return GimmickLibrary.OPEN_CALCULATOR;
            case "PULSE":  return GimmickLibrary.PULSE;
            case "FLIP":   return GimmickLibrary.FLIP;
            case "DISCO":  return GimmickLibrary.DISCO;
            case "BOUNCE": return GimmickLibrary.BOUNCE;
            case "OPEN_NOTEPAD": return GimmickLibrary.OPEN_NOTEPAD;
            case "BLUR":   return GimmickLibrary.BLUR;
            case "WOBBLE": return GimmickLibrary.WOBBLE;
            case "JELLO":  return GimmickLibrary.JELLO;
            case "JUMPSCARE": return GimmickLibrary.JUMPSCARE;
            case "SHOW_BURUNG": return GimmickLibrary.SHOW_BURUNG;
            case "OPEN_PAINT":   return GimmickLibrary.OPEN_PAINT;
            default: return null; // Jika "NONE" atau typo, return null
        }
    }
}