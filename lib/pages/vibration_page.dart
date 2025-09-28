import 'package:flutter/material.dart';
import 'package:app_flutter_tcc/utils/vibration_util.dart';

class VibrationPage extends StatefulWidget {
  const VibrationPage({super.key});

  @override
  State<VibrationPage> createState() => _VibrationPageState();
}

class _VibrationPageState extends State<VibrationPage> {
  void _testVibration() async {
    await VibrationUtil.vibrate();
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        title: const Text("Vibração"),
        backgroundColor: theme.colorScheme.inversePrimary,
      ),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              if (VibrationUtil.deviceHasVibrator)
                const InfoCard(
                  icon: Icons.vibration_rounded,
                  colorScheme: 'info',
                  text: "Este dispositivo suporta vibração.",
                )
              else
                const InfoCard(
                  icon: Icons.error_rounded,
                  colorScheme: 'error',
                  text: "Este dispositivo não suporta vibração.",
                ),
              if (VibrationUtil.deviceHasAmplitudeControl)
                const InfoCard(
                  icon: Icons.graphic_eq_rounded,
                  colorScheme: 'success',
                  text: "Suporte a controle de amplitude disponível.",
                ),
              if (VibrationUtil.deviceHasCustomVibrationsSupport)
                const InfoCard(
                  icon: Icons.tune_rounded,
                  colorScheme: 'success',
                  text: "Suporte a vibrações customizadas disponível.",
                ),
              const SizedBox(height: 16),
              SizedBox(
                width: double.infinity,
                child: FilledButton.icon(
                  onPressed: VibrationUtil.deviceHasVibrator
                      ? _testVibration
                      : null,
                  style: FilledButton.styleFrom(
                    padding: const EdgeInsets.all(12.0),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(10.0),
                    ),
                  ),
                  icon: const Icon(Icons.vibration_rounded),
                  label: const Text(
                    "Testar Vibração",
                    style: TextStyle(fontSize: 16.0),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class InfoCard extends StatelessWidget {
  final IconData icon;
  final String colorScheme;
  final String text;

  const InfoCard({
    super.key,
    required this.icon,
    required this.colorScheme,
    required this.text,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    Color cardColor;
    Color iconColor;
    Color textColor;

    if (colorScheme == 'error') {
      cardColor = theme.colorScheme.errorContainer;
      iconColor = theme.colorScheme.onErrorContainer;
      textColor = theme.colorScheme.onErrorContainer;
    } else if (colorScheme == 'warning') {
      cardColor = const Color(0xFFFFF8E1);
      iconColor = const Color(0xFFFFA000);
      textColor = const Color(0xFFFFA000);
    } else if (colorScheme == 'info') {
      cardColor = const Color(0xFFE3F2FD);
      iconColor = const Color(0xFF1976D2);
      textColor = const Color(0xFF1976D2);
    } else if (colorScheme == 'success') {
      cardColor = const Color(0xFFE8F5E9);
      iconColor = const Color(0xFF388E3C);
      textColor = const Color(0xFF388E3C);
    } else {
      cardColor = theme.colorScheme.surfaceContainerHigh;
      iconColor = theme.colorScheme.onSurfaceVariant;
      textColor = theme.colorScheme.onSurfaceVariant;
    }

    return SizedBox(
      width: double.infinity,
      child: Card(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10.0),
        ),
        color: cardColor,
        child: Padding(
          padding: const EdgeInsets.all(12.0),
          child: Row(
            children: [
              Icon(icon, color: iconColor),
              const SizedBox(width: 8),
              Flexible(
                child: Text(
                  text,
                  style: theme.textTheme.bodyLarge?.copyWith(color: textColor),
                  softWrap: true,
                  overflow: TextOverflow.visible,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
