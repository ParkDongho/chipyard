.. _baremetal-programs:

Baremetal RISC-V Programs
==========================

시뮬레이션에서 실행할 Baremetal RISC-V 프로그램을 빌드하기 위해, riscv64-unknown-elf 크로스 컴파일러와 libgloss 보드 지원 패키지의 포크를 사용합니다. 이러한 프로그램을 직접 빌드하려면, 크로스 컴파일러를 "-fno-common -fno-builtin-printf -specs=htif_nano.specs" 플래그와 함께 호출하고, 링크할 때는 "-static -specs=htif_nano.specs" 인자를 사용하면 됩니다. 예를 들어, "Hello, World" 프로그램을 Baremetal에서 실행하려면 다음과 같이 할 수 있습니다.

.. code:: c

    #include <stdio.h>

    int main(void)
    {
        printf("Hello, World!\n");
        return 0;
    }

.. code:: bash

    $ riscv64-unknown-elf-gcc -fno-common -fno-builtin-printf -specs=htif_nano.specs -c hello.c
    $ riscv64-unknown-elf-gcc -static -specs=htif_nano.specs hello.o -o hello.riscv
    $ spike hello.riscv
    Hello, World!

더 많은 예제를 보려면, chipyard 리포지토리의 `tests/ 디렉토리 <https://github.com/ucb-bar/chipyard/tree/master/tests>`_ 를 확인하십시오.

libgloss 포트에 대한 자세한 내용은 `README <https://github.com/ucb-bar/libgloss-htif/blob/master/README.md>`_ 를 참조하십시오.

