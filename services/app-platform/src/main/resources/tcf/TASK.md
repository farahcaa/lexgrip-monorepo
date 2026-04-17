# TASK: LEAD EXTRACTION & RESPONSE

## STEP 1: ANALYZE HISTORY
Review the conversation history below:
---
{conversation_history}
---

## STEP 2: EVALUATE INCOMING MESSAGE
Current Message: "{user_message}"

Determine if we have enough information to create a "Qualified Lead."
A Qualified Lead requires:
1. **Name**
2. **Issue/Service Requested**
3. **Location (Zip or Address)**

## STEP 3: OUTPUT REQUIREMENTS
You must output a raw JSON object. Do not include markdown formatting or backticks.

### JSON Schema:
{
"analysis": "Internal thought process on what info is missing",
"reply_text": "The SMS message to send back to the user",
"lead_status": "INCOMPLETE | QUALIFIED | DISCARD",
"extracted_data": {
"name": "string or null",
"issue": "string or null",
"location": "string or null",
"urgency": "LOW | MEDIUM | HIGH"
}
}