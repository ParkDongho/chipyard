IOBinders and HarnessBinders
============================

Chipyard에서는 디지털 시스템 IO와 TestHarness 간의 연결을 위해 특별한 ``Parameters`` 키인 ``IOBinders`` 와 ``HarnessBinders`` 를 사용합니다.

IOBinders
---------

``IOBinder`` 함수는 ``ChipTop`` 레이어에서 IO 셀과 IOPort를 인스턴스화하는 역할을 합니다.

``IOBinders`` 는 일반적으로 ``OverrideIOBinder`` 또는 ``ComposeIOBinder`` 매크로를 사용하여 정의됩니다. ``IOBinder`` 는 특정 트레이트와 일치하는 ``Systems`` 를 대상으로 IO 포트와 IO 셀을 생성하고, 생성된 포트와 셀의 목록을 반환하는 함수로 구성됩니다.

예를 들어, ``WithUARTIOCells`` IOBinder는 UART 포트가 있는 ``System``(``HasPeripheryUARTModuleImp``)에 대해 ``ChipTop`` 내에 포트(``ports``)와 적절한 타입과 방향을 가진 IO 셀(``cells2d``)을 생성합니다. 이 함수는 생성된 포트 목록과 생성된 IO 셀 목록을 반환합니다. 생성된 포트 목록은 ``HarnessBinders`` 에 전달되어 ``TestHarness`` 장치에 연결될 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/iobinders/IOBinders.scala
   :language: scala
   :start-after: DOC include start: WithUARTIOCells
   :end-before: DOC include end: WithUARTIOCells

HarnessBinders
--------------

``HarnessBinder`` 함수는 ``TestHarness`` 에서 ``ChipTop`` 의 IO에 연결할 모듈을 결정합니다. ``HarnessBinder`` 인터페이스는 다양한 시뮬레이션/구현 모드에서 재사용될 수 있도록 설계되어, 타겟 디자인과 시뮬레이션 및 테스트 간의 결합을 최소화합니다.

 * SW RTL 또는 GL 시뮬레이션의 경우, 기본 ``HarnessBinders`` 세트는 외부 메모리 또는 UART와 같은 다양한 장치의 소프트웨어 시뮬레이션 모델을 인스턴스화하고 이러한 모델을 ``ChipTop`` 의 IO에 연결합니다.
 * FireSim 시뮬레이션의 경우, FireSim 전용 ``HarnessBinders`` 는 ``Bridges`` 를 인스턴스화하여 시뮬레이션된 칩의 IO 간에 정확한 사이클 시뮬레이션을 지원합니다. 자세한 내용은 FireSim 문서를 참조하십시오.
 * 미래에는 Chipyard FPGA 프로토타이핑 플로우가 ``HarnessBinders`` 를 사용하여 ``ChipTop`` IO를 FPGA 하니스의 다른 장치 또는 IO에 연결할 수 있습니다.

``IOBinders`` 와 마찬가지로, ``HarnessBinders`` 는 매크로(``OverrideHarnessBinder, ComposeHarnessBinder``)를 사용하여 정의되며, 특정 트레이트와 일치하는 ``Systems`` 를 대상으로 합니다. 그러나 ``HarnessBinders`` 는 또한 ``TestHarness``(``th: HasHarnessSignalReferences``)와 해당하는 ``IOBinder`` 가 생성한 포트 목록(``ports: Seq[Data]``)에 대한 참조도 전달받습니다.

예를 들어, ``WithUARTAdapter`` 는 앞서 설명한 ``WithUARTIOCells`` 에 의해 생성된 포트가 있는 경우 UART SW 디스플레이 어댑터를 해당 포트에 연결합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/harness/HarnessBinders.scala
   :language: scala
   :start-after: DOC include start: WithUARTAdapter
   :end-before: DOC include end: WithUARTAdapter

``IOBinder`` 와 ``HarnessBinder`` 시스템은 타겟 디자인과 시뮬레이션 시스템 간의 결합을 최소화하기 위해 설계되었습니다.

지정된 칩 IO 세트에 대해, 여러 시뮬레이션 플랫폼("harnesses"라고도 함)뿐만 아니라 여러 시뮬레이션 전략이 있을 수 있습니다. 예를 들어, 백킹 AXI4 메모리 포트를 정확한 DRAM 모델(``SimDRAM``)에 연결할지 또는 간단한 시뮬레이션 메모리 모델(``SimAXIMem``)에 연결할지는 ``HarnessBinders`` 에서 분리되어 처리되며, 이는 타겟 RTL 생성에 영향을 미치지 않습니다.

마찬가지로, 주어진 시뮬레이션 플랫폼과 전략에 대해, 칩 IO를 생성하는 데 여러 가지 전략이 있을 수 있습니다. 이 타겟 디자인 구성은 ``IOBinders`` 에 의해 분리되어 처리됩니다.
