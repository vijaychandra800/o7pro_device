# Release name
PRODUCT_RELEASE_NAME := SM-G600FY

# Inherit some common CM stuff.
$(call inherit-product, vendor/cm/config/common_full_phone.mk)

# Inherit device configuration
$(call inherit-product, device/samsung/o7prolte/device_o7prolte.mk)

## Device identifier. This must come after all inclusions
PRODUCT_BUILD_PROP_OVERRIDES += BUILD_UTC_DATE=0

PRODUCT_DEVICE := o7prolte
PRODUCT_NAME := cm_o7prolte
PRODUCT_BRAND := samsung
PRODUCT_MANUFACTURER := samsung
PRODUCT_MODEL := SM-G600FY
