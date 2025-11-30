# Smart Dictionary & Data Structure Visualization

**A robust English-Indonesian dictionary application featuring a custom Hash Map implementation with Red-Black Tree collision resolution.**

This project serves as the Final Project for the **Data Structures** course. It demonstrates the practical application of advanced data structures by integrating a highly efficient backend storage system with a modern **JavaFX** graphical user interface.

## 📋 Project Overview

Unlike standard dictionary applications that rely on built-in libraries, this project implements its own core data structures from scratch:
* **RBHashMap**: A custom Hash Map implementation.
* **Red-Black Tree Integration**: Instead of using standard Linked Lists for collision handling (Separate Chaining), this application utilizes **Red-Black Trees**. This ensures that even in worst-case collision scenarios, the time complexity for search operations remains **$O(\log N)$**, significantly outperforming the $O(N)$ complexity of traditional chaining.

## ✨ Key Features

### 1. Efficient Word Lookup
* Instant translation from English to Indonesian.
* Optimized backend ensures rapid retrieval of definitions.

### 2. Advanced Data Structure Implementation
* **Collision Handling**: Implements a self-balancing Binary Search Tree (Red-Black Tree) within each Hash Map bucket.
* **Visualization**: The internal structure of the buckets and trees can be visualized in the console output for debugging and educational analysis.

### 3. Interactive "Gimmicks" (Easter Eggs)
The application features a unique `GimmickLibrary` that triggers visual effects based on specific search queries, demonstrating dynamic UI manipulation in JavaFX.

### 4. Integrated Utilities
* **Mini Calculator**: A fully functional calculator tool built directly into the application ecosystem.

## 🛠️ Tech Stack

* **Language**: Java 21
* **GUI Framework**: JavaFX 23.0.1
* **Build Tool**: Maven
* **Architecture**: Modular design separating Data Structures (`RBTree`, `RBHashMap`) from UI Logic (`MainApp`, `GimmickLibrary`).

## 🚀 Getting Started

### Prerequisites
Ensure you have the following installed:
* **Java JDK 21** or higher.
* **Maven** (for dependency management).

### Installation & Execution

1.  **Clone the Repository**
    ```bash
    git clone <repository-url>
    cd <project-directory>
    ```

2.  **Build and Run (via Maven)**
    Execute the following command in your terminal:
    ```bash
    mvn clean javafx:run
    ```

3.  **Run via IntelliJ IDEA**
    * Open the project as a **Maven Project**.
    * Locate `src/main/java/MainApp.java`.
    * Right-click and select **Run 'MainApp'**.

## 🎮 User Guide & Easter Eggs

To test the application's unique features, try searching for the following keywords:

| Keyword | Description | Effect |
| :--- | :--- | :--- |
| **`apel`** | Standard Search | Shows definition: "Buah berwarna merah". |
| **`barrel`** | Visual Effect | Triggers a **360° Barrel Roll** animation on the UI. |
| **`earthquake`** | Visual Effect | Simulates a **shake/quake** effect on the window. |
| **`lost`** | Visual Effect | Causes the application interface to **fade out**. |
| **`calculator`** | Tool | Launches the integrated **Mini Calculator**. |

## 📂 Project Structure

* **`RBHashMap.java`**: The core map implementation handling hashing and bucket management.
* **`RBTree.java`**: The self-balancing tree implementation used for storing data within buckets.
* **`GimmickLibrary.java`**: Contains the logic for JavaFX animations and transitions.
* **`MainApp.java`**: The main entry point and UI controller.
* **`MiniCalculator.java`**: A separate JavaFX component for the calculator utility.

---
*Developed by [Your Name/Team Name]*
