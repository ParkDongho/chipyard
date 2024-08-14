.. _firesim-sim-intro:

FPGA-Accelerated Simulation
==============================

FireSim
-----------------------

`FireSim <https://fires.im/>`__ 은 오픈 소스 사이클 정확 FPGA 가속 풀 시스템 하드웨어 시뮬레이션 플랫폼으로, FPGA(아마존 EC2 F1 FPGA 및 로컬 FPGA)에서 실행됩니다.
FireSim은 소프트웨어 RTL 시뮬레이터보다 훨씬 빠른 속도로 RTL 수준의 시뮬레이션을 제공합니다.
또한, FireSim은 메모리 모델 및 네트워크 모델을 포함한 전체 시스템 시뮬레이션을 위한 추가 장치 모델을 제공합니다.

FireSim은 Amazon EC2 F1 FPGA 지원 클라우드 인스턴스와 FPGA가 연결된 로컬 Linux 머신에서 실행할 수 있습니다.
이 문서의 나머지 부분은 Amazon EC2 F1 FPGA 지원 가상 인스턴스에서 실행된다고 가정합니다.
FireSim을 사용하여 Chipyard 디자인을 시뮬레이션하려면 아직 하지 않았다면 :ref:`Chipyard-Basics/Initial-Repo-Setup:Initial Repository Setup` 에서 설명한 대로 리포지토리 설정을 따라야 합니다.
이 설정에서는 FireSim을 포함하여 Chipyard 리포지토리를 설정하기 위해 ``./scripts/firesim-setup.sh`` 스크립트를 실행해야 합니다.
``firesim-setup.sh`` 는 추가 서브모듈을 초기화한 후, FireSim을 Chipyard의 라이브러리 서브모듈로 올바르게 초기화하기 위해 ``--library`` 옵션을 추가하여 FireSim의 ``build-setup.sh`` 스크립트를 호출합니다.
``./sims/firesim/build-setup.sh --help`` 를 실행하여 더 많은 옵션을 확인할 수 있습니다.

마지막으로, FireSim 디렉토리의 루트에서 다음 환경을 소싱합니다:

.. code-block:: shell

    cd sims/firesim
    # (추천) 기본 관리자 환경(env.sh 포함)
    source sourceme-manager.sh`
    # 관리자를 사용하여 설정 완료
    firesim managerinit --platform f1

.. Note:: 새 셸에서 FireSim을 처음 사용할 때마다 ``sourceme-manager.sh`` 를 소싱해야 합니다.

이제 Chipyard에서 FireSim을 사용할 준비가 되었습니다. FireSim에 익숙하지 않다면, :fsim_doc:`FireSim Docs <Getting-Started-Guides/AWS-EC2-F1-Getting-Started/Initial-Setup/Setting-up-your-Manager-Instance.html#completing-setup-using-the-manager>` 로 돌아가 나머지 튜토리얼을 진행하십시오.

Running your Design in FireSim
------------------------------

FireSim에서 Chipyard 구성(``chipyard/src/main/scala`` 에 있는 구성)을 실행하기 위해서는 전통적인 구성 시스템 또는 FireSim의 빌드 레시피 스킴을 통해 간단하게 수행할 수 있습니다.

FireSim 시뮬레이션에는 3개의 추가 구성 조각이 필요합니다:

* ``WithFireSimConfigTweaks`` 는 FireSim 사용 모델에 더 잘 맞도록 디자인을 수정합니다. 이는 여러 작은 구성 조각으로 구성되어 있습니다. 예를 들어, 컴파일러의 올바른 작동을 위해 필요한 클럭 게이팅을 제거하는 구성 조각(예: ``WithoutClockGating``)이 있습니다. 이 구성 조각에는 설계에 UART를 포함하는 것과 같은 다른 구성 조각도 포함되어 있습니다. 이는 기술적으로 선택 사항일 수 있지만, *강력히* 권장됩니다.
* ``WithDefaultMemModel`` 은 FireSim 시뮬레이션에서 FASED 메모리 모델의 기본 구성을 제공합니다. 자세한 내용은 FireSim 문서를 참조하십시오. 이 구성 조각은 현재 ``WithFireSimConfigTweaks`` 내에 기본적으로 포함되어 있으므로 별도로 추가할 필요는 없지만, ``WithFireSimConfigTweaks`` 를 사용하지 않으려는 경우에는 필요합니다.
* ``WithDefaultFireSimBridges`` 는 ``IOBinders`` 키를 사용하여 FireSim의 브리지 시스템을 사용하도록 설정합니다. 이는 시뮬레이션 호스트에서 실행되는 소프트웨어 브리지 모델로 타겟 IO를 구동할 수 있습니다. 자세한 내용은 FireSim 문서를 참조하십시오.

이러한 구성 조각을 사용자 정의 Chipyard 구성에 추가하는 가장 간단한 방법은 FireSim의 빌드 레시피 스킴을 사용하는 것입니다.
FireSim 환경이 설정된 후, ``sims/firesim/deploy/config_build_recipes.yaml`` 에 사용자 정의 빌드 레시피를 정의합니다. FireSim 구성 조각을 Chipyard 구성 앞에 추가하여(각 조각을 ``_`` 로 구분) 이 구성 조각이 사용자 정의 구성에 추가되도록 할 수 있습니다. 예를 들어, Chipyard의 ``LargeBoomV3Config`` 를 DDR3 메모리 모델로 FireSim 시뮬레이션으로 변환하려면, 적절한 FireSim ``TARGET_CONFIG`` 는 ``DDR3FRFCFSLLC4MB_WithDefaultFireSimBridges_WithFireSimConfigTweaks_chipyard.LargeBoomV3Config`` 가 됩니다. FireSim 구성 조각은 ``firesim.firesim`` 스칼라 패키지의 일부이므로, Chipyard 구성 조각과 달리 전체 패키지 이름을 접두사로 붙일 필요가 없습니다.

FireSim 빌드 레시피에서 FireSim 구성 조각을 추가하는 대안 방법은 FireSim 구성 조각을 포함하는 새로운 "영구" FireChip 사용자 정의 구성을 만드는 것입니다.
우리는 동일한 타겟 RTL을 사용하고 있으며, 해당 모듈의 IO에 대한 새로운 연결 동작 세트만 지정하면 됩니다. ``chipyard`` 에 정의된 구성 상속을 통해 ``generators/firechip/src/main/scala/TargetConfigs`` 에 일치하는 구성을 생성하기만 하면 됩니다.

.. literalinclude:: ../../generators/firechip/src/main/scala/TargetConfigs.scala
    :language: scala
    :start-after: DOC include start: firesimconfig
    :end-before: DOC include end: firesimconfig

이 옵션은 추가 구성 코드를 유지 관리해야 하는 것처럼 보일 수 있지만, 사용자 정의 인수도 받을 수 있는 더 복잡한 구성 조각을 포함할 수 있는 이점이 있습니다(예: ``WithDefaultMemModel`` 는 선택적 인수를 받을 수 있음).

FireSim에서 자체 하드웨어 디자인을 빌드하는 방법에 대한 자세한 내용은 :fsim_doc:`FireSim Docs <Getting-Started-Guides/AWS-EC2-F1-Getting-Started/Building-a-FireSim-AFI.html#building-your-own-hardware-designs-firesim-amazon-fpga-images>` 를 참조하십시오.


