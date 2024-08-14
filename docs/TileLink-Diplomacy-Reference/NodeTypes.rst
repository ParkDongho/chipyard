.. _node_types:

TileLink Node Types
===================

Diplomacy는 SoC의 다양한 구성 요소를 방향성 비순환 그래프의 노드로 표현합니다. TileLink 노드는 여러 가지 유형으로 나올 수 있습니다.

Client Node
-----------

TileLink 클라이언트는 A 채널에서 요청을 보내고 D 채널에서 응답을 수신하여 TileLink 트랜잭션을 시작하는 모듈입니다. 클라이언트가 TL-C를 구현하는 경우 B 채널에서 프로브를 수신하고, C 채널에서 릴리스를 보내며, E 채널에서 승인 응답을 보냅니다.

RocketChip/Chipyard의 L1 캐시 및 DMA 장치에는 클라이언트 노드가 있습니다.

다음과 같이 LazyModule에 TileLink 클라이언트 노드를 추가할 수 있습니다:

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/NodeTypes.scala
    :language: scala
    :start-after: DOC include start: MyClient
    :end-before: DOC include end: MyClient

``name`` 인수는 Diplomacy 그래프에서 노드를 식별합니다. 이는 TLClientParameters에 필요한 유일한 인수입니다.

``sourceId`` 인수는 이 클라이언트가 사용할 소스 식별자의 범위를 지정합니다. 여기서 범위를 [0, 4)로 설정했으므로 이 클라이언트는 한 번에 최대 4개의 요청을 비행할 수 있습니다. 각 요청은 소스 필드에서 고유한 값을 가집니다. 이 필드의 기본값은 ``IdRange(0, 1)`` 으로, 이는 단일 요청만 비행할 수 있음을 의미합니다.

``requestFifo`` 인수는 기본값이 false인 부울 옵션입니다. true로 설정하면 클라이언트는 이를 지원하는 하위 관리자에게 응답을 FIFO 순서(즉, 해당 요청이 전송된 순서대로)로 보내달라고 요청합니다.

``visibility`` 인수는 클라이언트가 액세스할 주소 범위를 지정합니다. 기본값으로는 모든 주소가 포함됩니다. 이 예제에서는 ``AddressSet(0x10000, 0xffff)`` 라는 단일 주소 범위를 포함하도록 설정하여 클라이언트가 0x10000에서 0x1ffff까지의 주소에만 액세스할 수 있도록 했습니다. 일반적으로 이를 지정하지 않지만, 이는 하위 크로스바 생성기가 클라이언트가 가시성 범위와 겹치지 않는 주소 범위를 가진 관리자에 대해 중재하지 않도록 하드웨어를 최적화하는 데 도움이 될 수 있습니다.

LazyModule 구현 내부에서 ``node.out`` 을 호출하여 번들/엣지 쌍의 목록을 가져올 수 있습니다.

``tl`` 번들은 이 모듈의 IO에 연결되는 Chisel 하드웨어 번들입니다. 이는 TileLink 채널에 해당하는 두 개(TL-UL 및 TL-UH의 경우) 또는 다섯 개(TL-C의 경우)의 비동기 번들을 포함합니다. 이를 통해 실제로 TileLink 메시지를 보내고/수신하기 위해 하드웨어 로직에 연결해야 합니다.

``edge`` 객체는 Diplomacy 그래프의 엣지를 나타냅니다. 이 객체에는 :ref:`TileLink-Diplomacy-Reference/EdgeFunctions:TileLink Edge Object Methods` 에서 문서화된 유용한 도우미 함수가 포함되어 있습니다.

Manager Node
------------

TileLink 관리자는 A 채널에서 클라이언트의 요청을 받아 D 채널에서 응답을 보냅니다. 다음과 같이 관리자를 생성할 수 있습니다:

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/NodeTypes.scala
    :language: scala
    :start-after: DOC include start: MyManager
    :end-before: DOC include end: MyManager

``makeManagerNode`` 메서드는 두 가지 인수를 받습니다. 첫 번째는 TileLink 인터페이스의 물리적 너비(바이트 단위)인 ``beatBytes`` 이며, 두 번째는 TLManagerParameters 객체입니다.

``TLManagerParameters`` 의 유일한 필수 인수는 이 관리자가 서비스를 제공할 주소 범위 세트인 ``address`` 입니다. 이 정보는 클라이언트의 요청을 라우팅하는 데 사용됩니다. 이 예제에서 관리자는 0x20000에서 0x20fff까지의 주소에 대한 요청만 처리합니다. ``AddressSet`` 의 두 번째 인수는 크기가 아니라 마스크입니다. 일반적으로 이 값을 2의 거듭제곱보다 1 적은 값으로 설정해야 합니다. 그렇지 않으면 주소 지정 동작이 예상과 다를 수 있습니다.

두 번째 인수는 ``resources`` 로, 일반적으로 ``Device`` 객체에서 가져옵니다. 이 경우 ``SimpleDevice`` 객체를 사용합니다. 이 인수는 Linux 드라이버에서 읽을 수 있도록 BootROM의 DeviceTree에 항목을 추가하려는 경우에 필요합니다. ``SimpleDevice`` 의 두 인수는 디바이스 트리 항목의 이름과 호환성 목록입니다. 따라서 이 관리자의 디바이스 트리 항목은 다음과 같습니다.

.. code-block:: text

    L12: my-device@20000 {
        compatible = "tutorial,my-device0";
        reg = <0x20000 0x1000>;
    };

다음 인수는 ``regionType`` 으로, 관리자의 캐싱 동작에 대한 정보를 제공합니다. 아래에 나열된 7가지 지역 유형이 있습니다:

1. ``CACHED``      - 중간 에이전트가 이 영역의 사본을 캐시했을 수 있습니다.
2. ``TRACKED``     - 이 영역이 다른 마스터에 의해 캐시되었을 수 있지만, 일관성이 제공되고 있습니다.
3. ``UNCACHED``    - 이 영역은 아직 캐시되지 않았지만 가능한 경우 캐시해야 합니다.
4. ``IDEMPOTENT``  - Get은 가장 최근에 Put된 내용을 반환하지만, 내용은 캐시되지 않아야 합니다.
5. ``VOLATILE``    - 내용이 Put 없이 변경될 수 있지만, Put 및 Get에는 부작용이 없습니다.
6. ``PUT_EFFECTS`` - Put은 부작용을 일으키므로 결합/지연되어서는 안 됩니다.
7. ``GET_EFFECTS`` - Get은 부작용을 일으키므로 추측적으로 실행되어서는 안 됩니다.

다음은 ``executable`` 인수로, CPU가 이 관리자에서 명령어를 가져올 수 있는지 여부를 결정합니다. 기본값은 false이며, 대부분의 MMIO 주변 장치에서 이 값을 사용해야 합니다.

다음 여섯 개의 인수는 ``support`` 로 시작하며, 관리자가 수락할 수 있는 다양한 A 채널 메시지 유형을 결정합니다. 메시지 유형의 정의는 :ref:`TileLink-Diplomacy-Reference/EdgeFunctions:TileLink Edge Object Methods` 에서 설명됩니다. ``TransferSizes`` 케이스 클래스는 관리자가 특정 메시지 유형에 대해 수락할 수 있는 논리적 크기 범위를 바이트 단위로 지정합니다. 이는 포함 범위이며, 모든 논리적 크기는 2의 거듭제곱이어야 합니다. 따라서 이 경우 관리자는 1, 2, 4 또는 8바이트 크기의 요청을 수락할 수 있습니다.

마지막으로 ``fifoId`` 설정은 관리자가 속한 FIFO 도메인을 결정합니다. 이 인수를 ``None`` 으로 설정하면(기본값) 관리자는 응답의 순서를 보장하지 않습니다. ``fifoId`` 가 설정되면 동일한 ``fifoId`` 를 지정한 모든 다른 관리자와 FIFO 도메인을 공유하게 됩니다. 이는 해당 FIFO 도메인으로 전송된 클라이언트 요청이 동일한 순서로 응답을 받게 됨을 의미합니다.

Register Node
-------------

관리자 노드를 직접 지정하고 TileLink 요청을 처리하는 모든 논리를 작성할 수도 있지만, 일반적으로 레지스터 노드를 사용하는 것이 훨씬 쉽습니다. 이 유형의 노드는 제어/상태 레지스터를 지정하고 TileLink 프로토콜을 처리하는 논리를 자동으로 생성할 수 있는 ``regmap`` 메서드를 제공합니다. 레지스터 노드를 사용하는 방법에 대한 자세한 내용은 :ref:`TileLink-Diplomacy-Reference/Register-Node:Register Node` 에서 확인할 수 있습니다.

Identity Node
-------------

이전의 노드 유형과 달리 입력 또는 출력만 있는 것이 아니라, Identity 노드는 둘 다 가지고 있습니다. 이름에서 알 수 있듯이 입력을 출력으로 변경 없이 단순히 연결합니다. 이 노드는 주로 여러 노드를 단일 노드로 결합하여 다중 엣지로 만드는 데 사용됩니다. 예를 들어, 각기 다른 클라이언트 노드를 가진 두 개의 클라이언트 레이지 모듈이 있다고 가정합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/NodeTypes.scala
    :language: scala
    :start-after: DOC include start: MyClient1+MyClient2
    :end-before: DOC include end: MyClient1+MyClient2

이제 이 두 클라이언트를 다른 레이지 모듈에 인스턴스화하고, 그들의 노드를 단일 노드로 노출할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/NodeTypes.scala
    :language: scala
    :start-after: DOC include start: MyClientGroup
    :end-before: DOC include end: MyClientGroup

관리자에 대해서도 동일한 작업을 수행할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/NodeTypes.scala
    :language: scala
    :start-after: DOC include start: MyManagerGroup
    :end-before: DOC include end: MyManagerGroup

이제 클라이언트와 관리자 그룹을 서로 연결하려면 다음과 같이 할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/NodeTypes.scala
    :language: scala
    :start-after: DOC include start: MyClientManagerComplex
    :end-before: DOC include end: MyClientManagerComplex

``:=*`` 연산자의 의미는 :ref:`TileLink-Diplomacy-Reference/Diplomacy-Connectors:Diplomacy Connectors` 섹션에서 더 자세히 설명됩니다. 요약하자면, 이는 다중 엣지를 사용하여 두 노드를 연결합니다. Identity 노드의 엣지는 순서대로 할당되므로, 이 경우 ``client1.node`` 는 결국 ``manager1.node`` 에 연결되고, ``client2.node`` 는 ``manager2.node`` 에 연결됩니다.

Identity 노드의 입력 수는 출력 수와 일치해야 합니다. 일치하지 않으면 정교화 오류가 발생합니다.

Adapter Node
------------

Identity 노드와 마찬가지로 어댑터 노드는 일정 수의 입력을 받아 동일한 수의 출력을 생성합니다. 그러나 Identity 노드와 달리 어댑터 노드는 연결을 변경 없이 통과시키지 않습니다. 이는 입력과 출력 간의 논리적 및 물리적 인터페이스를 변경하고 메시지를 다시 작성할 수 있습니다. RocketChip은 어댑터 라이브러리를 제공하며, 이는 :ref:`TileLink-Diplomacy-Reference/Widgets:Diplomatic Widgets` 에서 확인할 수 있습니다.

어댑터 노드를 직접 생성할 필요는 거의 없지만, 호출 방법은 다음과 같습니다.

.. code-block:: scala

    val node = TLAdapterNode(
      clientFn = { cp =>
        // ..
      },
      managerFn = { mp =>
        // ..
      })

``clientFn`` 은 입력의 ``TLClientPortParameters`` 를 인수로 받아 출력에 대한 해당 매개변수를 반환하는 함수입니다. ``managerFn`` 은 출력의 ``TLManagerPortParameters`` 를 인수로 받아 입력에 대한 해당 매개변수를 반환합니다.

Nexus Node
----------

Nexus 노드는 어댑터 노드와 유사하며 출력 인터페이스가 입력 인터페이스와 다릅니다. 그러나 입력 수와 출력 수가 다를 수도 있습니다. 이 노드 유형은 주로 TileLink 크로스바 생성기를 제공하는 ``TLXbar`` 위젯에서 사용됩니다. 이 노드 유형을 수동으로 정의할 필요는 없지만, 호출 방법은 다음과 같습니다.

.. code-block:: scala

    val node = TLNexusNode(
      clientFn = { seq =>
        // ..
      },
      managerFn = { seq =>
        // ..
      })

이는 어댑터 노드의 생성자와 유사한 인수를 가지지만, 인수로 단일 매개변수 객체를 받는 대신 시퀀스의 매개변수를 받고 결과로 시퀀스를 반환합니다. 예상할 수 있듯이, 반환된 시퀀스의 크기는 입력 시퀀스의 크기와 동일할 필요는 없습니다.

