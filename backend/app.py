from flask import Flask, jsonify, request
from flask_cors import CORS
from datetime import datetime, timezone, timedelta
from zoneinfo import ZoneInfo
import time
import boto3

app = Flask(__name__)
CORS(app)

dynamodb = boto3.resource('dynamodb', region_name='us-east-2')
logs = boto3.client('logs', region_name='us-east-2')
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
            return jsonify(items)
        else:
            return jsonify({})

    except Exception as e:
        return jsonify({'error': str(e)}), 500


@app.route('/db-health', methods=['GET'])
def db_health():
    try:
        response = table.scan(Limit=1)

        if 'Items' in response:
            return jsonify({'status': 'ok'}), 200
        else:
            return jsonify({'status': 'error'}), 500

    except Exception as e:
        return jsonify({'status': 'error'}), 500


@app.route('/last-run-fetcher', methods=['GET'])
def health_check():
    try:
        response = logs.describe_log_streams(
            logGroupName="/ecs/pricefetcher",
            orderBy='LastEventTime',
            descending=True,
            limit=1
        )
        last_date_written = datetime.utcfromtimestamp(response['logStreams'][0].get('lastEventTimestamp') / 1000.0).timestamp()
        return jsonify({'status': 'ok', 'last_log_date': last_date_written}), 200
    except Exception as e:
        return jsonify({'status': 'error', 'message': str(e)}), 500


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=80, debug=False)
