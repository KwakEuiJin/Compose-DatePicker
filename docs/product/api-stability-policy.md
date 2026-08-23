# API 안정성 정책

> 상태: `0.8.0` 기준. `1.0.0` 이전까지 이 문서가 실제 약속을 정의한다.
> 관련 문서: [wheel-picker-engine-direction.md](wheel-picker-engine-direction.md), [../RELEASING.md](../RELEASING.md)

## 현재 단계: 0.x

`0.x`에서는 **source compatibility보다 최종 API 형태를 우선한다.** 메인테이너가 명시적으로 승인한
breaking change는 minor 버전에서 발생할 수 있다. 대신 다음을 항상 함께 제공한다.

- `CHANGELOG.md`의 `Changed (Breaking)` 항목
- `docs/migration/` 아래의 마이그레이션 문서
- 갱신·리뷰된 `pickers/api/` ABI dump
- README, README_KO, KDoc의 동시 갱신

## 무엇이 공개 API인가

공개 API는 `pickers/api/`의 ABI dump에 기록된 것이 전부다. dump에 없는 것은 지원 대상이 아니다.

- `pickers/api/pickers.klib.api` — 공통 및 Kotlin/Native·Wasm 타겟
- `pickers/api/android/pickers.api` — Android
- `pickers/api/desktop/pickers.api` — Desktop(JVM)

이 dump는 릴리스 게이트 데이터로 커밋된다. 공개 API를 바꾸는 PR은 `:pickers:updateKotlinAbi`로
dump를 갱신하고, 리뷰어는 의도한 picker/state API 변경과 컴파일러가 만들어내는 부수적 diff를
분리해서 본다.

`:pickers`는 Kotlin explicit API mode를 사용한다. 새 선언이 visibility를 생략해서 실수로 공개
API가 되는 일은 컴파일 단계에서 막힌다.

명시적으로 공개 API가 **아닌** 것:

- `internal` 선언. 예: composite picker가 사용하는 settled-only `Picker`.
- `@Preview` composable. 도구용 private 코드이며 ABI dump에 나타나면 안 된다.
- `:sample`, `:benchmark`, `:screenshot-tests` 모듈의 모든 코드. `QuantityUnitPicker`와
  combined `DateTimePicker`는 저장소 계약 증거일 뿐 배포 API가 아니다.
- 전이 의존성이 제공하는 타입. `compose-material3`는 기본값 계산에만 쓰이며 공개 시그니처에
  등장하지 않는다.

## 1.0.0에서 약속할 것

`1.0.0`부터는 다음을 지킨다.

- **Binary compatibility**: `1.x` 안에서 ABI dump의 항목을 제거하거나 시그니처를 바꾸지 않는다.
- **Source compatibility**: 위와 동일한 범위에서 유지한다.
- **제거 절차**: 공개 API 제거는 최소 한 개의 minor 버전 동안 `@Deprecated(WARNING)`를 거쳐
  `ERROR`로 올린 뒤 다음 major에서만 삭제한다.
- **실험 API**: 확정되지 않은 새 API는 opt-in 어노테이션 뒤에 두고, 그 API에는 위 호환성 약속을
  적용하지 않는다.

`1.0.0` 이전에 충족해야 하는 조건은
[wheel-picker-engine-direction.md](wheel-picker-engine-direction.md)의 Phase 4에 있다. 특히 외부
개발자 first-use 기록과 외부 pilot 기록은 API를 동결하기 전에 필요하다. 저장소 내부의 테스트,
샘플, AI 리뷰는 품질 증거이지 외부 수요 증거가 아니다.

## 버전 규칙

`MAJOR.MINOR.PATCH` (Semantic Versioning). `VERSION_NAME`은 `gradle.properties`에 있고, 릴리스
tag는 `v` 접두사 없이 같은 문자열을 쓴다.

| 변경 | 0.x | 1.x 이후 |
| :--- | :--- | :--- |
| 공개 API 제거·시그니처 변경 | MINOR | MAJOR |
| 공개 API 추가 | MINOR | MINOR |
| 동작 변경(공개 API 유지) | MINOR | MINOR, CHANGELOG에 명시 |
| 버그 수정 | PATCH | PATCH |

## 플랫폼·툴체인 지원 범위

다음 항목의 변경은 공개 API 변경과 동일하게 CHANGELOG의 breaking 항목으로 기록한다.

- **Android `minSdk`**: 현재 24. 상향은 breaking으로 취급한다.
- **배포 타겟**: 현재 Android, iOS(`iosArm64`, `iosSimulatorArm64`), Desktop(JVM), Web(Wasm).
  타겟 제거는 breaking이다. `0.8.0`의 `iosX64` 제거가 그 예다.
- **Kotlin / Compose Multiplatform 버전**: 라이브러리는 컴파일에 사용한 Kotlin·CMP 버전과
  호환되는 소비자를 대상으로 한다. Compose compiler 산출물이 ABI dump에 남기 때문에 툴체인 상향은
  ABI diff를 만들 수 있고, 그 diff는 지원 API 변경과 구분해서 기록한다.
- **JVM target**: 17.

## 배포 artifact에 포함되지 않는 것

라이브러리 소비자에게 전달되면 안 되는 의존성은 `debugImplementation`으로 격리한다. 현재 대상은
Android Studio preview 렌더러(`ui-tooling`)와 테스트 매니페스트다. 새 의존성을 추가할 때는
`:pickers:publishToMavenLocal` 후 생성된 POM의 `dependencies`를 확인한다.
