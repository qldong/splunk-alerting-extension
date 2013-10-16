# Copyright (C) 2012 AppDynamics, All Rights Reserved.  Version 3.5
from ConfigParser import ConfigParser
import json
import httplib2
import threading
import logging
import logging.handlers
import os
import sys
import time
import datetime


class Event(threading.Thread):

	def __init__(self, name, url, interval, username, password):
		self._interval = interval
		self._name = name
		self._url = url
		self._username = username
		self._password = password
		self._stopping = False
		self._event = threading.Event()

		threading.Thread.__init__(self)

	def run(self):
		while not self._event.is_set():
			try:
				myhttp = httplib2.Http(disable_ssl_certificate_validation=True)
				myhttp.add_credentials(self._username, self._password)
				url = self._url + '&output=JSON'
				logger.debug('Requesting event from url: %s' % url)
				response, content = myhttp.request(url, 'GET')
				logger.debug('Response: %s' % content)
				parsed = json.loads(content)

				for event in parsed:
					common_output = datetime.datetime.strftime(datetime.datetime.now(), "%Y-%m-%d %H:%M:%S.%f") + " "
					common_output += 'name="%s" ' % self._name
					common_output += 'archived=%s ' % event['archived']
					common_output += 'url=%s ' % event['deepLinkUrl']
					common_output += 'eventTime=%s ' % event['eventTime']
					common_output += 'id=%s ' % event['id']
					common_output += 'markedAsRead=%s ' % event['markedAsRead']
					common_output += 'markedAsResolved=%s ' % event['markedAsResolved']
					common_output += 'severity=%s ' % event['severity']
					common_output += 'subType=%s ' % event['subType']
					common_output += 'summary="%s" ' % event['summary']
					common_output += 'triggeredEntity=%s ' % event['triggeredEntity']
					common_output += 'type=%s ' % event['type']

					out = globals()['out']
					for entity in event['affectedEntities']:
						output = 'entityId=%s ' % entity['entityId']
						output += 'entityType=%s ' % entity['entityType']

						out.debug(common_output + output)
			except Exception, e:
				import traceback

				stack = traceback.format_exc()
				logger.error("Exception received attempting to retrieve event '%s': %s" % (self._name, e))
				logger.error("Stack trace for event '%s': %s" % (self._name, stack))

			self._event.wait(self._interval)

	def stop(self):
		self._event.set()


# Copied from http://danielkaes.wordpress.com/2009/06/04/how-to-catch-kill-events-with-python/
def set_exit_handler(func):
	if os.name == "nt":
		try:
			import win32api

			win32api.SetConsoleCtrlHandler(func, True)
		except ImportError:
			version = ".".join(map(str, sys.version_info[:2]))
			raise Exception("pywin32 not installed for Python " + version)
	else:
		import signal

		signal.signal(signal.SIGTERM, func)
		signal.signal(signal.SIGINT, func)


def handle_exit(sig=None, func=None):
	print '\n\nCaught kill, exiting...'
	for event in events:
		event.stop()
	sys.exit(0)


def get_events():
	conf = ConfigParser()
	conf.read([os.environ['SPLUNK_HOME'] + '/etc/apps/appdynamics/local/events.conf'])
	sections = conf.sections()

	events = []

	for section in sections:
		try:
			name = section
			items = dict(conf.items(section))
			url = items['url']
			username = items['username']
			password = items['password']

			if 'interval' not in items:
				interval = float(60)
			else:
				interval = float(items['interval'])

			events.append(Event(name, url, interval, username, password))
		except Exception, e:
			logger.error("Parsing error reading event '%s'.  Error: %s" % (section, e))

	return events


if __name__ == '__main__':
	# Setup logging
	logger = logging.getLogger('appdynamics_metrics')
	logger.propagate = False  # Prevent the log messages from being duplicated in the python.log file
	logger.setLevel(logging.DEBUG)
	formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
	fileHandler = logging.handlers.RotatingFileHandler(
		os.environ['SPLUNK_HOME'] + '/var/log/splunk/appdynamics_metrics.log', maxBytes=25000000, backupCount=5)
	formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
	fileHandler.setFormatter(formatter)
	logger.addHandler(fileHandler)
	logger.info('AppDynamics Events Grabber started')

	out = logging.getLogger('appdynamics_out')
	formatter = logging.Formatter('%(message)s')
	handler = logging.handlers.RotatingFileHandler(
		filename=os.environ['SPLUNK_HOME'] + '/etc/apps/appdynamics/output/events.log', maxBytes=25000000,
		backupCount=5)
	handler.setFormatter(formatter)
	out.addHandler(handler)
	out.setLevel(logging.DEBUG)

	events = get_events()
	for event in events:
		event.start()

	set_exit_handler(handle_exit)
	while True:
		try:
			time.sleep(1)
		except KeyboardInterrupt:
			handle_exit()
