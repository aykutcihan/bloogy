import 'package:flutter/material.dart';
import '../services/articles_service.dart';
import '../services/user_service.dart';

class CreateArticlePage extends StatefulWidget {
  @override
  _CreateArticlePageState createState() => _CreateArticlePageState();
}

class _CreateArticlePageState extends State<CreateArticlePage> {
  final ArticlesService _articlesService = ArticlesService();
  final UserService _userService = UserService();
  final TextEditingController _titleController = TextEditingController();
  final TextEditingController _contentController = TextEditingController();
  bool _isSaving = false;
  String _authorName = '';

  @override
  void initState() {
    super.initState();
    _loadUser();
  }

  Future<void> _loadUser() async {
    final user = await _userService.getCurrentUser();
    setState(() {
      _authorName = user?['name'] ?? '';
    });
  }

  Future<void> _saveArticle() async {
    final title = _titleController.text.trim();
    final content = _contentController.text.trim();

    if (title.isEmpty || content.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Title and content cannot be empty!')),
      );
      return;
    }

    setState(() => _isSaving = true);

    try {
      await _articlesService.createArticle(title, content);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Article saved!')),
      );
      Navigator.pop(context, true);
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Failed to save article.')),
      );
    } finally {
      setState(() => _isSaving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('New Article')),
      body: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          children: [
            TextField(
              controller: _titleController,
              decoration: InputDecoration(
                labelText: 'Title',
                border: OutlineInputBorder(),
              ),
            ),
            SizedBox(height: 16),
            TextField(
              controller: _contentController,
              decoration: InputDecoration(
                labelText: 'Content',
                border: OutlineInputBorder(),
              ),
              maxLines: 8,
            ),
            SizedBox(height: 8),
            if (_authorName.isNotEmpty)
              Text('Author: $_authorName', style: TextStyle(color: Colors.grey)),
            SizedBox(height: 16),
            ElevatedButton.icon(
              icon: Icon(Icons.save),
              label: _isSaving
                  ? SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2))
                  : Text('Save'),
              onPressed: _isSaving ? null : _saveArticle,
            ),
          ],
        ),
      ),
    );
  }
}
