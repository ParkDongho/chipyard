Ara
===

`Ara <https://github.com/pulp-platform/ara>`__ 는 PULP 프로젝트에서 개발한 RISC-V 벡터 유닛입니다.
Ara 벡터 유닛은 원래의 Ara+CVA6 시스템에서 사용된 것과 유사한 방법론을 따르며, Rocket 또는 Shuttle 인오더 코어와의 통합을 지원합니다.
Ara의 예제 구성은 ``generators/chipyard/src/main/scala/config/AraConfigs.scala`` 에 나열되어 있습니다.

.. Warning:: Ara는 전체 V-extension의 일부 하위 집합만을 지원합니다. 특히, Ara는 가상 메모리 또는 정확한 트랩을 지원하지 않습니다.

Ara를 사용하여 시뮬레이터를 컴파일하려면 makefile에 추가적인 ``USE_ARA`` 플래그를 전달해야 합니다.

.. Note:: Ara는 VCS 시뮬레이션만 지원합니다.

.. code-block:: shell

     make CONFIG=V4096Ara2LaneRocketConfig USE_ARA=1

