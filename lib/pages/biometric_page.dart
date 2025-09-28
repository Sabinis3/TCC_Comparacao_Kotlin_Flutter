import 'package:app_flutter_tcc/utils/biometric_util.dart';
import 'package:app_flutter_tcc/widgets/info_card.dart';
import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/biometric_notifier_provider.dart';

class BiometricPage extends ConsumerStatefulWidget {
  const BiometricPage({super.key});

  @override
  ConsumerState<BiometricPage> createState() => _BiometricPageState();
}

class _BiometricPageState extends ConsumerState<BiometricPage>
    with WidgetsBindingObserver {
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    Future.microtask(() => ref.read(biometricProvider.notifier).refresh());
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      ref.read(biometricProvider.notifier).refresh();
    }
  }

  void _authenticate() async {
    await ref
        .read(biometricProvider.notifier)
        .authenticate(reason: "Por favor, autentique-se para continuar")
        .then((success) {
          if (!mounted) return;

          showDialog(
            context: context,
            builder: (BuildContext context) {
              return AlertDialog(
                title: Text(success ? "Sucesso" : "Falha"),
                content: Text(
                  success
                      ? "Autenticação biométrica bem-sucedida."
                      : "Falha na autenticação biométrica.",
                ),
                actions: [
                  TextButton(
                    onPressed: () {
                      Navigator.of(context).pop();
                    },
                    child: const Text("OK"),
                  ),
                ],
              );
            },
          );
        });
  }

  @override
  Widget build(BuildContext context) {
    final biometricAsync = ref.watch(biometricProvider);
    var theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text("Biometria"),
        backgroundColor: theme.colorScheme.inversePrimary,
      ),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Center(
          child: biometricAsync.when(
            loading: () => const CircularProgressIndicator(),
            error: (e, _) => Text('Erro: $e'),
            data: (state) => Column(
              spacing: 8.0,
              mainAxisSize: MainAxisSize.min,
              children: [
                if (state.supported)
                  const InfoCard(
                    icon: Icons.fingerprint_rounded,
                    colorScheme: 'info',
                    text: "Este dispositivo suporta autenticação biométrica.",
                  )
                else
                  const InfoCard(
                    icon: Icons.error_rounded,
                    colorScheme: 'error',
                    text:
                        "Este dispositivo não suporta autenticação biométrica ou não está habilitada nas configurações.",
                  ),
                if (state.supported && !state.enabled) ...[
                  const InfoCard(
                    icon: Icons.warning_rounded,
                    colorScheme: 'warning',
                    text:
                        "Não há biometria configurada. Por favor, configure a biometria nas configurações do dispositivo.",
                  ),
                  SizedBox(
                    width: double.infinity,
                    child: FilledButton.icon(
                      onPressed: () {
                        BiometricUtil.openSettingsForEnrollment();
                      },
                      style: FilledButton.styleFrom(
                        padding: const EdgeInsets.all(12.0),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(10.0),
                        ),
                      ),
                      icon: const Icon(Icons.settings_rounded),
                      label: Text(
                        "Abrir configurações",
                        style: const TextStyle(fontSize: 16.0),
                      ),
                    ),
                  ),
                ],
                SizedBox(
                  width: double.infinity,
                  child: FilledButton.icon(
                    onPressed:
                        state.supported &&
                            state.enabled &&
                            !state.isAuthenticating
                        ? _authenticate
                        : null,
                    style: FilledButton.styleFrom(
                      padding: const EdgeInsets.all(12.0),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(10.0),
                      ),
                    ),
                    icon: const Icon(Icons.fingerprint_rounded),
                    label: Text(
                      state.isAuthenticating
                          ? "Autenticando..."
                          : "Autenticar com biometria",
                      style: const TextStyle(fontSize: 16.0),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
