.. _hetero_socs_:

Heterogeneous SoCs
===============================

Chipyard 프레임워크는 여러 코어와 가속기를 임의로 조합하여 SoC를 구성할 수 있도록 합니다.
이 문서에서는 특히 Rocket과 BOOM을 조합하여 고유한 SoC를 만드는 방법에 대해 논의합니다.

Creating a Rocket and BOOM System
-------------------------------------------

Rocket과 BOOM 코어를 포함하는 SoC를 인스턴스화하는 것은 모두 구성 시스템과 두 개의 특정 구성 조각(config fragment)으로 이루어집니다.
BOOM과 Rocket 모두 각각 ``WithN{Small|Medium|Large|etc.}BoomCores(X)`` 와 ``WithNBigCores(X)`` 로 레이블이 붙은 구성 조각을 가지고 있으며, 이는 자동으로 코어/타일의 ``X`` 복사본을 생성합니다 [1]_.
함께 사용하면 이종 시스템을 만들 수 있습니다.

다음 예제는 듀얼 코어 BOOM과 싱글 코어 Rocket을 보여줍니다.

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/HeteroConfigs.scala
    :language: scala
    :start-after: DOC include start: DualBoomAndSingleRocket
    :end-before: DOC include end: DualBoomAndSingleRocket

구성 조각은 오른쪽에서 왼쪽으로 (또는 여기에서 형식화된 것처럼 아래에서 위로) 적용되기 때문에, 오른쪽에 위치한 코어를 지정하는 구성 조각(위 예에서는 ``freechips.rocketchip.subsystem.WithNBigCores``)이 첫 번째 하트 ID를 받습니다.
다음 구성을 고려해 보십시오:

.. code-block:: scala

    class RocketThenBoomHartIdTestConfig extends Config(
      new boom.common.WithNLargeBooms(2) ++
      new freechips.rocketchip.subsystem.WithNBigCores(3) ++
      new chipyard.config.AbstractConfig)

이는 세 개의 Rocket 코어와 두 개의 BOOM 코어를 가진 SoC를 지정합니다.
Rocket 코어는 하트 ID 0, 1, 2를 가지며, BOOM 코어는 하트 ID 3과 4를 가집니다.
반면, 이러한 두 구성 조각의 순서를 반대로 하는 구성을 고려해 보십시오:

.. code-block:: scala

    class BoomThenRocketHartIdTestConfig extends Config(
      new freechips.rocketchip.subsystem.WithNBigCores(3) ++
      new boom.common.WithNLargeBooms(2) ++
      new chipyard.config.AbstractConfig)

이 또한 세 개의 Rocket 코어와 두 개의 BOOM 코어를 가진 SoC를 지정하지만, BOOM 구성 조각이 Rocket 구성 조각보다 먼저 평가되므로 하트 ID가 반대로 할당됩니다.
BOOM 코어는 하트 ID 0과 1을 가지며, Rocket 코어는 하트 ID 2, 3, 4를 가집니다.

.. [1] 이 섹션에서 "core"와 "tile"이 교대로 사용되지만, "core"와 "tile" 사이에는 미묘한 차이가 있습니다 ("tile"은 "core", L1D/I$, PTW를 포함합니다).
    문서의 여러 부분에서 "core"는 일반적으로 "tile"을 의미하며 (큰 차이는 없지만 언급할 가치가 있습니다).

