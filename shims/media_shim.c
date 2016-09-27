namespace android {

// ---------------------------------------------------------------------------

extern "C" {

    bool _ZNK7android12IMediaSource11ReadOptions9getSeekToEPxPNS1_8SeekModeE(int64_t *time_us, int64_t *mode);

    bool _ZNK7android11MediaSource11ReadOptions9getSeekToEPxPNS1_8SeekModeE(int64_t *time_us, int64_t *mode) {
		return _ZNK7android12IMediaSource11ReadOptions9getSeekToEPxPNS1_8SeekModeE(int64_t *time_us, int64_t *mode);
	}
}

// ---------------------------------------------------------------------------

}; // namespace android
