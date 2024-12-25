import 'package:flutter/material.dart';
import 'package:vietmap_map/core/navigators/app_route.dart';

class FuelBrakeScreen extends StatefulWidget {
  const FuelBrakeScreen({super.key});

  @override
  State<StatefulWidget> createState() => _FuelBrakeScreenState();
}

class _FuelBrakeScreenState extends State<FuelBrakeScreen> {
  double fuelLevel = 50.0;
  bool isHandBrakeEngaged = false;
  int selectedTabIndex = 0;

  void updateFuel(double value) {
    setState(() {
      fuelLevel = value;

      if (fuelLevel < 10) {
        showWarningDialog();
      }
    });
  }

  void toggleHandBrake(bool value) {
    setState(() {
      isHandBrakeEngaged = value;

      if (isHandBrakeEngaged) {
        showHandBrakeDialog();
      }
    });
  }

  void showWarningDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Warning!'),
        content: const Text('Fuel level is below 10%! Please refuel.'),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
              showFuelStationDialog(context);
            },
            child: const Text('Got it'),
          ),
        ],
      ),
    );
  }

  void showFuelStationDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Nearest Fuel Station'),
        content: const Text(
            'Would you like to go to the nearest fuel station to refuel?'),
        actions: [
          TextButton(
            onPressed: () async {
              Navigator.of(context).pop();
              Navigator.pushNamed(context, Routes.mapScreen,
                  arguments: {"navigator": true});
            },
            child: const Text('Yes'),
          ),
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
            },
            child: const Text('No'),
          ),
        ],
      ),
    );
  }

  void showHandBrakeDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('Handbrake Warning!'),
        content: const Text(
            'You need to release the handbrake to allow the car to move.'),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
            },
            child: const Text('Got it'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        flexibleSpace: Container(
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              colors: [Colors.blue, Colors.lightBlueAccent],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
          ),
        ),
        title: const Text(
          'Vehicle Status Checker',
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 22),
        ),
        centerTitle: true,
      ),
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            colors: [Colors.white, Colors.lightBlue[50]!],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
          ),
        ),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Icon(
                    Icons.directions_car,
                    size: 28,
                    color: isHandBrakeEngaged ? Colors.red : Colors.green,
                  ),
                  const SizedBox(width: 8),
                  Text(
                    isHandBrakeEngaged
                        ? '🚨 Handbrake engaged!'
                        : '✔️ Handbrake released',
                    style: TextStyle(
                      fontSize: 18,
                      color: isHandBrakeEngaged ? Colors.red : Colors.green,
                    ),
                  ),
                  const Spacer(),
                  Switch(
                    value: isHandBrakeEngaged,
                    onChanged: toggleHandBrake,
                    activeColor: Colors.red,
                    inactiveThumbColor: Colors.green,
                    inactiveTrackColor: Colors.grey,
                  ),
                ],
              ),
              const SizedBox(height: 20),
              Row(
                children: [
                  const Icon(
                    Icons.local_gas_station,
                    size: 28,
                    color: Colors.blue,
                  ),
                  const SizedBox(width: 8),
                  const Text(
                    'Fuel Level',
                    style: TextStyle(fontSize: 18),
                  ),
                  const SizedBox(width: 20),
                  Expanded(
                    child: Slider(
                      value: fuelLevel,
                      min: 0,
                      max: 100,
                      divisions: 100,
                      label: '${fuelLevel.toInt()}%',
                      onChanged: updateFuel,
                      activeColor: fuelLevel > 20 ? Colors.green : Colors.red,
                      inactiveColor: Colors.grey,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 20),
              LinearProgressIndicator(
                value: fuelLevel / 100,
                backgroundColor: Colors.grey[300],
                valueColor: AlwaysStoppedAnimation<Color>(
                  fuelLevel > 20 ? Colors.green : Colors.red,
                ),
              ),
            ],
          ),
        ),
      ),
      bottomNavigationBar: BottomAppBar(
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            AnimatedContainer(
              duration: const Duration(milliseconds: 300),
              padding: const EdgeInsets.all(8.0),
              decoration: BoxDecoration(
                color: selectedTabIndex == 0
                    ? Colors.blue.withOpacity(0.2)
                    : Colors.transparent,
                borderRadius: BorderRadius.circular(12),
              ),
              child: IconButton(
                icon: const Icon(Icons.home),
                onPressed: () {
                  setState(() {
                    selectedTabIndex = 0;
                  });
                  Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(
                        builder: (context) => const FuelBrakeScreen()),
                  );
                },
              ),
            ),
            AnimatedContainer(
              duration: const Duration(milliseconds: 300),
              padding: const EdgeInsets.all(8.0),
              decoration: BoxDecoration(
                color: selectedTabIndex == 1
                    ? Colors.blue.withOpacity(0.2)
                    : Colors.transparent,
                borderRadius: BorderRadius.circular(12),
              ),
              child: IconButton(
                icon: const Icon(Icons.map),
                onPressed: () {
                  setState(() {
                    selectedTabIndex = 1;
                  });
                  Navigator.pushNamed(context, Routes.mapScreen);
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}
