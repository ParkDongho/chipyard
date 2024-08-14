.. _noc-socs:

SoCs with NoC-based Interconnects
==================================

Chipyard SoC에 네트워크 온 칩(Network-on-Chip, NoC)을 통합하는 주요 방법은 표준 TileLink 크로스바 기반 버스(시스템 버스, 메모리 버스, 제어 버스 등)를 Constellation에서 생성된 NoC에 매핑하는 것입니다.

인터커넥트는 TileLink 버스에 대한 "개별적인" 인터커넥트로 매핑될 수 있으며, 이 경우 버스 트래픽을 전달하기 위한 전용 인터커넥트가 생성됩니다.
또는, 인터커넥트는 공유 글로벌 인터커넥트로 매핑될 수 있으며, 이 경우 여러 TileLink 버스가 단일 공유 인터커넥트를 통해 전송될 수 있습니다.

Private Interconnects
---------------------
시스템 버스, 메모리 버스, 제어 버스에 대한 전용 프라이빗 인터커넥트를 통합한 예시는 `generators/chipyard/src/main/scala/config/NoCConfigs.scala <https://github.com/ucb-bar/chipyard/blob/main/generators/chipyard/src/main/scala/config/NoCConfigs.scala>`__ 의 ``MultiNoCConfig`` 에서 확인할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/NoCConfigs.scala
    :language: scala
    :start-after: DOC include start: MultiNoCConfig
    :end-before: DOC include end: MultiNoCConfig

각 버스(``Sbus`` / ``Mbus`` / ``Cbus``)에 대해 구성 조각은 전용 NoC의 매개변수화와 TileLink 에이전트와 물리적 NoC 노드 간의 매핑을 모두 제공합니다.

NoC 매개변수를 구성하는 방법에 대한 자세한 내용은 `Constellation documentation <http://constellation.readthedocs.io>`__ 를 참조하십시오.

Shared Global Interconnect
---------------------------
여러 TileLink 버스의 전송을 지원하는 단일 글로벌 인터커넥트를 통합한 예시는 `generators/chipyard/src/main/scala/config/NoCConfigs.scala <https://github.com/ucb-bar/chipyard/blob/main/generators/chipyard/src/main/scala/config/NoCConfigs.scala>`__ 의 ``SharedNoCConfig`` 에서 확인할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/NoCConfigs.scala
    :language: scala
    :start-after: DOC include start: SharedNoCConfig
    :end-before: DOC include end: SharedNoCConfig

각 버스에 대해, 구성 조각은 TileLink 에이전트와 물리적 NoC 노드 간의 매핑만 제공하며, 글로벌 인터커넥트의 구성은 별도의 조각에서 제공합니다.

NoC 매개변수를 구성하는 방법에 대한 자세한 내용은 `Constellation documentation <http://constellation.readthedocs.io>`__ 를 참조하십시오.

