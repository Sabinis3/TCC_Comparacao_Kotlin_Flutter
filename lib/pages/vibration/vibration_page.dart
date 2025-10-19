import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:vibration/vibration.dart';

class VibrationPage extends StatefulWidget {
  const VibrationPage({super.key});

  @override
  State<VibrationPage> createState() => _VibrationPageState();
}

class _VibrationPageState extends State<VibrationPage> {
  bool hasVibrator = false;
  bool hasAmplitude = false;
  bool hasCustom = false;

  @override
  void initState() {
    super.initState();
    _checkVibrationCapabilities();
  }

  Future<void> _checkVibrationCapabilities() async {
    hasVibrator = await Vibration.hasVibrator();
    hasAmplitude = await Vibration.hasAmplitudeControl();
    hasCustom = await Vibration.hasCustomVibrationsSupport();
    setState(() {});
  }

  void _vibrateOneShot([int duration = 80]) {
    if (hasVibrator) {
      Vibration.vibrate(duration: duration);
    }
  }

  void _vibrateWaveform() {
    if (hasCustom) {
      Vibration.vibrate(pattern: [0, 400, 600, 800, 1200, 400]);
    }
  }

  void _vibrateWaveformWithAmplitude() {
    if (hasAmplitude) {
      Vibration.vibrate(
        pattern: [0, 300, 400, 500, 600],
        intensities: [0, 80, 0, 200, 0],
      );
    }
  }

  Widget _actionButton({
    required VoidCallback onPressed,
    required String text,
    required IconData icon,
    bool enabled = true,
  }) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: SizedBox(
        width: double.infinity,
        child: FilledButton.icon(
          onPressed: enabled ? onPressed : null,
          icon: Icon(icon),
          label: Text(text),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Vibração")),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Text(
              "Feedback Tátil - VibrationEffect",
              style: Theme.of(context).textTheme.titleLarge,
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 24),
            _actionButton(
              onPressed: () => _vibrateOneShot(80),
              text: "Pulso único",
              icon: Icons.vibration,
              enabled: hasVibrator,
            ),
            _actionButton(
              onPressed: _vibrateWaveform,
              text: "Forma de onda",
              icon: Icons.vibration,
              enabled: hasCustom,
            ),
            _actionButton(
              onPressed: _vibrateWaveformWithAmplitude,
              text: "Forma de onda com amplitude",
              icon: Icons.vibration,
              enabled: hasAmplitude,
            ),
            const SizedBox(height: 32),
            Text(
              "Feedback Tátil - HapticFeedbackType",
              style: Theme.of(context).textTheme.titleLarge,
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 24),
            _actionButton(
              onPressed: () => HapticFeedback.lightImpact(),
              text: "Vibração suave com Haptic",
              icon: Icons.vibration,
            ),
            _actionButton(
              onPressed: () => HapticFeedback.heavyImpact(),
              text: "Vibração longa com Haptic",
              icon: Icons.vibration,
            ),
            _actionButton(
              onPressed: () => HapticFeedback.selectionClick(),
              text: "Vibração para teclado virtual com Haptic",
              icon: Icons.vibration,
            ),
          ],
        ),
      ),
    );
  }
}
