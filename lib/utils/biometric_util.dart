import 'dart:developer' show log;
import 'dart:io' show Platform;
import 'package:app_settings/app_settings.dart';
import 'package:flutter/services.dart';
import 'package:local_auth/local_auth.dart';

enum _BiometricSupportState { unknown, supported, unsupported }

class BiometricUtil {
  factory BiometricUtil() => _instance;
  BiometricUtil._privateConstructor();
  static final BiometricUtil _instance = BiometricUtil._privateConstructor();

  static final LocalAuthentication _auth = LocalAuthentication();
  static List<BiometricType> _availableBiometrics = <BiometricType>[];
  static bool _canCheckBiometrics = false;
  static _BiometricSupportState _supportState =
      _BiometricSupportState.unsupported;

  static bool get deviceHasBiometricCapability =>
      _supportState == _BiometricSupportState.supported && _canCheckBiometrics;

  static bool get deviceHasBiometricsEnabled =>
      _availableBiometrics.contains(BiometricType.face) ||
      _availableBiometrics.contains(BiometricType.fingerprint) ||
      _availableBiometrics.contains(BiometricType.strong);

  static bool get deviceHasBiometricsCapabilityAndHasBiometricsEnabled =>
      deviceHasBiometricCapability && deviceHasBiometricsEnabled;

  static bool get deviceHasBiometricsCapabilityAndHasBiometricsDisabled =>
      deviceHasBiometricCapability && !deviceHasBiometricsEnabled;

  static bool get supportsFaceId =>
      Platform.isIOS && _availableBiometrics.contains(BiometricType.face);

  static Future<void> initialize() async {
    _supportState = await _isDeviceSupported();
    _canCheckBiometrics = await _canAuthenticateWithBiometrics();
    _availableBiometrics = await _getAvailableBiometrics();
  }

  static Future<_BiometricSupportState> _isDeviceSupported() async {
    try {
      final isSupported = await _auth.isDeviceSupported();
      return isSupported
          ? _BiometricSupportState.supported
          : _BiometricSupportState.unsupported;
    } on PlatformException catch (e) {
      log('Error checking device support: ${e.message}');
      return _BiometricSupportState.unknown;
    }
  }

  static Future<bool> _canAuthenticateWithBiometrics() async {
    try {
      if (_supportState == _BiometricSupportState.supported) {
        return await _auth.canCheckBiometrics;
      }
      return false;
    } on PlatformException catch (e) {
      log('Error checking biometrics: ${e.message}');
      return false;
    }
  }

  static Future<List<BiometricType>> _getAvailableBiometrics() async {
    try {
      return await _auth.getAvailableBiometrics();
    } on PlatformException catch (e) {
      log('Error getting available biometrics: ${e.message}');
      return <BiometricType>[];
    }
  }

  static Future<bool> didAuthenticate({String? authenticationReason}) async {
    if (!deviceHasBiometricCapability) {
      log('Device cannot authenticate with biometric');
      return false;
    }
    try {
      final didAuthenticate = await _auth.authenticate(
        localizedReason:
            authenticationReason ?? 'Please authenticate to proceed',
        options: const AuthenticationOptions(biometricOnly: true),
      );
      return didAuthenticate;
    } catch (e) {
      log('Authentication error: $e');
      return false;
    }
  }

  static Future<void> openSettingsForEnrollment() async =>
      await AppSettings.openAppSettings(type: AppSettingsType.security);
}
