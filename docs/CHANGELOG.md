# Changelog

All notable changes to this project will be documented in this file.

## [0.1.1] - 2026-07-23

### Fixed
- **Windows Junction Point Abort Bug**: Replaced throwing `std::filesystem::recursive_directory_iterator` with non-throwing `std::error_code` loop to prevent scan termination on Windows junction points (`My Music`, `My Pictures`, system folders).
- **Full Directory Tree Coverage**: Verified scanning of 32,460+ files across large user directory hierarchies.

### Added
- **Performance Metrics Calculation**: Accurate nano-second scan timings, item count tracking, and items/sec throughput calculation.
- **Case-Sensitive & Openability Relevance Scoring**: Exact case matches score 100%, openable source files (`.java`, `.md`, `.txt`) float above binary build outputs (`.class`, `.jar`).
- **Path Length Tie-Breaker**: Shallower folder depths rank higher when match scores are equal.

## [0.1.0] - 2026-05-23

### Added
- Initial release
- Native JNI implementation for FastFileIndex
- Binary file indexing with memory-mapped I/O support
- FNV-1a hashing for stable file IDs
- Type detection for common file types
- Full filesystem scanning with parallel support
- Maven build configuration
- GitHub Actions CI/CD
