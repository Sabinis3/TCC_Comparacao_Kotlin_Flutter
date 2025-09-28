import 'package:app_flutter_tcc/widgets/info_card.dart';
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
    return Scaffold(
      appBar: AppBar(title: const Text("Vibração")),
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
