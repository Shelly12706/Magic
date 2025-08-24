# 魔法學院課程管理系統

## 專案簡介
此系統為魔法學院課程管理系統，採用 **Java Swing + MVC + SQL** 架構，支援學生選課、老師管理課程、成績登錄、報表統計等功能。  

---

## 功能特色

| 類別 | 功能特色 |
|------|----------|
| **Util** | - `CodeGenerator`：自動生成學生、老師、課程編號 <br> - `PasswordUtil`：密碼雜湊 SHA-256 / MD5，支援驗證 <br> - `DbConnection`：提供 MySQL 連線 |
| **DAO** | - 提供 CRUD 介面 <br> - `StudentDao`, `TeacherDao`, `CourseDao`, `EnrollmentDao` <br> - `dao.impl` 實作資料庫操作 |
| **Service** | - 商業邏輯處理 <br> - `StudentService`, `TeacherService`, `CourseService`, `EnrollmentService` <br> - `service.impl` 實作 Service 介面，封裝 DAO 操作 |
| **Controller** | - Swing 畫面控制 <br> - `LoginFrame`, `CoursePanel`, `TeacherPanel`, `ReportPanel` <br> - 處理按鈕事件、流程導向、報表呼叫 |
| **報表** | - `ReportPanel` 顯示課程學生數、通過率、平均分數、個人成績趨勢 <br> - 支援 Excel 匯出 |

---

## 專案架構

```text
MagicAcademy/
├─ src/main/java/
│  ├─ model/
│  │  ├─ Student.java
│  │  ├─ Teacher.java
│  │  ├─ Course.java
│  │  └─ Enrollment.java
│  ├─ dao/
│  │  ├─ StudentDao.java
│  │  ├─ TeacherDao.java
│  │  ├─ CourseDao.java
│  │  ├─ EnrollmentDao.java
│  │  └─ impl/
│  │     ├─ StudentDaoImpl.java
│  │     ├─ TeacherDaoImpl.java
│  │     ├─ CourseDaoImpl.java
│  │     └─ EnrollmentDaoImpl.java
│  ├─ service/
│  │  ├─ StudentService.java
│  │  ├─ TeacherService.java
│  │  ├─ CourseService.java
│  │  ├─ EnrollmentService.java
│  │  └─ impl/
│  │     ├─ StudentServiceImpl.java
│  │     ├─ TeacherServiceImpl.java
│  │     ├─ CourseServiceImpl.java
│  │     └─ EnrollmentServiceImpl.java
│  ├─ controller/
│  │  ├─ LoginFrame.java
│  │  ├─ MainMenuFrame.java
│  │  ├─ CoursePanel.java
│  │  ├─ TeacherPanel.java
│  │  └─ ReportPanel.java
│  └─ util/
│     ├─ CodeGenerator.java
│     ├─ PasswordUtil.java
│     └─ DbConnection.java
└─ pom.xml





flowchart TD
    A[學生登入] --> B[CoursePanel 顯示課程列表]
    B --> C{選擇課程?}
    C -- 是 --> D[衝堂檢查]
    D -- 通過 --> E[成功加入選課]
    E --> F[報表更新] --> G[學生查看個人成績與報表]

    H[老師登入] --> I[TeacherPanel 顯示課程管理]
    I --> J{新增或編輯課程?}
    J -- 是 --> K[課程資料儲存至資料庫]
    K --> L[報表資料更新]

    I --> M[老師輸入分數]
    M --> N[成績寫入資料庫]
    N --> L

    L --> O[老師查看學生成績統計與選課統計]



CoursePanel / TeacherPanel 小圖示互動表格
CoursePanel（學生端）
| 欄位 / 按鈕     | 說明     | 互動效果                    |
| ----------- | ------ | ----------------------- |
| 課程列表 JTable | 顯示所有課程 | 點擊「選課」按鈕 → 發生衝堂檢查       |
| 選課按鈕 🟢     | 選擇課程   | 成功加入選課後 JTable 更新，報表刷新  |
| 衝堂警告 ⚠️     | 顯示衝堂訊息 | 阻止選課                    |
| 報表按鈕 📊     | 查看個人成績 | 開啟 ReportPanel 顯示折線圖與表格 |

TeacherPanel（老師端）
| 欄位 / 按鈕     | 說明            | 互動效果                     |
| ----------- | ------------- | ------------------------ |
| 課程列表 JTable | 顯示老師授課課程      | 點擊課程可查看學生列表              |
| 學生列表 JTable | 顯示已選課學生       | 可輸入分數                    |
| 分數輸入欄 ✏️    | 老師輸入學生分數      | 成績即寫入資料庫，報表刷新            |
| 新增課程按鈕 ➕    | 新增課程資料        | 存入資料庫，刷新課程列表             |
| 編輯課程按鈕 ✏️   | 編輯課程資料        | 更新資料庫，刷新列表               |
| 報表按鈕 📊     | 查看選課統計 & 成績統計 | 開啟 ReportPanel 顯示柱狀圖/圓餅圖 |

CoursePanel / TeacherPanel 流程示意箭頭
[課程列表] --> [選課按鈕] --> [JTable 更新] --> [報表刷新]
[學生列表] --> [分數輸入] --> [資料庫更新] --> [報表刷新]

ReportPanel 與 Service/DAO 關聯圖
ReportPanel
    |
    +--> CourseService / EnrollmentService
             |
             +--> CourseDaoImpl / EnrollmentDaoImpl
                     |
                     +--> MySQL 資料庫

# 教師端圖片

![老師成績管理](pic/teacher_grade_manage.png)
![老師成績管理_批次](pic/teacher_grade_batch.png)
![老師成績管理_單筆](pic/teacher_grade_single.png)
![老師登入](pic/teacher_login.png)
![老師新增課程_衝堂](pic/teacher_add_course_conflict.png)
![老師端統計總表](pic/teacher_report_total.png)
![老師課程及成績管理](pic/teacher_course_grade_manage.png)
![老師課程管理](pic/teacher_course_manage.png)
![老師課程管理_刪除](pic/teacher_course_delete.png)
![老師課程管理_修改](pic/teacher_course_edit.png)
![老師課程管理_新增](pic/teacher_course_add.png)
![註冊](pic/register.png)

# 學生端圖片

![學生成績趨勢](pic/student_grade_trend.png)
![學生登入](pic/student_login.png)
![學生選修成績](pic/student_select_grade.png)
![學生選課_不能低於](pic/student_course_invalid.png)
![學生選課_衝堂機制](pic/student_course_conflict.png)
![學生選課完成](pic/student_course_done.png)





