## 使用情況調查（2026-11-09）

> 本節為 hanlin-edu-tech repo 清理作業的調查紀錄，待負責人確認後決定封存或刪除。

- 調查結論：未使用
- 建議處置：刪除，理由：本 repo 是 2021-11 的反向代理原型，README 已標記 Deprecated（效能測試慢於 nginx），查無部署與引用，2021-11-25 後無活動。
- 最後更新：pushedAt 2021-11-25；最後 commit `87aad14` 2021-11-25 hotdog929 Deprecated
- 用途：以 Quarkus（Kotlin、native image）實作的 API 轉址代理，依 regex 設定檔（`rule-config.json`）轉送請求；`performance-testing/` 內為與 nginx 的效能比較腳本與報告。repo 內只有 Dockerfile 範本，無 cloudbuild.yaml 或部署設定。

### 檢查結果

| 檢查項目 | 結果 | 依據 |
|---|---|---|
| GitHub 活動 | 2021-11-25 後無 commit | 共 13 個 commit（2021-11-19～2021-11-25，皆為 hotdog929）；分支僅 `main`；tag 0、release 0；PR 0、issue 0；webhook 0；非 fork；Actions workflow 1 個（`Unit Test`，push 觸發，狀態 active），執行紀錄 0 筆 |
| org 內程式碼引用 | 查無 | `gh search code --owner hanlin-edu-tech`：`api-doorman`（12 筆）、`ApiDoorman`（2 筆）全部命中本 repo |
| GCP 部署 | 查無 | 19 個 GCP 專案的 Cloud Run、Cloud Functions、Cloud Build trigger、GCR、Artifact Registry、Cloud Scheduler、App Engine 清單與 8 個 GKE 叢集以 `doorman` 搜尋 0 筆 |
| AWS 部署 | 查無 | 4 個 AWS 帳號的 Lambda、API Gateway（REST 與 HTTP API）、CloudFront、ECR、ECS、EventBridge、SQS、S3 以 `doorman` 搜尋 0 筆 |

### 待確認事項
- 無

# Deprecated, the initial performance test is much slower than nginx.

# api-doorman Project

* 使用 quarkus native
* 轉址規則吃 regex 設定檔

---

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./gradlew build
```
It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./gradlew build -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./gradlew build -Dquarkus.package.type=native
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./gradlew build -Dquarkus.package.type=native -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/api-doorman-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/gradle-tooling.

## Related Guides

- Kotlin ([guide](https://quarkus.io/guides/kotlin)): Write your services in Kotlin

## Provided Code

### RESTEasy Reactive

Easily start your Reactive RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
