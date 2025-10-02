import 'dart:io';
import 'package:flutter/material.dart';

class MediaBottomSheet extends StatelessWidget {
  final List<File> mediaFiles;

  const MediaBottomSheet({super.key, required this.mediaFiles});

  @override
  Widget build(BuildContext context) {
    return ClipRRect(
      borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
      child: Material(
        borderRadius: const BorderRadius.vertical(top: Radius.circular(16)),
        color: Theme.of(context).canvasColor,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(vertical: 12.0),
              child: Center(
                child: Container(
                  width: 40,
                  height: 5,
                  decoration: BoxDecoration(
                    color: Colors.grey[400],
                    borderRadius: BorderRadius.circular(2.5),
                  ),
                ),
              ),
            ),
            if (mediaFiles.isEmpty)
              SizedBox(
                width: double.infinity,
                height: 160,
                child: Padding(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 16.0,
                    vertical: 24.0,
                  ),
                  child: Center(
                    child: Text(
                      'Nenhum arquivo capturado ainda.',
                      style: TextStyle(fontSize: 16),
                    ),
                  ),
                ),
              )
            else
              SizedBox(
                height: 400,
                child: ListView.builder(
                  itemCount: mediaFiles.length,
                  itemBuilder: (context, idx) {
                    final file = mediaFiles[idx];
                    final isVideo = file.path.endsWith('.mp4');
                    return Material(
                      color: Colors.transparent,
                      child: ListTile(
                        leading: isVideo
                            ? const Icon(Icons.videocam_rounded)
                            : ClipRRect(
                                borderRadius: BorderRadius.circular(8),
                                child: Image.file(
                                  file,
                                  width: 48,
                                  height: 48,
                                  fit: BoxFit.cover,
                                ),
                              ),
                        title: Text(file.path.split('/').last),
                        onTap: () {},
                      ),
                    );
                  },
                ),
              ),
          ],
        ),
      ),
    );
  }
}
