import 'package:flutter/material.dart';
import 'update_article_page.dart';
import 'feedbacks_page.dart';
import '../services/articles_service.dart';
import '../services/feedback_service.dart';
import '../services/user_service.dart';

class ArticleDetailsPage extends StatefulWidget {
  final String articleId;
  ArticleDetailsPage({required this.articleId});

  @override
  _ArticleDetailsPageState createState() => _ArticleDetailsPageState();
}

class _ArticleDetailsPageState extends State<ArticleDetailsPage> {
  final ArticlesService _articlesService = ArticlesService();
  final FeedbackService _feedbackService = FeedbackService();
  final UserService _userService = UserService();
  final TextEditingController _feedbackController = TextEditingController();

  Map<String, dynamic>? _article;
  String _currentUserName = '';
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    try {
      final results = await Future.wait([
        _articlesService.getArticleById(widget.articleId),
        _userService.getCurrentUser(),
      ]);
      setState(() {
        _article = results[0] as Map<String, dynamic>?;
        final user = results[1] as Map<String, dynamic>?;
        _currentUserName = user?['name'] ?? '';
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _submitFeedback() async {
    final text = _feedbackController.text.trim();
    if (text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Comment cannot be empty!')),
      );
      return;
    }

    try {
      await _feedbackService.submitFeedback(
        widget.articleId,
        _currentUserName.isNotEmpty ? _currentUserName : 'Anonymous',
        text,
      );
      _feedbackController.clear();
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Comment posted!')),
      );
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Failed to post comment.')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    final isOwner = _article?['author'] == _currentUserName;

    return Scaffold(
      appBar: AppBar(title: Text('Article Details')),
      body: SingleChildScrollView(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              _article?['title'] ?? 'No title',
              style: TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            SizedBox(height: 8),
            Text(
              'Author: ${_article?['author'] ?? 'Unknown'}',
              style: TextStyle(color: Colors.grey),
            ),
            Divider(height: 32),
            Text(
              _article?['content'] ?? 'No content',
              style: TextStyle(fontSize: 16),
            ),
            SizedBox(height: 24),
            if (isOwner)
              ElevatedButton.icon(
                icon: Icon(Icons.edit),
                label: Text('Edit Article'),
                onPressed: () async {
                  final result = await Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => UpdateArticlePage(
                        articleId: widget.articleId,
                        currentTitle: _article?['title'] ?? '',
                        currentContent: _article?['content'] ?? '',
                      ),
                    ),
                  );
                  if (result == true) _loadData();
                },
              ),
            Divider(height: 32),
            Text('Leave a Comment', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            SizedBox(height: 8),
            TextField(
              controller: _feedbackController,
              decoration: InputDecoration(
                labelText: 'Your comment...',
                border: OutlineInputBorder(),
              ),
              maxLines: 3,
            ),
            SizedBox(height: 8),
            ElevatedButton.icon(
              icon: Icon(Icons.send),
              label: Text('Post Comment'),
              onPressed: _submitFeedback,
            ),
            SizedBox(height: 16),
            OutlinedButton.icon(
              icon: Icon(Icons.comment),
              label: Text('View Comments'),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => FeedbacksPage(articleId: widget.articleId),
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}
