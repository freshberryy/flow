# 1. flow 언어 소개

## 1.1 변수
변수는 `타입 변수명 = 초기값;`으로 선언한다. 초기값 없이 선언하는 경우, 오류이다(예:`string x;`).
자료형은 `int, string, float(지수 표기는 지원하지 않음), bool`이 있다. 타입 추론은 지원하지 않는다.  
변수의 스코프는 블록 단위이다. 연속된 선언(예: int `x, y`)는 허용하지 않는다. 변수의 그림자(shadowing)은 허용된다. 
```
int count = 0;
string region = "천안";
```

## 1.2 제어문
`if`와 `else`, `else if`, `for`, `while`을 지원한다. 모든 제어문은 c와 마찬가지로 키워드로 시작해 표현식을 받음으로써 구문을 구분한다. 중괄호 생략은 지원하지 않는다. 
```
if (score > 90) {
    grade = "A";
} else {
    grade = "B";
}

while (i < 10) {
    sum = sum + i;
    i = i + 1;
}
```
break와 continue는 지원하지 않는다.
```
int i = 0;
for(i = 0; i < 5; i++){
    
}
```
for문의 루프 변수는 위와 같이 루프 바깥에서 선언 및 초기화를 거친 뒤 사용해야 한다.

## 1.3 함수
사용자 정의 함수를 지원한다. 반환형(기본 자료형 또는 void)을 명시하여 선언한다. void 형일 경우 return 생략이 가능하다. 모든 return문의 타입은 동일해야 하며, return의 타입이 하나라도 다르면 에러이다. 또한 분기문에서 return 시 모든 가능한 분기에서 return을 해야 한다. 함수 오버로딩이나 디폴트 매개변수 등은 지원하지 않는다. 함수는 일급 객체로 취급될 수 없다. 
```
void hi(string name) {
    print("Hello " + name); 
}
```
```
bool is_seoul(string region) {
    return region == "서울";   
}
```
파라미터 타입을 생략한 함수 선언은 허용하지 않으며, 함수를 인자로 받을 수 없다. 인자는 call by value로 전달된다. 함수 호출 시 인자는 타입이 정확히 일치해야 한다. 

```
--추후 추가 예정--
## 4. 내장 함수
- void import_csv(string path): csv 파일을 읽어 콘솔에 출력한다.
- string[][] csv_to_array(string path): csv 파일을 읽어 2차원 배열로 변환한다.
- int row_length(string[][] arr): 배열의 행 크기를 반환한다.
- int col_length(string[][] arr): 배열의 열 크기를 반환한다.
- void generate_table(string[][] arr, int pk_col): create문과 insert문을 생성하여 출력한다.

## 5. 배열
- 배열은 `string[][]` 형식의 2차원으로 선언 및 초기화한다. 초기화 없이 선언하면 오류이다. csv_to_array() 함수의 반환값을 저장하는 방식으로만 초기화 가능하다.
- 배열 내에는 string 타입만 저장 가능하다.
```
string[][] b = csv_to_array(string path);
```
- 인덱싱은 0-based이며 `arr[i][j]` 형태이다.
- 배열이 함수의 인자로 전달될 때는 call by value로 전달된다.  
- 배열 타입 추론은 불가하고, 배열 비교 연산 역시 불가하다.
- 함수 호출 뒤에 배열 인덱싱은 불가하다.
```

## 1.6 리터럴, 식별자, 연산자, 주석, 구두점

#### 리터럴
리터럴은 `int, string, float, bool(true, false)`형을 지원한다. 문자열 내부의 이스케이프 문자는 지원하지 않는다. 또한 `.5`, `0.` 등의 실수 리터럴은 허용되지 않는다.  

#### 식별자
첫 글자는 알파벳 대소문자 또는 `_`여야 하며, 이후에는 영문자, 숫자, `_`를 허용한다. 키워드와 중복 불가하다. 

### 연산자
`+, -(이항, 단항), *, /, %, ==, !=, <, >, <=, >=, &&, ||, =, !`만을 지원하며, 기능은 c와 동일하다.
연산자 우선순위는 다음과 같다.  
- 1순위(좌결합): 함수 호출(()), 배열 참조([])
- 2순위(우결합): 단항 증감(+, -), 논리 not(!)
- 3순위(좌결합): 곱셈(*), 나눗셈(/), 모듈로(%)
- 4순위(좌결합): 덧셈(+), 뺄셈(-)
- 5순위(좌결합): 관계 대소 연산(<, >, <=, >=)
- 6순위(좌결합): 관계 연산(==, !=)
- 7순위(좌결합): 논리 and(&&)
- 8순위(좌결합): 논리 or(||)
- 9순위(우결합): 대입(=)
- 주의: 단항 `+,-`과 이항 `+,-`를 구분해야 하므로, 둘은 렉서가 아니라 파서에서 구분. `()`과 `[]`도 파서에서 구분.

### 주석
`#`로 시작하는 한 줄짜리 주석을 지원한다. 복수 라인의 주석은 지원하지 않는다.

### 구두점(punctuator)
- `{}` : 블록(제어문, 함수), 배열 초기화
- `()` : 제어문의 조건식, 함수의 파라미터 목록
- `,` : 함수의 파라미터 구분, 배열 초기화
- `;` : statement의 끝, for문의 조건식 내

# 2. 실행 방법
flow 언어는 Java 17 이상에서 사용 가능하다. 빌드 도구로 그레이들을 사용한다.  
실행 절차는 다음과 같다.  

1. 저장소를 클론한다.
```
git clone https://github.com/freshberryy/flow.git
cd flow
```

2. 프로젝트를 빌드한다.
```
./gradlew build
```

3. 에플리케이션을 실행한다. 프로그램의 진입 main() 메서드는 `flow.gui.FlowEditor' 클래스에 정의되어 있다.
```
./gradlew classes
java -cp build/classes/java/main flow.gui.FlowEditor
```
실행 시 별도의 콘솔이 아닌 gui 그래픽 창이 표시된다.  

# 3. 사용 라이브러리
이 프로젝트는  다음 외부 라이브러리를 포함한다.  
- [RSyntaxTextArea](https://github.com/bobbylight/RSyntaxTextArea) 
- 라이선스: BSD 3-Clause License  

RSyntaxTextArea는 자바 스윙으로 작성된 간단한 코드 편집기이다. 프로젝트에 BSD 3-Clause 전문(LICENSE.md)을 포함하였다.  