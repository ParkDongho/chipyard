.. _dsptools-blocks:

Dsptools Blocks
===============

Dsptools는 커스텀 신호 처리 가속기를 작성하는 데 도움을 주는 Chisel 라이브러리입니다. 다음과 같은 기능을 제공합니다:
* 수학 연산을 보다 직접적으로 표현할 수 있는 타입과 도우미 기능을 제공합니다.
* 실제 값과 복소수 값을 모두 처리할 수 있는 FIR 필터 생성기와 같은 다형성 생성기를 작성할 수 있는 타입 클래스가 있습니다.
* DSP 블록을 패키징하고 이를 rocketchip 기반 SoC에 통합하는 구조를 제공합니다.
* DSP 회로를 테스트하기 위한 테스트 하니스와 DSP 블록용 VIP 스타일 드라이버 및 모니터를 제공합니다.

`Dsptools repository <https://github.com/ucb-bar/dsptools/>`_ 에는 더 많은 문서가 있습니다.


A ``DspBlock`` 는 SoC에 통합할 수 있는 신호 처리 기능의 기본 단위입니다.
이 블록은 AXI4-스트림 인터페이스와 선택적인 메모리 인터페이스를 가집니다.
``DspBlocks`` 는 쉽게 설계되고, 유닛 테스트되며, 레고 스타일로 조립되어 복잡한 기능을 구축할 수 있습니다.
``DspChain`` 은 ``DspBlocks`` 를 조립하는 한 가지 예입니다. 이 경우 스트리밍 인터페이스는 파이프라인으로 직렬 연결되고, 메모리 인터페이스가 있는 모든 블록에 연결된 버스가 인스턴스화됩니다.

Chipyard에는 MMIO 주변 장치로 rocketchip 기반 SoC에 통합된 ``DspBlock`` 의 예제 설계가 있습니다. 커스텀 ``DspBlock`` 은 앞에 ``ReadQueue`` 가 있고 뒤에 ``WriteQueue`` 가 있어 스트리밍 인터페이스에 메모리 매핑된 액세스를 제공하여 로켓 코어가 ``DspBlock`` 과 상호 작용할 수 있게 합니다 [#]_. 이 섹션은 주로 Tilelink 기반 주변 장치 설계에 중점을 둡니다. 그러나 Dsptools에서 제공하는 리소스를 통해 유사한 단계를 따라 AXI4 기반 주변 장치를 정의할 수도 있습니다. 또한 여기 제공된 예제는 간단하지만, 예를 들어 `OFDM baseband <https://github.com/grebe/ofdm>`_ 또는 `spectrometer <https://github.com/ucb-art/craft2-chip>`_ 와 같은 더 복잡한 가속기를 구현하는 데 확장될 수 있습니다.

.. figure:: ../_static/images/fir-block-diagram.svg
    :align: center
    :alt: FIR이 로켓과 통합되는 방법을 보여주는 블록 다이어그램.
    :width: 400px

이 예제에서는 위 그림과 같이 Dsptools를 사용하여 생성된 간단한 FIR 필터를 MMIO 주변 장치로 연결하는 방법을 보여줍니다. 전체 코드는 ``generators/chipyard/src/main/scala/example/dsptools/GenericFIR.scala`` 에서 확인할 수 있습니다. FIR 대신 준비된 유효 인터페이스를 사용하는 모듈을 대체하여도 동일한 결과를 얻을 수 있습니다. 모듈의 읽기 및 유효 신호가 해당 ``DSPBlock`` 래퍼의 신호에 연결되고 해당 래퍼가 ``ReadQueue`` 및 ``WriteQueue`` 가 있는 체인에 배치되는 한, 이 단계에서 설정한 일반적인 개요를 따르면 해당 블록과 메모리 매핑된 IO로 상호 작용할 수 있습니다.

``GenericFIR`` 모듈은 FIR 모듈의 전체 래퍼입니다. 이 모듈은 FIR 직접 폼 아키텍처에서 하나의 계수를 계산하는 각 ``GenericFIRDirectCell`` 서브 모듈을 연결합니다. 두 모듈 모두 타입 제네릭임을 주목해야 합니다. 즉, 덧셈, 곱셈, 항등 연산을 구현하는 데이터 타입 ``T`` 에 대해 인스턴스화할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/dsptools/GenericFIR.scala
    :language: scala
    :start-after: DOC include start: GenericFIR chisel
    :end-before: DOC include end: GenericFIR chisel

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/dsptools/GenericFIR.scala
    :language: scala
    :start-after: DOC include start: GenericFIRDirectCell chisel
    :end-before: DOC include end: GenericFIRDirectCell chisel

Creating a DspBlock
-------------------

FIR 필터를 MMIO 주변 장치로 연결하는 첫 번째 단계는 ``GenericFIR`` 모듈을 래핑하는 ``DspBlock`` 의 추상 서브클래스를 생성하는 것입니다. 스트리밍 출력 및 입력은 ``UInt`` 로 포장되고 풀립니다. 제어 신호가 있다면, 이 단계에서 원시 IO에서 메모리 매핑으로 변환됩니다. 이 과정의 주요 단계는 다음과 같습니다.

1. ``GenericFIR`` 를 ``GenericFIRBlock`` 내에서 인스턴스화합니다.
2. 인 및 아웃 연결에서 준비 및 유효 신호를 연결합니다.
3. 모듈 입력 데이터를 ``GenericFIR`` 의 입력 타입 (``GenericFIRBundle``)으로 캐스팅하고 연결합니다.
4. ``GenericFIR``의 출력을 ``UInt`` 로 캐스팅하고 모듈 출력에 연결합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/dsptools/GenericFIR.scala
    :language: scala
    :start-after: DOC include start: GenericFIRBlock chisel
    :end-before: DOC include end: GenericFIRBlock chisel

이 시점에서 ``GenericFIRBlock`` 에는 메모리 인터페이스 유형이 지정되지 않았음을 주목하십시오. 이 추상 클래스는 AXI-4, TileLink, AHB 또는 원하는 다른 메모리 인터페이스를 사용하는 다양한 버전을 생성하는 데 사용할 수 있습니다.

Connecting DspBlock by TileLink
-------------------------------
이러한 클래스가 구현되면 ``TLDspBlock`` 트레이트를 믹스인하여 ``GenericFIRBlock`` 을 확장함으로써 체인을 구성할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/dsptools/GenericFIR.scala
    :language: scala
    :start-after: DOC include start: TLGenericFIRBlock chisel
    :end-before: DOC include end: TLGenericFIRBlock chisel

그런 다음 ``generators/chipyard/src/main/scala/example/dsptools/DspBlocks.scala`` 에 있는 ``TLWriteQueue`` 및 ``TLReadeQueue`` 모듈을 사용하여 최종 체인을 구성할 수 있습니다. 체인은 ``TLChain`` 의 생성자에 팩토리 함수 목록을 전달하여 생성됩니다. 생성자는 이러한 ``DspBlocks`` 를 자동으로 인스턴스화하고, 스트림 노드를 순서대로 연결하며, 버스를 생성하고 메모리 인터페이스가 있는 ``DspBlocks`` 를 버스에 연결합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/dsptools/GenericFIR.scala
    :language: scala
    :start-after: DOC include start: TLGenericFIRChain chisel
    :end-before: DOC include end: TLGenericFIRChain chisel

Top Level Traits
----------------
이전의 MMIO 예제와 마찬가지로, 케이크 패턴을 사용하여 모듈을 SoC에 연결합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/dsptools/GenericFIR.scala
    :language: scala
    :start-after: DOC include start: CanHavePeripheryStreamingFIR chisel
    :end-before: DOC include end: CanHavePeripheryStreamingFIR chisel

이 시점에서 FIR의 데이터 타입을 결정하는 것을 주목하십시오. 복소수 값을 가진 FIR 필터를 인스턴스화하는 구성을 생성하는 것처럼 FIR에 대해 다른 타입을 사용하는 다른 구성을 생성할 수 있습니다.

Constructing the Top and Config
-------------------------------

이전 MMIO 예제의 경로를 다시 따라가며, 이제 시스템 전체에 트레이트를 믹스인하고자 합니다. 코드는 ``generators/chipyard/src/main/scala/DigitalTop.scala`` 에서 가져왔습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/DigitalTop.scala
    :language: scala
    :start-after: DOC include start: DigitalTop
    :end-before: DOC include end: DigitalTop

마지막으로, ``generators/chipyard/src/main/scala/config/MMIOAcceleratorConfigs.scala`` 에 ``generators/chipyard/src/main/scala/example/dsptools/GenericFIR.scala`` 에서 정의된 ``WithFIR`` 믹스인을 사용하는 구성 클래스를 생성합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/dsptools/GenericFIR.scala
    :language: scala
    :start-after: DOC include start: WithStreamingFIR
    :end-before: DOC include end: WithStreaming

FIR

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/MMIOAcceleratorConfigs.scala
    :language: scala
    :start-after: DOC include start: StreamingFIRRocketConfig
    :end-before: DOC include end: StreamingFIRRocketConfig

FIR Testing
-----------

이제 FIR이 작동하는지 테스트할 수 있습니다. 테스트 프로그램은 ``tests/streaming-fir.c`` 에 있습니다.

.. literalinclude:: ../../tests/streaming-fir.c
    :language: c

테스트는 일련의 값을 FIR에 전달하고 출력을 계산의 골든 모델과 비교합니다. 모듈의 MMIO 쓰기 영역의 기본값은 0x2000이고 읽기 영역의 기본값은 0x2100입니다.

``make`` 를 사용하여 이 프로그램을 컴파일하면 ``streaming-fir.riscv`` 실행 파일이 생성됩니다.

이제 시뮬레이션을 실행할 수 있습니다.

.. code-block:: shell

    cd sims/verilator
    make CONFIG=StreamingFIRRocketConfig BINARY=../../tests/streaming-fir.riscv run-binary
.. [#] ``ReadQueue`` 및 ``WriteQueue`` 는 ``DspBlock`` 을 작성하는 방법과 이를 로켓에 통합하는 방법을 보여주는 좋은 예이지만, 실제 설계에서는 DMA 엔진이 더 선호됩니다. ``ReadQueue`` 는 빈 큐를 읽으려고 하면 프로세서를 중단시키고, ``WriteQueue`` 는 큐가 가득 찼을 때 쓰기를 시도하면 중단되며, 이는 DMA 엔진이 더 우아하게 피할 수 있습니다. 또한, DMA 엔진은 데이터를 이동시키는 작업을 수행하여 프로세서가 다른 유용한 작업을 수행하거나 (또는 대기)할 수 있도록 해줍니다.

