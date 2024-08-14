.. _memory-hierarchy:

Memory Hierarchy
===============================

The L1 Caches
--------------

각 CPU 타일에는 L1 명령어 캐시와 L1 데이터 캐시가 있습니다. 이 캐시들의 크기와 연관도(associativity)는 설정할 수 있습니다. 기본 ``RocketConfig`` 는 16 KiB, 4-way set-associative 명령어 및 데이터 캐시를 사용합니다. 하지만, ``WithNMedCores`` 또는 ``WithNSmallCores`` 구성에서는 L1I 및 L1D에 대해 4 KiB 직접 매핑된 캐시를 구성할 수 있습니다.

크기나 연관도만 변경하고 싶다면, 이를 위한 설정 조각(config fragments)도 있습니다. 이러한 설정 조각을 사용자 정의 ``Config`` 에 추가하는 방법은 :ref:`Customization/Keys-Traits-Configs:Config Fragments` 를 참조하십시오.

.. code-block:: scala

         new freechips.rocketchip.subsystem.WithL1ICacheSets(128) ++  // rocket I$ 크기 변경
         new freechips.rocketchip.subsystem.WithL1ICacheWays(2) ++    // rocket I$ 연관도 변경
         new freechips.rocketchip.subsystem.WithL1DCacheSets(128) ++  // rocket D$ 크기 변경
         new freechips.rocketchip.subsystem.WithL1DCacheWays(2) ++    // rocket D$ 연관도 변경


L1 데이터 캐시를 데이터 스크래치패드로 구성할 수도 있습니다.
그러나 여기에는 몇 가지 제한 사항이 있습니다. 데이터 스크래치패드를 사용하는 경우, 단일 코어만 사용할 수 있으며 외부 DRAM을 설계에 추가할 수 없습니다.
이러한 설정은 L2 캐시와 메모리 버스를 완전히 제거합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/RocketConfigs.scala
    :language: scala
    :start-after: DOC include start: l1scratchpadrocket
    :end-before: DOC include end: l1scratchpadrocket

이 설정은 채널 수와 뱅크 수를 0으로 설정하여 L2 캐시와 메모리 버스를 완전히 제거합니다.

The System Bus
--------------

시스템 버스는 타일과 L2 에이전트 및 MMIO 주변 장치 사이에 위치한 TileLink 네트워크입니다. 일반적으로 이는 완전 연결된 크로스바이지만, Constellation을 사용하여 네트워크 온 칩 기반 구현을 생성할 수 있습니다.
자세한 내용은 :ref:`Customization/NoC-SoCs:SoCs with NoC-based Interconnects` 를 참조하십시오.

The Inclusive Last-Level Cache
---------------------------------

Chipyard 예제 프로젝트에서 제공되는 기본 ``RocketConfig`` 는 Rocket-Chip InclusiveCache 생성기를 사용하여 공유 L2 캐시를 생성합니다. 기본 설정에서 L2는 512 KiB 용량과 8-way set-associativity를 갖는 단일 캐시 뱅크를 사용합니다. 그러나 이러한 매개변수를 변경하여 원하는 캐시 구성을 얻을 수 있습니다. 주요 제한 사항은 ways 수와 뱅크 수가 2의 거듭제곱이어야 한다는 것입니다.

맞춤 설정 옵션에 대한 자세한 내용은 ``rocket-chip-inclusive-cache`` 에 정의된 ``CacheParameters`` 객체를 참조하십시오.

The Broadcast Hub
-----------------

L2 캐시를 사용하지 않으려면(예: 리소스가 제한된 임베디드 설계를 위해), L2 캐시 없이 구성을 생성할 수 있습니다. L2 캐시를 사용하는 대신, RocketChip의 TileLink 브로드캐스트 허브를 사용할 수 있습니다.
이러한 구성을 만들려면 ``RocketConfig`` 의 정의를 복사하고 포함된 믹스인 목록에서 ``WithInclusiveCache`` 구성 조각을 생략하면 됩니다.

리소스 사용을 더욱 줄이려면, 브로드캐스트 허브를 버퍼가 없는 디자인으로 구성할 수 있습니다. 이 구성 조각은 ``freechips.rocketchip.subsystem.WithBufferlessBroadcastHub`` 입니다.

The Outer Memory System
-----------------------

L2 일관성 에이전트(L2 캐시 또는 브로드캐스트 허브)는 AXI4 호환 DRAM 컨트롤러로 구성된 외부 메모리 시스템에 요청을 보냅니다.

기본 설정은 단일 메모리 채널을 사용하지만, 여러 채널을 사용하는 것으로 시스템을 구성할 수 있습니다. L2 뱅크 수와 마찬가지로 DRAM 채널 수는 2의 거듭제곱으로 제한됩니다.

.. code-block:: scala

    new freechips.rocketchip.subsystem.WithNMemoryChannels(2)

VCS 및 Verilator 시뮬레이션에서 DRAM은 각 메모리 채널에 단일 사이클 SRAM을 연결하는 ``SimAXIMem`` 모듈을 사용하여 시뮬레이션됩니다.

외부 DRAM에 연결하는 대신, 스크래치패드를 연결하고 외부 연결을 제거할 수 있습니다. 이는 구성에 ``testchipip.soc.WithScratchpad`` 조각을 추가하고 ``freechips.rocketchip.subsystem.WithNoMemPort`` 로 메모리 포트를 제거하여 수행됩니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/MemorySystemConfigs.scala
    :language: scala
    :start-after: DOC include start: mbusscratchpadrocket
    :end-before: DOC include end: mbusscratchpadrocket

보다 현실적인 메모리 시뮬레이션이 필요한 경우, DDR3 컨트롤러의 타이밍을 시뮬레이션할 수 있는 FireSim을 사용할 수 있습니다. FireSim 메모리 모델에 대한 자세한 문서는 `FireSim docs <https://docs.fires.im/en/latest/>`_ 에서 확인할 수 있습니다.

