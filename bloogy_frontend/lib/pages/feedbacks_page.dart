import 'package:flutter/material.dart';
import '../services/feedback_service.dart';

class FeedbacksPage extends StatefulWidget {
  final String articleId;
  FeedbacksPage({required this.articleId});

  @override
  _FeedbacksPageState createState() => _FeedbacksPageState();
}

class _FeedbacksPageState extends State<FeedbacksPage> {
  final FeedbackService _feedbackService = FeedbackService();
  List<dynamic> _feedbacks = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadFeedbacks();
  }

  Future<void> _loadFeedbacks() async {
    setState(() => _isLoading = true);
    try {
      final feedbacks = await _feedbackService.getFeedbacksByArticle(widget.articleId);
      setState(() => _feedbacks = feedbacks);
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Failed to load comments.')),
      );
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Comments')),
      body: _isLoading
          ? Center(child: CircularProgressIndicator())
          : _feedbacks.isEmpty
              ? Center(child: Text('No comments yet.'))
              : ListView.builder(
                  itemCount: _feedbacks.length,
                  itemBuilder: (context, index) {
                    final f = _feedbacks[index];
                    return ListTile(
                      title: Text(f['comment'] ?? ''),
                      subtitle: Text(f['commenterName'] ?? 'Anonymous'),
                      leading: Icon(Icons.comment),
                    );
                  },
                ),
    );
  }
}
