Initial Repository Setup
========================================================

Prerequisites
-------------------------------------------

Chipyard는 Linux 기반 시스템에서 개발되고 테스트됩니다.

.. Warning:: macOS 또는 다른 BSD 기반 시스템에서도 사용할 수 있지만, GNU 도구를 설치해야 합니다. 또한 ``brew`` 를 통해 RISC-V 툴체인을 설치하는 것이 권장됩니다.

.. Warning:: Windows를 사용하는 경우, `Windows Subsystem for Linux (WSL) <https://learn.microsoft.com/en-us/windows/wsl/>` 을 사용하는 것이 권장됩니다.

Running on AWS EC2 with FireSim
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

AWS EC2 인스턴스에서 FireSim과 함께 Chipyard를 사용할 계획이라면 :fsim_doc:`FireSim 문서 <>`를 참조해야 합니다.
특히, 문서의 :fsim_doc:`Initial Setup/Installation <Getting-Started-Guides/AWS-EC2-F1-Getting-Started/Initial-Setup/index.html>` 섹션을 따르고, :fsim_doc:`Setting up the FireSim Repo <Getting-Started-Guides/AWS-EC2-F1-Getting-Started/Initial-Setup/Setting-up-your-Manager-Instance.html#setting-up-the-firesim-repo>` 까지 진행해야 합니다.
그 시점에서 FireSim을 클론하는 대신 :ref:`Chipyard-Basics/Initial-Repo-Setup:Setting up the Chipyard Repo` 를 따라 Chipyard를 클론할 수 있습니다.

Default Requirements Installation
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Chipyard에서는 시스템 종속성을 관리하기 위해 `Conda <https://docs.conda.io/en/latest/>`__ 패키지 관리자를 사용합니다.
Conda를 사용하면 ``make``, ``gcc`` 등의 시스템 종속성을 포함한 "환경"을 생성할 수 있습니다.

.. Note:: Chipyard는 Conda 설치 없이도 실행할 수 있지만, 이 경우 사용자는 툴체인과 종속성을 수동으로 설치해야 합니다.

먼저, Chipyard를 사용하려면 최신 Conda를 시스템에 설치해야 합니다.
최신 Conda를 **Miniforge** 설치 프로그램으로 설치하는 방법에 대해서는 `Conda 설치 지침 <https://github.com/conda-forge/miniforge/#download>`__ 를 참조하세요.

Conda가 설치되고 ``PATH`` 에 추가되면, 먼저 ``git`` 의 버전을 설치하여 리포지토리를 체크아웃해야 합니다.
이를 위해 시스템 패키지 관리자(예: ``yum`` 또는 ``apt`` )를 사용하여 ``git`` 을 설치할 수 있습니다.
이 ``git`` 은 리포지토리를 처음 체크아웃하는 데만 사용되며, 이후 Conda를 통해 최신 버전의 ``git`` 이 설치됩니다.

다음으로, 리포지토리를 처음 설정할 때 종속성을 훨씬 더 빠르게 해결할 수 있도록 `libmamba <https://www.anaconda.com/blog/a-faster-conda-for-a-growing-community>`__ 를 설치합니다.

.. code-block:: shell

    conda install -n base conda-libmamba-solver
    conda config --set solver libmamba

그런 다음 Conda를 사용할 수 있는지 확인합니다.
기본적으로 Conda 설정 후에는 이미 ``base`` 환경에 있어야 하지만, 필요하다면 다음 명령어를 실행하여 해당 환경에 들어갈 수 있습니다:

.. code-block:: shell

    conda activate base


Setting up the Chipyard Repo
-------------------------------------------

적절한 Chipyard 버전을 체크아웃하는 것으로 시작합니다. 다음 명령어를 실행하세요:

.. parsed-literal::

    git clone https://github.com/ucb-bar/chipyard.git
    cd chipyard
    # 최신 공식 Chipyard 릴리스를 체크아웃합니다.
    # 참고: 문서 버전이 "stable"이 아닌 경우 최신 릴리스가 아닐 수 있습니다.
    git checkout |version|

그런 다음, ``riscv-tools`` 툴체인으로 Chipyard를 완전히 설정하려면 다음 스크립트를 실행하세요.

.. Warning:: 다음 스크립트는 Chipyard의 "전체" 설치를 완료하며, 시스템에 따라 시간이 오래 걸릴 수 있습니다.
    계속하기 전에 이 스크립트가 완전히 완료되었는지(중단 없이) 확인하십시오. 사용자는 ``--skip`` 또는 ``-s`` 플래그를 사용하여 단계를 건너뛸 수 있습니다:

    ``-s 1`` Conda 환경 초기화를 건너뜁니다.

    ``-s 2`` Chipyard 서브모듈 초기화를 건너뜁니다.

    ``-s 3`` 툴체인 관련 파일(Spike, PK, 테스트, libgloss) 초기화를 건너뜁니다.

    ``-s 4`` ctags 초기화를 건너뜁니다.

    ``-s 5`` Chipyard Scala 소스 사전 컴파일을 건너뜁니다.

    ``-s 6`` FireSim 초기화를 건너뜁니다.

    ``-s 7`` FireSim 소스 사전 컴파일을 건너뜁니다.

    ``-s 8`` FireMarshal 초기화를 건너뜁니다.

    ``-s 9`` FireMarshal 기본 buildroot Linux 소스 사전 컴파일을 건너뜁니다.

    ``-s 10`` CIRCT 설치를 건너뜁니다.

    ``-s 11`` 리포지토리 정리 작업을 건너뜁니다.

.. code-block:: shell

    ./build-setup.sh riscv-tools

이 스크립트는 conda 환경 초기화 과정, 모든 서브모듈 초기화(``init-submodules-no-riscv-tools.sh`` 스크립트 포함), 툴체인 설치 및 기타 설정을 포함합니다.
이 스크립트가 수행하는 작업 및 설정의 일부를 비활성화하는 방법에 대한 자세한 내용은 ``./build-setup.sh --help`` 를 참조하십시오.

.. Warning:: ``git`` 을 직접 사용하는 경우 모든 서브모듈을 초기화하려고 시도합니다. 이는 이 동작을 명확히 원하지 않는 한 권장되지 않습니다.

.. Note:: ``build-setup.sh`` 스크립트가 충돌 문제로 실패하는 경우, ``conda update -n base --all`` 을 실행하여 conda 환경의 모든 패키지를 업그레이드하면 도움이 될 때가 있습니다.

.. Note:: 기본적으로, ``build-setup.sh`` 스크립트는 추가 툴체인 유틸리티(RISC-V 테스트, PK, Spike 등)를 ``$CONDA_PREFIX/<toolchain-type>`` 에 설치합니다. 따라서, ``conda remove`` 를 사용하여 컴파일러를 제거하면 이러한 유틸리티/테스트도 다시 설치/빌드해야 합니다.

.. Note:: 이미 작동 중인 conda 환경이 설정되어 있는 경우, 별도의 Chipyard 클론은 앞서 언급한 스크립트를 실행하면서 해당 사전 사용된 환경을 사용할 수 있습니다(``init-submodules...``, ``build-toolchain...`` 등).

.. Note:: 고급 사용자이며 직접 컴파일러/툴체인을 빌드하고자 하는 경우, https://github.com/ucb-bar/riscv-tools-feedstock 리포지토리(툴체인의 ``toolchains/*`` 디렉토리에 서브모듈로 포함)를 참조하여 직접 컴파일러를 빌드할 수 있습니다.

다음 명령어를 실행하면 ``$CHIPYARD_DIRECTORY/.conda-env`` 경로가 표시된 환경을 확인할 수 있습니다.

.. code-block:: shell

    conda env list

.. Note:: Conda 사용 방법과 그 이점에 대한 자세한 내용은 FireSim의 :fsim_doc:`Conda 문서 <Advanced-Usage/Conda.html>` 를 참조하십시오.

Sourcing ``env.sh``
-------------------

설치가 완료되면 최상위 리포지토리에 ``env.sh`` 파일이 생성되어 있어야 합니다.
이 파일은 ``build-setup.sh``에서 생성된 conda 환경을 활성화하고, 이후 Chipyard 단계에 필요한 환경 변수를 설정합니다(``make`` 시스템이 제대로 작동하기 위해 필요).
이 스크립트를 실행하면 ``PATH``, ``RISCV``, ``LD_LIBRARY_PATH`` 환경 변수가 요청된 툴체인에 맞게 설정됩니다.
이 파일을 ``.bashrc`` 또는 이에 상응하는 환경 설정 파일에 소스로 추가하여 적절한 변수를 얻을 수 있으며, 현재 환경에 직접 포함할 수도 있습니다:

.. Note:: Mac 또는 RHEL/CentOS 기반 Linux 배포판을 사용하는 경우, 먼저 ``conda deactivate`` 로 기본 conda 환경을 비활성화한 후 계속 진행해야 합니다. 또한, ``conda config --set auto_activate_base false`` 로 기본적으로 비활성화된 상태로 유지할 수도 있습니다. 자세한 내용은 이 `이슈 <https://github.com/conda/conda/issues/9392>`__ 를 참조하세요.

.. code-block:: shell

    source ./env.sh

.. Warning:: 이 ``env.sh`` 파일은 ``make`` 명령어를 실행하기 전에 항상 소스로 추가되어야 합니다.

.. Note:: 컴파일러/툴체인을 비활성화/활성화할 수 있습니다(설치는 유지). ``source $CONDA_PREFIX/etc/conda/deactivate.d/deactivate-${PKG_NAME}.sh`` 또는 ``$CONDA_PREFIX/etc/conda/activate.d/activate-${PKG_NAME}.sh`` 를 실행하면 됩니다(``PKG_NAME`` 은 예를 들어 ``ucb-bar-riscv-tools`` 일 수 있습니다). 이것은 앞서 언급한 3가지 환경 변수를 수정합니다.

.. Warning:: ``env.sh`` 파일은 Chipyard 리포지토리마다 생성됩니다.
    다중 Chipyard 리포지토리 설정에서는 여러 ``env.sh`` 파일을 소스로 추가할 수 있습니다(순서는 상관없음).
    그러나 최종적으로 소스로 추가된 ``env.sh`` 파일은 ``make`` 명령어를 실행할 Chipyard 리포지토리에 위치한 것이어야 합니다.

DEPRECATED: Pre-built Docker Image
-------------------------------------------

Chipyard 리포지토리를 로컬에 설정하는 대신, Docker Hub에서 사전 빌드된 Docker 이미지를 가져올 수 있습니다. 이 이미지는 모든 종속성이 설치된 상태로 제공되며, Chipyard가 클론되고 툴체인이 초기화됩니다. 이 이미지는 기본 Chipyard(초기 FireMarshal, FireSim, Hammer 초기화 제외)를 설정합니다. 각 이미지는 해당 이미지에서 클론/설정된 Chipyard 버전에 해당하는 태그와 함께 제공됩니다. 가져오는 동안 태그를 지정하지 않으면 최신 버전의 Chipyard가 포함된 이미지를 가져옵니다.
먼저 Docker 이미지를 가져옵니다. 다음 명령어를 실행하세요:

.. code-block:: shell

    sudo docker pull ucbbar/chipyard-image:<TAG>

Docker 컨테이너를 대화형 셸에서 실행하려면 다음 명령어를 실행하세요:

.. code-block:: shell

    sudo docker run -it ucbbar/chipyard-image bash

What's Next?
-------------------------------------------

다음 단계는 Chipyard로 무엇을 할 계획인지에 따라 달라집니다.

* Chipyard 예제 중 하나의 시뮬레이션을 실행하려면, :ref:`sw-rtl-sim-intro` 로 이동하여 지침을 따르세요.

* 사용자 정의 Chipyard SoC 구성을 시뮬레이션하려면, :ref:`Simulation/Software-RTL-Simulation:Simulating A Custom Project` 로 이동하여 지침을 따르세요.

* 전체 시스템 FireSim 시뮬레이션을 실행하려면, :ref:`firesim-sim-intro` 로 이동하여 지침을 따르세요.

* 새로운 가속기를 추가하려면, :ref:`customization` 로 이동하여 지침을 따르세요.

* Chipyard의 구조를 알고 싶다면, :ref:`chipyard-components` 로 이동하세요.

* 생성기(BOOM, Rocket 등) 자체를 변경하려면, :ref:`generator-index` 를 참조하세요.

* Chipyard 예제를 사용한 튜토리얼 VLSI 흐름을 실행하려면, :ref:`tutorial` 로 이동하여 지침을 따르세요.

* Chipyard 예제 중 하나를 사용하여 칩을 빌드하려면, :ref:`build-a-chip` 으로 이동하여 지침을 따르세요.

Upgrading Chipyard Release Versions
-------------------------------------------

Chipyard 버전 간 업그레이드를 위해서는 리포지토리의 새 클론을 사용하는 것이 좋습니다(또는 새로운 릴리스를 병합한 후 포크).

Chipyard는 빌드 시스템과 스크립트가 혼합된 복잡한 프레임워크입니다. 구체적으로는 git 서브모듈, sbt 빌드 파일, 맞춤 작성된 bash 스크립트 및 생성된 파일에 의존합니다.
이 때문에, Chipyard 버전 간 업그레이드는 단순히 ``git submodule update --recursive`` 를 실행하는 것만큼 간단하지 않습니다. 이는 특정 Chipyard 환경에서 사용되지 않을 수 있는 대형 서브모듈의 재귀적 클로닝을 초래할 수 있습니다.
또한, 릴리스 버전 간 호환되지 않을 수 있는 생성된 파일의 오래된 상태를 해결할 수 없습니다.

고급 git 사용자인 경우, 새 리포지토리 클론 대신 ``git clean -dfx`` 를 실행한 다음 표준 Chipyard 설정 순서를 실행하는 방법을 사용할 수 있습니다.
이 접근 방식은 위험하며, git에 익숙하지 않은 사용자에게는 권장되지 않습니다. 이는 리포지토리 상태를 "폭발"시키고 경고 없이 추적되지 않은 수정된 파일을 모두 제거하기 때문입니다.
따라서, 사용자 정의 비커밋 변경 작업을 수행 중이었다면 이를 잃게 됩니다.

여전히 제자리에서 수동 버전 업그레이드(**권장하지 않음**)를 시도하고 싶다면, 다음 영역에서 오래된 상태를 해결해보는 것이 좋습니다:

* sbt에서 생성된 오래된 ``target`` 디렉토리 삭제.

* 생성된 스크립트 및 소스 파일 재생성(예: ``env.sh``)

* FireMarshal 내의 타겟 소프트웨어 상태(Linux 커널 바이너리, Linux 이미지) 재생성/삭제

이는 Chipyard 내의 잠재적 오래된 상태에 대한 포괄적인 목록이 아닙니다.
따라서, 앞서 언급한 것처럼 Chipyard 버전 업그레이드의 권장 방법은 새 클론(또는 병합 후 새 클론)입니다.
