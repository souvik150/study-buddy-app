require('dotenv').config()
const nodemailer = require('nodemailer')

const sendEmail = async (email, subject, text) => {
  try {
    const transporter = nodemailer.createTransport({
      host: 'smtp.gmail.com',
      port: 465,
      secure: true,
      auth: {
        user: process.env.GMAIL_ID,
        pass: process.env.GMAIL_PASSWORD
      }
    })

    await transporter.sendMail({
      from: 'StudyBuddy CodeChef VIT <no-reply@studybuddy.cc>',
      to: email,
      subject: subject,
      text: text
    })
    console.log('email sent sucessfully')
  } catch (error) {
    console.log('email not sent')
    console.log(error)
  }
}

module.exports = sendEmail