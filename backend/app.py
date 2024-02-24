from flask import Flask, jsonify, request
from flask_cors import CORS
import time
import boto3

app = Flask(__name__)
CORS(app)

dynamodb = boto3.resource('dynamodb', region_name='us-east-2')
table_name = 'PriceTable'
table = dynamodb.Table(table_name)

@app.route('/frontpage', methods=['GET'])
def get_items_last_2_hours():
    try:
        hours = request.args.get('hrs')

        if not hours or not hours.isdigit() or int(hours) <= 0:
            return jsonify({'message': 'Invalid Input'}), 422

        max_hour = min(int(hours), 96)

        two_hours_ago_timestamp = int(time.time()) - (max_hour * 60 * 60)

        response = table.scan(
            FilterExpression='#timestamp > :start',
            ExpressionAttributeNames={'#timestamp': 'timestamp'},
            ExpressionAttributeValues={':start': two_hours_ago_timestamp}
        )

        items = response.get('Items')

        if items:
            #result = [{'upc': item['upc']['S'], 'timestamp': int(item['timestamp']['N'])} for item in items]

            return jsonify(items)
        else:
            return jsonify({})#{'message': 'None'}), 404


    except Exception as e:
        return jsonify({'error': str(e)}), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80, debug=False)
