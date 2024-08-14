.. _incorporating-verilog-blocks:

Incorporating Verilog Blocks
============================

기존 Verilog IP를 활용하는 것은 많은 칩 설계 흐름에서 중요한 부분입니다. 다행히도 Chisel과 Chipyard는 Verilog 통합을 광범위하게 지원합니다.

여기에서는 최대 공약수(GCD) 알고리즘의 Verilog 구현을 사용하는 MMIO 주변 장치를 통합하는 과정을 살펴보겠습니다. Verilog 주변 장치를 추가하는 데는 몇 가지 단계가 있습니다:

* Verilog 리소스 파일을 프로젝트에 추가하기
* Verilog 모듈을 나타내는 Chisel ``BlackBox`` 정의하기
* ``BlackBox`` 를 인스턴스화하고 ``RegField`` 항목과 인터페이스 정의하기
* 주변 장치를 사용하는 칩 ``Top`` 및 ``Config`` 설정하기

Adding a Verilog Blackbox Resource File
---------------------------------------

앞서 설명한 것처럼, 주변 장치를 독자적인 생성기 프로젝트의 일부로 통합할 수 있습니다. 그러나 Verilog 리소스 파일은 Chisel(Scala) 소스와는 다른 디렉토리에 있어야 합니다.

.. code-block:: none

    generators/yourproject/
        build.sbt
        src/main/
            scala/
            resources/
                vsrc/
                    YourFile.v

이 구체적인 GCD 예제에서는 ``chipyard`` 프로젝트에 정의된 ``GCDMMIOBlackBox`` Verilog 모듈을 사용합니다. Scala와 Verilog 소스는 규정된 디렉토리 구조를 따릅니다.

.. code-block:: none

    generators/chipyard/
        build.sbt
        src/main/
            scala/
                example/
                    GCD.scala
            resources/
                vsrc/
                    GCDMMIOBlackBox.v

Defining a Chisel BlackBox
--------------------------

Chisel ``BlackBox`` 모듈은 외부 Verilog 소스에 의해 정의된 모듈을 인스턴스화하는 방법을 제공합니다. Blackbox의 정의에는 Verilog 모듈의 인스턴스로 변환할 수 있게 해주는 여러 가지 측면이 포함됩니다:

* ``io`` 필드: Verilog 모듈의 포트 리스트에 해당하는 필드를 가진 번들.
* Verilog 매개변수 이름에서 정교화된 값으로의 ``Map`` 을 받는 생성자 매개변수
* Verilog 소스 종속성을 나타내기 위해 추가된 하나 이상의 리소스

특히 관심 있는 점은 매개변수화된 Verilog 모듈이 가능한 매개변수 값의 전체 공간을 전달받을 수 있다는 것입니다. 이 값들은 Chisel 생성기에서 정교화 시간 값에 따라 달라질 수 있으며, 이 예제에서는 GCD 계산의 비트 너비에 따라 달라집니다.

**Verilog GCD 포트 리스트 및 매개변수**

.. literalinclude:: ../../generators/chipyard/src/main/resources/vsrc/GCDMMIOBlackBox.v
    :language: Verilog
    :start-after: DOC include start: GCD portlist
    :end-before: DOC include end: GCD portlist

**Chisel BlackBox Definition**

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/GCD.scala
    :language: scala
    :start-after: DOC include start: GCD blackbox
    :end-before: DOC include end: GCD blackbox

Instantiating the BlackBox and Defining MMIO
--------------------------------------------

다음으로, BlackBox를 인스턴스화해야 합니다. 시스템 버스에서 외교적 메모리 매핑을 활용하기 위해, 여전히 Chisel 레벨에서 주변 장치를 통합해야 하며, 이를 위해 TileLink RegisterNode를 인스턴스화하는 LazyModule 래퍼를 인스턴스화해야 합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/GCD.scala
    :language: scala
    :start-after: DOC include start: GCD router
    :end-before: DOC include end: GCD router

LazyModule 내에서, ``regmap`` 함수를 호출하여 MMIO 포트에 와이어 및 레지스터를 연결할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/GCD.scala
    :language: scala
    :start-after: DOC include start: GCD instance regmap
    :end-before: DOC include end: GCD instance regmap

Defining a Chip with a BlackBox
---------------------------------------

GCD 인스턴스화가 Chisel 모듈과 Verilog 모듈 중 선택할 수 있도록 매개변수화되어 있기 때문에, 구성을 만드는 것은 간단합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/MMIOAcceleratorConfigs.scala
    :language: scala
    :start-after: DOC include start: GCDAXI4BlackBoxRocketConfig
    :end-before: DOC include end: GCDAXI4BlackBoxRocketConfig

GCD의 TL/AXI4, BlackBox/Chisel 버전을 선택할 수 있도록 매개변수화를 조정할 수 있습니다.

Software Testing
----------------

GCD 모듈은 더 복잡한 인터페이스를 가지고 있으므로, 각 트리거링 읽기 또는 쓰기 전에 장치의 상태를 확인하기 위해 폴링이 사용됩니다.

.. literalinclude:: ../../tests/gcd.c
    :language: scala
    :start-after: DOC include start: GCD test
    :end-before: DOC include end: GCD test

Support for Verilog Within Chipyard Tool Flows
----------------------------------------------

Chipyard 프레임워크 내의 다양한 흐름에서 Verilog BlackBox가 처리되는 방식에는 중요한 차이가 있습니다. Chipyard의 일부 흐름은 원본 코드를 강력하고 비침습적으로 변환하기 위해 FIRRTL을 사용합니다. Verilog BlackBox는 FIRRTL에서 BlackBox 상태로 유지되므로, FIRRTL 변환에 의해 처리될 수 있는 능력은 제한적이며, Chipyard의 일부 고급 기능은 BlackBox에 대해 더 약한 지원을 제공할 수 있습니다. 다만, 설계의 나머지 부분(즉, "비-Verilog" 부분)은 일반적으로 Chipyard FIRRTL 변환에 의해 여전히 변환되거나 보강될 수 있습니다.

* Verilog BlackBox는 테이프아웃 준비가 된 RTL을 생성하는 데 완전히 지원됩니다.
* HAMMER 워크플로우는 Verilog BlackBox 통합에 대한 강력한 지원을 제공합니다.
* FireSim은 FPGA 시뮬레이터를 생성하기 위해 FIRRTL 변환에 의존합니다. 따라서 FireSim에서 Verilog BlackBox 지원은 현재 제한적이지만 빠르게 발전하고 있습니다. 계속 지켜봐 주세요!
* 사용자 정의 FIRRTL 변환 및 분석은 특정 변환 메커니즘에 따라 BlackBox Verilog를 처리할 수 있는 경우가 있습니다.

이 섹션에서 언급했듯이, ``BlackBox`` 리소스 파일은 빌드 프로세스에 통합되어야 하므로, ``BlackBox`` 리소스를 제공하는 모든 프로젝트는 ``build.sbt`` 에서 ``tapeout`` 프로젝트에 가시적으로 만들어야 합니다.

Differences between ``HasBlackBoxPath`` and ``HasBlackBoxResource``
-------------------------------------------------------------------

Chisel은 Chipyard에서 약간 다르게 작동하는 두 가지 메커니즘을 제공합니다: ``HasBlackBoxPath`` 와 ``HasBlackBoxResource``.

``HasBlackBoxResource`` 는 프로젝트의 ``src/main/resources`` 영역 내에서 파일의 상대 경로를 찾아 추가 파일을 통합합니다.
이는 ``addResource`` 에 의해 추가된 파일이 ``src/main/resources`` 영역에 있으며 **자동으로 생성되지 않는** 파일임을 요구합니다(파일은 RTL 생성 기간 동안 정적이어야 함).
이는 Chisel 소스가 컴파일될 때 ``jar`` 파일에 포함되며, ``src/main/resources`` 영역도 포함되어 Chisel 생성기를 실행하는 데 사용되기 때문입니다.
``addResource``로 참조된 파일은 Chisel 정교화 중에 이 ``jar`` 파일 내에 있어야 합니다.
따라서 파일이 Chisel 생성 중에 생성된 경우 다음 번 Chisel 소스가 컴파일될 때까지 이 ``jar`` 파일에 존재하지 않을 것입니다.

``HasBlackBoxPath`` 는 추가 파일을 통합할 때 절대 경로를 사용한다는 점에서 다릅니다.
빌드 프로세스 후반부에 FIRRTL 컴파일러는 해당 위치에서 파일을 생성된 소스 디렉토리로 복사합니다.
따라서 FIRRTL 컴파일러가 실행되기 전에 파일이 존재해야 합니다(즉, 파일이 ``src/main/resources`` 에 있지 않거나 Chisel 정교화 중에 자동으로 생성될 수 있음).

또한, 두 메커니즘 모두 추가된 파일의 순서를 강제하지 않습니다.
예를 들어:

.. code-block:: scala

    addResource("fileA")
    addResource("fileB")

이 경우, ``fileA`` 가 ``fileB`` 보다 먼저 다운스트림 도구에 전달되는 것이 보장되지 않습니다.
이를 우회하려면, 파일을 연결하여 필요한 순서로 자동으로 파일을 생성하고 ``HasBlackBoxPath`` 에서 제공하는 ``addPath`` 를 사용하는 것이 좋습니다.
이 예는 https://github.com/ucb-bar/ibex-wrapper/blob/main/src/main/scala/IbexCoreBlackbox.scala에서 볼 수 있습니다.
