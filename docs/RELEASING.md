# Maven Central 릴리스 절차

`compose-pickers`의 Maven Central 릴리스는 immutable artifact를 올리는 작업이다. 따라서
`main`에서 임의로 배포하지 않고, `VERSION_NAME`과 같은 **annotated tag**를 먼저 만든 뒤
태그를 명시해 수동 워크플로를 실행한다.

## 릴리스 전

1. `gradle.properties`의 `VERSION_NAME`, `CHANGELOG.md`, README의 dependency 예시와
   릴리스 상태 문구를 점검한다.
2. `main`이 원격과 동기화된 깨끗한 상태에서 다음 로컬 게이트를 통과시킨다.

   ```bash
   git diff --check origin/main...HEAD
   ./gradlew :pickers:check :pickers:checkKotlinAbi --no-daemon
   ./gradlew :screenshot-tests:validateDebugScreenshotTest --no-daemon
   ./gradlew :pickers:publishToMavenLocal --no-daemon
   ```

   샘플을 바꾼 릴리스라면 `:sample:compileKotlinDesktop`,
   `:sample:wasmJsBrowserDistribution`, `:sample:assembleDebugAndroidTest`도 실행한다.

   공개 API를 바꾼 릴리스라면 `pickers/api/` dump가 갱신·리뷰됐는지, 그리고 breaking change에
   대응하는 `docs/migration/` 문서가 있는지 확인한다. 무엇을 공개 API로 보는지는
   [product/api-stability-policy.md](product/api-stability-policy.md)에 있다.

   `publishToMavenLocal` 뒤에는 생성된 POM의 `dependencies`를 확인해서 debug 전용 의존성이
   섞여 들어가지 않았는지 본다.

   ```bash
   sed -n '/<dependencies>/,/<\/dependencies>/p' \
     ~/.m2/repository/io/github/kez-lab/compose-pickers-android/<version>/compose-pickers-android-<version>.pom
   ```

3. 현재 `VERSION_NAME`과 같은 형식의 annotated tag를 만든다. 이 프로젝트의 새 릴리스
   형식은 `0.7.0`처럼 `v` 접두사가 없는 숫자 버전이다.

   ```bash
   git tag -a 0.7.0 -m "Release 0.7.0"
   git push origin 0.7.0
   ```

## 배포

GitHub Actions의 **Publish Release** 워크플로를 수동 실행하고 `release_tag`에 정확히
`0.7.0`을 입력한다. 워크플로는 다음을 모두 확인한 뒤 태그가 가리키는 소스만
`publishAndReleaseToMavenCentral`로 배포·공개한다. 이 task는 Central Portal deployment가
검증된 뒤 공개될 때까지 기다리므로, Portal에서 별도로 **Publish**를 누를 필요가 없다.

- tag가 annotated tag인지
- tag commit이 `origin/main`에서 도달 가능한지
- tag 이름이 `VERSION_NAME`과 정확히 일치하는지
- 버전이 `-SNAPSHOT`이 아닌지

GitHub Actions 성공만으로 끝내지 말고, 아래 배포 후 확인에서 공개 Maven repository의
artifact까지 확인한다.

GitHub Actions UI에서 워크플로 정의를 실행할 branch는 현재 `main`을 선택한다. 실제
배포 대상 소스는 `release_tag`로 명시된 tag이므로 branch 선택에 따라 artifact가 바뀌지
않는다.

## 배포 후

1. 워크플로 성공을 확인한다.
2. Maven Central에서 `io.github.kez-lab:compose-pickers:<version>` 및 Android, Desktop,
   iOS, Wasm publication을 확인한다. 색인이 반영되기 전까지는 잠시 지연될 수 있다.
3. 깨끗한 소비자 프로젝트에서 Maven Central 의존성 해석을 확인한다.
4. README의 “아직 배포되지 않음” 상태 문구를 배포 사실과 최신 버전으로 바꾸고, 필요하면
   GitHub Release를 같은 tag에 연결한다.
