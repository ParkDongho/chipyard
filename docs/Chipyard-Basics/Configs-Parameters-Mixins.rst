Configs, Parameters, Mixins, and Everything In Between
========================================================

Chipyard 프레임워크의 많은 생성기는 Rocket Chip 파라미터 시스템을 사용합니다.  
이 파라미터 시스템은 침습적인 RTL 변경 없이 SoC를 유연하게 구성할 수 있게 해줍니다.  
파라미터 시스템을 올바르게 사용하기 위해 여러 용어와 관례를 사용할 것입니다.

Parameters
--------------------

Rocket 파라미터 시스템의 주요 과제 중 하나는 사용할 올바른 파라미터를 식별하고,  
그 파라미터가 전체 시스템에 미치는 영향을 이해하는 것입니다.  
우리는 여전히 파라미터 탐색과 발견을 촉진할 방법을 연구 중입니다.

Configs
---------------------

*config* 는 여러 생성기 파라미터를 특정 값으로 설정한 모음입니다.  
Config는 추가적이며, 서로 덮어쓸 수 있고, 다른 config로 구성될 수 있습니다(때로는 config 조각이라고도 함).  
추가적인 config나 config 조각의 명명 규칙은 ``With<YourConfigName>`` 이며, 비추가적인 config의 명명 규칙은 ``<YourConfig>`` 입니다.  
Config는 인자를 받을 수 있으며, 이는 설계에서 파라미터를 설정하거나 다른 파라미터를 참조하는 데 사용됩니다  
(참조: :ref:`Chipyard-Basics/Configs-Parameters-Mixins:Parameters` ).

이 예제는 인자를 받지 않고 하드코딩된 값으로 RTL 설계 파라미터를 설정하는 기본적인 config 조각 클래스를 보여줍니다.  
이 예제에서 ``MyAcceleratorConfig`` 는 설계에서 ``MyAcceleratorKey`` 를 참조할 때 생성기가 사용할 수 있는 변수 집합을 정의하는 Scala case 클래스입니다.

.. _basic-config-example:
.. code-block:: scala

  class WithMyAcceleratorParams extends Config((site, here, up) => {
    case BusWidthBits => 128
    case MyAcceleratorKey =>
      MyAcceleratorConfig(
        rows = 2,
        rowBits = 64,
        columns = 16,
        hartId = 1,
        someLength = 256)
  })

다음 예제는 설정된 이전 파라미터를 사용하여 다른 파라미터를 유도하는 "상위 수준" 추가 config 조각을 보여줍니다.

.. _complex-config-example:
.. code-block:: scala

  class WithMyMoreComplexAcceleratorConfig extends Config((site, here, up) => {
    case BusWidthBits => 128
    case MyAcceleratorKey =>
      MyAcceleratorConfig(
        Rows = 2,
        rowBits = site(SystemBusKey).beatBits,
        hartId = up(RocketTilesKey, site).length)
  })

다음 예제는 ``++`` 를 사용하여 이전의 두 config 조각을 결합하거나 "조립"하는 비추가적인 config를 보여줍니다.  
추가적인 config 조각은 목록에서 오른쪽에서 왼쪽으로(또는 예제에서 아래에서 위로) 적용됩니다.  
따라서 설정되는 파라미터의 순서는 먼저 ``DefaultExampleConfig`` , 그 다음 ``WithMyAcceleratorParams`` ,  
그 다음 ``WithMyMoreComplexAcceleratorConfig`` 이 됩니다.

.. _top-level-config:
.. code-block:: scala

  class SomeAdditiveConfig extends Config(
    new WithMyMoreComplexAcceleratorConfig ++
    new WithMyAcceleratorParams ++
    new DefaultExampleConfig
  )

``WithMyMoreComplexAcceleratorConfig`` 에서 ``site`` , ``here`` , ``up`` 객체는 구성 키에서 정의로의 매핑입니다.  
``site`` 맵은 구성 계층 구조의 루트에서 보는 정의를 제공합니다(이 예제에서는 ``SomeAdditiveConfig`` ).  
``here`` 맵은 계층 구조의 현재 수준(즉, ``WithMyMoreComplexAcceleratorConfig`` 자체)에서 보는 정의를 제공합니다.  
``up`` 맵은 현재 수준에서 한 단계 위에서 보는 정의를 제공합니다(즉, ``WithMyAcceleratorParams`` 에서).

Cake Pattern / Mixin
-------------------------

Cake 패턴 또는 믹스인은 여러 트레이트나 인터페이스 정의를 "믹싱"할 수 있게 해주는 Scala 프로그래밍 패턴입니다(때로는 의존성 주입이라고도 함).  
이 패턴은 Rocket Chip SoC 라이브러리와 Chipyard 프레임워크에서 여러 시스템 구성 요소와 IO 인터페이스를  
큰 시스템 구성 요소로 병합하는 데 사용됩니다.

이 예제는 다양한 선택적 구성 요소를 포함한 완전한 기능의 SoC로 여러 트레이트를 결합하는 Chipyard 기본 탑을 보여줍니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/DigitalTop.scala
    :language: scala
    :start-after: DOC include start: DigitalTop
    :end-before: DOC include end: DigitalTop

여기에는 두 가지 "케이크" 또는 믹스인이 있습니다. 하나는 lazy 모듈을 위한 것이고(ex. ``CanHavePeripheryTLSerial`` ),  
하나는 lazy 모듈 구현을 위한 것입니다(ex. ``CanHavePeripheryTLSerialModuleImp`` 에서 ``Imp`` 는 구현을 의미함).  
Lazy 모듈은 생성기 간의 논리적 연결을 정의하고 구성 정보를 교환하며,  
lazy 모듈 구현은 실제 Chisel RTL 구현을 수행합니다.

``DigitalTop`` 예제 클래스에서 "외부" ``DigitalTop`` 은 "내부" ``DigitalTopModule`` 을 lazy 모듈 구현으로 인스턴스화합니다.  
이것은 모든 논리적 연결이 결정되고 모든 구성 정보가 교환될 때까지 모듈의 즉각적인 구현을 지연시킵니다.  
``System`` 외부 기본 클래스와 ``CanHavePeriphery<X>`` 외부 트레이트는 상위 수준의 논리적 연결을 수행하는 코드를 포함합니다.  
예를 들어, ``CanHavePeripheryTLSerial`` 외부 트레이트는 ``TLSerdesser`` 를 선택적으로 lazy하게 인스턴스화하고,  
``TLSerdesser`` 의 TileLink 노드를 프론트 버스에 연결하는 코드를 포함합니다.

``ModuleImp`` 클래스와 트레이트는 실제 RTL을 구현합니다.

테스트 하네스에서 SoC는 ``val dut = p(BuildTop)(p)`` 로 구현됩니다.

구현 후, ``ChipTop`` 의 시스템 서브모듈은 ``DigitalTop`` 모듈이 될 것이며,  
해당 블록을 인스턴스화하도록 config에서 지정한 경우 ``TLSerdesser`` 모듈(및 기타)을 포함하게 됩니다.

높은 수준에서, ``LazyModule`` 을 확장하는 클래스는 반드시 ``lazy val module`` 을 통해  
모듈 구현을 참조해야 하며, 선택적으로 다른 lazy 모듈을 참조할 수 있습니다  
(이 경우 해당 모듈 계층 구조에서 자식 모듈로 구현됨). "내부" 모듈은 모듈의 구현을 포함하며,  
다른 일반 모듈 또는 lazy 모듈(예: 중첩된 Diplomacy 그래프)을 인스턴스화할 수 있습니다.

추가 믹스인 또는 트레이트의 명명 규칙은 ``CanHave<YourMixin>`` 입니다.  
이는 ``Top`` 클래스에서 ``CanHavePeripheryTLSerial`` 과 같은 것이 RTL 구성 요소를 버스에 연결하고  
상위 수준으로 신호를 노출하는 예에서 볼 수 있습니다.

Additional References
---------------------------

트레이트/믹스인 및 config 조각에 대한 또 다른 설명은 :ref:`Customization/Keys-Traits-Configs:Keys, Traits, and Configs` 에서 확인할 수 있습니다.  
또한, 일부 주제에 대한 간단한 설명(약간 다른 이름으로)은 다음 비디오에서 확인할 수 있습니다: https://www.youtube.com/watch?v=Eko86PGEoDY.

.. Note:: Chipyard는 config가 적용되는지 시스템 ``Top`` 에 적용되는지에 대한 혼동을 피하기 위해 "config fragments"라는 이름을 "config mixins" 대신 사용합니다(둘 다 기술적으로는 Scala mixin임에도 불구하고).

