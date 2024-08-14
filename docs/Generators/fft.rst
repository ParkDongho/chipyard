FFT Generator
====================================

FFT 생성기는 매개변수화 가능한 FFT 가속기입니다.

Configuration
--------------------------
다음 구성은 8점 FFT를 생성합니다:

.. literalinclude:: ../../generators/chipyard/src/main/scala/config/MMIOAcceleratorConfigs.scala
   :language: scala
   :start-after: DOC include start: FFTRocketConfig
   :end-before: DOC include end: FFTRocketConfig

:code:`baseAddress` 는 FFT의 읽기 및 쓰기 레인의 시작 주소를 지정합니다. FFT 쓰기 레인은 항상 :code:`baseAddress` 에 위치합니다. 출력 포인트당 1개의 읽기 레인이 있으며, 이 구성은 8점 FFT를 지정하므로 8개의 읽기 레인이 있습니다. 읽기 레인 :code:`i` (출력 포인트 :code:`i` 를 가져오려면)는 :code:`baseAddr + 64비트(64비트 시스템 가정) + (i * 8)` 에 위치합니다. :code:`baseAddress` 는 64비트 정렬이어야 합니다.

:code:`width` 는 입력 포인트의 크기를 이진수로 나타냅니다. :code:`w` 크기의 폭은 각 포인트가 실수 성분에 대해 :code:`w` 비트, 허수 성분에 대해 :code:`w` 비트를 가지며, 포인트당 총 `2w` 비트를 갖는 것을 의미합니다. :code:`decPt`는 각 포인트의 실수 및 허수 값의 고정 소수점 표현에서 소수점의 위치를 나타냅니다. 위의 구성에서 각 포인트는 `32` 비트이며, `16` 비트는 실수 성분을 나타내고, 나머지 `16` 비트는 허수 성분을 나타냅니다. 각 성분의 `16` 비트 중 `8` LSB는 소수 성분을 나타내고, 나머지 `8` MSB는 정수 성분을 나타냅니다. 실수 및 허수 성분은 고정 소수점 표현을 사용합니다.

이 예제 Chipyard 구성을 시뮬레이션하려면 다음 명령을 실행하십시오:

.. code-block:: shell

    cd sims/verilator # 또는 "cd sims/vcs"
    make CONFIG=FFTRocketConfig

Usage and Testing
--------------------------

포인트는 단일 쓰기 레인을 통해 FFT로 전달됩니다. C 의사 코드에서 이것은 다음과 같이 보일 수 있습니다:

.. code-block:: C

    for (int i = 0; i < num_points; i++) {
        // FFT_WRITE_LANE = baseAddress
        uint32_t write_val = points[i];
        volatile uint32_t* ptr = (volatile uint32_t*) FFT_WRITE_LANE;
        *ptr = write_val;
    }

입력 값의 정확한 수가 전달되면(위의 구성에서는 8개의 값이 전달됩니다), 읽기 레인에서 값을 읽을 수 있습니다(C 의사 코드):

.. code-block:: C

    for (int i = 0; i < num_points; i++) {
        // FFT_RD_LANE_BASE = baseAddress + 64비트(쓰기 레인용)
        volatile uint32_t* ptr_0 = (volatile uint32_t*) (FFT_RD_LANE_BASE + (i * 8));
        uint32_t read_val = *ptr_0;
    }

:code:`tests/` 디렉토리의 :code:`fft.c` 테스트 파일을 사용하여 :code:`FFTRocketConfig` 로 빌드된 SoC에서 FFT의 기능을 확인할 수 있습니다.

Acknowledgements
--------------------------
FFT 생성기 코드는 UC Berkeley의 ADEPT Lab에서 제공한 `Hydra Spine <https://adept.eecs.berkeley.edu/projects/hydra-spine/>`_ 프로젝트에서 수정되었습니다.

원래 프로젝트의 저자(특정 순서 없음):

* James Dunn, UC Berkeley (dunn [at] eecs [dot] berkeley [dot] edu)
   * :code:`Deserialize.scala`
   * :code:`Tail.scala`
   * :code:`Unscramble.scala`
* Stevo Bailey (stevo.bailey [at] berkeley [dot] edu)
   * :code:`FFT.scala`
