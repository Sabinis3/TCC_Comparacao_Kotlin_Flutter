import 'package:flutter/material.dart';

class HomeFilledIconButton extends StatelessWidget {
  final Widget icon;
  final VoidCallback onPressed;
  final Widget label;

  const HomeFilledIconButton({
    super.key,
    required this.icon,
    required this.onPressed,
    required this.label,
  });

  @override
  Widget build(BuildContext context) {
    return FilledButton.icon(
      icon: icon,
      onPressed: onPressed,
      style: FilledButton.styleFrom(
        padding: EdgeInsets.all(12.0),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10.0),
        ),
      ),
      label: label,
    );
  }
}
