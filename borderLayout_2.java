import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;


public class borderLayout_2 {

    // משתני המערכת והניקוד
    static int totalGames = 0;      // כמות המשחקים הכוללת בטורניר (נקבע על ידי המשתמש)
    static int currentGame = 1;     // מספר הסיבוב הנוכחי
    static int scoreX = 0;          // מדד הניקוד של שחקן X
    static int scoreO = 0;          // מדד הניקוד של שחקן O

    // משתנים לשמירת שמות השחקנים באופן דינמי (ברירת מחדל ניטרלית)
    static String playerXName = "Player X";
    static String playerOName = "Player O";

    static boolean xturn = true;    // תור מי לשחק: true = תור של X, false = תור של O
    static boolean gameOver = true; // האם המשחק חסום/נגמר (מתחיל כ-true כדי לחסום לחיצות עד שילחצו Update)
    static JButton[] buttons = new JButton[9]; // מערך המכיל את 9 הכפתורים של לוח ה-איקס עיגול

    // משתני זמן וטיימרים
    static int totalSeconds = 0;     // סך כל השניות שהוגדרו לסיבוב (לפי ה-MM:SS שהוזן)
    static int remainingSeconds = 0; // השניות שנותרו בפועל ומצטמצמות בזמן אמת בסיבוב הנוכחי
    static Timer gameTimer;          // הטיימר הראשי שמנהל את זמן הסיבוב (יורד כל שנייה)
    static Timer roundDelayTimer;    // טיימר ההמתנה (5 שניות ספירה לאחור) בין סיבוב לסיבוב

    // רכיבי הממשק הגרפי (Label-ים שצריך לעדכן להם את הטקסט דינמית)
    static JLabel timerLabel;        // תווית להצגת הזמן שנותר (פאנל ורוד תחתון)
    static JLabel label3;            // תווית להצגת מספר הסיבוב הנוכחי מתוך הסך הכל (פאנל שמאלי)
    static JLabel label3_2;          // תווית להצגת הזמן המקורי שהוגדר לכל משחק (פאנל שמאלי)
    static JLabel label4_2;          // תווית להצגת לוח התוצאות והשמות הנוכחיים (פאנל ימני)

    // פונקציה המאפסת את לוח המשחק ומכינה אותו לסיבוב חדש
    public static void resetBoard() {
        // לולאה שמנקה את הטקסט מכל 9 הכפתורים בלוח ומחזירה את הגוון הוורוד
        for (int i = 0; i < buttons.length; i++) {
            JButton btn = buttons[i];
            btn.setText("");
            btn.setBackground(new Color(235, 188, 190));
        }
        gameOver = false; // פתיחת הלוח ללחיצות

// עדכון סטטוס המשחק הנוכחי בתצוגה
        label3.setText("Game: " + currentGame + " / " + totalGames);
        startTimer();     // הפעלת טיימר השניות של הסיבוב
    }

    public static void endRound(String soundName, boolean isWin) {
        gameOver = true; // חסימת הלוח מיידית

        SoundManager.stopTimerSound(); // עצירת סאונד הטיימר דרך מנהל הסאונד

        if (gameTimer != null) {
            gameTimer.stop(); // עצירת טיימר הסיבוב
        }

        // אם יש מנצח
        if (isWin) {
            if (xturn) {
                scoreX++;
            } else {
                scoreO++;
            }
            // עדכון לוח התוצאות הגרפי
            label4_2.setText(playerXName + ": " + scoreX + "   " + playerOName + ": " + scoreO);
        } else {
            // במקרה של תיקו: נעביר את הזכות להתחיל לשחקן השני
            xturn = !xturn;
        }

        // השמעת סאונד סיום הסיבוב במצב של ניצחון או תיקו באמצעות המחלקה החדשה
        SoundManager.playEffect(soundName);
        currentGame++;     // קידום מספר המשחק הבא
        startRoundDelay(); // מעבר למנגנון ההמתנה של 5 שניות
    }

    // פונקציה המנהלת את ההמתנה של 5 השניות בין סיבוב לסיבוב או סיום הטורניר
    public static void startRoundDelay() {
        // בדיקה: האם נשארו עוד משחקים לשחק בטורניר?
        if (currentGame <= totalGames) {
            final int[] delayRemaining = {5}; // מערך בגודל 1 המכיל את שניות ההמתנה (5 שניות)

            // הצגת הודעת המתנה ראשונית בשעון
            timerLabel.setText("Next round in " + delayRemaining[0] + "s...");

            // יצירת טיימר שפועל כל 1000 מילישניות (שנייה אחת)
            roundDelayTimer = new Timer(1000, e -> {
                delayRemaining[0]--; // הורדת שנייה אחת בכל פעימה
                if (delayRemaining[0] > 0) {
                    // עדכון הודעת הספירה לאחור במסך
                    timerLabel.setText("Next round in " + delayRemaining[0] + "s...");
                } else {
                    ((Timer)e.getSource()).stop(); // עצירת טיימר ההמתנה כשהגענו ל-0
                    resetBoard();                  // איפוס הלוח והתחלת המשחק הבא
                }
            }
            );
            roundDelayTimer.start(); // הפעלת טיימר ההמתנה
        } else {
            // אם הגענו לכאן - הטורניר הסתיים לחלוטין! נבנה את הודעת הסיכום הדינמית
            String message = "Tournament Finished!\n\n"
                    + playerXName + ": " + scoreX + "\n"
                    + playerOName + ": " + scoreO + "\n\n";

            // קביעת המנצח הגדול בטורניר לפי הניקוד המצטבר והצגת שמו המותאם אישית
            if (scoreX > scoreO) {
                message += "🏆 The Winner Is: " + playerXName + " 🏆";
            } else if (scoreO > scoreX) {
                message += "🏆 The Winner Is: " + playerOName + " 🏆";
            } else {
                message += "🤝 It's a Draw Tournament! 🤝";
            }

            // הקפצת חלון הודעה חגיגי שמציג את תוצאות הטורניר
            JOptionPane.showMessageDialog(null, message, "Tournament Over", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // יצירת חלון האפליקציה הראשי והגדרת המאפיינים שלו
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // סגירת התוכנית ביציאה מהחלון

        // פקודה הפותחת את החלון אוטומטית על כל המסך (Maximized)
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLayout(new BorderLayout(8, 8));               // שימוש ב-BorderLayout עם רווחים אסתטיים של 8 פיקסלים

        // הגדרת פלטת צבעים שמחה, חמה ואחידה: שילוב של ורוד עתיק ושמנת
        Color bgCream = new Color(249, 246, 240);                       // צבע רקע שמנת חם ורך לחלון הראשי ולמרווחי הלוח
        Color panelCream = new Color(242, 235, 224);                    // צבע שמנת מעט עמוק יותר לפאנלים העוטפים
        Color dustyRose = new Color(210, 145, 148);                     // ורוד עתיק קלאסי ונעים לכפתור העדכון הראשי
        Color textDark = new Color(74, 60, 60);                         // חום-אפרפר כהה ועמוק לטקסט קריא, רך ואלגנטי
        Font mainFont = new Font("Segoe UI", Font.BOLD, 18);            // גופן ראשי נקי ואחיד לכל כותרות הנתונים
        Font titleFont = new Font("Segoe UI", Font.BOLD, 32);           // גופן גדול ומודרני עבור כותרת המשחק הראשית

        // יצירת חמשת הפאנלים שמייצגים את אזורי המסך
        JPanel panel1 = new JPanel(); // פאנל עליון (צפון)
        JPanel panel2 = new JPanel(); // פאנל תחתון (דרום)
        JPanel panel3 = new JPanel(); // פאנל שמאלי (מערב)
        JPanel panel4 = new JPanel(); // פאנל ימני (מזרח)
        JPanel panel5 = new JPanel(); // פאנל מרכזי (לוח המשחק)

        // הגדרת צבעי הרקע לפאנלים
        panel1.setBackground(panelCream);
        panel2.setBackground(panelCream);
        panel3.setBackground(panelCream);
        panel4.setBackground(panelCream);
        panel5.setBackground(bgCream); // פאנל לוח המשחק מקבל את הרקע הבהיר המשלים

        // פאנל 1: עליון (כותרת המשחק)
        JLabel label = new JLabel("Welcome to Tic Tac Toe", SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0)); // הוספת מרווח פנימי מעודן מלמעלה ומלמטה
        label.setForeground(textDark);                                 // צבע טקסט חם וכהה התואם לפלטה
        label.setFont(titleFont);                                      // הלבשת הפונט המודרני הגדול
        panel1.add(label);

        // פאנל 3: שמאלי (נתוני הטורניר הנוכחיים)
        panel3.setLayout(new GridLayout(4, 1, 10, 10)); // חלוקה ל-4 שורות בעמודה אחת
        panel3.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // שוליים פנימיים כדי שהטקסט לא יידבק לקצה

        label3 = new JLabel("Number of games: 0", SwingConstants.CENTER);
        label3.setForeground(textDark);
        label3.setFont(mainFont);

        label3_2 = new JLabel("Time for each game: 0", SwingConstants.CENTER);
        label3_2.setForeground(textDark);
        label3_2.setFont(mainFont);

        panel3.add(label3);
        panel3.add(label3_2);

        // פאנל 4: ימני (טופס קלט ובחירת שמות/נתונים מותאמים אישית)
        panel4.setLayout(new GridLayout(11, 1, 5, 8)); // חלוקה ל-11 שורות עבור כל שדות הקלט והכותרות
        panel4.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // שוליים בצדדים למניעת מריחה של השדות לקצוות
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 15);          // גופן עדין וקטן יותר עבור כותרות השדות

        // שדות לקלט שמות השחקנים באופן גנרי ומותאם אישית
        JLabel xNameTitle = new JLabel("Player X Name:", SwingConstants.CENTER);
        xNameTitle.setForeground(textDark);
        xNameTitle.setFont(labelFont);
        JTextField xNameField = new JTextField(" "); // ערך התחלתי ניטרלי בשדה הטקסט
        xNameField.setHorizontalAlignment(JTextField.CENTER);
        xNameField.setFont(mainFont);
        xNameField.setBorder(BorderFactory.createLineBorder(dustyRose, 2)); // מסגרת מעודנת בגוון ורוד עתיק

        JLabel oNameTitle = new JLabel("Player O Name:", SwingConstants.CENTER);
        oNameTitle.setForeground(textDark);
        oNameTitle.setFont(labelFont);
        JTextField oNameField = new JTextField(" "); // ערך התחלתי ניטרלי בשדה הטקסט
        oNameField.setHorizontalAlignment(JTextField.CENTER);
        oNameField.setFont(mainFont);
        oNameField.setBorder(BorderFactory.createLineBorder(dustyRose, 2));

        // שדות לקלט כמות משחקים וזמן לכל סיבוב
        JLabel gamesTitle = new JLabel("Enter number of games:", SwingConstants.CENTER);
        gamesTitle.setForeground(textDark);
        gamesTitle.setFont(labelFont);
        JTextField gamesField = new JTextField();
        gamesField.setHorizontalAlignment(JTextField.CENTER);
        gamesField.setFont(mainFont);
        gamesField.setBorder(BorderFactory.createLineBorder(dustyRose, 2));

        JLabel timeTitle = new JLabel("Enter time per game (MM:SS):", SwingConstants.CENTER);
        timeTitle.setForeground(textDark);
        timeTitle.setFont(labelFont);
        JTextField timeField = new JTextField("00:00");
        timeField.setHorizontalAlignment(JTextField.CENTER);
        timeField.setFont(mainFont);
        timeField.setBorder(BorderFactory.createLineBorder(dustyRose, 2));

        // כפתור העדכון והחלת הנתונים
        JButton updateButton = new JButton("Update");
        updateButton.setFont(mainFont);
        updateButton.setBackground(dustyRose);                           // צבע רקע ורוד עתיק משמח
        updateButton.setForeground(Color.WHITE);                        // צבע טקסט לבן נקי קריא
        updateButton.setFocusPainted(false);                            // ביטול מסגרת הפוקוס הפנימית המיושנת של ווינדוס
        updateButton.setBorder(BorderFactory.createEmptyBorder());       // מחיקת הגבולות התלת-ממדיים המובנים של הכפתור

        // תצוגת לוח התוצאות הדינמי המשתנה לפי השמות שהוזנו
        label4_2 = new JLabel("Player X: 0   Player O: 0", SwingConstants.CENTER);
        label4_2.setForeground(textDark);                                // שימוש בצבע המלל הכהה המעוצב
        label4_2.setFont(new Font("Segoe UI", Font.BOLD, 20));

        // הוספת כל הרכיבים לפאנל הימני לפי סדר ההופעה מלמעלה למטה
        panel4.add(xNameTitle);
        panel4.add(xNameField);
        panel4.add(oNameTitle);
        panel4.add(oNameField);
        panel4.add(gamesTitle);
        panel4.add(gamesField);
        panel4.add(timeTitle);
        panel4.add(timeField);
        panel4.add(updateButton);
        panel4.add(label4_2);

        // מאזין לחיצות עבור כפתור Update
        updateButton.addActionListener(e -> {
            try {
                // שליפת השמות מהשדות
                playerXName = xNameField.getText().trim().isEmpty() ? "Player X" : xNameField.getText().trim();
                playerOName = oNameField.getText().trim().isEmpty() ? "Player O" : oNameField.getText().trim();

                // המרת הטקסט של כמות המשחקים למספר שלם
                totalGames = Integer.parseInt(gamesField.getText().trim());
                String timeText = timeField.getText().trim();

                // בדיקת תקינות: וידוא שהטקסט מכיל נקודתיים בפורמט הזמן
                if (!timeText.contains(":")) {
                    throw new Exception();
                }

                // פירוק הזמן לדקות ושניות וחישוב סך כל השניות
                String[] parts = timeText.split(":");
                int minutes = Integer.parseInt(parts[0]);
                int seconds = Integer.parseInt(parts[1]);
                totalSeconds = minutes * 60 + seconds;

                // הגנה מפני קלט שלילי או אפס
                if (totalSeconds <= 0 || totalGames <= 0) {
                    JOptionPane.showMessageDialog(null, "Please enter values greater than 0");
                    return;
                }

                // אתחול נתוני הטורניר מחדש
                currentGame = 1;
                scoreX = 0;
                scoreO = 0;
                xturn = true;

                // עדכון תוויות הטקסט בממשק
                label4_2.setText(playerXName + ": 0   " + playerOName + ": 0");
                label3.setText("Game: " + currentGame + " / " + totalGames);
                label3_2.setText("Time for each game: " + timeText);

                // קפיאת השעון על הזמן ההתחלתי המלא
                timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));

                // חסימת הלוח ועצירת כל טיימר קודם שפעל ברקע
                gameOver = true;

                SoundManager.stopTimerSound(); // עצירת סאונד הטיימר הפעיל דרך מחלקת השמע

                if (gameTimer != null) gameTimer.stop();
                if (roundDelayTimer != null) roundDelayTimer.stop();

                // השמעת מוזיקת הפתיחה/סטארט של הטורניר
                SoundManager.playEffect("start");

                // יצירת טיימר השהייה ייעודי באורך 5 שניות עבור קובץ הסאונד
                int musicDuration = 5000;
                javax.swing.Timer musicDelayTimer = new javax.swing.Timer(musicDuration, event -> {
                    ((Timer) event.getSource()).stop(); // עצירת טיימר המוזיקה
                    gameOver = false;                   // פתיחת הלוח ללחיצות (המשחק מתחיל)
                    resetBoard();                       // קריאה לפונקציית איפוס הלוח והפעלת שעון המשחק
                });
                musicDelayTimer.setRepeats(false); // ביטול חזרה אוטומטית של הטיימר
                musicDelayTimer.start();           // הפעלת טיימר ההשהייה של המוזיקה

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid numbers and time format (MM:SS)");
            }
        });

        // פאנל 2: תחתון (תצוגת טיימר המשחק הראשי)
        timerLabel = new JLabel("Time Left: 00:00", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));         // הגדרת גופן בולט וגדול לשעון
        timerLabel.setForeground(textDark);                             // צבע שעון כהה ורך התואם לעיצוב הבהיר החדש
        panel2.add(timerLabel);

        // פאנל 5: מרכזי (לוח הלחצנים של איקס עיגול)
        panel5.setLayout(new GridLayout(3, 3, 6, 6)); // סידור הלוח בטבלה של 3X3 עם רווח מובנה של 6 פיקסלים

        // לולאה ליצירת 9 כפתורי המשחק והגדרת המאפיינים שלהם
        for (int i = 0; i < 9; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Segoe UI", Font.BOLD, 68)); // גופן גדול ונקי עבור סימני ה-X וה-O על הלוח
            buttons[i].setFocusable(false);

            // העלמת המראה התלת-ממדי והחלפתו בעיצוב שטוח
            buttons[i].setBackground(new Color(235, 188, 190));          // ורוד עתיק בהיר
            buttons[i].setBorder(BorderFactory.createLineBorder(bgCream, 4)); // מסגרת שמנת עבה למפרידים יציבים ואסתטיים
            buttons[i].setOpaque(true);                                 // וידוא שהצבע החדש יוצג בצורה מלאה ויציבה

            // הוספת מאזין לחיצות לכל כפתור בלוח
            buttons[i].addActionListener(e -> {
                JButton btn = (JButton) e.getSource(); // זיהוי הכפתור הספציפי עליו לחצו

                // בדיקת הגנה: אם המשחק חסום או שהכפתור כבר תפוס - אל תעשה כלום
                if (gameOver || !btn.getText().equals("")) {
                    return;
                }

                // סימון הסימן המתאים על הכפתור לפי התור הנוכחי
                if (xturn) {
                    btn.setText("X");
                    btn.setForeground(new Color(139, 38, 53));          // בורדו לשחקן X
                } else {
                    btn.setText("O");
                    btn.setForeground(new Color(92, 64, 51));           // חום שוקולד לשחקן O
                }

                // בדיקה: האם המהלך האחרון הוביל לניצחון?
                if (checkWinner()) {
                    endRound("win", true); // סיום הסיבוב עם סאונד ניצחון ועדכון נקודה
                    return;
                }

                // בדיקה: האם המהלך האחרון הוביל לתיקו (הלוח מלא)?
                if (checkDraw()) {
                    endRound("drow", false); // סיום הסיבוב עם סאונד תיקו בלי להוסיף נקודה
                    return;
                }

                // אם אין ניצחון או תיקו - מחליפים את התור לשחקן השני
                xturn = !xturn;
            });
            panel5.add(buttons[i]); // הוספת הכפתור לפאנל הגרפי המרכזי
        }

        // הגדרת מימדים מועדפים לפאנלים השונים המותאמים לפריסת מסך מלא (Maximized)
        panel1.setPreferredSize(new Dimension(100, 95));
        panel2.setPreferredSize(new Dimension(300, 85));
        panel3.setPreferredSize(new Dimension(380, 400));                // רוחב מורחב לפאנל המידע השמאלי
        panel4.setPreferredSize(new Dimension(400, 100));                // רוחב מורחב לפאנל הטופס הימני

        // הוספת חמשת הפאנלים המעוצבים אל תוך ה-JFrame הראשי לפי אזורי BorderLayout
        frame.add(panel1, BorderLayout.NORTH);
        frame.add(panel2, BorderLayout.SOUTH);
        frame.add(panel3, BorderLayout.WEST);
        frame.add(panel4, BorderLayout.EAST);
        frame.add(panel5, BorderLayout.CENTER);

        frame.getContentPane().setBackground(bgCream);                  // צביעת רקע בסיס החלון בצבע השמנת הראשי
        frame.setVisible(true);      // הצגת החלון על המסך
        frame.setResizable(false);   // חסימת האפשרות לשינוי גודל החלון
    }

    // פונקציה המפעילה ומנהלת את טיימר הספירה לאחור של השניות במהלך הסיבוב
    public static void startTimer() {
        remainingSeconds = totalSeconds; // אתחול השניות שנותרו לערך המלא שהוגדר

        if (gameTimer != null) {
            gameTimer.stop(); // עצירת טיימר קודם ליתר ביטחון
        }

        // הפעלת סאונד הטיימר בלולאה דרך מנהל הסאונד
        SoundManager.playTimerSoundLoop();

        // הצגת הזמן המלא המדויק על המסך מיידית ברגע האתחול
        int initialMin = remainingSeconds / 60;
        int initialSec = remainingSeconds % 60;
        timerLabel.setText(String.format("Time Left: %02d:%02d", initialMin, initialSec));

        // יצירת טיימר שמתעורר כל שנייה אחת בדיוק (1000 מילישניות)
        gameTimer = new Timer(1000, e -> {
            // אם הזמן נגמר - נעצור את פעולת הטיימר
            if (remainingSeconds <= 0) {
                ((Timer) e.getSource()).stop();
                SoundManager.stopTimerSound();
                return;
            }

            remainingSeconds--; // הורדת שנייה אחת בכל פעימה

            // חישוב הפורמט להצגה בדקות ושניות (MM:SS)
            int min = remainingSeconds / 60;
            int sec = remainingSeconds % 60;

            // עדכון שעון הזמן במסך הראשי
            timerLabel.setText(String.format("Time Left: %02d:%02d", min, sec));

            // בדיקה: האם הזמן הגיע ל-0? (הפסד עקב סיום הזמן)
            if (remainingSeconds <= 0) {
                ((Timer) e.getSource()).stop(); // עצירת השעון

                SoundManager.stopTimerSound(); // עצירת סאונד הטיימר

                gameOver = true;                // חסימת לוח המשחק

                JOptionPane.showMessageDialog(null, "Time is over!"); // הקפצת הודעה שהזמן נגמר

                xturn = !xturn;    // העברת התור לשחקן השני
                currentGame++;     // מעבר למשחק הבא
                startRoundDelay(); // הפעלת השהיית 5 השניות בין הסיבובים
            }

        });
        gameTimer.start(); // הזנקת טיימר המשחק הראשי
    }

    // פונקציה הסורקת את הלוח ומחזירה האם יש רצף מנצח של 3 סימנים זהים
    public static boolean checkWinner() {
        int[][] win = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // שורות אופקיות
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // עמודות אנכיות
                {0, 4, 8}, {2, 4, 6}             // אלכסונים
        };

        for (int[] line : win) {
            String a = buttons[line[0]].getText();
            String b = buttons[line[1]].getText();
            String c = buttons[line[2]].getText();

            if (!a.equals("") && a.equals(b) && b.equals(c)) {
                return true;
            }
        }
        return false;
    }

    // פונקציה הבודקת האם הלוח התמלא לחלוטין בסימנים (מצב של תיקו)
    public static boolean checkDraw() {
        for (JButton btn : buttons) {
            if (btn.getText().equals("")) {
                return false;
            }
        }
        return true;
    }
}