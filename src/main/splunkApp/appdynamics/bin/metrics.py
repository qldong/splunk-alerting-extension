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


class Metric(threading.Thread):
	_interval = None
	_name = None
	_url = None
	_stopping = None

	def __init__(self, name, url, interval, username, password):
		self._interval = interval
		self._name = name
		self._url = url
		self._username = username
		self._password = password
		self._stopping = False

		threading.Thread.__init__(self)

	def run(self):
		while not self._stopping:
			try:
				myhttp = httplib2.Http(disable_ssl_certificate_validation=True)
				myhttp.add_credentials(self._username, self._password)
				url = self._url + '&output=JSON'
				logger.debug('Requesting metric from url: %s' % url)
				response, content = myhttp.request(url, 'GET')
				logger.debug('Response: %s' % content)
				parsed = json.loads(content)

				for metric in parsed:
					output = datetime.datetime.strftime(datetime.datetime.now(), "%Y-%m-%d %H:%M:%S.%f") + " "
					output += 'name="%s" ' % self._name
					output += 'frequency=%s ' % metric['frequency']
					output += 'metricPath="%s" ' % metric['metricPath']
					output += 'value=%s ' % metric['metricValues'][0]['value']
					output += 'current=%s ' % metric['metricValues'][0]['current']
					output += 'min=%s ' % metric['metricValues'][0]['min']
					output += 'max=%s ' % metric['metricValues'][0]['max']

					out = globals()['out']
					out.debug(output)

				time.sleep(self._interval)

			except Exception, e:
				import traceback

				stack = traceback.format_exc()
				logger.error("Exception received attempting to retrieve metric '%s': %s" % (self._name, e))
				logger.error("Stack trace for metric '%s': %s" % (self._name, stack))
				time.sleep(self._interval)

	def stop(self):
		self._stopping = True


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
	for metric in metrics:
		metric.stop()
	sys.exit(0)


def getMetrics():
	conf = ConfigParser()
	conf.read([os.environ['SPLUNK_HOME'] + '/etc/apps/appdynamics/local/metrics.conf'])
	sections = conf.sections()

	metrics = []

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

			metrics.append(Metric(name, url, interval, username, password))
		except Exception, e:
			logger.error("Parsing error reading metric '%s'.  Error: %s" % (section, e))

	return metrics


if __name__ == '__main__':
	# Setup logging
	logger = logging.getLogger('appdynamics_metrics')
	logger.propagate = False # Prevent the log messages from being duplicated in the python.log file
	logger.setLevel(logging.DEBUG)
	formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
	fileHandler = logging.handlers.RotatingFileHandler(
		os.environ['SPLUNK_HOME'] + '/var/log/splunk/appdynamics_metrics.log', maxBytes=25000000, backupCount=5)
	formatter = logging.Formatter('%(asctime)s %(levelname)s %(message)s')
	fileHandler.setFormatter(formatter)
	logger.addHandler(fileHandler)
	logger.info('AppDynamics Metrics Grabber started')

	out = logging.getLogger('appdynamics_out')
	formatter = logging.Formatter('%(message)s')
	handler = logging.handlers.RotatingFileHandler(
		filename=os.environ['SPLUNK_HOME'] + '/etc/apps/appdynamics/output/metrics.log', maxBytes=25000000,
		backupCount=5)
	handler.setFormatter(formatter)
	out.addHandler(handler)
	out.setLevel(logging.DEBUG)

	metrics = getMetrics()
	for metric in metrics:
		metric.start()

	set_exit_handler(handle_exit)
	while True:
		try:
			time.sleep(1.0)
		except KeyboardInterrupt:
			handle_exit()
