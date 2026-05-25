# ⏱️ Pro Stopwatch

A modern, high-performance desktop stopwatch application built using Java Swing. This project transforms a basic GUI into a sleek, production-ready utility featuring high-definition rendering, multi-theme layouts, dynamic lapses management, and a mathematical real-time vector Analog display mode.

---

## ✨ Features

- **Sharp, Clear Display:** Configured explicitly with 2D vector text anti-aliasing rendering hints to prevent any interface blurriness on modern high-resolution displays.
- **Digital ↔ Analog Conversion:** Toggle seamlessly between a bold monospaced digital interface and a dynamically painted vector clock face.
- **Dynamic Visual Themes:** Cycle seamlessly between **Emerald Neon**, **Stealth Dark**, and **Minimalist Light Paper** modes.
- **Advanced Lap Management:** Record active splits, clear logs instantly, or export your lap statistics to an external tracking text file (`stopwatch_laps.txt`).
- **Utility Window Adjustments:** Toggle the "Always on Top" checkbox to anchor the stopwatch on top of all concurrent system work applications.
- **Custom Flat UI Controls:** Modernized with flat, uniform, rounded-corner buttons utilizing manual color transformations for interactive hover/click responses.

---

## 🛠️ Architecture & Math Behind the UI

The **Analog View** bypasses traditional image rendering and draws the clock face procedurally in real-time. 

Using trigonometry inside a custom `JPanel` canvas override, the angular coordinates for the second sweep hand are recalculated continuously on every internal engine tick ($10\text{ ms}$):

$$\theta = \text{radians}\left(\text{seconds} \times 6 - 90\right)$$

The runtime endpoints of the moving vector line are plotted dynamically using standard cartesian mapping:

$$x = x_{\text{center}} + r \cdot \cos(\theta)$$
$$y = y_{\text{center}} + r \cdot \sin(\theta)$$

This ensures perfect mathematical precision and flawless, infinitely scalable visual fluid mechanics.

---

## 🚀 How to Run the Project

### Prerequisites
- **Java Development Kit (JDK):** Version 21 or higher (Optimized for Java 26 language structures).
- **IDE (Optional):** IntelliJ IDEA, Eclipse, or NetBeans.

### Execution via Command Line
1. Clone the repository to your local system:
   ```bash
   git clone [https://github.com/Jamiha07/StopWatch.git](https://github.com/Jamiha07/StopWatch.git)
