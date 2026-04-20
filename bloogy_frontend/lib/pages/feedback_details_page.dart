import 'package:flutter/material.dart';
import 'dart:async';
// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html;
import 'dart:convert';

class FeedbackDetailsPage extends StatefulWidget {
  final String articleId;
  final String feedbackId;

  FeedbackDetailsPage({required this.articleId, required this.feedbackId});

  @override
  _FeedbackDetailsPageState createState() => _FeedbackDetailsPageState();
}

class _FeedbackDetailsPageState extends State<FeedbackDetailsPage> {
  Map<String, dynamic>? _feedback;
  bool _isLoading = true;
  bool _isUpdating = false;
  final TextEditingController _commentController = TextEditingController();

  Future<String> _getWithCookies(String url) async {
    final completer = Completer<String>();
    final xhr = html.HttpRequest();
    xhr.open('GET', url);
    xhr.withCredentials = true;
    xhr.onLoad.listen((_) => completer.complete(xhr.responseText));
    xhr.onError.listen((_) => completer.completeError('Request failed'));
    xhr.send();
    return completer.future;
  }

  Future<String> _putWithCookies(String url, String body) async {
    final completer = Completer<String>();
    final xhr = html.HttpRequest();
    xhr.open('PUT', url);
    xhr.withCredentials = true;
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onLoad.listen((_) => completer.complete(xhr.responseText));
    xhr.onError.listen((_) => completer.completeError('Request failed'));
    xhr.send(body);
    return completer.future;
  }

  @override
  void initState() {
    super.initState();
    _loadFeedback();
  }

  Future<void> _loadFeedback() async {
    setState(() => _isLoading = true);
    try {
      final url = 'http://localhost:18080/api/v1/feedbacks/get/${widget.articleId}/${widget.feedbackId}';
      final responseText = await _getWithCookies(url);
      final data = json.decode(responseText)['data'];
      setState(() {
        _feedback = data;
        _commentController.text = data['comment'] ?? '';
      });
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Failed to load comment.')),
      );
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _updateFeedback() async {
    setState(() => _isUpdating = true);
    try {
      final url = 'http://localhost:18080/api/v1/feedbacks/update/${widget.articleId}/${widget.feedbackId}';
      final body = json.encode({
        "comment": _commentController.text,
        "commenterName": _feedback?['commenterName'] ?? 'Anonymous',
      });
      await _putWithCookies(url, body);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Comment updated!')),
      );
      Navigator.pop(context, true);
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Update failed.')),
      );
    } finally {
      setState(() => _isUpdating = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Comment Details')),
      body: _isLoading
          ? Center(child: CircularProgressIndicator())
          : Padding(
              padding: EdgeInsets.all(16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'Commenter: ${_feedback?['commenterName'] ?? 'Anonymous'}',
                    style: TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                  ),
                  SizedBox(height: 16),
                  TextField(
                    controller: _commentController,
                    decoration: InputDecoration(
                      labelText: 'Comment',
                      border: OutlineInputBorder(),
                    ),
                    maxLines: 3,
                  ),
                  SizedBox(height: 16),
                  ElevatedButton.icon(
                    icon: Icon(Icons.save),
                    label: _isUpdating
                        ? SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2))
                        : Text('Update'),
                    onPressed: _isUpdating ? null : _updateFeedback,
                  ),
                ],
              ),
            ),
    );
  }
}
