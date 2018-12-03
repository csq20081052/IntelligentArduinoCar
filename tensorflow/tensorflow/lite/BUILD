package(default_visibility = [
    "//visibility:public",
])

licenses(["notice"])  # Apache 2.0

load("//tensorflow:tensorflow.bzl", "tf_cc_test")
load("//tensorflow/lite:build_def.bzl", "tflite_copts", "gen_selected_ops")

exports_files(glob([
    "testdata/*.bin",
    "testdata/*.pb",
    "models/testdata/*",
]))

config_setting(
    name = "mips",
    values = {
        "cpu": "mips",
    },
)

config_setting(
    name = "mips64",
    values = {
        "cpu": "mips64",
    },
)

# Enables inclusion of select TensorFlow kernels via the TF Lite Flex delegate.
# WARNING: This build flag is experimental and subject to change.
config_setting(
    name = "with_select_tf_ops",
    define_values = {"with_select_tf_ops": "true"},
    visibility = ["//visibility:public"],
)

cc_library(
    name = "schema_fbs_version",
    hdrs = ["version.h"],
)

cc_library(
    name = "arena_planner",
    srcs = ["arena_planner.cc"],
    hdrs = ["arena_planner.h"],
    deps = [
        ":graph_info",
        ":memory_planner",
        ":simple_memory_arena",
        "//tensorflow/lite/c:c_api_internal",
    ],
)

cc_test(
    name = "arena_planner_test",
    size = "small",
    srcs = ["arena_planner_test.cc"],
    tags = [
        "no_oss",
        "tflite_not_portable",
    ],
    deps = [
        ":arena_planner",
        "//tensorflow/core:framework",
        "//tensorflow/core:lib",
        "//tensorflow/lite/testing:util",
        "@com_google_googletest//:gtest",
    ],
)

# Main library. No ops are included here.
# TODO(aselle): Resolve problems preventing C99 usage.
cc_library(
    name = "context",
    hdrs = ["context.h"],
    deps = ["//tensorflow/lite/c:c_api_internal"],
)

cc_library(
    name = "graph_info",
    hdrs = ["graph_info.h"],
    deps = ["//tensorflow/lite/c:c_api_internal"],
)

cc_library(
    name = "memory_planner",
    hdrs = ["memory_planner.h"],
    deps = ["//tensorflow/lite/c:c_api_internal"],
)

cc_library(
    name = "simple_memory_arena",
    srcs = ["simple_memory_arena.cc"],
    hdrs = ["simple_memory_arena.h"],
    deps = ["//tensorflow/lite/c:c_api_internal"],
)

cc_library(
    name = "builtin_op_data",
    hdrs = [
        "builtin_op_data.h",
    ],
    deps = ["//tensorflow/lite/c:c_api_internal"],
)

cc_library(
    name = "kernel_api",
    hdrs = [
        "builtin_op_data.h",
        "builtin_ops.h",
        "context.h",
        "context_util.h",
    ],
)

exports_files(["builtin_ops.h"])

cc_library(
    name = "string",
    hdrs = [
        "string.h",
    ],
    deps = [
        "//tensorflow/core:lib_platform",
    ],
)

# TODO(ahentz): investigate dependency on gemm_support requiring usage of tf_copts.
cc_library(
    name = "framework",
    srcs = [
        "allocation.cc",
        "core/subgraph.cc",
        "graph_info.cc",
        "interpreter.cc",
        "model.cc",
        "mutable_op_resolver.cc",
        "optional_debug_tools.cc",
        "stderr_reporter.cc",
    ] + select({
        "//tensorflow:android": [
            "nnapi_delegate.cc",
            "mmap_allocation.cc",
        ],
        "//tensorflow:windows": [
            "nnapi_delegate_disabled.cc",
            "mmap_allocation_disabled.cc",
        ],
        "//conditions:default": [
            "nnapi_delegate_disabled.cc",
            "mmap_allocation.cc",
        ],
    }),
    hdrs = [
        "allocation.h",
        "context.h",
        "context_util.h",
        "core/subgraph.h",
        "error_reporter.h",
        "graph_info.h",
        "interpreter.h",
        "model.h",
        "mutable_op_resolver.h",
        "nnapi_delegate.h",
        "op_resolver.h",
        "optional_debug_tools.h",
        "stderr_reporter.h",
    ],
    copts = tflite_copts(),
    linkopts = [
    ] + select({
        "//tensorflow:android": [
            "-llog",
        ],
        "//conditions:default": [
        ],
    }),
    deps = [
        ":arena_planner",
        ":graph_info",
        ":memory_planner",
        ":schema_fbs_version",
        ":simple_memory_arena",
        ":string",
        ":util",
        "//tensorflow/lite/c:c_api_internal",
        "//tensorflow/lite/core/api",
        "//tensorflow/lite/kernels:eigen_support",
        "//tensorflow/lite/kernels:gemm_support",
        "//tensorflow/lite/nnapi:nnapi_lib",
        "//tensorflow/lite/profiling:profiler",
        "//tensorflow/lite/schema:schema_fbs",
    ] + select({
        ":with_select_tf_ops": [
            "//tensorflow/lite/delegates/flex:delegate",
        ],
        "//conditions:default": [],
    }),
)

cc_library(
    name = "string_util",
    srcs = ["string_util.cc"],
    hdrs = ["string_util.h"],
    copts = tflite_copts(),
    deps = [
        ":framework",
        ":string",
    ],
)

cc_test(
    name = "string_util_test",
    size = "small",
    srcs = ["string_util_test.cc"],
    deps = [
        ":framework",
        ":string_util",
        "//tensorflow/lite/testing:util",
        "@com_google_googletest//:gtest",
    ],
)

# Test main interpreter
cc_test(
    name = "interpreter_test",
    size = "small",
    srcs = ["interpreter_test.cc"],
    deps = [
        ":framework",
        ":string_util",
        "//tensorflow/lite/c:c_api_internal",
        "//tensorflow/lite/core/api",
        "//tensorflow/lite/kernels:builtin_ops",
        "//tensorflow/lite/kernels:kernel_util",
        "//tensorflow/lite/kernels/internal:tensor_utils",
        "//tensorflow/lite/schema:schema_fbs",
        "//tensorflow/lite/testing:util",
        "@com_google_googletest//:gtest",
    ],
)

# Test graph utils
cc_test(
    name = "graph_info_test",
    size = "small",
    srcs = ["graph_info_test.cc"],
    tags = ["no_oss"],
    deps = [
        ":framework",
        ":string_util",
        "//tensorflow/lite/testing:util",
        "@com_google_googletest//:gtest",
    ],
)

# Test arena allocator
cc_test(
    name = "simple_memory_arena_test",
    size = "small",
    srcs = ["simple_memory_arena_test.cc"],
    deps = [
        ":simple_memory_arena",
        "//tensorflow/lite/testing:util",
        "@com_google_googletest//:gtest",
    ],
)

# Test model framework.
cc_test(
    name = "model_test",
    size = "small",
    srcs = ["model_test.cc"],
    data = [
        "testdata/0_subgraphs.bin",
        "testdata/2_subgraphs.bin",
        "testdata/empty_model.bin",
        "testdata/multi_add_flex.bin",
        "testdata/test_model.bin",
        "testdata/test_model_broken.bin",
    ],
    deps = [
        ":framework",
        "//tensorflow/lite/c:c_api_internal",
        "//tensorflow/lite/core/api",
        "//tensorflow/lite/kernels:builtin_ops",
        "//tensorflow/lite/testing:util",
        "@com_google_googletest//:gtest",
    ],
)

# Test model framework with the flex library linked into the target.
tf_cc_test(
    name = "model_flex_test",
    size = "small",
    srcs = ["model_flex_test.cc"],
    data = [
        "testdata/multi_add_flex.bin",
    ],
    tags = ["no_windows"],  # TODO(b/116667551): No weak symbols with MSVC.
    deps = [
        ":framework",
        "//tensorflow/lite/core/api",
        "//tensorflow/lite/delegates/flex:delegate",
        "//tensorflow/lite/kernels:builtin_ops",
        "//tensorflow/lite/testing:util",
        "@com_google_googletest//:gtest",
    ],
)

# Test OpResolver.
cc_test(
    name = "mutable_op_resolver_test",
    size = "small",
    srcs = ["mutable_op_resolver_test.cc"],
    tags = ["no_oss"],
    deps = [
        ":framework",
        "//tensorflow/lite/testing:util",
        "@com_google_googletest//:gtest",
    ],
)

cc_library(
    name = "util",
    srcs = ["util.cc"],
    hdrs = ["util.h"],
    copts = tflite_copts(),
    deps = [
        "//tensorflow/lite/c:c_api_internal",
    ],
)

cc_test(
    name = "util_test",
    size = "small",
    srcs = ["util_test.cc"],
    tags = ["no_oss"],
    deps = [
        ":util",
        "//tensorflow/lite/testing:util",
        "@com_google_googletest//:gtest",
    ],
)

# Test the serialization of a model with optional tensors.

# Model tests

#cc_library(
#    name = "models_test_utils",
#    testonly = 1,
#    hdrs = ["models/test_utils.h"],
#    deps = select({
#        "//tensorflow:android": [],
#        "//conditions:default": [
#            "@com_google_absl//absl/strings",
#            "//tensorflow/core:test",
#        ],
#    }),
#)
