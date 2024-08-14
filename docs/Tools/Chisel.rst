Chisel
===========================

`Chisel <https://chisel-lang.org/>`__ 은 스칼라에 내장된 오픈 소스 하드웨어 기술 언어입니다.
이 언어는 고도로 매개변수화된 생성기를 사용한 고급 하드웨어 설계를 지원하며, Rocket Chip과 BOOM 같은 프로젝트를 지원합니다.

Chisel을 작성한 후에는 Chisel 소스 코드가 Verilog로 "변환"되기 전에 여러 단계가 있습니다.
첫 번째 단계는 컴파일 단계입니다.
Chisel을 스칼라 내의 라이브러리로 간주한다면, 이 클래스들이 빌드되는 것은 단지 Chisel 함수를 호출하는 스칼라 클래스일 뿐입니다.
따라서 스칼라/Chisel 파일을 컴파일할 때 발생하는 오류는 타입 시스템을 위반했거나, 문법을 잘못 사용했거나, 그 외의 오류입니다.
컴파일이 완료되면, 정교화(elaboration)가 시작됩니다.
Chisel 생성기는 전달된 모듈 및 구성 클래스들을 사용하여 정교화를 시작합니다.
이 과정에서 Chisel "라이브러리 함수"가 주어진 매개변수로 호출되며, Chisel은 Chisel 코드를 기반으로 회로를 구성하려고 시도합니다.
여기서 런타임 오류가 발생하면, Chisel은 코드와 Chisel "라이브러리" 간의 "위반"으로 인해 회로를 "구성"할 수 없음을 나타냅니다.
그러나 이 과정을 통과하면, 생성기의 출력은 FIRRTL 파일과 기타 여러 관련 자료를 제공합니다!
FIRRTL 파일을 Verilog로 변환하는 방법에 대한 자세한 내용은 :ref:`Tools/FIRRTL:FIRRTL` 을 참조하십시오.

Chisel 사용법과 시작하는 방법에 대한 대화형 튜토리얼은 `Chisel Bootcamp <https://github.com/freechipsproject/chisel-bootcamp>`__ 를 방문하십시오.
또한 API 문서, 뉴스 등을 포함한 Chisel 관련 모든 정보는 `웹사이트 <https://chisel-lang.org/>`__ 에서 확인할 수 있습니다.

