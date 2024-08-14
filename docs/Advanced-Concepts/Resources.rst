Accessing Scala Resources
===============================

Scala 리소스를 접근하는 간단한 방법은 FIRRTL에서 제공하는 ``addResource`` 기능을 사용하는 것입니다. 이를 통해 시뮬레이션 컴파일 또는 VLSI 흐름에서 사용할 소스 파일을 빌드 디렉토리로 복사할 수 있습니다. 예제는 `generators/testchipip/src/main/scala/SimTSI.scala <https://github.com/ucb-bar/testchipip/blob/master/src/main/scala/SimTSI.scala>`_ 에서 확인할 수 있습니다. 다음은 그 예제를 인라인으로 보여줍니다:

.. code-block:: scala

    class SimTSI extends BlackBox with HasBlackBoxResource {
      val io = IO(new Bundle {
        val clock = Input(Clock())
        val reset = Input(Bool())
        val tsi = Flipped(new TSIIO)
        val exit = Output(Bool())
      })

      addResource("/testchipip/vsrc/SimTSI.v")
      addResource("/testchipip/csrc/SimTSI.cc")
    }

이 예제에서는 ``SimTSI`` 파일이 특정 폴더(이 경우 ``path/to/testchipip/src/main/resources/testchipip/...``)에서 빌드 폴더로 복사됩니다. ``addResource`` 경로는 ``src/main/resources`` 디렉토리에서 리소스를 가져옵니다. 따라서 ``src/main/resources/fileA.v`` 에 있는 파일을 가져오려면 ``addResource("/fileA.v")`` 를 사용할 수 있습니다. 

그러나 이 접근 방식의 주의사항은 FIRRTL 컴파일 중에 파일을 검색하려면 해당 프로젝트가 FIRRTL 컴파일러의 클래스패스에 포함되어 있어야 한다는 점입니다. 따라서 Chipyard의 ``build.sbt`` 에서 FIRRTL 컴파일러에 해당 SBT 프로젝트를 종속성으로 추가해야 합니다. 예를 들어, Chipyard ``build.sbt`` 에 ``myAwesomeAccel`` 이라는 새로운 프로젝트를 추가했다고 가정합니다. 그런 다음 이 프로젝트를 ``tapeout`` 프로젝트의 ``dependsOn`` 종속성으로 추가할 수 있습니다. 예를 들면 다음과 같습니다:

.. code-block:: scala

    lazy val myAwesomeAccel = (project in file("generators/myAwesomeAccelFolder"))
      .dependsOn(rocketchip)
      .settings(commonSettings)

