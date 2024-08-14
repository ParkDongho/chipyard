Register Node
===============

메모리 매핑 장치는 일반적으로 공통 패턴을 따릅니다. 이들은 CPU에 레지스터 세트를 노출합니다. 레지스터에 쓰기를 통해 CPU는 장치의 설정을 변경하거나 명령을 보낼 수 있습니다. 레지스터를 읽으면 CPU는 장치의 상태를 조회하거나 결과를 가져올 수 있습니다.

디자이너는 관리 노드를 수동으로 인스턴스화하고 레지스터를 노출하는 논리를 직접 작성할 수 있지만, RocketChip의 ``regmap`` 인터페이스를 사용하는 것이 대부분의 글루 논리를 생성하는 데 훨씬 쉽습니다.

TileLink 장치의 경우, ``TLRegisterNode`` 의 ``regmap`` 인터페이스를 사용할 수 있습니다.

Basic Usage
-----------

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/RegisterNodeExample.scala
    :language: scala
    :start-after: DOC include start: MyDeviceController
    :end-before: DOC include end: MyDeviceController

위의 코드 예시는 ``TLRegisterNode`` 를 사용하여 다양한 크기의 하드웨어 레지스터를 메모리에 매핑하는 간단한 레이지 모듈을 보여줍니다. 생성자는 두 가지 필수 인수를 가집니다: 레지스터의 기본 주소인 ``address`` 와 디바이스 트리 항목인 ``device`` 입니다. 또한 두 가지 선택적 인수가 있습니다. ``beatBytes`` 인수는 인터페이스의 너비를 바이트 단위로 지정합니다. 기본값은 4바이트입니다. ``concurrency`` 인수는 TileLink 요청에 대한 내부 큐의 크기입니다. 기본값은 0이며, 이는 큐가 없음을 의미합니다. 이 값이 0보다 커야 레지스터 액세스를 위한 요청과 응답을 비동기화할 수 있습니다. 이에 대한 자세한 내용은 :ref:`TileLink-Diplomacy-Reference/Register-Node:Using Functions` 에서 설명됩니다.

노드와 상호작용하는 주된 방법은 ``regmap`` 메서드를 호출하는 것입니다. 이 메서드는 쌍의 시퀀스를 인수로 받습니다. 쌍의 첫 번째 요소는 기본 주소로부터의 오프셋이며, 두 번째 요소는 서로 다른 레지스터를 매핑하는 ``RegField`` 객체의 시퀀스입니다. ``RegField`` 생성자는 두 가지 인수를 받습니다. 첫 번째 인수는 레지스터의 너비(비트 단위)이며, 두 번째 인수는 레지스터 자체입니다.

인수가 시퀀스이므로, 오프셋과 여러 개의 ``RegField`` 객체를 연관시킬 수 있습니다. 이렇게 하면 오프셋이 액세스될 때 레지스터가 병렬로 읽거나 쓰여집니다. 레지스터는 리틀 엔디안 순서로 배열되므로, 목록의 첫 번째 레지스터는 작성된 값의 가장 낮은 비트에 해당합니다. 이 예에서 CPU가 0x0E 오프셋에 0xAB 값을 쓴다면, ``tinyReg0`` 는 0xB 값을 받고 ``tinyReg1`` 은 0xA 값을 받게 됩니다.

Decoupled Interfaces
--------------------

때로는 하드웨어 레지스터에서 읽고 쓰는 것 외에 다른 작업을 수행하고 싶을 때가 있습니다. ``RegField`` 인터페이스는 또한 ``DecoupledIO`` 인터페이스의 읽기 및 쓰기를 지원합니다. 예를 들어, 다음과 같이 하드웨어 FIFO를 구현할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/RegisterNodeExample.scala
    :language: scala
    :start-after: DOC include start: MyQueueRegisters
    :end-before: DOC include end: MyQueueRegisters

이 ``RegField`` 생성자의 변형은 두 가지 대신 세 가지 인수를 받습니다. 첫 번째 인수는 비트 너비입니다. 두 번째 인수는 읽을 디커플드 인터페이스이고, 세 번째 인수는 쓸 디커플드 인터페이스입니다. 이 예제에서 "레지스터"에 쓰기를 하면 데이터가 큐에 푸시되고, 읽기를 하면 큐에서 데이터가 팝됩니다.

레지스터에 대해 읽기와 쓰기 중 하나만 지정할 수도 있습니다. 읽기 전용 또는 쓰기 전용 레지스터를 만들 수도 있습니다. 따라서 이전 예제에서 인큐와 디큐가 다른 주소를 사용하도록 하려면 다음과 같이 작성할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/RegisterNodeExample.scala
    :language: scala
    :start-after: DOC include start: MySeparateQueueRegisters
    :end-before: DOC include end: MySeparateQueueRegisters

읽기 전용 레지스터 함수는 레지스터가 아닌 신호를 읽는 데도 사용할 수 있습니다.

.. code-block:: scala

    val constant = 0xf00d.U

    node.regmap(
      0x00 -> Seq(RegField.r(8, constant)))

Using Functions
---------------

함수를 사용하여 레지스터를 생성할 수도 있습니다. 예를 들어, 쓰기를 통해 카운터를 증가시키고 읽기를 통해 카운터를 감소시키는 레지스터를 만들고 싶다고 가정해 봅시다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/RegisterNodeExample.scala
    :language: scala
    :start-after: DOC include start: MyCounterRegisters
    :end-before: DOC include end: MyCounterRegisters

여기서 함수는 본질적으로 디커플드 인터페이스와 동일합니다. 읽기 함수는 ``ready`` 신호를 받고, ``valid`` 및 ``bits`` 신호를 반환합니다. 쓰기 함수는 ``valid`` 및 ``bits`` 를 받고 ``ready`` 를 반환합니다.

또한 읽기/쓰기 요청과 응답을 비동기화하는 함수를 전달할 수도 있습니다. 요청은 비동기 입력으로 나타나고, 응답은 비동기 출력으로 나타납니다. 따라서 이전 예제를 위해 이렇게 할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/RegisterNodeExample.scala
    :language: scala
    :start-after: DOC include start: MyCounterReqRespRegisters
    :end-before: DOC include end: MyCounterReqRespRegisters

각 함수에서 ``responding`` 이라는 상태 변수를 설정합니다. 이 값이 false일 때 함수는 요청을 받을 준비가 되어 있으며, true일 때 응답을 보내고 있습니다.

이 변형에서는 읽기와 쓰기 모두 입력으로 valid를 받고 출력으로 ready를 반환합니다. 유일한 차이점은 bits가 읽기의 경우 입력이고, 쓰기의 경우 출력이라는 점입니다.

이 변형을 사용하려면 ``concurrency`` 를 0보다 큰 값으로 설정해야 합니다.

Register Nodes for Other Protocols
------------------------------------

레지스터 노드 인터페이스의 유용한 기능 중 하나는 사용 중인 프로토콜을 쉽게 변경할 수 있다는 것입니다. 예를 들어, :ref:`TileLink-Diplomacy-Reference/Register-Node:Basic Usage` 의 첫 번째 예제에서 ``TLRegisterNode`` 를 ``AXI4RegisterNode`` 로 간단히 변경할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/RegisterNodeExample.scala
    :language: scala
    :start-after: DOC include start: MyAXI4DeviceController
    :end-before: DOC include end: MyAXI4DeviceController

AXI4 노드는 ``device`` 인수를 받지 않으며, 다중 AddressSet 대신 단일 AddressSet만 가질 수 있다는 점 외에는 모든 것이 동일합니다.
