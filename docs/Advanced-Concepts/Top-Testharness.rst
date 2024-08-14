Tops, Test-Harnesses, and the Test-Driver
===========================================

Chipyard SoC의 세 가지 최상위 계층은 ``ChipTop``(DUT), ``TestHarness``, 그리고 ``TestDriver`` 입니다.
``ChipTop`` 과 ``TestHarness`` 는 모두 Chisel 생성기에 의해 생성됩니다.
``TestDriver`` 는 테스트벤치로 사용되며, Rocket Chip의 Verilog 파일입니다.

ChipTop/DUT
-------------------------

``ChipTop`` 은 ``System`` 서브모듈을 인스턴스화하는 최상위 모듈로, 보통 구체적인 클래스인 ``DigitalTop`` 의 인스턴스입니다.
디자인의 대부분은 ``System`` 내에 존재합니다.
``ChipTop`` 계층 내부에 존재하는 다른 구성 요소는 일반적으로 IO 셀, 클럭 수신기 및 멀티플렉서, 리셋 동기화기, 그리고 ``System`` 외부에 존재해야 하는 기타 아날로그 IP입니다.
``IOBinders`` 는 ``System`` 의 IO에 해당하는 ``ChipTop`` IO에 대한 IO 셀을 인스턴스화하는 역할을 합니다.
``HarnessBinders`` 는 ``ChipTop`` 포트에 연결되는 테스트 하네스 부속품을 인스턴스화하는 역할을 합니다.
대부분의 장치 유형과 테스트 부속품은 사용자 정의 ``IOBinders`` 와 ``HarnessBinders`` 를 사용하여 인스턴스화할 수 있습니다.

Custom ChipTops
^^^^^^^^^^^^^^^^^^^^^^^^^

기본 표준 ``ChipTop`` 은 ``DigitalTop`` trait 주변에 ``IOBinders`` 가 IO 셀을 생성하는 최소한의 템플릿을 제공합니다.
테이프아웃, 아날로그 IP 통합 또는 기타 비표준 사용 사례의 경우, Chipyard는 ``BuildTop`` 키를 사용하여 사용자 정의 ``ChipTop`` 을 지정하는 것을 지원합니다.
비표준 IO 셀을 사용하는 사용자 정의 ``ChipTop`` 의 예는 `generators/chipyard/src/main/scala/example/CustomChipTop.scala <https://github.com/ucb-bar/chipyard/blob/main/generators/chipyard/src/main/scala/example/CustomChipTop.scala>`__ 에 제공되어 있습니다.

또한 RocketChip이나 Chipyard SoC 구성 요소를 전혀 사용하지 않는 완전한 사용자 정의 ChipTop을 지정할 수도 있습니다. 이러한 예는 `generators/chipyard/src/main/scala/example/EmptyChipTop.scala <https://github.com/ucb-bar/chipyard/blob/main/generators/chipyard/src/main/scala/example/EmptyChipTop.scala>`__ 에 제공되어 있으며, ``EmptyChipTop`` 예제는 ``make CONFIG=EmptyChipTopConfig TOP=EmptyChipTop`` 명령어로 빌드할 수 있습니다.

System/DigitalTop
-------------------------

Rocket Chip SoC의 시스템 모듈은 케이크 패턴을 사용하여 구성됩니다.
구체적으로, ``DigitalTop`` 은 ``System`` 을 확장하고, ``System`` 은 ``Subsystem`` 을 확장하며, ``Subsystem`` 은 ``BaseSubsystem`` 을 확장합니다.

BaseSubsystem
^^^^^^^^^^^^^^^^^^^^^^^^^

``BaseSubsystem`` 은 ``generators/rocketchip/src/main/scala/subsystem/BaseSubsystem.scala`` 에 정의되어 있습니다. ``BaseSubsystem`` 추상 클래스를 살펴보면 이 클래스가 상위 레벨 버스(frontbus, systembus, peripherybus 등)를 인스턴스화하지만 토폴로지를 지정하지 않는다는 것을 알 수 있습니다.
또한 이 클래스는 칩 설계 후에 Chisel이 작성한 몇 가지 ``ElaborationArtefacts`` (예: 장치 트리 문자열, 외교 그래프 시각화 GraphML 파일 등)를 정의합니다.

Subsystem
^^^^^^^^^^^^^^^^^^^^^^^^^

`generators/chipyard/src/main/scala/Subsystem.scala <https://github.com/ucb-bar/chipyard/blob/main/generators/chipyard/src/main/scala/Subsystem.scala>`__ 에서 Chipyard의 ``Subsystem`` 이 ``BaseSubsystem`` 추상 클래스를 어떻게 확장하는지 볼 수 있습니다. ``Subsystem`` 은 BOOM 또는 Rocket 타일을 정의하고 인스턴스화하는 ``HasBoomAndRocketTiles`` trait을 혼합합니다. 여기서 우리는 타일에 대한 hartid와 리셋 벡터를 지정하는 기본 IO를 연결합니다.

System
^^^^^^^^^^^^^^^^^^^^^^^^^

``generators/chipyard/src/main/scala/System.scala`` 에서 ``System`` 의 정의를 완성합니다.

- ``HasHierarchicalBusTopology`` 는 Rocket Chip에서 정의되며 상위 레벨 버스 간의 연결을 지정합니다.
- ``HasAsyncExtInterrupts`` 및 ``HasExtInterruptsModuleImp`` 는 외부 인터럽트에 대한 IO를 추가하고 이를 타일에 적절하게 연결합니다.
- ``CanHave...AXI4Port`` 는 다양한 마스터 및 슬레이브 AXI4 포트를 추가하고, TL-AXI4 변환기를 추가하며, 이를 적절한 버스에 연결합니다.
- ``HasPeripheryBootROM`` 은 BootROM 장치를 추가합니다.

Tops
^^^^^^^^^^^^^^^^^^^^^^^^^

SoC Top은 ``System`` 클래스를 확장하여 사용자 정의 구성 요소에 대한 트레이트를 추가합니다.
Chipyard에서는 NIC, UART, GPIO 등을 추가하고 하드웨어 초기화 방법을 설정하는 것과 같은 작업이 포함됩니다.
이 초기화 방법에 대한 더 많은 정보는 :ref:`Advanced-Concepts/Chip-Communication:Communicating with the DUT` 를 참조하십시오.

TestHarness
-------------------------

``TestHarness`` 와 Top 간의 연결은 Top에 추가된 트레이트에서 정의된 메서드로 수행됩니다.
이러한 메서드가 ``TestHarness`` 에서 호출되면 하네스 범위 내에서 모듈을 인스턴스화한 다음 DUT에 연결할 수 있습니다. 예를 들어, ``CanHaveMasterAXI4MemPortModuleImp`` 트레이트에서 정의된 ``connectSimAXIMem`` 메서드는 ``TestHarness`` 에서 호출될 때 ``SimAXIMems`` 를 인스턴스화하고 이를 Top의 올바른 IO에 연결합니다.

이러한 복잡한 방법으로 Top의 IO에 연결하는 것이 불필요하게 복잡해 보일 수 있지만, 이를 통해 설계자가 특정 트레이트의 구현 세부 사항에 대해 걱정할 필요 없이 사용자 정의 트레이트를 함께 구성할 수 있습니다.

TestDriver
-------------------------

``TestDriver`` 는 ``generators/rocketchip/src/main/resources/vsrc/TestDriver.v`` 에 정의되어 있습니다.
이 Verilog 파일은 ``TestHarness`` 를 인스턴스화하고, 클럭과 리셋 신호를 구동하며, 성공 출력을 해석하여 시뮬레이션을 실행합니다.
이 파일은 ``TestHarness`` 와 Top에 대한 생성된 Verilog와 함께 컴파일되어 시뮬레이터를 생성합니다.

