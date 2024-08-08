# See LICENSE for license details.

##########################
# Driver Sources & Flags #
##########################

driver_dir = $(firesim_base_dir)/src/main/cc

# TODO: figure out how to hook in
#firesim_lib_dir = $(firesim_base_dir)/firesim-lib/src/main/cc/
#testchipip_csrc_dir = $(firesim_base_dir)/target-rtl/testchipip/src/main/resources/testchipip/csrc

DRIVER_H = $(shell find $(driver_dir) -name "*.h")

DRIVER_CC := \
		$(driver_dir)/bridges/BridgeHarness.cc \
		$(driver_dir)/bridges/$(DESIGN).cc

# TODO: figure out how to hook in
#		$(testchipip_csrc_dir)/testchip_tsi.cc \
#		$(testchipip_csrc_dir)/testchip_dtm.cc \
#		$(testchipip_csrc_dir)/testchip_htif.cc \
#		$(wildcard $(addprefix $(firesim_lib_dir)/, \
#			bridges/uart.cc \
#			bridges/tsibridge.cc \
#			bridges/dmibridge.cc \
#			bridges/blockdev.cc \
#			bridges/tracerv.cc \
#			fesvr/firesim_tsi.cc \
#			fesvr/firesim_dtm.cc \
#			$(addsuffix .cc, fesvr/* bridges/tracerv/*) \
#		))

TARGET_CXX_FLAGS := \
		-I$(RISCV)/include \
		-I$(driver_dir)/midasexamples \
		-I$(driver_dir) \
		-I$(driver_dir)/bridges \
		-g

# TODO: figure out how to hook in
		#-isystem $(testchipip_csrc_dir) \
		#-I$(firesim_lib_dir) \

TARGET_LD_FLAGS := \
		-L$(RISCV)/lib \
		-lfesvr \
		-l:libdwarf.so -l:libelf.so
