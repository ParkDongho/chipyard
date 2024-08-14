Rocket Chip
===========

Rocket Chip 생성기는 Berkeley와 SiFive에서 개발되었으며, 현재는 Chips Alliance에서 공개적으로 유지 관리되고 있는 SoC 생성기입니다.
Chipyard는 RISC-V SoC를 생성하는 기본으로 Rocket Chip 생성기를 사용합니다.

`Rocket Chip` 은 인오더 RISC-V CPU 생성기인 `Rocket core` 와는 구별됩니다.
Rocket Chip에는 CPU 외에도 SoC의 많은 부분이 포함되어 있습니다. 기본적으로 Rocket Chip은 Rocket core CPU를 사용하지만, BOOM 아웃오브오더 코어 생성기 또는 다른 사용자 정의 CPU 생성기를 사용하도록 구성할 수도 있습니다.

일반적인 Rocket Chip 시스템의 상세한 다이어그램은 아래에 표시되어 있습니다.

.. image:: ../_static/images/rocketchip-diagram.png

Tiles
-----

다이어그램은 듀얼 코어 ``Rocket`` 시스템을 보여줍니다. 각 ``Rocket`` 코어는 페이지 테이블 워커, L1 명령어 캐시, L1 데이터 캐시와 함께 ``RocketTile`` 로 그룹화됩니다.

``Rocket`` 코어는 또한 ``BOOM`` 코어로 교체할 수 있습니다. 각 타일은 코프로세서로 코어에 연결되는 RoCC 가속기로 구성될 수도 있습니다.

Memory System
-------------
타일들은 ``SystemBus`` 에 연결되어 L2 캐시 뱅크에 연결됩니다. L2 캐시 뱅크는 ``MemoryBus`` 에 연결되고, 이는 TileLink에서 AXI 변환기를 통해 DRAM 컨트롤러에 연결됩니다.

메모리 계층에 대해 더 알아보려면 :ref:`Customization/Memory-Hierarchy:Memory Hierarchy` 섹션을 참조하십시오.

MMIO
----

MMIO 주변 장치를 위해 ``SystemBus`` 는 ``ControlBus`` 및 ``PeripheryBus`` 에 연결됩니다.

``ControlBus`` 는 BootROM, 플랫폼 수준 인터럽트 컨트롤러(PLIC), 코어 로컬 인터럽트(CLINT), 디버그 유닛과 같은 표준 주변 장치를 연결합니다.

BootROM은 시스템이 리셋에서 벗어날 때 실행되는 첫 번째 명령어인 1단계 부트로더를 포함하고 있습니다. 또한 Linux가 다른 주변 장치가 무엇인지 확인하는 데 사용하는 장치 트리도 포함되어 있습니다.

PLIC은 장치 인터럽트 및 외부 인터럽트를 집계하고 마스킹합니다.

코어 로컬 인터럽트에는 각 CPU에 대한 소프트웨어 인터럽트와 타이머 인터럽트가 포함됩니다.

디버그 유닛은 칩을 외부에서 제어하는 데 사용됩니다. 이 유닛은 데이터를 메모리에 로드하거나 메모리에서 데이터를 추출하는 데 사용할 수 있습니다. 맞춤형 DMI 또는 표준 JTAG 프로토콜을 통해 제어할 수 있습니다.

``PeripheryBus`` 는 NIC 및 블록 장치와 같은 추가 주변 장치를 연결합니다.
또한 선택적으로 외부 AXI4 포트를 노출하여 공급업체 제공 AXI4 IP에 연결할 수 있습니다.

MMIO 주변 장치를 추가하는 방법에 대해 더 알아보려면 :ref:`mmio-accelerators` 섹션을 참조하십시오.

DMA
---

메모리 시스템에서 직접 읽고 쓸 수 있는 DMA 장치도 추가할 수 있습니다. 이러한 장치는 ``FrontendBus`` 에 연결됩니다. ``FrontendBus`` 는 AXI4에서 TileLink 변환기를 통해 공급업체 제공 AXI4 DMA 장치에도 연결할 수 있습니다.

DMA 장치를 추가하는 방법에 대해 더 알아보려면 :ref:`dma-devices` 섹션을 참조하십시오.

