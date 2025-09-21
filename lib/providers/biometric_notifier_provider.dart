import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:local_auth/local_auth.dart';
import '../utils/biometric_util.dart';

class BiometricState {
  final bool supported;
  final bool enabled;
  final bool isAuthenticating;

  const BiometricState({
    required this.supported,
    required this.enabled,
    this.isAuthenticating = false,
  });

  BiometricState copyWith({
    bool? supported,
    bool? enabled,
    List<BiometricType>? available,
    bool? isAuthenticating,
  }) {
    return BiometricState(
      supported: supported ?? this.supported,
      enabled: enabled ?? this.enabled,
      isAuthenticating: isAuthenticating ?? this.isAuthenticating,
    );
  }
}

class BiometricNotifier extends AsyncNotifier<BiometricState> {
  @override
  Future<BiometricState> build() async {
    await BiometricUtil.initialize();
    return BiometricState(
      supported: BiometricUtil.deviceHasBiometricCapability,
      enabled: BiometricUtil.deviceHasBiometricsEnabled,
    );
  }

  Future<void> refresh() async {
    state = const AsyncLoading();
    state = await AsyncValue.guard(() async => await build());
  }

  Future<bool> authenticate({String? reason}) async {
    state = AsyncData(state.value!.copyWith(isAuthenticating: true));
    final result = await BiometricUtil.didAuthenticate(
      authenticationReason: reason,
    );
    state = AsyncData(state.value!.copyWith(isAuthenticating: false));
    return result;
  }
}

final biometricProvider =
    AsyncNotifierProvider<BiometricNotifier, BiometricState>(
      BiometricNotifier.new,
    );
