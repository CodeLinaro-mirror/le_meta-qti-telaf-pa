SUMMARY = "Platform Adaptor for Telematics Application Framework"
DESCRIPTION = "Platform Adaptor for Telematics Application Framework"
HOMEPAGE = "https://www.codeaurora.org/"
LICENSE = "BSD-3-Clause-Clear"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/BSD-3-Clause-Clear;md5=7a434440b651f4a472ca93716d01033a"

inherit cmake pkgconfig
DEPENDS += "openssl libxml2 xmllib telux telux-lib"

FILESPATH =+ "${WORKSPACE}:"
SRC_URI = "file://telaf-pa/"
SRC_DIR = "${WORKSPACE}/telaf-pa/"

S = "${WORKDIR}/telaf-pa"

# do not pack the libraries to rootfs
PACKAGES = ""

# Prevent debug symbol files (.build-id/*.debug) from being stripped
# when staged into the sysroot; otherwise, the deployed debug_files
# will lose their symbol tables.
INHIBIT_SYSROOT_STRIP = "1"

SYSROOT_DIRS:append = " /telaf"

INSANE_SKIP:${PN} += "already-stripped"

do_compile:prepend() {
    # Clean up old build directory if it exists
    if [ -d "${S}/build" ]; then
        rm -rf "${S}/build"
    fi
}

do_install() {
    cmake --install "${B}" --prefix "${S}/build/staging"
    set -eu
    install -d "${D}/telaf/target-pa/lib"

    if ls "${S}/build/staging/lib/"*.so* >/dev/null 2>&1; then
        cp -a --no-preserve=ownership "${S}/build/staging/lib/"*.so* "${D}/telaf/target-pa/lib/"
    else
        bbwarn "No shared libraries (*.so*) produced in ${S}/build/staging/lib"
    fi

    if [ -d "${S}/build/staging/.build-id" ]; then
        install -d "${D}/telaf/target-pa/.build-id"
        cp -a --no-preserve=ownership "${S}/build/staging/.build-id/." "${D}/telaf/target-pa/.build-id/"
    else
        bbwarn "No .build-id directory found in ${S}/build/staging"
    fi
}

RM_WORK_EXCLUDE += "telaf-pa-target-build"
