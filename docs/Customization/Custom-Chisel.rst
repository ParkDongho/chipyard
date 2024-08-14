.. _custom_chisel:

Integrating Custom Chisel Projects into the Generator Build System
==================================================================

.. warning::
   이 섹션은 git 서브모듈을 통해 사용자 정의 Chisel을 통합하는 것을 가정합니다.
   Chipyard 프레임워크에 직접 사용자 정의 Chisel을 커밋하는 것도 가능하지만,
   사용자 정의 코드를 git 서브모듈로 관리하는 것을 강력히 권장합니다. 서브모듈을 사용하면
   Chipyard 프레임워크 개발과 사용자 정의 기능 개발을 분리할 수 있습니다.

개발 중에는 Chisel 코드를 서브모듈로 포함시켜 여러 프로젝트에서 공유할 수 있도록 하고 싶을 것입니다.
Chipyard 프레임워크에 서브모듈을 추가하려면, 프로젝트가 다음과 같이 구성되어 있는지 확인하십시오.

.. code-block:: none

    yourproject/
        build.sbt
        src/main/scala/
            YourFile.scala

이 구조를 가진 git 저장소를 만들고 접근할 수 있도록 설정하십시오.
그런 다음, 다음 디렉토리 계층 아래에 서브모듈로 추가합니다: ``generators/yourproject``.

``build.sbt`` 는 Chisel 프로젝트에 대한 메타데이터를 설명하는 최소한의 파일입니다.
간단한 프로젝트의 경우, ``build.sbt`` 는 비어 있을 수도 있지만, 아래에 예시적인
``build.sbt`` 를 제공합니다.

.. code-block:: scala

    organization := "edu.berkeley.cs"

    version := "1.0"

    name := "yourproject"

    scalaVersion := "2.12.4"

다음과 같이 쉘에서 서브모듈을 추가하십시오.

.. code-block:: shell

    cd generators/
    git submodule add https://git-repository.com/yourproject.git

그런 다음, Chipyard 최상위 ``build.sbt`` 파일에 ``yourproject`` 를 추가합니다.

.. code-block:: scala

    lazy val yourproject = (project in file("generators/yourproject")).settings(commonSettings).dependsOn(rocketchip)

이제 새 프로젝트에서 서브모듈에 정의된 클래스를 가져오려면,
이를 의존성으로 추가해야 합니다. 예를 들어, ``chipyard`` 프로젝트에서
이 코드를 사용하려면, ``lazy val chipyard`` 의 `.dependsOn()` 목록에
프로젝트를 추가하십시오. 원본 코드는 시간이 지남에 따라 변경될 수 있지만,
다음과 유사하게 보일 것입니다:

.. code-block:: scala

    lazy val chipyard = (project in file("generators/chipyard"))
        .dependsOn(testchipip, rocketchip, boom, rocketchip_blocks, rocketchip_inclusive_cache,
            dsptools, `rocket-dsp-utils`,
            gemmini, icenet, tracegen, cva6, nvdla, sodor, ibex, fft_generator,
            yourproject, // <- 간단함을 위해 목록 중간에 추가됨
            constellation, mempress)
        .settings(libraryDependencies ++= rocketLibDeps.value)
        .settings(
            libraryDependencies ++= Seq(
            "org.reflections" % "reflections" % "0.10.2"
            )
        )
        .settings(commonSettings)

