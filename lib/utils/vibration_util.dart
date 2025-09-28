import 'dart:developer' show log;
import 'package:vibration/vibration.dart';
import 'package:vibration/vibration_presets.dart';

enum _VibrationSupportState { unknown, supported, unsupported }

class VibrationUtil {
  factory VibrationUtil() => _instance;
  VibrationUtil._privateConstructor();
  static final VibrationUtil _instance = VibrationUtil._privateConstructor();

  static _VibrationSupportState _supportState = _VibrationSupportState.unknown;
  static bool _hasAmplitudeControl = false;
  static bool _hasCustomVibrationsSupport = false;

  static bool get deviceHasVibrator =>
      _supportState == _VibrationSupportState.supported;
  static bool get deviceHasAmplitudeControl =>
      _supportState == _VibrationSupportState.supported && _hasAmplitudeControl;
  static bool get deviceHasCustomVibrationsSupport =>
      _supportState == _VibrationSupportState.supported &&
      _hasCustomVibrationsSupport;

  static Future<void> initialize() async {
    var hasVibrator = await Vibration.hasVibrator();
    _hasAmplitudeControl = await Vibration.hasAmplitudeControl();
    _hasCustomVibrationsSupport = await Vibration.hasCustomVibrationsSupport();
    _supportState = hasVibrator
        ? _VibrationSupportState.supported
        : _VibrationSupportState.unsupported;
  }

  static Future<void> vibrate({
    int duration = 500,
    List<int> pattern = const [],
    int repeat = -1,
    List<int> intensities = const [],
    int amplitude = -1,
    double sharpness = 0.5,
    VibrationPreset? preset,
  }) async {
    if (!deviceHasVibrator) {
      log('Device does not support vibration');
      return;
    }
    try {
      await Vibration.vibrate(
        duration: duration,
        pattern: pattern,
        repeat: repeat,
        intensities: intensities,
        amplitude: amplitude,
        sharpness: sharpness,
        preset: preset,
      );
    } catch (e) {
      log('Vibration error: $e');
    }
  }

  static Future<void> cancel() async {
    try {
      await Vibration.cancel();
    } catch (e) {
      log('Cancel vibration error: $e');
    }
  }
}
