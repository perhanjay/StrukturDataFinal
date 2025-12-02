# 📚 Smart Dictionary & Gimmick App

<div align="center">

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-23.0.1-4285F4?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)

**Aplikasi Kamus Modern dengan Implementasi Struktur Data Red-Black Tree & Kejutan Interaktif.**

[View Demo] . [Report Bug] . [Request Feature]

</div>

---

## 📖 Tentang Proyek

**Smart Dictionary** bukan sekadar aplikasi kamus biasa. Proyek ini dibangun sebagai Tugas Akhir mata kuliah **Struktur Data** untuk mendemonstrasikan implementasi struktur data pohon biner seimbang (*Self-Balancing Binary Search Tree*) dalam skenario dunia nyata.

Alih-alih menggunakan `java.util.HashMap` standar, proyek ini mengimplementasikan **Red-Black Tree (RBTree)** kustom dari nol untuk menangani penyimpanan data dan resolusi *collision*. Hal ini menjamin kompleksitas waktu pencarian tetap **O(log n)** bahkan dalam skenario terburuk, memberikan performa yang jauh lebih stabil dibandingkan Linked List pada Hash Map konvensional.

Dibalut dengan antarmuka **JavaFX** yang bersih dan modern (terinspirasi dari desain iOS), aplikasi ini juga dilengkapi dengan berbagai *"Easter Eggs"* (Gimmicks) dan aplikasi mini terintegrasi yang membuat pengalaman pengguna menjadi unik dan menghibur.

## ✨ Fitur Utama

### 🧠 1. Advanced Data Structure (RBTree)
Jantung dari aplikasi ini adalah `RBTree.java`.
* **Custom Implementation:** Struktur data pohon merah-hitam yang ditulis manual.
* **Efficient Lookup:** Pencarian kata dilakukan dengan menelusuri node pohon, bukan iterasi linear array.
* **Auto-Balancing:** Otomatis menyeimbangkan diri (Rotate Left/Right, Recoloring) saat data baru dimasukkan.

### 🎨 2. Modern & Interactive UI
* **Clean Aesthetic:** Desain minimalis hitam-putih dengan tipografi yang nyaman dibaca.
* **Smart Search:** Fitur *Live Search* dengan *Debouncing* (menunggu pengguna selesai mengetik sebelum mencari) dan *Suggestion List* yang muncul otomatis.
* **Dark Mode Support:** Dukungan penuh untuk tema gelap dan terang.

### 🛠 3. Integrated Mini-Apps Suite
Aplikasi ini bertindak sebagai "OS Mini" yang dapat meluncurkan alat lain:
* **🧮 Mini Calculator:** Kalkulator fungsional dengan UI modern.
* **📝 Mini Notepad:** Editor teks sederhana dengan fitur penyimpanan file (`.txt`).
* **🎨 Mini Paint:** Kanvas digital untuk menggambar sketsa cepat.

---

## 🥚 Easter Eggs & Gimmicks

Coba ketik kata kunci berikut di kolom pencarian untuk memicu efek rahasia!

| Keyword (Input) | Efek / Respon | Kategori |
| :--- | :--- | :--- |
| `apel` | Menampilkan definisi standar buah apel. | 🟢 Normal |
| `tong` / `barrel` | Aplikasi melakukan **Barrel Roll** (Putar 360°). | 🌀 Animasi |
| `gempa` / `earthquake` | Layar bergetar hebat (**Shake Effect**). | 🫨 Animasi |
| `hilang` / `lost` | Aplikasi perlahan menghilang (**Fade Out**). | 👻 Animasi |
| `pesta` / `party` | Mode **Disco** (Lampu warna-warni & musik visual). | 🎉 Visual |
| `hacker` | Menampilkan efek **Matrix Rain** (Kode hijau jatuh). | 💻 Visual |
| `dvd` / `lama` | Menampilkan screensaver **DVD** yang memantul. | 📺 Visual |
| `kecoa` / `jorok` | **PRANK!** Banyak kecoa muncul berjalan di layar. | 🪳 Prank |
| `presiden` | Memutar audio spesial (`presiden.mp3`). | 🔊 Audio |
| `kalkulator` | Membuka aplikasi **Mini Calculator**. | 🛠 Tools |
| `catatan` / `python` | Membuka aplikasi **Mini Notepad**. | 🛠 Tools |
| `gambar` | Membuka aplikasi **Mini Paint** (Canvas). | 🛠 Tools |
| `gelap` | Mengaktifkan **Dark Mode**. | 🌙 Tema |
| `terang` | Mengaktifkan **Light Mode**. | ☀️ Tema |

---

## ⚙️ Tech Stack

* **Language:** [Java 21](https://www.oracle.com/java/technologies/downloads/#java21) (LTS)
* **GUI Framework:** [JavaFX 23.0.1](https://openjfx.io/)
* **Build Tool:** [Apache Maven](https://maven.apache.org/)
* **Data Format:** JSON (Parsed using Jackson)

## 🚀 Instalasi & Cara Menjalankan

Pastikan Anda telah menginstal **JDK 21** atau yang lebih baru.

1.  **Clone Repository**
    ```bash
    git clone [https://github.com/username/Smart-Dictionary-RBTree.git](https://github.com/username/Smart-Dictionary-RBTree.git)
    cd Smart-Dictionary-RBTree
    ```

2.  **Build & Run (Menggunakan Maven)**
    Jalankan perintah berikut di terminal root folder proyek:
    ```bash
    mvn clean javafx:run
    ```

**Developed by Empat Trio Strukdeath Destroyer.**