.. _mmio-accelerators:

MMIO Peripherals
==================

MMIO 주변 장치를 생성하는 가장 쉬운 방법은 GCD TileLink MMIO 예제를 따르는 것입니다. Chipyard와 Rocket Chip SoCs는 주로 Tilelink를 온칩 인터커넥트 프로토콜로 사용하므로, 이 섹션에서는 주로 Tilelink 기반 주변 장치 설계에 초점을 맞출 것입니다. 그러나, Tilelink 그래프를 통해 변환기를 통해 연결된 AXI4 기반 주변 장치의 정의 방법은 ``generators/chipyard/src/main/scala/example/GCD.scala`` 를 참조하십시오.

MMIO 매핑된 주변 장치를 생성하려면, Diplomacy Node로 TileLink 포트를 포함하는 ``LazyModule`` 래퍼와 MMIO의 구현 및 TileLink가 아닌 I/O를 정의하는 내부 ``LazyModuleImp`` 클래스를 지정해야 합니다.

이 예제에서는 GCD를 계산하는 MMIO 주변 장치를 연결하는 방법을 보여줍니다.
전체 코드는 ``generators/chipyard/src/main/scala/example/GCD.scala`` 에서 찾을 수 있습니다.

이 경우, 실제로 GCD를 수행하는 하위 모듈 ``GCDMMIOChiselModule`` 을 사용합니다. ``GCDTL`` 및 ``GCDAXI4`` 클래스는 TileLink 또는 AXI4 포트를 생성하고 내부 ``GCDMMIOChiselModule`` 을 래핑하는 ``LazyModule`` 클래스입니다.
``node`` 객체는 주변 장치를 Diplomacy 인터커넥트 그래프에 연결하는 Diplomacy 노드입니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/GCD.scala
    :language: scala
    :start-after: DOC include start: GCD chisel
    :end-before: DOC include end: GCD chisel

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/GCD.scala
    :language: scala
    :start-after: DOC include start: GCD router
    :end-before: DOC include end: GCD router


Advanced Features of RegField Entries
-------------------------------------

``RegField`` 는 읽기 전용 및 쓰기 전용 메모리 매핑 레지스터를 하드웨어에 여러 방식으로 인터페이스할 수 있는 다형성 ``r`` 및 ``w`` 메서드를 제공합니다.

* ``RegField.r(2, status)`` 는 ``status`` 신호의 현재 값을 읽을 때 캡처하는 2비트 읽기 전용 레지스터를 생성하는 데 사용됩니다.
* ``RegField.r(params.width, gcd)`` 는 탈결합 핸드셰이킹 인터페이스 ``gcd`` 를 읽기 전용 메모리 매핑 레지스터에 "연결"합니다. 이 레지스터가 MMIO를 통해 읽힐 때 ``ready`` 신호가 어서트됩니다. 이는 glue logic을 통해 GCD 모듈의 ``output_ready`` 에 연결됩니다.
* ``RegField.w(params.width, x)`` 는 평범한 레지스터를 MMIO를 통해 노출하지만 쓰기 전용으로 만듭니다.
* ``RegField.w(params.width, y)`` 는 탈결합 인터페이스 신호 ``y`` 를 쓰기 전용 메모리 매핑 레지스터와 연관시켜, 레지스터가 기록될 때 ``y.valid`` 가 어서트되도록 합니다.

``y`` 의 ready/valid 신호가 GCD 모듈의 ``input_ready`` 및 ``input_valid`` 신호에 각각 연결되어 있기 때문에, 이 레지스터 맵과 glue logic은 ``y`` 에 기록할 때 GCD 알고리즘을 트리거하는 효과를 갖습니다. 따라서 알고리즘은 먼저 ``x`` 를 기록한 다음 ``y`` 에 트리거 기록을 수행하여 설정됩니다. 상태 확인은 폴링을 통해 수행할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/GCD.scala
    :language: scala
    :start-after: DOC include start: GCD instance regmap
    :end-before: DOC include end: GCD instance regmap

.. note::
   이전 Chipyard 및 Rocket-Chip 버전에서는 ``TLRegisterRouter`` 추상 클래스를 사용하여 MMIO 주변 장치를 구성하는 데 필요한 ``TLRegisterNode`` 및 ``LazyModule`` 클래스를 추상화했습니다. 이는 제거되었으며, 사용자가 필요한 클래스를 명시적으로 구성하도록 요구합니다.

   이는 표준 ``Modules`` 및 ``LazyModules`` 가 구성되는 방식과 더 밀접하게 일치하며, MMIO 주변 장치가 ``Module`` 및 ``LazyModule`` 설계 패턴에 어떻게 맞는지 더 명확하게 합니다.


Connecting by TileLink
----------------------

TileLink Diplomacy 그래프에 연결하는 핵심은 이 주변 장치의 TileLink 노드를 구성하는 것입니다.
이 경우, 주변 장치가 일부 레지스터 매핑된 주소 공간의 관리자로 작동하므로, ``TLRegisterNode`` 객체를 사용합니다.
``TLRegisterNode`` 객체의 매개변수는 관리되는 공간의 크기, 기본 주소 및 포트 너비를 지정합니다.

레지스터 매핑된 주변 장치 내에서 제어 레지스터는 위에서 설명한 것처럼 ``node.regmap`` 함수를 사용하여 매핑될 수 있습니다.
유사한 절차는 AXI4 및 TileLink 주변 장치 모두에 적용됩니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/GCD.scala
    :language: scala
    :start-after: DOC include start: GCD router
    :end-before: DOC include end: GCD router


Top-level Traits
----------------

모듈을 생성한 후, 이를 SoC에 연결해야 합니다.
``LazyModule`` 추상 클래스는 주변 장치의 I/O를 나타내는 TileLink 노드를 포함합니다.
간단한 메모리 매핑 주변 장치의 경우, 주변 장치의 TileLink 노드를 관련 버스에 연결해야 합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/GCD.scala
    :language: scala
    :start-after: DOC include start: GCD lazy trait
    :end-before: DOC include end: GCD lazy trait

또한 이 주변 장치의 AXI4 버전에서는 추가 AXI4 버퍼 및 변환기를 배치해야 함을 알 수 있습니다.

I/O를 노출하는 주변 장치는 ``InModuleBody`` 를 사용하여 I/O를 ``DigitalTop`` 모듈로 연결할 수 있습니다.
이 예제에서, GCD 모듈의 ``gcd_busy`` 신호는 DigitalTop의 I/O로 노출됩니다.

Constructing the DigitalTop and Config
--------------------------------------

이제 우리의 특성을 시스템 전체에 혼합하고자 합니다.
이 코드는 ``generators/chipyard/src/main/scala/DigitalTop.scala`` 에서 가져온 것입니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/DigitalTop.scala
    :language: scala
    :start-after: DOC include start: DigitalTop
    :end-before: DOC include end: DigitalTop

마찬가지로 ``LazyModule`` 및 모듈 구현을 생성하기 위해 별도의 클래스가 필요하듯이, 시스템을 구성하기 위해 두 개의 클래스가 필요합니다.
``DigitalTop`` 클래스는 ``DigitalTop`` 을 매개변수화하고 정의하는 특성 집합을 포함합니다. 일반적으로 이러한 특성은 ``DigitalTop`` 에 IO 또는 주변 장치를 선택적으로 추가합니다.
``DigitalTop`` 클래스에는 사전 설명 코드가 포함되어 있으며, 또한 모듈 구현을 생성하기 위한 ``lazy val`` 도 포함되어 있습니다(``LazyModule`` 이기 때문에).
``DigitalTopModule`` 클래스는 실제로 합성되는 RTL입니다.

마지막으로, 앞서 정의된 ``WithGCD`` 구성 조각을 사용하는 ``generators/chipyard/src/main/scala/config/MMIOAcceleratorConfigs.scala`` 에서 구성 클래스를 생성합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/GCD.scala
    :language: scala
    :start-after: DOC include start: GCD config fragment
    :end-before: DOC include end: GCD config fragment

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/MMIOAcceleratorConfigs.scala
    :language: scala
    :start-after: DOC include start: GCDTLRocketConfig
    :end-before: DOC include end: GCDTLRocketConfig

Testing
-------

이제 GCD가 작동하는지 테스트할 수 있습니다. 테스트 프로그램은 ``tests/gcd.c`` 에 있습니다.

.. literalinclude:: ../../tests/gcd.c
    :language: c

이는 우리가 이전에 정의한 레지스터에 쓰기를 수행합니다.
모듈의 MMIO 영역의 기본 주소는 기본적으로 0x2000입니다.
이는 Verilog 코드를 생성할 때 주소 맵 부분에 출력됩니다.
또한, 이 내용이 생성된 ``.json`` 주소맵 파일에서 어떻게 변경되는지도 확인할 수 있습니다.

이 프로그램을 ``make`` 로 컴파일하면 ``gcd.riscv`` 실행 파일이 생성됩니다.

이제 모든 작업이 완료되었으므로 시뮬레이션을 실행할 수 있습니다.

.. code-block:: shell

    cd sims/verilator
    make CONFIG=GCDTLRocketConfig BINARY=../../tests/gcd.riscv run-binary

