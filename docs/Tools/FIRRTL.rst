FIRRTL
================================

`FIRRTL <https://github.com/freechipsproject/firrtl>`__ 은 회로의 중간 표현(intermediate representation)입니다.
이 표현은 Chisel 컴파일러에 의해 생성되며, Chisel 소스 파일을 Verilog와 같은 다른 표현으로 변환하는 데 사용됩니다.
자세한 설명은 생략하지만, FIRRTL은 FIRRTL 컴파일러에 의해 처리되어 회로를 일련의 회로 수준 변환을 거치게 됩니다.
FIRRTL 패스(변환)의 예로는 사용되지 않는 신호를 최적화하여 제거하는 변환이 있습니다.
변환이 완료되면 Verilog 파일이 생성되며 빌드 프로세스가 완료됩니다.

FIRRTL에 대한 자세한 정보는 `웹사이트 <https://chisel-lang.org/firrtl/>`__ 를 방문하십시오.

