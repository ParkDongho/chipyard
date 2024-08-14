.. _rocc-accelerators:

Adding a RoCC Accelerator
-------------------------

RoCC 가속기는 특정 Rocket 또는 BooM 타일에 추가할 수 있는 컴포넌트입니다.
이 가속기는 특정 opcode와 일치하는 명령을 수신하고, 코어 또는 SoC의 다른 부분(L1, L2, PTW, FPU)과 통신한 후, 선택적으로 명령의 ``rd`` 필드와 대응하는 레지스터에 값을 다시 쓸 수 있습니다.
RoCC 가속기는 ``LazyRoCC`` 클래스를 확장하는 모듈을 통해 인스턴스화됩니다.
이 모듈은 ``LazyRoCCModule`` 클래스를 확장하는 또 다른 모듈을 지연적으로 인스턴스화합니다.
이 추가적인 간접 계층은 Diplomacy가 RoCC 모듈을 칩에 연결하는 방법을 파악할 수 있도록 하여, 모듈을 미리 인스턴스화할 필요가 없도록 합니다.
지연 모듈에 대한 자세한 내용은 :ref:`Chipyard-Basics/Configs-Parameters-Mixins:Cake Pattern / Mixin` 섹션에서 설명합니다.
아래는 RoCC 가속기를 최소한으로 인스턴스화한 예시입니다.

.. code-block:: scala

    class CustomAccelerator(opcodes: OpcodeSet)
        (implicit p: Parameters) extends LazyRoCC(opcodes) {
      override lazy val module = new CustomAcceleratorModule(this)
    }

    class CustomAcceleratorModule(outer: CustomAccelerator)
        extends LazyRoCCModuleImp(outer) {
      val cmd = Queue(io.cmd)
      // 명령의 구성 요소는 다음과 같습니다
      // inst - 명령 자체의 부분
      //   opcode
      //   rd - 목적지 레지스터 번호
      //   rs1 - 첫 번째 소스 레지스터 번호
      //   rs2 - 두 번째 소스 레지스터 번호
      //   funct
      //   xd - 목적지 레지스터가 사용되고 있는가?
      //   xs1 - 첫 번째 소스 레지스터가 사용되고 있는가?
      //   xs2 - 두 번째 소스 레지스터가 사용되고 있는가?
      // rs1 - 소스 레지스터 1의 값
      // rs2 - 소스 레지스터 2의 값
      ...
    }

``LazyRoCC`` 의 ``opcodes`` 매개변수는 이 가속기에 매핑될 사용자 정의 opcode 세트를 나타냅니다.
이에 대한 자세한 내용은 다음 소절에서 설명합니다.

``LazyRoCC`` 클래스에는 두 개의 TLOutputNode 인스턴스, ``atlNode`` 와 ``tlNode`` 가 포함되어 있습니다.
전자는 L1 명령어 캐시의 후단과 함께 타일 로컬 중재기에 연결됩니다.
후자는 L1-L2 크로스바에 직접 연결됩니다.
모듈 구현의 IO 번들에서 해당하는 Tilelink 포트는 각각 ``atl`` 과 ``tl`` 입니다.

가속기에서 사용할 수 있는 다른 인터페이스로는 L1 캐시에 대한 접근을 제공하는 ``mem``, 페이지 테이블 워커에 대한 접근을 제공하는 ``ptw``, 가속기가 여전히 명령을 처리 중인지를 나타내는 ``busy`` 신호, 그리고 CPU를 인터럽트하는 데 사용할 수 있는 ``interrupt`` 신호가 있습니다.

다양한 IO에 대한 자세한 정보는 ``generators/rocket-chip/src/main/scala/tile/LazyRoCC.scala`` 에 있는 예제를 참조하십시오.
또한, UCSD에서 작성한 `RoCC Documentation <https://docs.google.com/document/d/1CH2ep4YcL_ojsa3BVHEW-uwcKh1FlFTjH_kg5v8bxVw/edit>`_ 에서 각 신호에 대한 더 많은 정보를 찾을 수 있지만, 이 문서는 트리 외부에서 업데이트되었으며 현재와 다를 수 있습니다.


Accessing Memory via L1 Cache
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

RoCC 가속기는 연결된 코어의 L1 캐시를 통해 메모리에 접근할 수 있습니다.
이는 가속기 설계자가 구현하기에 더 간단한 인터페이스이지만, 전용 TileLink 포트에 비해 일반적으로 달성 가능한 처리량이 낮습니다.

``LazyRoCCModuleImp`` 에서 ``io.mem`` 신호는 ``HellaCacheIO`` 이며, 이는 ``generators/rocket-chip/src/main/scala/rocket/HellaCache.scala`` 에서 정의됩니다.

.. code-block:: scala

    class HellaCacheIO(implicit p: Parameters) extends CoreBundle()(p) {
        val req = Decoupled(new HellaCacheReq)
        val s1_kill = Output(Bool()) // 이전 사이클의 요청 제거
        val s1_data = Output(new HellaCacheWriteData()) // 이전 사이클의 요청에 대한 데이터
        val s2_nack = Input(Bool()) // 두 사이클 전의 요청이 거부됨
        val s2_nack_cause_raw = Input(Bool()) // nack의 원인은 스토어-로드 RAW 위험(성능 힌트)
        val s2_kill = Output(Bool()) // 두 사이클 전의 요청 제거
        val s2_uncached = Input(Bool()) // 접근이 MMIO임을 알리는 신호
        val s2_paddr = Input(UInt(paddrBits.W)) // 변환된 주소

        val resp = Flipped(Valid(new HellaCacheResp))
        val replay_next = Input(Bool())
        val s2_xcpt = Input(new HellaCacheExceptions)
        val s2_gpa = Input(UInt(vaddrBitsExtended.W))
        val s2_gpa_is_pte = Input(Bool())
        val uncached_resp = tileParams.dcache.get.separateUncachedResp.option(Flipped(Decoupled(new HellaCacheResp)))
        val ordered = Input(Bool())
        val perf = Input(new HellaCachePerfEvents())

        val keep_clock_enabled = Output(Bool()) // D$가 자체적으로 클럭 게이팅을 피해야 하는가?
        val clock_enabled = Input(Bool()) // D$가 현재 클럭되고 있는가?
    }

높은 수준에서, 이 인터페이스를 통해 전송하는 요청에 ``io.mem.req.tag`` 를 사용하여 태그를 지정해야 하며, 데이터가 준비되면 해당 태그가 반환됩니다.
여러 요청을 발행하면 응답이 순서 없이 돌아올 수 있으므로, 이 태그를 사용하여 어떤 데이터가 반환되었는지 확인할 수 있습니다.
태그 비트의 수는 일반적으로 6으로 설정된 ``dcacheReqTagBits`` 에 의해 제어됩니다.
6비트를 초과하여 사용하면 오류나 정지가 발생할 수 있습니다.


Adding RoCC accelerator to Config
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

RoCC 가속기는 구성에서 ``BuildRoCC`` 매개변수를 재정의하여 코어에 추가할 수 있습니다.
이 매개변수는 추가하려는 각 가속기마다 ``LazyRoCC`` 객체를 생성하는 함수들의 시퀀스를 받습니다.

예를 들어, 이전에 정의된 가속기를 추가하고 custom0 및 custom1 명령을 이 가속기로 라우팅하려면 다음과 같이 할 수 있습니다.

.. code-block:: scala

    class WithCustomAccelerator extends Config((site, here, up) => {
      case BuildRoCC => Seq((p: Parameters) => LazyModule(
        new CustomAccelerator(OpcodeSet.custom0 | OpcodeSet.custom1)(p)))
    })

    class CustomAcceleratorConfig extends Config(
      new WithCustomAccelerator ++
      new RocketConfig)

프로그램에서 RoCC 명령을 추가하려면 ``tests/rocc.h`` 에서 제공되는 RoCC C 매크로를 사용하십시오. 예제는 ``tests/accum.c`` 및 ``charcount.c`` 파일에서 찾을 수 있습니다.



