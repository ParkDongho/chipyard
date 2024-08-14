.. _diplomatic_widgets:

Diplomatic Widgets
==================

RocketChip은 TileLink 및 AXI4 위젯의 외교적 라이브러리를 제공합니다.
가장 일반적으로 사용되는 위젯들은 여기에 문서화되어 있습니다. TileLink 위젯은 ``freechips.rocketchip.tilelink`` 에서 사용할 수 있으며, AXI4 위젯은 ``freechips.rocketchip.amba.axi4`` 에서 사용할 수 있습니다.

TLBuffer
--------

TileLink 트랜잭션을 버퍼링하는 위젯입니다. 이는 각 채널(또는 TL-C의 경우 5개의 채널)에 대해 큐를 인스턴스화합니다. 각 채널에 대한 큐를 구성하려면 ``freechips.rocketchip.diplomacy.BufferParams`` 객체를 생성자에 전달합니다. 이 케이스 클래스의 인수는 다음과 같습니다:

 - ``depth: Int`` - 큐의 항목 수
 - ``flow: Boolean`` - true인 경우, 유효 신호를 조합적으로 결합하여 입력이 큐에 삽입된 같은 사이클에서 소비될 수 있도록 합니다.
 - ``pipe: Boolean`` - true인 경우, 준비 신호를 조합적으로 결합하여 단일 항목 큐가 전체 속도로 작동할 수 있도록 합니다.

``Int``에서 암시적 변환이 가능합니다. ``BufferParams`` 객체 대신 정수를 전달하면, 큐는 주어진 정수의 깊이가 되며 ``flow`` 와 ``pipe`` 는 모두 false가 됩니다.

또한 미리 정의된 BufferParams 객체를 사용할 수도 있습니다.

 - ``BufferParams.default`` = ``BufferParams(2, false, false)``
 - ``BufferParams.none`` = ``BufferParams(0, false, false)``
 - ``BufferParams.flow`` = ``BufferParams(1, true, false)``
 - ``BufferParams.pipe`` = ``BufferParams(1, false, true)``

**인수:**

네 가지 생성자가 있으며, 인수가 0개, 1개, 2개 또는 5개일 수 있습니다.

인수가 없는 생성자는 모든 채널에 대해 ``BufferParams.default`` 를 사용합니다.

인수가 하나인 생성자는 모든 채널에 대해 사용할 ``BufferParams`` 객체를 가져옵니다.

두 인수 생성자의 인수는 다음과 같습니다:

 - ``ace: BufferParams`` - A, C, E 채널에 사용할 매개변수.
 - ``bd: BufferParams`` - B, D 채널에 사용할 매개변수.

다섯 인수 생성자의 인수는 다음과 같습니다:

 - ``a: BufferParams`` - A 채널에 대한 버퍼 매개변수
 - ``b: BufferParams`` - B 채널에 대한 버퍼 매개변수
 - ``c: BufferParams`` - C 채널에 대한 버퍼 매개변수
 - ``d: BufferParams`` - D 채널에 대한 버퍼 매개변수
 - ``e: BufferParams`` - E 채널에 대한 버퍼 매개변수

**사용 예:**

.. code-block:: scala

    // 기본 설정
    manager0.node := TLBuffer() := client0.node

    // 암시적 변환을 사용하여 채널당 8개의 큐 항목이 있는 버퍼 생성
    manager1.node := TLBuffer(8) := client1.node

    // A 채널에는 기본 설정을 사용하지만 D 채널에는 pipe 사용
    manager2.node := TLBuffer(BufferParams.default, BufferParams.pipe) := client2.node

    // A 및 D 채널에만 큐 추가
    manager3.node := TLBuffer(
      BufferParams.default,
      BufferParams.none,
      BufferParams.none,
      BufferParams.default,
      BufferParams.none) := client3.node

AXI4Buffer
----------

:ref:`TileLink-Diplomacy-Reference/Widgets:TLBuffer` 와 유사하지만 AXI4용입니다. 이 위젯도 ``BufferParams`` 객체를 인수로 받습니다.

**인수:**

TLBuffer와 마찬가지로 AXI4Buffer에는 0개, 1개, 2개, 5개의 인수를 가진 생성자가 있습니다.

인수가 없는 생성자는 모든 채널에 대해 기본 BufferParams를 사용합니다.

인수가 하나인 생성자는 제공된 BufferParams를 모든 채널에 사용합니다.

두 인수 생성자의 인수는 다음과 같습니다:

 - ``aw: BufferParams`` - "ar", "aw", "w" 채널에 대한 버퍼 매개변수.
 - ``br: BufferParams`` - "b", "r" 채널에 대한 버퍼 매개변수.

다섯 인수 생성자의 인수는 다음과 같습니다:

 - ``aw: BufferParams`` - "ar" 채널에 대한 버퍼 매개변수
 - ``w: BufferParams`` - "w" 채널에 대한 버퍼 매개변수
 - ``b: BufferParams`` - "b" 채널에 대한 버퍼 매개변수
 - ``ar: BufferParams`` - "ar" 채널에 대한 버퍼 매개변수
 - ``r: BufferParams`` - "r" 채널에 대한 버퍼 매개변수

**사용 예:**

.. code-block:: scala

    // 기본 설정
    slave0.node := AXI4Buffer() := master0.node

    // 암시적 변환을 사용하여 채널당 8개의 큐 항목이 있는 버퍼 생성
    slave1.node := AXI4Buffer(8) := master1.node

    // aw/w/ar 채널에는 기본 설정을 사용하지만 b/r 채널에는 pipe 사용
    slave2.node := AXI4Buffer(BufferParams.default, BufferParams.pipe) := master2.node

    // aw, b, ar에는 단일 항목 큐, w와 r에는 2항목 큐
    slave3.node := AXI4Buffer(1, 2, 1, 1, 2) := master3.node

AXI4UserYanker
--------------

이 위젯은 사용자 필드를 가진 AXI4 포트를 받아서 사용자 필드가 없는 포트로 변환합니다. 입력 AR 및 AW 요청의 사용자 필드 값은 ARID/AWID와 연관된 내부 큐에 저장되며, 이는 응답에 올바른 사용자 필드를 연결하는 데 사용됩니다.

**인수:**

 - ``capMaxFlight: Option[Int]`` - (선택 사항) 각 ID에 대해 처리할 수 있는 요청 수를 포함할 수 있는 옵션입니다. ``None`` (기본값)으로 설정된 경우, UserYanker는 최대 처리 가능한 요청 수를 지원합니다.

**사용 예:**

.. code-block:: scala

    nouser.node := AXI4UserYanker(Some(1)) := hasuser.node

AXI4Deinterleaver
-----------------

다른 ID에 대한 다중 비트 AXI4 읽기 응답이 교차될 수 있습니다. 이 위젯은 슬레이브로부터의 읽기 응답을 재정렬하여 단일 트랜잭션의 모든 비트가 연속되도록 합니다.

**인수:**

 - ``maxReadBytes: Int`` - 단일 트랜잭션에서 읽을 수 있는 최대 바이트 수.

**사용 예:**

.. code-block:: scala

    interleaved.node := AXI4Deinterleaver() := consecutive.node

TLFragmenter
------------

TLFragmenter 위젯은 TileLink 인터페이스의 최대 논리 전송 크기를 줄여 더 큰 트랜잭션을 여러 작은 트랜잭션으로 나눕니다.

**인수:**

 - ``minSize: Int`` - 모든 외부 관리자에서 지원하는 전송의 최소 크기.
 - ``maxSize: Int`` - Fragmenter가 적용된 후 지원되는 전송의 최대 크기.
 - ``alwaysMin: Boolean`` - (선택 사항) 모든 요청을 minSize로 분할합니다 (그렇지 않으면 관리자가 지원하는 최대 크기로 분할). (기본값: false)
 - ``earlyAck: EarlyAck.T`` - (선택 사항) 멀티비트 Put을 첫 번째 비트 또는 마지막 비트에서 확인해야 합니까?
   가능한 값 (기본값: ``EarlyAck.None``):

    - ``EarlyAck.AllPuts`` - 항상 첫 번째 비트에서 확인합니다.
    - ``EarlyAck.PutFulls`` - PutFull일 경우 첫 번째 비트에서 확인하고, 그렇지 않으면 마지막 비트에서 확인합니다.
    - ``EarlyAck.None`` - 항상 마지막 비트에서 확인합니다.

 - ``holdFirstDeny: Boolean`` - (선택 사항) Fragmenter가 멀티비트 Get을 처음으로 거부된 비트로 조합하여 전체 버스트를 거부하게 허용합니다. (기본값: false)

**사용 예:**

.. code-block:: scala

    val beatBytes = 8
    val blockBytes = 64

    single.node := TLFragmenter(beatBytes, blockBytes) := multi.node

    axi4lite.node := AXI4Fragmenter() := axi4full.node

**추가 참고 사항**

 - TLFragmenter는 다음을 수정합니다: PutFull, PutPartial, LogicalData, Get, Hint
 - TLFragmenter는 다음을 통과합니다: ArithmeticData (

alwaysMin이 설정된 경우 minSize로 잘립니다)
 - TLFragmenter는 획득을 수정할 수 없습니다 (라이브록이 발생할 수 있음); 따라서 캐시를 양쪽에 모두 배치하는 것은 안전하지 않습니다.

AXI4Fragmenter
--------------

AXI4Fragmenter는 :ref:`TileLink-Diplomacy-Reference/Widgets:TLFragmenter` 와 유사합니다. AXI4Fragmenter는 모든 AXI 액세스를 관리자가 지원하는 최대 크기의 단순 2의 거듭제곱 크기 및 정렬된 전송으로 분할합니다. 이는 AXI4=>TL 브리지를 적용하기 전에 첫 번째 단계 변환으로 적합합니다. 또한 TL=>AXI4 브리지를 통해 AXI-lite 슬레이브를 구동하는 경우에도 적합합니다.

**사용 예:**

.. code-block:: scala

    axi4lite.node := AXI4Fragmenter() := axi4full.node

TLSourceShrinker
----------------

관리자가 보는 소스 ID의 수는 일반적으로 연결된 클라이언트에 따라 계산됩니다. 일부 경우에는 소스 ID의 수를 고정하고 싶을 수 있습니다. 예를 들어, TileLink 포트를 Verilog 블랙 박스로 내보내려는 경우에 그렇게 할 수 있습니다. 그러나 클라이언트가 더 많은 소스 ID를 요구하는 경우 문제가 발생할 수 있습니다. 이 상황에서는 TLSourceShrinker를 사용해야 합니다.

**인수:**

 - ``maxInFlight: Int`` - TLSourceShrinker에서 관리자에게 전송될 소스 ID의 최대 수.

**사용 예:**

.. code-block:: scala

    // client.node는 16개 이상의 소스 ID를 가질 수 있음
    // manager.node는 16개만 보게 됨
    manager.node := TLSourceShrinker(16) := client.node

AXI4IdIndexer
-------------

:ref:`TileLink-Diplomacy-Reference/Widgets:TLSourceShrinker` 의 AXI4 버전입니다. 이는 슬레이브 AXI4 인터페이스에서 AWID/ARID 비트 수를 제한합니다. 외부 또는 블랙 박스 AXI4 포트에 연결할 때 유용합니다.

**인수:**

 - ``idBits: Int`` - 슬레이브 인터페이스의 ID 비트 수.

**사용 예:**

.. code-block:: scala

    // master.node는 16개 이상의 고유 ID를 가질 수 있음
    // slave.node는 4개의 ID 비트만 보게 됨
    slave.node := AXI4IdIndexer(4) := master.node

**참고 사항:**

AXI4IdIndexer는 슬레이브 인터페이스에 ``user`` 필드를 생성합니다. 이는 마스터 요청의 ID를 이 필드에 저장하기 때문입니다. ``user`` 필드가 없는 AXI4 인터페이스에 연결하려면 :ref:`TileLink-Diplomacy-Reference/Widgets:AXI4UserYanker` 를 사용해야 합니다.

TLWidthWidget
-------------

이 위젯은 TileLink 인터페이스의 물리적 너비를 변경합니다. TileLink 인터페이스의 너비는 관리자가 구성하지만 때로는 클라이언트가 특정 너비를 보도록 하고 싶을 수 있습니다.

**인수:**

 - ``innerBeatBytes: Int`` - 클라이언트가 보는 물리적 너비(바이트 단위)

**사용 예:**

.. code-block:: scala

    // 관리 노드가 beatBytes를 8로 설정한다고 가정
    // WidthWidget을 사용하여 클라이언트가 beatBytes 4를 보게 함
    manager.node := TLWidthWidget(4) := client.node

TLFIFOFixer
-----------

FIFO 도메인을 선언하는 TileLink 관리자는 FIFO 정렬을 요청한 클라이언트로부터의 모든 요청이 정렬된 응답을 보도록 해야 합니다. 그러나 관리자는 자신의 응답의 정렬만 제어할 수 있으며, 동일한 FIFO 도메인 내의 다른 관리자의 응답과 이들이 어떻게 교차하는지 제어할 수 없습니다. 관리자를 초월한 FIFO 순서를 보장하는 책임은 TLFIFOFixer에게 있습니다.

**인수:**

 - ``policy: TLFIFOFixer.Policy`` - (선택 사항) TLFIFOFixer가 어떤 관리자에 대해 순서를 강제할 것인가? (기본값: ``TLFIFOFixer.all``)

``policy`` 의 가능한 값은 다음과 같습니다:

 - ``TLFIFOFixer.all`` - FIFO 도메인을 정의하지 않은 관리자도 포함하여 모든 관리자에게 순서 보장
 - ``TLFIFOFixer.allFIFO`` - FIFO 도메인을 정의한 모든 관리자에게 순서 보장
 - ``TLFIFOFixer.allVolatile`` - ``VOLATILE`` , ``PUT_EFFECTS`` , ``GET_EFFECTS`` 의 RegionType을 가진 모든 관리자에게 순서 보장 (지역 유형 설명은 :ref:`TileLink-Diplomacy-Reference/NodeTypes:Manager Node` 참조)

TLXbar and AXI4Xbar
-------------------

이는 TileLink와 AXI4에 대한 크로스바 생성기로, TL 클라이언트/AXI4 마스터 노드의 요청을 관리자가 정의한 주소에 따라 TL 관리자/AXI4 슬레이브 노드로 라우팅합니다. 일반적으로 이들은 인수 없이 생성됩니다. 그러나 중재 정책을 변경하여 어떤 클라이언트 포트가 중재자에서 우선 순위를 가질지 결정할 수 있습니다. 기본 정책은 ``TLArbiter.roundRobin`` 이지만, 고정된 중재 우선 순위를 원할 경우 ``TLArbiter.lowestIndexFirst`` 로 변경할 수 있습니다.

**인수:**

모든 인수는 선택 사항입니다.

 - ``arbitrationPolicy: TLArbiter.Policy`` - 사용할 중재 정책.
 - ``maxFlightPerId: Int`` - (AXI4 전용) 동일한 ID를 가진 트랜잭션이 동시에 처리될 수 있는 수. (기본값: 7)
 - ``awQueueDepth: Int`` - (AXI4 전용) 쓰기 주소 큐의 깊이. (기본값: 2)

**사용 예:**

.. code-block:: scala

    // 크로스바 레이지 모듈 인스턴스화
    val tlBus = LazyModule(new TLXbar)

    // 단일 입력 엣지 연결
    tlBus.node := tlClient0.node
    // 다중 입력 엣지 연결
    tlBus.node :=* tlClient1.node

    // 단일 출력 엣지 연결
    tlManager0.node := tlBus.node
    // 다중 출력 엣지 연결
    tlManager1.node :*= tlBus.node

    // lowestIndexFirst 중재 정책이 적용된 크로스바 인스턴스화
    // 이게 AXI4여도 TLArbiter 싱글톤을 여전히 사용합니다.
    val axiBus = LazyModule(new AXI4Xbar(TLArbiter.lowestIndexFirst))

    // 연결은 TL과 동일하게 작동합니다.
    axiBus.node := axiClient0.node
    axiBus.node :=* axiClient1.node
    axiManager0.node := axiBus.node
    axiManager1.node :*= axiBus.node



TLToAXI4 and AXI4ToTL
---------------------

이 위젯들은 TileLink와 AXI4 프로토콜 간의 변환기입니다. TLToAXI4는 TileLink 클라이언트를 받아 AXI4 슬레이브에 연결합니다. AXI4ToTL은 AXI4 마스터를 받아 TileLink 관리자에 연결합니다. 일반적으로 이러한 위젯의 생성자에 대한 기본 인수를 재정의하지 않는 것이 좋습니다.

**사용 예:**

.. code-block:: scala

    axi4slave.node :=
        AXI4UserYanker() :=
        AXI4Deinterleaver(64) :=
        TLToAXI4() :=
        tlclient.node

    tlmanager.node :=
        AXI4ToTL() :=
        AXI4UserYanker() :=
        AXI4Fragmenter() :=
        axi4master.node

TLToAXI4 변환기 이후에는 :ref:`TileLink-Diplomacy-Reference/Widgets:AXI4Deinterleaver` 를 추가해야 합니다. TLToAXI4 변환기는 교차된 읽기 응답을 처리할 수 없기 때문입니다. TLToAXI4 변환기는 또한 AXI4 사용자 필드를 사용하여 일부 정보를 저장하므로, 사용자 필드가 없는 AXI4 포트에 연결하려면 :ref:`TileLink-Diplomacy-Reference/Widgets:AXI4UserYanker` 를 사용해야 합니다.

AXI4 포트를 AXI4ToTL 위젯에 연결하기 전에 :ref:`TileLink-Diplomacy-Reference/Widgets:AXI4Fragmenter` 및 :ref:`TileLink-Diplomacy-Reference/Widgets:AXI4UserYanker` 를 추가해야 합니다. 변환기는 멀티비트 트랜잭션이나 사용자 필드를 처리할 수 없기 때문입니다.

TLROM
------

TLROM 위젯은 TileLink를 사용하여 액세스할 수 있는 읽기 전용 메모리를 제공합니다. 참고: 이 위젯은 ``freechips.rocketchip.devices.tilelink`` 패키지에 있으며, 다른 것들과는 달리 ``freechips.rocketchip.tilelink`` 패키지에 없습니다.

**인수:**

 - ``base: BigInt`` - 메모리의 기본 주소
 - ``size: Int`` - 메모리의 크기(바이트 단위)
 - ``contentsDelayed: => Seq[Byte]`` - 호출될 때 ROM의 바이트 내용을 생성하는 함수.
 - ``executable: Boolean`` - (선택 사항) CPU가 ROM에서 명령어를 가져올 수 있는지 여부를 지정합니다. (기본값: ``true`` )
 - ``beatBytes: Int`` - (선택 사항) 인터페이스의 너비(바이트 단위). (기본값: 4)
 - ``resources: Seq[Resource]`` - (선택 사항) 디바이스 트리에 추가할 리소스의 시퀀스.

**사용 예:**

.. code-block:: scala

    val rom = LazyModule(new TLROM(
      base = 0x100A0000,
      size = 64,
      contentsDelayed = Seq.tabulate(64) { i => i.toByte },
      beatBytes = 8))
    rom.node := TLFragmenter(8, 64) := client.node

**지원되는 작업:**

TLROM은 단일 비트 읽기만 지원합니다. 멀티비트 읽기를 수행하려면 ROM 앞에 TLFragmenter를 연결해야 합니다.

TLRAM and AXI4RAM
-----------------

TLRAM과 AXI4RAM 위젯은 SRAM으로 구현된 읽기-쓰기 메모리를 제공합니다.

**인수:**

 - ``address: AddressSet`` - 이 RAM이 커버할 주소 범위.
 - ``cacheable: Boolean`` - (선택 사항) 이 RAM의 내용을 캐시할 수 있는지 여부. (기본값: ``true`` )
 - ``executable: Boolean`` - (선택 사항) 이 RAM의 내용을 명령어로 가져올 수 있는지 여부. (기본값: ``true`` )
 - ``beatBytes: Int`` - (선택 사항) TL/AXI4 인터페이스의 너비(바이트 단위). (기본값: 4)
 - ``atomics: Boolean`` - (선택 사항, TileLink 전용) RAM이 원자적 작업을 지원합니까? (기본값: ``false`` )

**사용 예:**

.. code-block:: scala

    val xbar = LazyModule(new TLXbar)

    val tlram = LazyModule(new TLRAM(
      address = AddressSet(0x1000, 0xfff)))

    val axiram = LazyModule(new AXI4RAM(
      address = AddressSet(0x2000, 0xfff)))

    tlram.node := xbar.node
    axiram := TLToAXI4() := xbar.node

**지원되는 작업:**

TLRAM은 단일 비트 TL-UL 요청만 지원합니다. ``atomics`` 를 true로 설정하면 Logical 및 Arithmetic 작업도 지원합니다. 멀티비트 읽기/쓰기를 원한다면 ``TLFragmenter`` 를 사용하십시오.

AXI4RAM은 AXI4-Lite 작업만 지원하므로, 멀티비트 읽기/쓰기 및 전체 너비보다 작은 읽기/쓰기는 지원하지 않습니다. 전체 AXI4 프로토콜을 사용하려면 ``AXI4Fragmenter`` 를 사용하십시오.

