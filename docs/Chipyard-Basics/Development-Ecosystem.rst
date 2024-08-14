Development Ecosystem
===============================

Chipyard Approach
-------------------------------------------

애자일 하드웨어 설계 및 평가를 향한 트렌드는 컴퓨터 아키텍처 연구자들이 새로운 개념을 개발하기 쉽게 해주는 디버깅 및 구현 도구의 생태계를 제공합니다. Chipyard는 `Berkeley Architecture Research <https://bar.eecs.berkeley.edu/index.html>`__ 내의 여러 프로젝트가 공존하고 함께 사용될 수 있는 단일 위치를 생성하기 위해 이전 작업을 기반으로 구축하려고 합니다. Chipyard는 사용자가 고유한 System on a Chip (SoC)을 생성하고 테스트할 수 있는 "원스톱 숍"이 되는 것을 목표로 합니다.

Chisel/FIRRTL
-------------------------------------------

새로운 RTL 설계를 신속하게 생성하는 데 도움을 주는 도구 중 하나는 `Chisel Hardware Construction Language <https://chisel-lang.org/>`__ 와 `FIRRTL Compiler <https://chisel-lang.org/firrtl/>`__ 입니다. Chisel은 하드웨어 설계자가 매우 매개변수화된 RTL을 생성하는 데 도움을 주는 라이브러리 세트를 제공하는 Scala 내의 임베디드 언어입니다. FIRRTL은 하드웨어를 위한 컴파일러로, 사용자가 불필요한 코드 제거, 회로 분석, 연결성 검사를 수행할 수 있는 FIRRTL 패스를 실행할 수 있게 해줍니다. 이 두 도구를 결합하면 설계 공간 탐색과 새로운 RTL 개발을 신속하게 수행할 수 있습니다.

RTL Generators
-------------------------------------------

이 저장소 내에서 모든 Chisel RTL은 생성기로 작성됩니다. 생성기는 구성 사양에 따라 RTL 코드를 생성하도록 설계된 매개변수화된 프로그램입니다. 생성기를 사용하여 고유한 생성기 프로젝트에 조직된 시스템 구성 요소 모음을 사용하여 System-on-Chip (SoC)을 생성할 수 있습니다. 생성기를 통해 단일 설계 인스턴스가 아닌 SoC 설계 계열을 생성할 수 있습니다!
