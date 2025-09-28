import 'package:flutter/material.dart';

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
