.. _keys-traits-configs:

Keys, Traits, and Configs
=========================

여러분은 아마도 이 시점에서 Chisel에서 키, 트레이트, 그리고 설정과 관련된 코드 조각들을 보았을 것입니다.
이 섹션에서는 이러한 Chisel/Scala 구성 요소 간의 상호작용을 설명하고, 매개변수화된 설계를 생성하고 이를 구성하는 방법에 대한 모범 사례를 제공하는 것을 목표로 합니다.

GCD 예제를 계속 사용하겠습니다.

Keys
----

Keys는 일부 사용자 지정 위젯을 제어하는 매개변수를 지정합니다. Keys는 일반적으로 **Option 타입** 으로 구현되어야 하며, 시스템에서 변경이 없음을 의미하는 기본 값으로 ``None`` 을 설정해야 합니다. 즉, 사용자가 키를 명시적으로 설정하지 않을 때의 기본 동작은 아무런 작업도 하지 않는 것입니다.

Keys는 일반적으로 특정 블록과 관련이 있으며 시스템 수준의 통합과 관련이 없으므로 서브 프로젝트에서 정의되고 문서화되어야 합니다. (예제 GCD 위젯의 경우 예외를 둡니다.)

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/GCD.scala
    :language: scala
    :start-after: DOC include start: GCD key
    :end-before: DOC include end: GCD key

Key 내의 객체는 일반적으로 해당 블록이 허용하는 매개변수 세트를 정의하는 ``case class XXXParams`` 입니다. 예를 들어, GCD 위젯의 ``GCDParams`` 는 주소, 피연산자 너비, 위젯이 Tilelink 또는 AXI4로 연결되어야 하는지 여부, 그리고 위젯이 블랙박스 Verilog 구현을 사용할지 또는 Chisel 구현을 사용할지를 매개변수화합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/GCD.scala
    :language: scala
    :start-after: DOC include start: GCD params
    :end-before: DOC include end: GCD params

Chisel에서 키에 저장된 값을 액세스하는 것은 관련 모듈에 ``implicit p: Parameters`` 객체가 전달되는 한 간단합니다. 예를 들어, ``p(GCDKey).get.address`` 는 ``GCDParams`` 의 주소 필드를 반환합니다. 이는 ``GCDKey`` 가 ``None`` 으로 설정되지 않은 경우에만 작동하므로, 해당 경우에 대한 체크가 필요합니다!

Traits
------

대부분의 사용자 지정 블록은 기존 블록의 동작을 수정할 필요가 있습니다. 예를 들어, GCD 위젯은 ``DigitalTop`` 모듈이 위젯을 Tilelink를 통해 인스턴스화하고 연결하며, 상위 레벨 ``gcd_busy`` 포트를 생성하고 이를 모듈에 연결할 필요가 있습니다. Traits를 사용하면 기존 ``DigitalTop`` 코드에 수정 없이 이를 수행할 수 있으며, 다양한 사용자 지정 블록에 대한 코드의 구획화를 가능하게 합니다.

상위 레벨 트레이트는 ``DigitalTop`` 이 일부 사용자 지정 키를 읽고, 선택적으로 해당 키로 정의된 위젯을 인스턴스화하고 연결하도록 매개변수화되었음을 지정합니다. Traits는 사용자 지정 로직의 인스턴스화를 강제해서는 **안 됩니다**. 즉, Traits는 ``CanHave`` 의미론을 사용하여 키가 설정되지 않은 경우의 기본 동작이 아무 작업도 하지 않는 것으로 작성되어야 합니다.

상위 레벨 트레이트는 해당 키와 함께 서브 프로젝트에서 정의되고 문서화되어야 합니다. 그런 다음 해당 트레이트는 Chipyard에서 사용되는 ``DigitalTop`` 에 추가되어야 합니다.

아래에서는 GCD 예제의 트레이트를 볼 수 있습니다. Lazy 트레이트는 GCD 모듈을 Diplomacy 그래프에 연결합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/GCD.scala
    :language: scala
    :start-after: DOC include start: GCD lazy trait
    :end-before: DOC include end: GCD lazy trait

이러한 트레이트는 Chipyard의 기본 ``DigitalTop`` 에 추가됩니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/DigitalTop.scala
    :language: scala
    :start-after: DOC include start: DigitalTop
    :end-before: DOC include end: DigitalTop

Config Fragments
----------------

Config fragments는 키를 기본값이 아닌 값으로 설정합니다. 구성 프래그먼트를 정의하는 구성의 집합은 생성기에서 사용되는 모든 키에 대한 값을 생성합니다.

예를 들어, ``WithGCD`` 구성 프래그먼트는 인스턴스화하려는 GCD 위젯의 유형으로 매개변수화됩니다. 이 구성 프래그먼트를 구성에 추가하면, ``GCDKey`` 가 ``GCDParams`` 의 인스턴스로 설정되어, 앞서 언급한 트레이트가 GCD 위젯을 적절하게 인스턴스화하고 연결하도록 합니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/example/GCD.scala
    :language: scala
    :start-after: DOC include start: GCD config fragment
    :end-before: DOC include end: GCD config fragment

이 구성 프래그먼트를 구성에 사용할 수 있습니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/MMIOAcceleratorConfigs.scala
    :language: scala
    :start-after: DOC include start: GCDTLRocketConfig
    :end-before: DOC include end: GCDTLRocketConfig

.. note::
   구성 시스템에 대해 더 많은 정보를 원하는 독자는 :ref:`cdes` 를 읽어볼 수 있습니다.

Chipyard Config Fragments
-------------------------

탐색 가능성을 위해, 사용자는 ``make find-config-fragments`` 를 실행하여 구성 프래그먼트 목록을 볼 수 있습니다.

