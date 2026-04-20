import 'package:flutter/material.dart';
import '../services/articles_service.dart';

class UpdateArticlePage extends StatefulWidget {
  final String articleId;
  final String currentTitle;
  final String currentContent;

  UpdateArticlePage({
    required this.articleId,
    required this.currentTitle,
    required this.currentContent,
  });

  @override
  _UpdateArticlePageState createState() => _UpdateArticlePageState();
}

class _UpdateArticlePageState extends State<UpdateArticlePage> {
  final ArticlesService _articlesService = ArticlesService();
  final TextEditingController _titleController = TextEditingController();
  final TextEditingController _contentController = TextEditingController();
  bool _isSaving = false;

  @override
  void initState() {
    super.initState();
    _titleController.text = widget.currentTitle;
    _contentController.text = widget.currentContent;
  }

  Future<void> _update() async {
    setState(() => _isSaving = true);
    try {
      await _articlesService.updateArticle(
        widget.articleId,
        _titleController.text.trim(),
        _contentController.text.trim(),
      );
      Navigator.pop(context, true);
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Update failed.')),
      );
    } finally {
      setState(() => _isSaving = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Edit Article')),
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
            SizedBox(height: 16),
            ElevatedButton.icon(
              icon: Icon(Icons.save),
              label: _isSaving
                  ? SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2))
                  : Text('Save'),
              onPressed: _isSaving ? null : _update,
            ),
          ],
        ),
      ),
    );
  }
}
