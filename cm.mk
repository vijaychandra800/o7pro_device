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

PRODUCT_BUILD_PROP_OVERRIDES += BUILD_FINGERPRINT=samsung/fortunave3gxx/fortunave3g:5.0.2/LRX22G/G530HXCU1BPB1:user/release-keys PRIVATE_BUILD_DESC="fortunave3gxx-user 5.0.2 LRX22G G530HXCU1BPB1 release-keys"