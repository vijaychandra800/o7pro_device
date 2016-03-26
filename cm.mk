# Release name
PRODUCT_RELEASE_NAME := SM-G530H

# Inherit some common CM stuff.
$(call inherit-product, vendor/cm/config/common_full_phone.mk)

# Inherit device configuration
$(call inherit-product, device/samsung/fortunave3g/device_fortunave3g.mk)

## Device identifier. This must come after all inclusions
PRODUCT_DEVICE := fortunave3g
PRODUCT_NAME := cm_fortunave3g
PRODUCT_BRAND := samsung
PRODUCT_MANUFACTURER := samsung
PRODUCT_MODEL := SM-G530H
PRODUCT_CHARACTERISTICS := phone
