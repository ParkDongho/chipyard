Managing Published Scala Dependencies
=====================================

Chipyard 1.5에서는 Chisel 3.5 준비를 위해 Chisel, FIRRTL, FIRRTL 인터프리터, Treadle이 소스에서 빌드되는 대신 발행된 종속성으로 관리되도록 전환되었습니다. 이로 인해 해당 서브모듈들이 제거되었습니다. 발행된 버전 간의 전환은 Chipyard의 ``build.sbt`` 에서 지정된 버전을 변경하여 달성할 수 있습니다.

사용 가능한 아티팩트 목록은 search.maven.org 또는 mvnrepository.org를 사용하여 검색할 수 있습니다:

- `Chisel3 <https://mvnrepository.com/artifact/edu.berkeley.cs/chisel3>`_
- `FIRRTL <https://mvnrepository.com/artifact/edu.berkeley.cs/firrtl>`_
- `FIRRTL Interpreter <https://mvnrepository.com/artifact/edu.berkeley.cs/firrtl-interpreter>`_
- `Treadle <https://mvnrepository.com/artifact/edu.berkeley.cs/treadle>`_


Publishing Local Changes
-------------------------

새로운 시스템에서 위 패키지들에 대해 커스텀 소스 수정을 가장 간단하게 적용하는 방법은 해당 저장소의 로컬로 수정된 클론에서 ``sbt +publishLocal`` 을 실행하는 것입니다. 이는 커스텀 변형을 로컬 ivy2 저장소 (일반적으로 ``~/.ivy2``)에 게시합니다. 자세한 내용은 `SBT documentation <https://www.scala-sbt.org/1.x/docs/Publishing.html#Publishing+locally>`_ 를 참조하세요.

실제로는 다음 단계를 수행해야 합니다:

#. 원하는 프로젝트를 체크아웃하고 수정합니다.
#. 각 프로젝트의 ``build.sbt`` 에서 버전을 기록하거나 수정합니다. ``master`` 에서 클론하는 경우 일반적으로 기본 버전은 ``1.X-SNAPSHOT`` 이며, 여기서 ``X`` 는 아직 출시되지 않은 다음 주요 버전입니다. 버전 문자열을 ``1.X-<MYSUFFIX>`` 와 같이 수정하여 변경 사항을 고유하게 식별할 수 있습니다.
#. 각 서브프로젝트에서 ``sbt +publishLocal`` 을 호출합니다. 다른 발행된 종속성을 다시 빌드해야 할 수도 있습니다. SBT는 무엇을 발행하고 어디에 저장하는지 명확히 보여줍니다. ``+`` 는 일반적으로 필요하며 패키지의 모든 크로스 버전이 발행되도록 합니다.
#. Chipyard의 ``build.sbt`` 에서 Chisel 또는 FIRRTL 버전을 로컬로 발행된 패키지의 버전과 일치하도록 업데이트합니다.
#. 일반적으로 Chipyard를 사용합니다. 이제 Chipyard에서 ``make`` 를 호출할 때 SBT가 로컬 ivy2 저장소에서 로컬로 발행된 인스턴스로 종속성을 해결하는 것을 볼 수 있습니다.
#. 작업을 마친 후에는 로컬로 발행된 패키지를 제거하여(ivy2 저장소의 적절한 디렉토리를 제거하여) 나중에 실수로 재사용하는 것을 방지하십시오.

마지막으로 주의할 점: 로컬 ivy 저장소에 발행한 패키지는 시스템에서 빌드 중인 다른 프로젝트에서도 볼 수 있습니다. 예를 들어, Chisel 3.5.0을 로컬로 발행한 경우 Chisel 3.5.0에 의존하는 다른 프로젝트는 Maven에서 사용 가능한 버전(“실제” 3.5.0)보다 로컬로 발행된 변형을 우선적으로 사용합니다. 발행하는 버전을 주의 깊게 확인하고 작업이 완료되면 로컬로 발행된 버전을 제거하는 것이 좋습니다.

