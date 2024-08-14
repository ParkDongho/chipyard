.. _dma-devices:

Adding a DMA Device
===================

DMA 장치는 Tilelink 위젯으로, 마스터 역할을 합니다. 즉, DMA 장치는 칩의 메모리 시스템에 자체적으로 읽기 및 쓰기 요청을 보낼 수 있습니다.

디스크나 네트워크 드라이버와 같은 IO 장치나 가속기의 경우, CPU가 장치에서 데이터를 폴링하는 대신 장치가 직접 일관된 메모리 시스템에 쓰기를 수행하도록 할 수 있습니다. 예를 들어, 여기에서는 설정된 주소에 메모리에 0을 쓰는 장치를 보여줍니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/InitZero.scala
    :language: scala

.. literalinclude:: ../../generators/chipyard/src/main/scala/DigitalTop.scala
    :language: scala
    :start-after: DOC include start: DigitalTop
    :end-before: DOC include end: DigitalTop

우리는 ``TLClientNode`` 를 사용하여 TileLink 클라이언트 노드를 생성합니다.
그런 다음 클라이언트 노드를 프론트 버스(fbus)를 통해 메모리 시스템에 연결합니다.
TileLink 클라이언트 노드 생성에 대한 자세한 내용은 :ref:`TileLink-Diplomacy-Reference/NodeTypes:Client Node` 를 참조하십시오.

DMA 위젯을 포함한 최상위 모듈을 생성한 후 이전과 같이 이를 위한 구성을 생성할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/InitZero.scala
    :language: scala
    :start-after: DOC include start: WithInitZero
    :end-before: DOC include end: WithInitZero

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/MMIOAcceleratorConfigs.scala
    :language: scala
    :start-after: DOC include start: InitZeroRocketConfig
    :end-before: DOC include end: InitZeroRocketConfig

