import cherrypy
import os



class hostYOURLS(object):

    @cherrypy.expose
    def __index__(self):
        return "<html><body><h1>Hello World!</h1></body></html>"


if __name__ == "__main__":
    cherrypy.config.update(os.path.join(os.getcwd(), "app_config"))
    cherrypy.quickstart(hostYOURLS(), config=os.path.join(os.getcwd(), "app_config"))