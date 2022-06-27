const mongoose = require('mongoose')

const GroupsSchema = new mongoose.Schema({
  name: { type: String, required: true },
  description: { type: String, default: '' },
  inviteCode: { type: String, required: true, unique: true },
  members: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
  requests: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
  admin: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  subject: { type: String, required: true },
  quizes: [{ 
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Quiz'
  }],
  modules: [{
    name: { type: String, required: true },
    daysToComplete: { type: Number, required: true },
    completedUsers: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User' }],
    date: { type: Date, default: Date.now }
  }],
  date: { type: Date, default: Date.now }
})


const QuizSchema = new mongoose.Schema({
  group: { type: mongoose.Schema.Types.ObjectId, ref: 'Groups' },
  time: { type: Number, required: true, default: 0 },
  creator: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
  attempted: [{
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User' },
    noOfAttempts: { type: Number, default: 0 },
    lastAttempt: [{
      question: { type: String },
      answer: { type: String },
      correct: { type: Boolean }
    }],
    score: { type: Number, default: 0 },
    date: { type: Date, default: Date.now }
  }],
  questions: [{
    question: { type: String, required: true },
    options: [{ type: String, required: true }],
    answer: { type: String, required: true },
  }],
  date: { type: Date, default: Date.now }
})


module.exports = {
  Groups: mongoose.model('Groups', GroupsSchema),
  Quiz: mongoose.model('Quiz', QuizSchema)
}
