import 'package:flutter/material.dart';

class FloatingSearchBar extends StatelessWidget {
  const FloatingSearchBar({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.all(10).add(const EdgeInsets.only(right: 10)),
      width: MediaQuery.of(context).size.width - 80,
      padding: const EdgeInsets.only(left: 20),
      height: 50,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(10),
      ),
      alignment: Alignment.centerLeft,
      child: const Text('Nhập từ khoá để tìm kiếm'),
    );
  }
}
