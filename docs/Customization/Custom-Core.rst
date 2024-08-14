.. _custom_core:

Adding a custom core
====================

Chipyard 프레임워크에 사용자 지정 RISC-V 코어를 통합하려면 다음 단계를 따르세요. 이 문서 페이지는 이를 달성하는 방법을 단계별로 설명합니다.

.. note::

    RoCC는 현재 Rocket 및 BOOM 이외의 코어에서는 지원되지 않습니다. RoCC를 사용해야 하는 경우 Rocket 또는 BOOM을 RoCC 기본 코어로 사용하십시오.

.. note::

    이 페이지에는 Rocket 칩 저장소의 중요한 정의가 포함된 파일에 대한 링크가 포함되어 있습니다. 이 저장소는 Chipyard와 별도로 유지 관리됩니다.
    이 페이지의 코드와 소스 파일의 코드 간에 불일치가 발견되면 GitHub 이슈를 통해 보고해 주십시오!

Wrap Verilog Module with Blackbox (Optional)
--------------------------------------------

Chipyard는 Scala와 Chisel을 사용하기 때문에 코어의 최상위 모듈이 Chisel이 아닌 경우, Chipyard에서 처리할 수 있도록 Verilog 블랙박스를 먼저 생성해야 합니다. 자세한 내용은 :ref:`incorporating-verilog-blocks` 을 참조하십시오.

Create Parameter Case Classes
-----------------------------

Chipyard는 ``TilesLocated(InSubsystem)`` 키에 있는 ``InstantiableTileParams`` 객체마다 코어를 생성합니다.
이 객체는 ``TileParams`` 에서 파생된 트레이트로, 타일을 생성하는 데 필요한 정보를 포함합니다. 모든 코어는 ``InstantiableTileParams`` 와 ``CoreParams`` 를 구현해야 하며, 이들은 타일 클래스의 필드로 전달됩니다.

``TileParams``는 타일의 매개변수를 보유하며, 여기에는 코어, 캐시, MMU 등의 매개변수가 포함됩니다. ``CoreParams`` 는 타일의 코어에 특정한 매개변수를 포함합니다.
이들은 케이스 클래스 형태로 구현되어야 하며, 생성자 매개변수로 다른 구성 조각에서 재정의될 수 있습니다. 구현해야 할 변수 목록은 페이지 하단의 부록을 참조하십시오.
커스텀 필드를 추가할 수도 있지만, 표준 필드를 우선 사용해야 합니다.

``InstantiableTileParams[TileType]`` 는 ``TileParams`` 필드 외에 ``TileType`` 의 생성자를 보유합니다.
모든 사용자 지정 코어는 타일 클래스 ``TileType`` 의 새 인스턴스를 반환하기 위해 타일 매개변수 클래스에서 ``instantiate()`` 를 구현해야 합니다.

``TileParams`` (파일 `BaseTile.scala <https://github.com/chipsalliance/rocket-chip/blob/master/src/main/scala/tile/BaseTile.scala>`_),
``InstantiableTileParams`` (파일 `BaseTile.scala <https://github.com/chipsalliance/rocket-chip/blob/master/src/main/scala/tile/BaseTile.scala>`_),
``CoreParams`` (파일 `Core.scala <https://github.com/chipsalliance/rocket-chip/blob/master/src/main/scala/tile/Core.scala>`_),
및 ``FPUParams`` (파일 `FPU.scala <https://github.com/chipsalliance/rocket-chip/blob/master/src/main/scala/tile/FPU.scala>`_)
에는 다음과 같은 필드가 포함됩니다.

.. code-block:: scala

    trait TileParams {
      val core: CoreParams                  // 코어 매개변수 (아래 참조)
      val icache: Option[ICacheParams]      // Rocket 전용: I1 캐시 옵션
      val dcache: Option[DCacheParams]      // Rocket 전용: D1 캐시 옵션
      val btb: Option[BTBParams]            // Rocket 전용: BTB / 브랜치 예측기 옵션
      val hartId: Int                       // Hart ID: 설계 구성 내에서 고유해야 함 (이것은 케이스 클래스 매개변수여야 함)
      val beuAddr: Option[BigInt]           // Rocket 전용: Rocket Core 용 버스 오류 장치
      val blockerCtrlAddr: Option[BigInt]   // Rocket 전용: Rocket Core 용 버스 차단기
      val name: Option[String]              // 코어의 이름
    }

    abstract class InstantiableTileParams[TileType <: BaseTile] extends TileParams {
      def instantiate(crossing: TileCrossingParamsLike, lookup: LookupByHartIdImpl)
                    (implicit p: Parameters): TileType
    }

    trait CoreParams {
      val bootFreqHz: BigInt              // 주파수
      val useVM: Boolean                  // 가상 메모리 지원
      val useUser: Boolean                // 사용자 모드 지원
      val useSupervisor: Boolean          // 감독자 모드 지원
      val useDebug: Boolean               // RISC-V 디버그 사양 지원
      val useAtomics: Boolean             // A 확장 지원
      val useAtomicsOnlyForIO: Boolean    // 메모리 맵 IO 용 A 확장 지원 (useAtomics가 false 인 경우에도 true 일 수 있음)
      val useCompressed: Boolean          // C 확장 지원
      val useVector: Boolean = false      // V 확장 지원
      val useSCIE: Boolean                // 사용자 정의 명령 지원 (custom-0 및 custom-1에서)
      val useRVE: Boolean                 // E 기본 ISA 사용
      val mulDiv: Option[MulDivParams]    // *Rocket 전용: M 확장 관련 설정 (M 확장을 지원하는 경우 Some(MulDivParams()) 사용)
      val fpu: Option[FPUParams]          // F 및 D 확장 및 관련 설정 (아래 참조)
      val fetchWidth: Int                 // 매 사이클마다 가져올 수 있는 최대 명령어 수
      val decodeWidth: Int                // 매 사이클마다 해독할 수 있는 최대 명령어 수
      val retireWidth: Int                // 매 사이클마다 은퇴할 수 있는 최대 명령어 수
      val instBits: Int                   // 명령어 비트 (32비트와 64비트가 모두 지원되는 경우 64 사용)
      val nLocalInterrupts: Int           // 로컬 인터럽트 수 (SiFive 인터럽트 요리책 참조)
      val nPMPs: Int                      // 물리적 메모리 보호 장치 수
      val pmpGranularity: Int             // PMP 장치의 가장 작은 지역 단위 크기 (2의 제곱이어야 함)
      val nBreakpoints: Int               // 지원되는 하드웨어 브레이크포인트 수 (RISC-V 디버그 사양)
      val useBPWatch: Boolean             // 하드웨어 브레이크포인트 지원
      val nPerfCounters: Int              // 지원되는 성능 카운터 수
      val haveBasicCounters: Boolean      // RISC-V 카운터 확장에서 정의한 기본 카운터 지원
      val haveFSDirty: Boolean            // true 인 경우, 코어가 적절할 때 mstatus CSR의 FS 필드를 더럽게 설정
      val misaWritable: Boolean           // 쓰기 가능한 misa CSR 지원 (가변 명령어 비트와 같은)
      val haveCFlush: Boolean             // Rocket 전용: 캐시를 플러시하는 Rocket의 사용자 정의 명령 확장 사용 가능
      val nL2TLBEntries: Int              // L2 TLB 항목 수
      val mtvecInit: Option[BigInt]       // mtvec CSR (V 확장의) 초기 값
      val mtvecWritable: Boolean          // mtvec CSR이 쓰기 가능한지 여부

      // 일반적으로, lrscCycles를 제외하고 이러한 값을 변경할 필요는 없습니다.
      def customCSRs(implicit p: Parameters): CustomCSRs = new CustomCSRs

      def hasSupervisorMode: Boolean = useSupervisor || useVM
      def instBytes: Int = instBits / 8
      def fetchBytes: Int = fetchWidth * instBytes
      // Rocket 전용: Rocket 코어 D1 캐시의 최장 가능한 대기 시간. 사용하지 않는 경우 기본값 80으로 설정.
      def lrscCycles: Int

      def dcacheReqTagBits: Int = 6

      def minFLen: Int = 32
      def vLen: Int = 0
      def sLen: Int = 0
      def eLen(xLen: Int, fLen: Int): Int = xLen max fLen
      def vMemDataBits: Int = 0
    }

    case class FPUParams(
      minFLen: Int = 32,          // 최소 부동 소수점 길이 (변경할 필요 없음)
      fLen: Int = 64,             // 최대 부동 소수점 길이, 단일 정밀도만 지원되는 경우 32 사용
      divSqrt: Boolean = true,    // Div/Sqrt 연산 지원
      sfmaLatency: Int = 3,       // Rocket 전용: 융합 곱셈-덧셈 파이프라인 대기 시간 (단일 정밀도)
      dfmaLatency: Int = 4        // Rocket 전용: 융합 곱셈-덧셈 파이프라인 대기 시간 (이중 정밀도)
    )

여기에 있는 대부분의 필드 (``Rocket specific`` 으로 표시된 필드)는 원래 Rocket 코어를 위해 설계되었으며, 일부 구현 특정 세부 정보가 포함되어 있지만 많은 필드가 다른 코어에도 유용하게 사용될 수 있습니다.
``Rocket specific`` 으로 표시된 필드는 기본값을 사용하여 무시할 수 있지만, 의미나 사용이 이와 유사한 추가 정보를 저장해야 하는 경우, 사용자 정의 필드를 만드는 대신 이러한 필드를 사용하는 것이 좋습니다.

또한 타일 구성을 구성 시스템에 추가하기 위해 ``CanAttachTile`` 클래스를 생성해야 하며, 다음 형식을 따라야 합니다:

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/TutorialTile.scala
    :language: scala
    :start-after: DOC include start: CanAttachTile
    :end-before: DOC include end: CanAttachTile

Chipyard는 구성 시스템에서 ``CanAttachTile`` 의 하위 클래스를 찾고, 이 클래스에서 매개변수를 기반으로 타일을 인스턴스화합니다.

.. note::

    구현체는 여기에 나열된 일부 필드를 무시하거나 비표준 방식으로 사용할 수 있지만, 부정확한 값을 사용하면 Chipyard 구성 요소에 의존하는 모듈에 영향을 줄 수 있습니다 (예: 지원되는 ISA 확장에 대한 부정확한 표시로 인해 잘못된 테스트 스위트가 생성될 수 있음).
    무시되거나 변경된 사용법의 필드는 코어 구현에 문서화하고, 이러한 구성 값을 조회하는 다른 장치를 구현할 경우에도 문서화하십시오. ``Rocket specific`` 값은 일반적으로 무시해도 안전하지만, 사용한 경우 이를 문서화해야 합니다.

Create Tile Class
-----------------

Chipyard에서 모든 타일은 외교적으로 인스턴스화됩니다. 첫 번째 단계에서는 타일에서 시스템으로의 인터커넥트를 지정하는 외교 노드가 평가되고, 두 번째 "모듈 구현" 단계에서는 하드웨어가 구체화됩니다. 자세한 내용은 :ref:`tilelink_and_diplomacy` 를 참조하십시오. 이 단계에서는 코어의 매개변수와 다른 외교 노드와의 연결을 지정하는 타일 클래스를 구현해야 합니다. 이 클래스는 일반적으로 외교 / TileLink 코드만 포함하며, Chisel RTL 코드는 여기에 포함되지 않아야 합니다.

모든 타일 클래스는 ``BaseTile`` 을 구현하며, 일반적으로 ``SinksExternalInterrupts`` 와 ``SourcesExternalNotifications`` 를 구현하여 타일이 외부 인터럽트를 수신할 수 있도록 합니다. 일반적인 타일의 형식은 다음과 같습니다:

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/TutorialTile.scala
    :language: scala
    :start-after: DOC include start: Tile class
    :end-before: DOC include end: Tile class

Connect TileLink Buses
----------------------

Chipyard는 TileLink를 온보드 버스 프로토콜로 사용합니다. 코어가 TileLink를 사용하지 않는 경우, 타일 모듈 내에서 코어의 메모리 프로토콜과 TileLink 사이에 변환기를 삽입해야 합니다. 아래는 AXI4를 사용하는 코어를 TileLink 버스에 변환기와 함께 연결하는 예제입니다. 변환기는 Rocket 칩에서 제공됩니다:

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/TutorialTile.scala
    :language: scala
    :start-after: DOC include start: AXI4 convert
    :end-before: DOC include end: AXI4 convert

모든 중간 위젯이 필요하지 않을 수 있습니다. 각 중간 위젯의 의미는 :ref:`diplomatic_widgets` 를 참조하십시오. TileLink를 사용하는 경우, 구성 요소에서 사용하는 TileLink 노드와 탭 노드만 필요합니다. Chipyard는 AHB, APB, AXIS에 대한 변환기도 제공하며, 대부분의 AXI4 위젯은 이러한 버스 프로토콜에 대한 동등한 위젯을 가지고 있습니다. 자세한 내용은 ``generators/rocket-chip/src/main/scala/amba`` 에 있는 소스 파일을 참조하십시오.

다른 버스 프로토콜을 사용하는 경우, TileLink에 익숙하지 않은 경우 변환기를 구현할 수 있으며, ``generators/rocket-chip/src/main/scala/amba`` 파일을 템플릿으로 사용할 수 있지만, 권장되지는 않습니다.

``memAXI4Node`` 는 AXI4 마스터 노드이며, 다음과 같이 정의됩니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/TutorialTile.scala
    :language: scala
    :start-after: DOC include start: AXI4 node
    :end-before: DOC include end: AXI4 node

``portName`` 과 ``idBits`` (포트 ID를 나타내는 비트 수)는 타일에서 제공하는 매개변수입니다. Chipyard에서 지원하는 노드 유형과 매개변수를 확인하려면 :ref:`node_types` 를 확인하십시오!

또한 기본적으로, 타일을 떠날 때 버스 요청/응답을 버퍼링하기 위해 마스터 및 슬레이브 연결에 대한 경계 버퍼가 있습니다. 다음 두 기능을 재정의하여 버스 요청/응답을 버퍼링하는 방법을 제어할 수 있습니다:
(이 두 기능의 정의는 ``BaseTile`` 클래스에서 확인할 수 있습니다. 파일 `BaseTile.scala <https://github.com/chipsalliance/rocket-chip/blob/master/src/main/scala/tile/BaseTile.scala>`_)

.. code-block:: scala

    // 기본적으로, 그들의 값은 "TLBuffer(BufferParams.none)"입니다.
    protected def makeMasterBoundaryBuffers(implicit p: Parameters): TLBuffer
    protected def makeSlaveBoundaryBuffers(implicit p: Parameters): TLBuffer

``TLBuffer`` 에 대한 자세한 내용은 :ref:`diplomatic_widgets` 를 참조하십시오.

Create Implementation Class
---------------------------

구현 클래스는 Tile 클래스에서 제공된 정보에 따라 외교 프레임워크에서 해결된 값에 따라 하드웨어를 구체화하는 파라미터화된 실제 하드웨어를 포함합니다. 이 클래스는 일반적으로 Chisel RTL 코드를 포함하며, 코어가 Verilog에 있는 경우 Verilog 구현을 래핑하는 블랙박스 클래스를 인스턴스화하고 이를 버스 및 다른 구성 요소와 연결해야 합니다. 이 클래스에는 외교 / TileLink 코드가 없어야 하며, TileLink 인터페이스 또는 외교적으로 정의된 구성 요소에서 IO 신호를 연결해야 합니다.

코어에 대한 구현 클래스는 다음 형식을 가집니다:

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/TutorialTile.scala
    :language: scala
    :start-after: DOC include start: Implementation class
    :end-before: DOC include end: Implementation class

AXI4 노드(또는 동등한 노드)를 생성한 경우, 이를 코어에 연결해야 합니다. 포트를 다음과 같이 연결할 수 있습니다:

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/TutorialTile.scala
    :language: scala
    :start-after: DOC include start: AXI4 connect
    :end-before: DOC include end: AXI4 connect

Connect Interrupt
-----------------

Chipyard는 타일이 다른 장치로부터 인터럽트를 수신하거나 다른 코어/장치에 알림을 보내기 위해 인터럽트를 시작할 수 있습니다.
``SinksExternalInterrupts`` 를 상속받은 타일에서는 ``TileInterrupts`` 객체(Chisel 번들)를 생성하고, 해당 객체를 인수로 사용하여 ``decodeCoreInterrupts()`` 를 호출할 수 있습니다. 이 함수는 Chisel 번들을 반환하기 때문에 구현 클래스에서 호출해야 합니다. 그런 다음, ``TileInterrupts`` 번들에서 인터럽트 비트를 읽을 수 있습니다.
``TileInterrupts`` 의 정의
(파일 `Interrupts.scala <https://github.com/chipsalliance/rocket-chip/blob/master/src/main/scala/tile/Interrupts.scala>`_)는 다음과 같습니다:

.. code-block:: scala

    class TileInterrupts(implicit p: Parameters) extends CoreBundle()(p) {
      val debug = Bool() // 디버그 인터럽트
      val mtip = Bool() // 머신 레벨 타이머 인터럽트
      val msip = Bool() // 머신 레벨 소프트웨어 인터럽트
      val meip = Bool() // 머신 레벨 외부 인터럽트
      val seip = usingSupervisor.option(Bool()) // 감독자 모드를 지원하는 경우에만 유효
      val lip = Vec(coreParams.nLocalInterrupts, Bool())  // 로컬 인터럽트
    }

다음은 구현 클래스에서 이러한 신호를 연결하는 예제입니다:

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/TutorialTile.scala
    :language: scala
    :start-after: DOC include start: connect interrupt
    :end-before: DOC include end: connect interrupt

또한, 타일은 특정 이벤트에 대해 다른 코어 또는 장치에 알림을 보낼 수도 있으며, 이는 ``SourcesExternalNotifications`` 의 구현 클래스에서 다음 함수를 호출하여 수행할 수 있습니다:
(이 함수들은 `Interrupts.scala <https://github.com/chipsalliance/rocket-chip/blob/master/src/main/scala/tile/Interrupts.scala>`_ 파일의 ``SourcesExternalNotifications`` 특성에서 확인할 수 있습니다)

.. code-block:: scala

    def reportHalt(could_halt: Option[Bool]) // 복구 불가능한 하드웨어 오류가 발생했을 때 트리거됨 (머신 중지)
    def reportHalt(errors: Seq[CanHaveErrors]) // 표준 오류 번들을 위한 변형 (Rocket 전용: 캐시에 ECC 오류가 있을 때 사용)
    def reportCease(could_cease: Option[Bool], quiescenceCycles: Int = 8) // 코어가 명령을 은퇴하지 않을 때 트리거됨 (예: 클럭 게이팅)
    def reportWFI(could_wfi: Option[Bool]) // WFI 명령이 실행될 때 트리거됨

다음은 인터럽트를 발생시키기 위해 이러한 함수를 사용하는 예제입니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/TutorialTile.scala
    :language: scala
    :start-after: DOC include start: raise interrupt
    :end-before: DOC include end: raise interrupt

Create Config Fragments to Integrate the Core
---------------------------------------------

Chipyard 구성에서 코어를 사용하려면 현재 구성에서 코어의 ``TileParams`` 객체를 생성하는 구성 조각이 필요합니다. 이와 같은 구성 조각의 예는 다음과 같습니다:

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/TutorialTile.scala
    :language: scala
    :start-after: DOC include start: Config fragment
    :end-before: DOC include end: Config fragment

Chipyard는 ``TilesLocated(InSubsystem)`` 필드에서 타일 매개변수를 찾으며, 이 필드의 유형은 ``InstantiableTileParams`` 목록입니다.
이 구성 조각은 이 목록의 끝에 새로운 타일 매개변수를 단순히 추가합니다.

이제 코어를 Chipyard에 통합하기 위한 모든 준비가 완료되었습니다! 사용자 지정 코어를 생성하려면 :ref:`custom_chisel` 의 지침에 따라 프로젝트를 빌드 시스템에 추가한 다음, :ref:`hetero_socs_` 의 단계에 따라 구성을 생성하십시오.
이제 새 구성에 대해 대부분의 원하는 워크플로우를 내장된 코어와 마찬가지로 실행할 수 있습니다 (코어가 지원하는 기능에 따라 다름).

서드파티 Verilog 코어를 Chipyard에 통합한 전체 예제를 보고 싶다면, ``generators/ariane/src/main/scala/CVA6Tile.scala`` 에서 CVA6 코어의 구체적인 예를 확인할 수 있습니다. 이 특정 예제는 AXI 인터페이스와 메모리 일관성 시스템 간의 상호 작용과 관련된 추가적인 세부 사항이 포함되어 있습니다.

