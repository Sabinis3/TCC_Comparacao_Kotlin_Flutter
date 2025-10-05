import 'package:flutter/material.dart';
import 'package:wifi_scan/wifi_scan.dart';
import 'package:network_info_plus/network_info_plus.dart';

class WifiPage extends StatefulWidget {
  const WifiPage({super.key});

  @override
  State<WifiPage> createState() => _WifiPageState();
}

class _WifiPageState extends State<WifiPage> {
  bool isLoadingCurrentWifi = true;
  String? _connectedSSID;
  List<WiFiAccessPoint> _wifiList = [];

  @override
  void initState() {
    super.initState();
    _getConnectedWifi();
    _scanWifi();
  }

  Future<void> _getConnectedWifi() async {
    final info = NetworkInfo();
    final ssid = await info.getWifiName();
    setState(() {
      _connectedSSID = ssid;
      isLoadingCurrentWifi = false;
    });
  }

  Future<void> _scanWifi() async {
    final can = await WiFiScan.instance.canStartScan();
    if (can == CanStartScan.yes) {
      await WiFiScan.instance.startScan();
      final aps = await WiFiScan.instance.getScannedResults();
      setState(() {
        _wifiList = aps;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    var theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(title: const Text('Wi-Fi')),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          spacing: 8.0,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SizedBox(
              width: double.infinity,
              child: Card(
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: isLoadingCurrentWifi
                      ? const Center(child: CircularProgressIndicator())
                      : Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            const Text(
                              'Rede conectada atualmente',
                              style: TextStyle(
                                fontWeight: FontWeight.bold,
                                fontSize: 16,
                              ),
                            ),
                            const SizedBox(height: 8),
                            Text('SSID: ${_connectedSSID ?? "-"}'),
                            Builder(
                              builder: (context) {
                                final connectedAp = _wifiList
                                    .where((ap) => ap.ssid == _connectedSSID)
                                    .toList();
                                if (connectedAp.isNotEmpty) {
                                  return Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text('BSSID: ${connectedAp.first.bssid}'),
                                      Text(
                                        'Sinal: ${connectedAp.first.level} dBm',
                                      ),
                                    ],
                                  );
                                } else {
                                  return Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      const Text('BSSID: -'),
                                      const Text('Sinal: - dBm'),
                                    ],
                                  );
                                }
                              },
                            ),
                          ],
                        ),
                ),
              ),
            ),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  'Redes disponíveis',
                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                ),
                IconButton(
                  icon: const Icon(Icons.refresh),
                  onPressed: _scanWifi,
                ),
              ],
            ),
            Expanded(
              child: ListView.builder(
                itemCount: _wifiList.length,
                itemBuilder: (context, index) {
                  final ap = _wifiList[index];
                  final isConnected = ap.ssid == _connectedSSID;
                  return Container(
                    margin: const EdgeInsets.symmetric(horizontal: 16),
                    decoration: BoxDecoration(
                      color: isConnected
                          ? theme.colorScheme.primary.withAlpha(30)
                          : Colors.transparent,
                      borderRadius: BorderRadius.circular(8),
                      border: isConnected
                          ? Border.all(
                              color: theme.colorScheme.primary,
                              width: 1.5,
                            )
                          : null,
                    ),
                    child: ListTile(
                      title: Text(
                        ap.ssid,
                        style: TextStyle(
                          fontWeight: isConnected
                              ? FontWeight.bold
                              : FontWeight.normal,
                        ),
                      ),
                      subtitle: Text(
                        'Sinal: ${ap.level} dBm • ${ap.frequency} MHz',
                      ),
                      trailing: isConnected
                          ? Text(
                              '✓ Conectada',
                              style: TextStyle(
                                color: theme.colorScheme.primary,
                                fontWeight: FontWeight.bold,
                              ),
                            )
                          : null,
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
