.. _harness-clocks:

Creating Clocks in the Test Harness
===================================

Chipyard에서는 현재 SoC 설계(``ChipTop`` 아래의 모든 것)에 대해 독립적인 클럭 도메인을 지원하며, 이는 Diplomacy를 통해 이루어집니다.
``ChipTop`` 클럭 포트는 ``harnessClockInstantiator.requestClock(freq)``에 의해 구동됩니다.
``ChipTop`` 리셋 포트는 비동기 리셋을 제공하기 위한 ``referenceReset()`` 함수에 의해 구동됩니다.

``ChipTop`` 의 ``HarnessBinder`` 는 ``HarnessBinderClockFrequencyKey`` 값에 의해 클럭이 제공됩니다. 리셋은 클럭에 동기화된 동기 리셋으로 제공됩니다.

테스트 하네스에서 클럭을 요청하는 것은 ``generators/chipyard/src/main/scala/harness/HarnessClocks.scala`` 에 있는 ``HarnessClockInstantiator`` 클래스에 의해 수행됩니다.
그런 다음 ``requestClock`` 함수를 호출하여 특정 주파수에서 클럭과 동기화된 리셋을 요청할 수 있습니다.
다음 예제를 참고하세요:

.. literalinclude:: ../../generators/chipyard/src/main/scala/harness/HarnessBinders.scala
    :language: scala
    :start-after: DOC include start: HarnessClockInstantiatorEx
    :end-before: DOC include end: HarnessClockInstantiatorEx

여기에서 ``th.harnessClockInstantiator`` 를 사용하여 ``memFreq`` 주파수에서 클럭과 리셋을 요청하는 예제를 볼 수 있습니다.

