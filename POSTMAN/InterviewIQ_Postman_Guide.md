# מדריך שימוש ב-Postman עבור InterviewIQ Backend

יצרתי לך שני קבצים:
- `InterviewIQ_Postman_Collection.json`
- `InterviewIQ_Postman_Environment.json`

## 1) ייבוא לתוך Postman
1. פתח את Postman.
2. לחץ `Import`.
3. ייבא קודם את ה-Collection ואז את ה-Environment.
4. בפינה הימנית העליונה בחר את הסביבה: `InterviewIQ Local Environment`.

## 2) מה להפעיל לפני הבדיקות
כדי שהבדיקות יעבדו כמו שצריך, ה-backend שלך צריך לרוץ על:
- `http://localhost:8080`

לפי `application.properties` בפרויקט שלך:
- השרת רץ על פורט `8080`
- בסיס הנתונים הוא PostgreSQL מקומי על `cvoptimizer`
- Flyway פעיל
- RAG bootstrap פעיל
- AI evaluation כבוי כברירת מחדל

## 3) סדר הרצה מומלץ בתוך Postman
הרץ לפי הסדר הזה:

### תיקייה 00 - Setup & Smoke
1. `Get Interview Topics`
2. `RAG Search`

### תיקייה 01 - Job Scraping
3. `Create Manual Job Input`
4. `Get Manual Job Inputs`
5. `Scrape Job By URL (Optional)` — אופציונלי, כי תלוי באתר חיצוני

### תיקייה 02 - Regular Interview Flow
6. `Start Interview`
7. `Answer Current Interview Question`
8. `Get Interview Summary`
9. `Get Interview Details`
10. `Get Interview History`
11. `Get Skill Graph By Session`
12. `Get Skill Graph By User`

### תיקייה 03 - Job-Specific Interview Flow
13. `Start Job-Specific Interview (TEXT payload)`
14. `Answer Job-Specific Interview Question`
15. `Get Job-Specific Summary`

### תיקייה 04 - OCR (Optional Manual Test)
16. `Upload Image For OCR (Optional)`

## 4) מה ה-Collection בודק בפועל
הבדיקות לא מסתפקות ב-200 בלבד. הן בודקות גם:
- שהתגובה היא JSON כשצריך
- שקיימים שדות קריטיים כמו `sessionId`, `firstQuestion`, `status`
- שמערכים באמת מוחזרים כמערכים
- שמבני עומק כמו `chunks`, `context`, `strengths`, `weaknesses`, `skills` קיימים
- ששדות תלויי-זרימה נשמרים לסביבה, למשל:
  - `sessionId`
  - `jobSessionId`
- שהיסטוריית הראיונות וה-skill graph עובדים אחרי ריצה אמיתית

## 5) משתנים חשובים בסביבה
בסביבה תמצא משתנים שאפשר לשנות בקלות:
- `baseUrl`
- `userId`
- `topic`
- `ragQuery`
- `answerText`
- `jobAnswerText`
- `manualJobTitle`
- `manualJobCompany`
- `manualJobLocation`
- `sessionId`
- `jobSessionId`
- `ocrImagePath`

## 6) איך לבדוק תוצאות בתוך Postman
בכל בקשה:
1. לחץ `Send`
2. פתח את לשונית `Test Results`
3. ודא שכל הבדיקות מסומנות בירוק
4. אם אחת נכשלה, בדוק:
   - `Body`
   - `Status`
   - `Console` של Postman

כדי לפתוח קונסול:
- `View` → `Show Postman Console`

## 7) איך להריץ הכל בבת אחת
1. לחץ על שלוש הנקודות ליד ה-Collection
2. בחר `Run collection`
3. בחר את הסביבה `InterviewIQ Local Environment`
4. הרץ את כל הבקשות לפי הסדר

הערה: בגלל שיש בקשות שתלויות ב-session שנוצר קודם, חשוב לא לשנות את הסדר.

## 8) נקודות חשובות לפי הפרויקט שלך
### OCR
ה-endpoint של OCR תלוי ב-Tesseract מקומי, ובקוד שלך מוגדר הנתיב:
`C:\Program Files\Tesseract-OCR	essdata`

אם Tesseract לא מותקן או הנתיב שונה, הבדיקה עלולה להחזיר שגיאה.

### Scraping by URL
ה-endpoint `/api/scrape?url=...` תלוי באתר חיצוני, ולכן הוא פחות דטרמיניסטי.
לכן הכנסתי גם בדיקות דטרמיניסטיות דרך `manual-input`.

### AI evaluation
בפרויקט שלך `ai.evaluation.enabled=false`, לכן אין לצפות כאן לבדיקת OpenAI אמיתית כברירת מחדל.

## 9) מה הייתי בודק אחר כך ידנית
אחרי שהכל ירוץ, כדאי לעשות עוד 3 בדיקות ידניות:
1. להתחיל כמה ראיונות עם `userId` שונה ולוודא שהיסטוריה ו-skill graph באמת מופרדים
2. לענות מספר תשובות ברצף על אותה session כדי לראות שינוי ב-`status`, `scoreSoFar`, `agentAction`
3. לבדוק `/api/interview/job-specific` גם עם `sourceType=URL` אם יש לך URL יציב למשרת בדיקה

## 10) אם בקשה נופלת
המקומות הכי סבירים לנפילה אצלך הם:
- PostgreSQL לא רץ
- Flyway לא מצליח לעלות
- pgvector לא מותקן
- Tesseract לא מותקן
- scraping לא מצליח על אתר ספציפי

במצב כזה, התחל מ:
- `Get Interview Topics`
- `RAG Search`
- `Create Manual Job Input`
- `Start Interview`

אם אלה עוברים, הליבה של המערכת חיה.
